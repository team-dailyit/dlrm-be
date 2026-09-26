package com.dailyit.dlrm.core.exception;

import org.springframework.http.HttpStatus;

/** API 에러 응답의 HTTP 상태, 코드, 메시지. 기능별 에러 enum이 구현한다. */
public interface ErrorCode {
    HttpStatus status();

    String code();

    String message();
}
