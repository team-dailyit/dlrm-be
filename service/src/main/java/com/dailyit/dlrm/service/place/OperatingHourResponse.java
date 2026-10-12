package com.dailyit.dlrm.service.place;

import com.dailyit.dlrm.core.domain.OperatingHour;
import com.dailyit.dlrm.core.domain.type.DayOfWeekCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

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
}
