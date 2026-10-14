package com.dailyit.dlrm.service.place;

import static com.dailyit.dlrm.service.place.OperatingHourResponse.toOperatingHours;

import com.dailyit.dlrm.core.domain.Keyword;
import com.dailyit.dlrm.core.domain.Place;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record PlaceDetailResponse(
        Long id,
        String name,
        String thumbnailUrl,
        Long categoryId,
        BigDecimal lat,
        BigDecimal lng,
        String address,
        List<OperatingHourResponse> operatingHours,
        boolean isOpenNow,
        String snsUrl,
        String homepageUrl,
        List<KeywordResponse> keywords) {

    public static PlaceDetailResponse from(Place place, LocalDateTime now) {
        return new PlaceDetailResponse(
                place.getId(),
                place.getName(),
                place.getThumbnailUrl(),
                place.getCategory().getId(),
                place.getLat(),
                place.getLng(),
                place.getAddress(),
                toOperatingHours(place.getOperatingHours()),
                place.isOpenAt(now),
                place.getSnsUrl(),
                place.getHomepageUrl(),
                place.getKeywords().stream()
                        .sorted(Comparator.comparing(Keyword::getId))
                        .map(KeywordResponse::from)
                        .toList());
    }
}
