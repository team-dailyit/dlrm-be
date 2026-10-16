package com.dailyit.dlrm.service.category;

import com.dailyit.dlrm.core.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryResponse(
        @Schema(description = "카테고리 ID", example = "1") Long id,
        @Schema(description = "카테고리 이름", example = "전시") String name,
        @Schema(
                        description = "카테고리 아이콘 이미지 URL",
                        example = "https://dlrm.test/icons/exhibition.png")
                String iconUrl) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getIconUrl());
    }
}
