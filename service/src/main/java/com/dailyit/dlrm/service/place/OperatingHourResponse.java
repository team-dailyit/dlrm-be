package com.dailyit.dlrm.service.place;

import com.dailyit.dlrm.core.domain.OperatingHour;
import com.dailyit.dlrm.core.domain.type.DayOfWeekCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record OperatingHourResponse(
        @Schema(description = "요일", example = "MON") DayOfWeekCode dayOfWeek,
        @Schema(description = "true면 휴무일. 영업시간이 등록되지 않은 요일도 휴무일로 응답한다", example = "false")
                boolean isDayOff,
        @Schema(description = "오픈 시각(HH:mm). 휴무일이면 null", type = "string", example = "18:00")
                @JsonFormat(pattern = "HH:mm")
                LocalTime openTime,
        @Schema(
                        description =
                                "마감 시각(HH:mm). 휴무일이면 null. 오픈 시각보다 이르면 다음날 마감이고,"
                                        + " 오픈·마감이 모두 00:00이면 24시간 영업",
                        type = "string",
                        example = "02:00")
                @JsonFormat(pattern = "HH:mm")
                LocalTime closeTime) {

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
