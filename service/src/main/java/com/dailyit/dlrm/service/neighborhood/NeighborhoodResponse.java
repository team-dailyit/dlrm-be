package com.dailyit.dlrm.service.neighborhood;

import com.dailyit.dlrm.core.domain.Neighborhood;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record NeighborhoodResponse(
        @Schema(description = "동네 ID", example = "1") Long id,
        @Schema(description = "역 이름", example = "성수") String stationName,
        @Schema(description = "역 위도", example = "37.5445810") BigDecimal lat,
        @Schema(description = "역 경도", example = "127.0559610") BigDecimal lng,
        @Schema(description = "지금 영업 중인 장소 수. 요청 시점의 한국 시간 기준", example = "12")
                long openPlaceCount) {
    public static NeighborhoodResponse from(Neighborhood neighborhood, long openPlaceCount) {
        return new NeighborhoodResponse(
                neighborhood.getId(),
                neighborhood.getStationName(),
                neighborhood.getLat(),
                neighborhood.getLng(),
                openPlaceCount);
    }
}
