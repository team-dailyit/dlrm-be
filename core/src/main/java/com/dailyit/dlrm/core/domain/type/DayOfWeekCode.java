package com.dailyit.dlrm.core.domain.type;

import java.time.DayOfWeek;

public enum DayOfWeekCode {
    MON,
    TUE,
    WED,
    THU,
    FRI,
    SAT,
    SUN;

    public static DayOfWeekCode from(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> MON;
            case TUESDAY -> TUE;
            case WEDNESDAY -> WED;
            case THURSDAY -> THU;
            case FRIDAY -> FRI;
            case SATURDAY -> SAT;
            case SUNDAY -> SUN;
        };
    }
}
