package com.dailyit.dlrm.core.config;

import com.dailyit.dlrm.core.exception.BaseException;
import com.dailyit.dlrm.core.exception.CommonErrorCode;
import com.dailyit.dlrm.core.exception.ErrorCode;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        ErrorCode errorCode = e.errorCode();
        ErrorResponse body = ErrorResponse.from(errorCode);
        return ResponseEntity.status(errorCode.status()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception", e);
        ErrorResponse body = ErrorResponse.from(CommonErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(CommonErrorCode.INTERNAL_SERVER_ERROR.status()).body(body);
    }

    // Spring 예외의 상태 코드와 헤더는 그대로 두고 본문만 바꾼다.
    // 응답 생성은 super에 맡겨 부모의 방어 로직(응답이 이미 커밋된 경우 등)을 유지한다.
    @Override
    protected @Nullable ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {
        if (statusCode.is5xxServerError()) {
            log.error("Unhandled exception", ex);
        }
        ErrorResponse errorResponse = toErrorResponse(statusCode);
        return super.handleExceptionInternal(ex, errorResponse, headers, statusCode, request);
    }

    private ErrorResponse toErrorResponse(HttpStatusCode statusCode) {
        for (CommonErrorCode errorCode : CommonErrorCode.values()) {
            if (errorCode.status().value() == statusCode.value()) {
                return ErrorResponse.from(errorCode);
            }
        }
        HttpStatus httpStatus = HttpStatus.resolve(statusCode.value());
        String code = httpStatus != null ? httpStatus.name() : "UNKNOWN";
        return new ErrorResponse(statusCode.value(), code, "요청을 처리할 수 없습니다.");
    }
}
