package com.dailyit.dlrm.service.neighborhood;

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
class NeighborhoodControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired NeighborhoodRepository neighborhoodRepository;

    @Autowired PlaceRepository placeRepository;

    @Autowired CategoryRepository categoryRepository;

    @Autowired EntityManager entityManager;

    Category category;

    @BeforeEach
    void setUp() {
        category =
                categoryRepository.save(
                        Category.builder()
                                .name("팝업")
                                .iconUrl("https://dlrm.test/icons/popup.png")
                                .build());
    }

    @Test
    @DisplayName("동네 정보와 영업 중인 장소 수를 응답한다")
    void getNeighborhoods() throws Exception {
        Neighborhood seongsu =
                saveNeighborhood("성수", new BigDecimal("37.5446"), new BigDecimal("127.0559"));
        savePlace(seongsu, PlaceStatus.ACTIVE, LocalTime.of(9, 0), LocalTime.of(22, 0));

        mockMvc.perform(get("/neighborhoods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(seongsu.getId()))
                .andExpect(jsonPath("$[0].station_name").value("성수"))
                .andExpect(jsonPath("$[0].lat").value(37.5446))
                .andExpect(jsonPath("$[0].lng").value(127.0559))
                .andExpect(jsonPath("$[0].open_place_count").value(1));
    }

    @Test
    @DisplayName("id 오름차순으로 응답한다")
    void sortedById() throws Exception {
        Neighborhood seongsu =
                saveNeighborhood("성수", new BigDecimal("37.5446"), new BigDecimal("127.0559"));
        Neighborhood gangnam =
                saveNeighborhood("강남", new BigDecimal("37.4979"), new BigDecimal("127.0276"));
        Neighborhood anguk =
                saveNeighborhood("안국", new BigDecimal("37.5765"), new BigDecimal("126.9854"));

        // 정렬 없이도 우연히 id 순으로 나오지 않도록 "성수"를 수정해 저장 위치를 뒤로 보낸다
        neighborhoodRepository.flush();
        entityManager
                .createNativeQuery("UPDATE neighborhoods SET updated_at = now() WHERE id = :id")
                .setParameter("id", seongsu.getId())
                .executeUpdate();

        mockMvc.perform(get("/neighborhoods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(seongsu.getId()))
                .andExpect(jsonPath("$[1].id").value(gangnam.getId()))
                .andExpect(jsonPath("$[2].id").value(anguk.getId()));
    }

    @Test
    @DisplayName("영업 중인 ACTIVE 장소만 센다")
    void countsOnlyOpenActivePlaces() throws Exception {
        Neighborhood neighborhood =
                saveNeighborhood("성수", new BigDecimal("37.5446"), new BigDecimal("127.0559"));
        savePlace(neighborhood, PlaceStatus.ACTIVE, LocalTime.of(9, 0), LocalTime.of(22, 0));
        savePlace(neighborhood, PlaceStatus.ACTIVE, LocalTime.of(9, 0), LocalTime.of(13, 0));
        savePlace(neighborhood, PlaceStatus.INACTIVE, LocalTime.of(9, 0), LocalTime.of(22, 0));

        mockMvc.perform(get("/neighborhoods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].open_place_count").value(1));
    }

    @Test
    @DisplayName("영업 중인 장소 수를 동네별로 센다")
    void countsOpenPlacesPerNeighborhood() throws Exception {
        Neighborhood seongsu =
                saveNeighborhood("성수", new BigDecimal("37.5446"), new BigDecimal("127.0559"));
        Neighborhood gangnam =
                saveNeighborhood("강남", new BigDecimal("37.4979"), new BigDecimal("127.0276"));
        savePlace(seongsu, PlaceStatus.ACTIVE, LocalTime.of(9, 0), LocalTime.of(22, 0));
        savePlace(seongsu, PlaceStatus.ACTIVE, LocalTime.of(9, 0), LocalTime.of(22, 0));
        savePlace(gangnam, PlaceStatus.ACTIVE, LocalTime.of(9, 0), LocalTime.of(22, 0));

        mockMvc.perform(get("/neighborhoods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(seongsu.getId()))
                .andExpect(jsonPath("$[0].open_place_count").value(2))
                .andExpect(jsonPath("$[1].id").value(gangnam.getId()))
                .andExpect(jsonPath("$[1].open_place_count").value(1));
    }

    @Test
    @DisplayName("영업 중인 장소가 없는 동네는 0으로 응답한다")
    void zeroWhenNoOpenPlace() throws Exception {
        saveNeighborhood("성수", new BigDecimal("37.5446"), new BigDecimal("127.0559"));

        mockMvc.perform(get("/neighborhoods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].open_place_count").value(0));
    }

    private Neighborhood saveNeighborhood(String stationName, BigDecimal lat, BigDecimal lng) {
        return neighborhoodRepository.save(
                Neighborhood.builder().stationName(stationName).lat(lat).lng(lng).build());
    }

    // 지금(화요일 15:00) 기준으로 영업 여부를 정할 수 있도록 화요일 영업시간만 넣는다.
    private void savePlace(
            Neighborhood neighborhood,
            PlaceStatus status,
            LocalTime openTime,
            LocalTime closeTime) {
        Place place =
                Place.builder()
                        .category(category)
                        .neighborhood(neighborhood)
                        .name("들름 팝업스토어")
                        .thumbnailUrl("https://dlrm.test/places/popup.png")
                        .address("서울시 성동구 성수이로")
                        .lat(neighborhood.getLat())
                        .lng(neighborhood.getLng())
                        .scheduleType(ScheduleType.ALWAYS)
                        .status(status)
                        .statusChangeType(StatusChangeType.MANUAL)
                        .build();
        place.addOperatingHour(
                OperatingHour.builder()
                        .dayOfWeek(DayOfWeekCode.TUE)
                        .isDayOff(false)
                        .openTime(openTime)
                        .closeTime(closeTime)
                        .build());
        placeRepository.save(place);
    }
}
