package com.dailyit.dlrm.service.neighborhood;

import com.dailyit.dlrm.core.domain.Neighborhood;
import java.math.BigDecimal;

public record NeighborhoodResponse(
        Long id, String stationName, BigDecimal lat, BigDecimal lng, long openPlaceCount) {
    public static NeighborhoodResponse from(Neighborhood neighborhood, long openPlaceCount) {
        return new NeighborhoodResponse(
                neighborhood.getId(),
                neighborhood.getStationName(),
                neighborhood.getLat(),
                neighborhood.getLng(),
                openPlaceCount);
    }
}
