package com.dailyit.dlrm.service.place;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dailyit.dlrm.core.domain.Category;
import com.dailyit.dlrm.core.domain.Neighborhood;
import com.dailyit.dlrm.core.domain.OperatingHour;
import com.dailyit.dlrm.core.domain.Place;
import com.dailyit.dlrm.core.domain.type.DayOfWeekCode;
import com.dailyit.dlrm.core.domain.type.PlaceStatus;
import com.dailyit.dlrm.core.domain.type.ScheduleType;
import com.dailyit.dlrm.core.domain.type.StatusChangeType;
import com.dailyit.dlrm.core.exception.NeighborhoodErrorCode;
import com.dailyit.dlrm.core.repository.CategoryRepository;
import com.dailyit.dlrm.core.repository.NeighborhoodRepository;
import com.dailyit.dlrm.core.repository.PlaceRepository;
import com.dailyit.dlrm.core.testsupport.FixedClockTestConfig;
import com.dailyit.dlrm.core.testsupport.PostgresTestContainer;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Import({PostgresTestContainer.class, FixedClockTestConfig.class})
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class PlaceListControllerTest {

    // 기준점: 성수역
    // 같은 위도에서 경도 0.001도 ≈ 88m
    private static final String LAT = "37.5445";
    private static final String LNG = "127.0559";

    @Autowired MockMvc mockMvc;

    @Autowired PlaceRepository placeRepository;

    @Autowired CategoryRepository categoryRepository;

    @Autowired NeighborhoodRepository neighborhoodRepository;

    @Autowired EntityManager entityManager;

    Category category;

    Neighborhood neighborhood;

    @BeforeEach
    void setUp() {
        category =
                categoryRepository.save(
                        Category.builder()
                                .name("팝업")
                                .iconUrl("https://dlrm.test/icons/popup.png")
                                .build());
        neighborhood =
                neighborhoodRepository.save(
                        Neighborhood.builder()
                                .stationName("성수")
                                .lat(new BigDecimal(LAT))
                                .lng(new BigDecimal(LNG))
                                .build());
    }

    @Test
    @DisplayName("좌표 기준 반경 안의 장소를 가까운 순으로 응답한다")
    void getPlacesByLocation() throws Exception {
        placeRepository.save(buildPlace("먼 장소", "127.0604", PlaceStatus.ACTIVE));
        placeRepository.save(buildPlace("가까운 장소", "127.0569", PlaceStatus.ACTIVE));
        placeRepository.save(buildPlace("반경 밖 장소", "127.0638", PlaceStatus.ACTIVE));

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/places").param("lat", LAT).param("lng", LNG))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.places.length()").value(2))
                .andExpect(jsonPath("$.places[0].name").value("가까운 장소"))
                .andExpect(jsonPath("$.places[0].distance_m").value(88))
                .andExpect(jsonPath("$.places[1].name").value("먼 장소"))
                .andExpect(jsonPath("$.places[1].distance_m").value(398));
    }

    @Test
    @DisplayName("동네로 조회하면 역 좌표를 기준으로 거리를 계산한다")
    void getPlacesByNeighborhood() throws Exception {
        placeRepository.save(buildPlace("장소", "127.0569", PlaceStatus.ACTIVE));

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(
                        get("/places")
                                .param("neighborhood_id", String.valueOf(neighborhood.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.places.length()").value(1))
                .andExpect(jsonPath("$.places[0].distance_m").value(88));
    }

    @Test
    @DisplayName("radius가 없으면 500m, 있으면 그 반경 안의 장소를 응답한다")
    void applyRadius() throws Exception {
        placeRepository.save(buildPlace("88m 장소", "127.0569", PlaceStatus.ACTIVE));
        placeRepository.save(buildPlace("398m 장소", "127.0604", PlaceStatus.ACTIVE));
        placeRepository.save(buildPlace("700m 장소", "127.0638", PlaceStatus.ACTIVE));

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/places").param("lat", LAT).param("lng", LNG))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.places.length()").value(2));

        mockMvc.perform(get("/places").param("lat", LAT).param("lng", LNG).param("radius", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.places.length()").value(3));
    }

    @ParameterizedTest
    @EnumSource(
            value = PlaceStatus.class,
            names = {"SCHEDULED", "INACTIVE", "PENDING"})
    @DisplayName("운영 중이 아닌 장소는 응답하지 않는다")
    void excludeNotActive(PlaceStatus status) throws Exception {
        placeRepository.save(buildPlace("ACTIVE 장소", "127.0604", PlaceStatus.ACTIVE));
        placeRepository.save(buildPlace(status + " 장소", "127.0604", status));

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/places").param("lat", LAT).param("lng", LNG))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.places.length()").value(1))
                .andExpect(jsonPath("$.places[0].name").value("ACTIVE 장소"));
    }

    @Test
    @DisplayName("장소 정보와 영업 정보를 응답한다")
    void openNowAndOperatingHours() throws Exception {
        Place place = buildPlace("가까운 장소", "127.0569", PlaceStatus.ACTIVE);
        // 월요일 휴무, 화요일 09:00~22:00 등록
        place.addOperatingHour(
                OperatingHour.builder().dayOfWeek(DayOfWeekCode.MON).isDayOff(true).build());
        place.addOperatingHour(
                OperatingHour.builder()
                        .dayOfWeek(DayOfWeekCode.TUE)
                        .isDayOff(false)
                        .openTime(LocalTime.of(9, 0))
                        .closeTime(LocalTime.of(22, 0))
                        .build());
        placeRepository.save(place);

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/places").param("lat", LAT).param("lng", LNG))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.places.length()").value(1))
                // 기본 필드
                .andExpect(jsonPath("$.places[0].id").value(place.getId()))
                .andExpect(jsonPath("$.places[0].name").value("가까운 장소"))
                .andExpect(
                        jsonPath("$.places[0].thumbnail_url")
                                .value("https://dlrm.test/places/popup.png"))
                .andExpect(jsonPath("$.places[0].category_id").value(category.getId()))
                .andExpect(jsonPath("$.places[0].lat").value(37.5445))
                .andExpect(jsonPath("$.places[0].lng").value(127.0569))
                .andExpect(jsonPath("$.places[0].distance_m").value(88))
                // 목록에서 뺀 필드
                .andExpect(jsonPath("$.places[0].schedule_type").doesNotExist())
                .andExpect(jsonPath("$.places[0].end_date").doesNotExist())
                // 영업 정보 (지금: 화요일 15:00)
                .andExpect(jsonPath("$.places[0].is_open_now").value(true))
                .andExpect(jsonPath("$.places[0].operating_hours.length()").value(7))
                .andExpect(jsonPath("$.places[0].operating_hours[0].day_of_week").value("MON"))
                .andExpect(jsonPath("$.places[0].operating_hours[0].is_day_off").value(true))
                .andExpect(jsonPath("$.places[0].operating_hours[1].day_of_week").value("TUE"))
                .andExpect(jsonPath("$.places[0].operating_hours[1].is_day_off").value(false))
                .andExpect(jsonPath("$.places[0].operating_hours[1].open_time").value("09:00"))
                .andExpect(jsonPath("$.places[0].operating_hours[1].close_time").value("22:00"))
                .andExpect(jsonPath("$.places[0].operating_hours[6].day_of_week").value("SUN"))
                .andExpect(jsonPath("$.places[0].operating_hours[6].is_day_off").value(true));
    }

    @Test
    @DisplayName("반경 안에 장소가 없으면 빈 목록을 응답한다")
    void emptyResult() throws Exception {
        mockMvc.perform(get("/places").param("lat", LAT).param("lng", LNG))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.places.length()").value(0));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "", // 좌표, 동네 둘 다 없음
                "lat=37.5445&lng=127.0559&neighborhood_id=1", // 둘 다 있음
                "lat=37.5445", // lat만
                "lng=127.0559", // lng만
                "lat=37.5445&lng=127.0559&radius=700", // 허용되지 않은 radius
                "lat=90.1&lng=127.0559", // 위도 범위 밖
                "lat=37.5445&lng=-180.1", // 경도 범위 밖
                "lat=NaN&lng=127.0559", // 숫자가 아닌 좌표
                "lat=37.5445&lng=Infinity" // 무한대 좌표
            })
    @DisplayName("요청 파라미터가 올바르지 않으면 400을 응답한다")
    void badRequest(String query) throws Exception {
        mockMvc.perform(get("/places?" + query)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("없는 동네로 조회하면 404를 응답한다")
    void neighborhoodNotFound() throws Exception {
        mockMvc.perform(get("/places").param("neighborhood_id", String.valueOf(Long.MAX_VALUE)))
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.code")
                                .value(NeighborhoodErrorCode.NEIGHBORHOOD_NOT_FOUND.name()));
    }

    private Place buildPlace(String name, String lng, PlaceStatus status) {
        return Place.builder()
                .category(category)
                .neighborhood(neighborhood)
                .name(name)
                .thumbnailUrl("https://dlrm.test/places/popup.png")
                .address("서울시 영등포구 당산로")
                .lat(new BigDecimal(LAT))
                .lng(new BigDecimal(lng))
                .snsUrl("https://instagram.com/dlrm_popup")
                .scheduleType(ScheduleType.ALWAYS)
                .status(status)
                .statusChangeType(StatusChangeType.MANUAL)
                .build();
    }
}
