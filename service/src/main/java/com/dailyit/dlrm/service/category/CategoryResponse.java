package com.dailyit.dlrm.service.category;

import com.dailyit.dlrm.core.domain.Category;

public record CategoryResponse(Long id, String name, String iconUrl) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getIconUrl());
    }
}
