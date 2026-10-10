package com.dailyit.dlrm.service.place;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dailyit.dlrm.core.domain.Category;
import com.dailyit.dlrm.core.domain.Keyword;
import com.dailyit.dlrm.core.domain.OperatingHour;
import com.dailyit.dlrm.core.domain.Place;
import com.dailyit.dlrm.core.domain.type.DayOfWeekCode;
import com.dailyit.dlrm.core.domain.type.PlaceStatus;
import com.dailyit.dlrm.core.domain.type.ScheduleType;
import com.dailyit.dlrm.core.domain.type.StatusChangeType;
import com.dailyit.dlrm.core.repository.CategoryRepository;
import com.dailyit.dlrm.core.repository.KeywordRepository;
import com.dailyit.dlrm.core.repository.PlaceRepository;
import com.dailyit.dlrm.core.testsupport.PostgresTestContainer;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Import(PostgresTestContainer.class)
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class PlaceControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired PlaceRepository placeRepository;

    @Autowired CategoryRepository categoryRepository;

    @Autowired KeywordRepository keywordRepository;

    @Autowired EntityManager entityManager;

    private Place buildPlace(PlaceStatus status) {
        Category category =
                categoryRepository.save(
                        Category.builder()
                                .name("팝업")
                                .iconUrl("https://example.com/icons/popup.png")
                                .build());
        return Place.builder()
                .category(category)
                .name("슈퍼마리오 스페셜 팝업")
                .thumbnailUrl("https://example.com/places/mario.png")
                .address("서울 성동구 연무장7길 1")
                .lat(new BigDecimal("37.5445"))
                .lng(new BigDecimal("127.0559"))
                .snsUrl("https://instagram.com/mario")
                .scheduleType(ScheduleType.ALWAYS)
                .status(status)
                .statusChangeType(StatusChangeType.MANUAL)
                .build();
    }

    @Test
    @DisplayName("장소 상세 정보를 응답한다")
    void getPlaceDetail() throws Exception {
        Place place = buildPlace(PlaceStatus.ACTIVE);

        Keyword family = keywordRepository.save(Keyword.builder().name("가족과함께").build());
        Keyword popup = keywordRepository.save(Keyword.builder().name("팝업스토어").build());

        // 월요일 휴무, 화~일 09:00~22:00
        for (DayOfWeekCode day : DayOfWeekCode.values()) {
            if (day == DayOfWeekCode.MON) {
                place.addOperatingHour(
                        OperatingHour.builder().dayOfWeek(day).isOpen(false).build());
            } else {
                place.addOperatingHour(
                        OperatingHour.builder()
                                .dayOfWeek(day)
                                .isOpen(true)
                                .openTime(LocalTime.of(9, 0))
                                .closeTime(LocalTime.of(22, 0))
                                .build());
            }
        }
        place.getKeywords().add(popup);
        place.getKeywords().add(family);
        placeRepository.save(place);

        // 메모리에 남은 객체가 아니라 DB에서 다시 읽은 값으로 응답하는지 확인한다
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/places/{id}", place.getId()))
                .andExpect(status().isOk())
                // 기본 필드
                .andExpect(jsonPath("$.id").value(place.getId()))
                .andExpect(jsonPath("$.name").value("슈퍼마리오 스페셜 팝업"))
                .andExpect(
                        jsonPath("$.thumbnail_url").value("https://example.com/places/mario.png"))
                .andExpect(jsonPath("$.address").value("서울 성동구 연무장7길 1"))
                .andExpect(jsonPath("$.lat").value(37.5445))
                .andExpect(jsonPath("$.lng").value(127.0559))
                .andExpect(jsonPath("$.sns_url").value("https://instagram.com/mario"))
                .andExpect(jsonPath("$.homepage_url").value(nullValue()))
                .andExpect(jsonPath("$.category_id").value(place.getCategory().getId()))
                // 운영 시간
                .andExpect(jsonPath("$.operating_hours.length()").value(7))
                .andExpect(jsonPath("$.operating_hours[0].day_of_week").value("MON"))
                .andExpect(jsonPath("$.operating_hours[0].is_open").value(false))
                .andExpect(jsonPath("$.operating_hours[0].open_time").value(nullValue()))
                .andExpect(jsonPath("$.operating_hours[0].close_time").value(nullValue()))
                .andExpect(jsonPath("$.operating_hours[1].day_of_week").value("TUE"))
                .andExpect(jsonPath("$.operating_hours[1].is_open").value(true))
                .andExpect(jsonPath("$.operating_hours[1].open_time").value("09:00"))
                .andExpect(jsonPath("$.operating_hours[1].close_time").value("22:00"))
                // 키워드
                .andExpect(jsonPath("$.keywords.length()").value(2))
                .andExpect(jsonPath("$.keywords[0].id").value(family.getId()))
                .andExpect(jsonPath("$.keywords[0].name").value("가족과함께"))
                .andExpect(jsonPath("$.keywords[1].id").value(popup.getId()))
                .andExpect(jsonPath("$.keywords[1].name").value("팝업스토어"));
    }

    @Test
    @DisplayName("등록되지 않은 요일은 휴무로 채워 요일 순으로 7개를 응답한다")
    void fillMissingDaysAsClosed() throws Exception {
        Place place = buildPlace(PlaceStatus.ACTIVE);

        // 영업시간이 월~금만 존재
        for (DayOfWeekCode day : DayOfWeekCode.values()) {
            if (day == DayOfWeekCode.SAT || day == DayOfWeekCode.SUN) {
                continue;
            }
            place.addOperatingHour(
                    OperatingHour.builder()
                            .dayOfWeek(day)
                            .isOpen(true)
                            .openTime(LocalTime.of(9, 0))
                            .closeTime(LocalTime.of(22, 0))
                            .build());
        }
        placeRepository.save(place);

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/places/{id}", place.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operating_hours", hasSize(7)))
                // 월~금
                .andExpect(jsonPath("$.operating_hours[0].day_of_week").value("MON"))
                .andExpect(jsonPath("$.operating_hours[0].is_open").value(true))
                .andExpect(jsonPath("$.operating_hours[1].day_of_week").value("TUE"))
                .andExpect(jsonPath("$.operating_hours[1].is_open").value(true))
                .andExpect(jsonPath("$.operating_hours[2].day_of_week").value("WED"))
                .andExpect(jsonPath("$.operating_hours[2].is_open").value(true))
                .andExpect(jsonPath("$.operating_hours[3].day_of_week").value("THU"))
                .andExpect(jsonPath("$.operating_hours[3].is_open").value(true))
                .andExpect(jsonPath("$.operating_hours[4].day_of_week").value("FRI"))
                .andExpect(jsonPath("$.operating_hours[4].is_open").value(true))
                // 토~일
                .andExpect(jsonPath("$.operating_hours[5].day_of_week").value("SAT"))
                .andExpect(jsonPath("$.operating_hours[5].is_open").value(false))
                .andExpect(jsonPath("$.operating_hours[5].open_time").value(nullValue()))
                .andExpect(jsonPath("$.operating_hours[5].close_time").value(nullValue()))
                .andExpect(jsonPath("$.operating_hours[6].day_of_week").value("SUN"))
                .andExpect(jsonPath("$.operating_hours[6].is_open").value(false))
                .andExpect(jsonPath("$.operating_hours[6].open_time").value(nullValue()))
                .andExpect(jsonPath("$.operating_hours[6].close_time").value(nullValue()));
    }

    @Test
    @DisplayName("없는 장소를 조회하면 404를 응답한다")
    void notFound() throws Exception {
        mockMvc.perform(get("/places/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PLACE_NOT_FOUND"));
    }

    @ParameterizedTest
    @EnumSource(
            value = PlaceStatus.class,
            names = {"SCHEDULED", "INACTIVE", "PENDING"})
    @DisplayName("운영 중이 아닌 장소를 조회하면 404를 응답한다")
    void notActive(PlaceStatus status) throws Exception {
        Place place = buildPlace(status);
        placeRepository.save(place);

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/places/{id}", place.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PLACE_NOT_FOUND"));
    }
}
