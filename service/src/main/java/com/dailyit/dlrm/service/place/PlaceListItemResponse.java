package com.dailyit.dlrm.service.place;

import static com.dailyit.dlrm.service.place.OperatingHourResponse.toOperatingHours;

import com.dailyit.dlrm.core.domain.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PlaceListItemResponse(
        @Schema(description = "장소 ID", example = "1") Long id,
        @Schema(description = "장소 이름", example = "들름 팝업스토어") String name,
        @Schema(description = "썸네일 이미지 URL", example = "https://dlrm.test/places/popup.png")
                String thumbnailUrl,
        @Schema(description = "카테고리 ID", example = "1") Long categoryId,
        @Schema(description = "장소 위도", example = "37.5443210") BigDecimal lat,
        @Schema(description = "장소 경도", example = "127.0557650") BigDecimal lng,
        @Schema(description = "기준점에서 장소까지의 직선 거리(m). 소수점 첫째 자리에서 반올림", example = "120")
                int distanceM,
        @Schema(description = "요일별 영업시간. 월요일부터 일요일까지 순서대로 항상 7개")
                List<OperatingHourResponse> operatingHours,
        @Schema(description = "지금 영업 중인지 여부. 요청 시점의 한국 시간 기준", example = "true")
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
