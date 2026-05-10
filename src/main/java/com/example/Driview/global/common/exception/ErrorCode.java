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
    SAMPLE_NOT_FOUND(HttpStatus.NOT_FOUND, "SAMPLE001", "샘플 데이터를 찾을 수 없습니다."),

    // DRIVING
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "DRIVING001", "운전 세션을 찾을 수 없습니다."),
    SESSION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "DRIVING002", "해당 세션에 접근 권한이 없습니다."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "DRIVING003", "운행 리포트를 찾을 수 없습니다."),

    // COMMUNITY
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY001", "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY002", "댓글을 찾을 수 없습니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMUNITY003", "댓글 삭제 권한이 없습니다."),
    ALREADY_LIKED(HttpStatus.CONFLICT, "COMMUNITY004", "이미 좋아요를 누른 게시글입니다."),

    // FACEAI
    INVALID_VIDEO_FORMAT(HttpStatus.BAD_REQUEST, "FACEAI001", "지원하지 않는 영상 형식입니다. (.mp4, .avi, .mov, .mkv 만 허용)"),
    FACEAI_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "FACEAI002", "Face AI 서버와 통신 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}