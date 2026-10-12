package com.dailyit.dlrm.core.domain;

import com.dailyit.dlrm.core.domain.type.DayOfWeekCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "operating_hours")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperatingHour extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private DayOfWeekCode dayOfWeek;

    @Column(nullable = false)
    private boolean isDayOff;

    private LocalTime openTime;

    private LocalTime closeTime;

    @Builder
    private OperatingHour(
            DayOfWeekCode dayOfWeek, boolean isDayOff, LocalTime openTime, LocalTime closeTime) {
        this.dayOfWeek = dayOfWeek;
        this.isDayOff = isDayOff;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    void assignPlace(Place place) {
        this.place = place;
    }
}
