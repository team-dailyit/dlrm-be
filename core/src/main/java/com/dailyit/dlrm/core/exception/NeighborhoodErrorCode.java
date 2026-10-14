package com.dailyit.dlrm.core.exception;

import org.springframework.http.HttpStatus;

public enum NeighborhoodErrorCode implements ErrorCode {
    NEIGHBORHOOD_NOT_FOUND(HttpStatus.NOT_FOUND, "동네를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    NeighborhoodErrorCode(HttpStatus status, String message) {
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
