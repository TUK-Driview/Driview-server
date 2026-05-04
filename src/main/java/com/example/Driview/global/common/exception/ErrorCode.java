package com.example.Driview.global.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode implements BaseErrorCode {

    // 공통
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON001", "잘못된 요청입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON002", "입력값이 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON003", "서버 내부 오류가 발생했습니다."),

    // Auth
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH001", "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH002", "이미 사용 중인 닉네임입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH003", "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH004", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH005", "비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH006", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH007", "만료된 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH008", "리프레시 토큰을 찾을 수 없습니다."),
    WITHDRAWN_USER(HttpStatus.FORBIDDEN, "AUTH009", "탈퇴한 사용자입니다."),

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