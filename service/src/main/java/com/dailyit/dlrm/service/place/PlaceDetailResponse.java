package com.dailyit.dlrm.service.place;

import static com.dailyit.dlrm.service.place.OperatingHourResponse.toOperatingHours;

import com.dailyit.dlrm.core.domain.Keyword;
import com.dailyit.dlrm.core.domain.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record PlaceDetailResponse(
        @Schema(description = "장소 ID", example = "1") Long id,
        @Schema(description = "장소 이름", example = "들름 팝업스토어") String name,
        @Schema(description = "썸네일 이미지 URL", example = "https://dlrm.test/places/popup.png")
                String thumbnailUrl,
        @Schema(description = "카테고리 ID", example = "1") Long categoryId,
        @Schema(description = "장소 위도", example = "37.5443210") BigDecimal lat,
        @Schema(description = "장소 경도", example = "127.0557650") BigDecimal lng,
        @Schema(description = "주소", example = "서울시 성동구 성수이로") String address,
        @Schema(description = "요일별 영업시간. 월요일부터 일요일까지 순서대로 항상 7개")
                List<OperatingHourResponse> operatingHours,
        @Schema(description = "지금 영업 중인지 여부. 요청 시점의 한국 시간 기준", example = "true") boolean isOpenNow,
        @Schema(description = "SNS URL. 없으면 null", example = "https://instagram.com/dlrm_popup")
                String snsUrl,
        @Schema(description = "홈페이지 URL. 없으면 null", example = "https://dlrm.test/popup")
                String homepageUrl,
        @Schema(description = "키워드 목록. 키워드 ID 오름차순") List<KeywordResponse> keywords) {

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
