package com.dailyit.dlrm.service.place;

import com.dailyit.dlrm.core.domain.Keyword;

public record KeywordResponse(Long id, String name) {
    public static KeywordResponse from(Keyword keyword) {
        return new KeywordResponse(keyword.getId(), keyword.getName());
    }
}
