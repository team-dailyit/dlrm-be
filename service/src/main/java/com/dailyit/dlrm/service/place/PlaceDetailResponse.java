package com.dailyit.dlrm.service.place;

import com.dailyit.dlrm.core.domain.Keyword;
import com.dailyit.dlrm.core.domain.OperatingHour;
import com.dailyit.dlrm.core.domain.Place;
import com.dailyit.dlrm.core.domain.type.DayOfWeekCode;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record PlaceDetailResponse(
        Long id,
        String name,
        String thumbnailUrl,
        Long categoryId,
        BigDecimal lat,
        BigDecimal lng,
        String address,
        List<OperatingHourResponse> operatingHours,
        String snsUrl,
        String homepageUrl,
        List<KeywordResponse> keywords) {

    public static PlaceDetailResponse from(Place place) {
        return new PlaceDetailResponse(
                place.getId(),
                place.getName(),
                place.getThumbnailUrl(),
                place.getCategory().getId(),
                place.getLat(),
                place.getLng(),
                place.getAddress(),
                toOperatingHours(place.getOperatingHours()),
                place.getSnsUrl(),
                place.getHomepageUrl(),
                place.getKeywords().stream()
                        .sorted(Comparator.comparing(Keyword::getId))
                        .map(KeywordResponse::from)
                        .toList());
    }

    // 요일 순(MON~SUN)으로 항상 7개를 응답하고, 등록되지 않은 요일은 휴무로 채운다.
    private static List<OperatingHourResponse> toOperatingHours(
            List<OperatingHour> operatingHours) {
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
