package com.dailyit.dlrm.service.place;

import com.dailyit.dlrm.core.domain.OperatingHour;
import com.dailyit.dlrm.core.domain.type.DayOfWeekCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record OperatingHourResponse(
        DayOfWeekCode dayOfWeek,
        boolean isDayOff,
        @JsonFormat(pattern = "HH:mm") LocalTime openTime,
        @JsonFormat(pattern = "HH:mm") LocalTime closeTime) {

    public static OperatingHourResponse from(OperatingHour operatingHour) {
        return new OperatingHourResponse(
                operatingHour.getDayOfWeek(),
                operatingHour.isDayOff(),
                operatingHour.getOpenTime(),
                operatingHour.getCloseTime());
    }

    public static OperatingHourResponse closed(DayOfWeekCode dayOfWeek) {
        return new OperatingHourResponse(dayOfWeek, true, null, null);
    }

    // 요일 순(MON~SUN)으로 항상 7개를 응답하고, 등록되지 않은 요일은 휴무로 채운다.
    public static List<OperatingHourResponse> toOperatingHours(List<OperatingHour> operatingHours) {
        Map<DayOfWeekCode, OperatingHour> byDay =
                operatingHours.stream()
                        .collect(
                                Collectors.toMap(OperatingHour::getDayOfWeek, Function.identity()));

        return Arrays.stream(DayOfWeekCode.values())
                .map(
                        day ->
                                byDay.containsKey(day)
                                        ? OperatingHourResponse.from(byDay.get(day))
                                        : OperatingHourResponse.closed(day))
                .toList();
    }
}
