package com.dailyit.dlrm.core.config;

import com.dailyit.dlrm.core.exception.ErrorCode;

public record ErrorResponse(int status, String code, String message) {

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.status().value(), errorCode.code(), errorCode.message());
    }
}
