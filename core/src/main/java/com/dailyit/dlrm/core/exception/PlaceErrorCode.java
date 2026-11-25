package com.dailyit.dlrm.core.exception;

import org.springframework.http.HttpStatus;

public enum PlaceErrorCode implements ErrorCode {
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "장소를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    PlaceErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String code() {
        return this.name();
    }

    @Override
    public HttpStatus status() {
        return this.status;
    }

    @Override
    public String message() {
        return this.message;
    }
}
