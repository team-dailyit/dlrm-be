package com.dailyit.dlrm.core.exception;

/** 서비스 예외의 부모. 상속한 예외를 던지면 전역 핸들러가 ErrorCode로 응답을 만든다. */
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    protected BaseException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }
}
