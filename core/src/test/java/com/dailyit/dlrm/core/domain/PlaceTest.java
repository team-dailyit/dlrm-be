package com.dailyit.dlrm.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dailyit.dlrm.core.domain.type.DayOfWeekCode;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlaceTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 10, 12);
    private static final LocalDate TUESDAY = LocalDate.of(2026, 10, 13);
    private static final LocalDate WEDNESDAY = LocalDate.of(2026, 10, 14);

    @Test
    @DisplayName("영업 시간 안이면 영업 중이다")
    void openDuringOperatingH일ours() {
        Place place =
                placeWithOperatingHour(DayOfWeekCode.TUE, LocalTime.of(9, 0), LocalTime.of(22, 0));

        boolean result = place.isOpenAt(TUESDAY.atTime(15, 0));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("오픈 시각 정각이면 영업 중이다")
    void openAtOpenTime() {
        Place place =
                placeWithOperatingHour(DayOfWeekCode.TUE, LocalTime.of(9, 0), LocalTime.of(22, 0));

        boolean result = place.isOpenAt(TUESDAY.atTime(9, 0));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("마감 시각 정각이면 영업 중이 아니다")
    void closedAtCloseTime() {
        Place place =
                placeWithOperatingHour(DayOfWeekCode.TUE, LocalTime.of(9, 0), LocalTime.of(22, 0));

        boolean result = place.isOpenAt(TUESDAY.atTime(22, 0));

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("오픈 시각 전이면 영업 중이 아니다")
    void closedBeforeOpenTime() {
        Place place =
                placeWithOperatingHour(DayOfWeekCode.TUE, LocalTime.of(9, 0), LocalTime.of(22, 0));

        boolean result = place.isOpenAt(TUESDAY.atTime(8, 0));

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("휴무일이면 영업 중이 아니다")
    void closedOnDayOff() {
        Place place = Place.builder().build();
        place.addOperatingHour(
                OperatingHour.builder()
                        .dayOfWeek(DayOfWeekCode.TUE)
                        .isDayOff(true)
                        .openTime(null)
                        .closeTime(null)
                        .build());

        boolean result = place.isOpenAt(TUESDAY.atTime(13, 0));

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("24시간 영업이면 언제든 영업 중이다")
    void openAllDay() {
        Place place =
                placeWithOperatingHour(DayOfWeekCode.TUE, LocalTime.of(0, 0), LocalTime.of(0, 0));

        boolean result = place.isOpenAt(TUESDAY.atTime(22, 0));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("자정을 넘기는 영업은 오픈 이후 자정 전까지 영업 중이다")
    void openBeforeMidnightWhenOvernight() {
        Place place =
                placeWithOperatingHour(DayOfWeekCode.TUE, LocalTime.of(9, 0), LocalTime.of(2, 0));

        boolean result = place.isOpenAt(TUESDAY.atTime(23, 0));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("자정을 넘기는 영업은 다음날 마감 전까지 영업 중이다")
    void openAfterMidnightUntilCloseTime() {
        Place place =
                placeWithOperatingHour(DayOfWeekCode.TUE, LocalTime.of(9, 0), LocalTime.of(2, 0));

        boolean result = place.isOpenAt(WEDNESDAY.atTime(1, 0));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("자정을 넘기는 영업은 다음날 마감 이후 영업 중이 아니다")
    void closedAfterMidnightCloseTime() {
        Place place =
                placeWithOperatingHour(DayOfWeekCode.TUE, LocalTime.of(9, 0), LocalTime.of(2, 0));

        boolean result = place.isOpenAt(WEDNESDAY.atTime(3, 0));

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("일요일에 자정을 넘기는 영업은 월요일 새벽까지 영업 중이다")
    void openOnMondayAfterSundayOvernight() {
        Place place =
                placeWithOperatingHour(DayOfWeekCode.SUN, LocalTime.of(9, 0), LocalTime.of(2, 0));

        boolean result = place.isOpenAt(MONDAY.atTime(1, 0));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("그 요일 영업시간이 없으면 영업 중이 아니다")
    void closedWhenNoOperatingHour() {
        Place place =
                placeWithOperatingHour(DayOfWeekCode.SUN, LocalTime.of(9, 0), LocalTime.of(22, 0));

        boolean result = place.isOpenAt(TUESDAY.atTime(13, 0));

        assertThat(result).isFalse();
    }

    private Place placeWithOperatingHour(
            DayOfWeekCode dayOfWeek, LocalTime openTime, LocalTime closeTime) {
        Place place = Place.builder().build();
        place.addOperatingHour(
                OperatingHour.builder()
                        .dayOfWeek(dayOfWeek)
                        .isDayOff(false)
                        .openTime(openTime)
                        .closeTime(closeTime)
                        .build());

        return place;
    }
}
