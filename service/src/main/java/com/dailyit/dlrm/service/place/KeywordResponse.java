package com.dailyit.dlrm.service.place;

import com.dailyit.dlrm.core.domain.Keyword;
import io.swagger.v3.oas.annotations.media.Schema;

public record KeywordResponse(
        @Schema(description = "키워드 ID", example = "1") Long id,
        @Schema(description = "키워드 이름", example = "가족과함께") String name) {
    public static KeywordResponse from(Keyword keyword) {
        return new KeywordResponse(keyword.getId(), keyword.getName());
    }
}
