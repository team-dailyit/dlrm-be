package com.dailyit.dlrm.service.place;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PlaceListResponse(
        @Schema(description = "반경 안의 장소 목록. 기준점에서 가까운 순이고, 없으면 빈 배열")
                List<PlaceListItemResponse> places) {}
