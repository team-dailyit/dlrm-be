package com.dailyit.dlrm.service.place;

import static com.dailyit.dlrm.service.place.OperatingHourResponse.toOperatingHours;

import com.dailyit.dlrm.core.domain.Place;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PlaceListItemResponse(
        Long id,
        String name,
        String thumbnailUrl,
        Long categoryId,
        BigDecimal lat,
        BigDecimal lng,
        int distanceM,
        List<OperatingHourResponse> operatingHours,
        boolean isOpenNow) {
    public static PlaceListItemResponse from(Place place, int distanceM, LocalDateTime now) {
        return new PlaceListItemResponse(
                place.getId(),
                place.getName(),
                place.getThumbnailUrl(),
                place.getCategory().getId(),
                place.getLat(),
                place.getLng(),
                distanceM,
                toOperatingHours(place.getOperatingHours()),
                place.isOpenAt(now));
    }
}
