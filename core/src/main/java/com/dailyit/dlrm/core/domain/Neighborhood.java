package com.dailyit.dlrm.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "neighborhoods")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Neighborhood extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String stationName;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal lat;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal lng;

    @Builder
    private Neighborhood(String stationName, BigDecimal lat, BigDecimal lng) {
        this.stationName = stationName;
        this.lat = lat;
        this.lng = lng;
    }
}
