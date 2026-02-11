package com.example.Driview.global.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode implements BaseErrorCode {

    // 공통
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON001", "잘못된 요청입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON002", "입력값이 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON003", "서버 내부 오류가 발생했습니다."),

    // SAMPLE
    SAMPLE_NOT_FOUND(HttpStatus.NOT_FOUND, "SAMPLE001", "샘플 데이터를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}