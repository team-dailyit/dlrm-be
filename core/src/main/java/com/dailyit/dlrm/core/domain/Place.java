package com.dailyit.dlrm.core.domain;

import com.dailyit.dlrm.core.domain.type.DayOfWeekCode;
import com.dailyit.dlrm.core.domain.type.PlaceStatus;
import com.dailyit.dlrm.core.domain.type.ScheduleType;
import com.dailyit.dlrm.core.domain.type.StatusChangeType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "places")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "neighborhood_id", nullable = false)
    private Neighborhood neighborhood;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal lat;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal lng;

    private String snsUrl;

    private String homepageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ScheduleType scheduleType;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlaceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatusChangeType statusChangeType;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OperatingHour> operatingHours = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "place_keywords",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id"))
    private Set<Keyword> keywords = new HashSet<>();

    @Builder
    private Place(
            Category category,
            Neighborhood neighborhood,
            String name,
            String thumbnailUrl,
            String address,
            BigDecimal lat,
            BigDecimal lng,
            String snsUrl,
            String homepageUrl,
            ScheduleType scheduleType,
            LocalDate startDate,
            LocalDate endDate,
            PlaceStatus status,
            StatusChangeType statusChangeType) {
        this.category = category;
        this.neighborhood = neighborhood;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.snsUrl = snsUrl;
        this.homepageUrl = homepageUrl;
        this.scheduleType = scheduleType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.statusChangeType = statusChangeType;
    }

    public void addOperatingHour(OperatingHour operatingHour) {
        operatingHours.add(operatingHour);
        operatingHour.assignPlace(this);
    }

    public boolean isOpenAt(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        DayOfWeekCode today = DayOfWeekCode.from(dateTime.getDayOfWeek());
        DayOfWeekCode yesterday = DayOfWeekCode.from(dateTime.getDayOfWeek().minus(1));

        return findOperatingHour(today)
                        .map(operatingHour -> operatingHour.isOpenSameDayAt(time))
                        .orElse(false)
                || findOperatingHour(yesterday)
                        .map(operatingHour -> operatingHour.isOpenNextDayAt(time))
                        .orElse(false);
    }

    private Optional<OperatingHour> findOperatingHour(DayOfWeekCode dayOfWeek) {
        return operatingHours.stream()
                .filter(operatingHour -> operatingHour.getDayOfWeek() == dayOfWeek)
                .findFirst();
    }
}
