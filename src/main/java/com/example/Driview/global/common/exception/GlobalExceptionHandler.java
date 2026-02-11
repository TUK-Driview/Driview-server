package com.example.Driview.global.common.exception;

import com.example.Driview.global.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    // @Valid 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {

        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse(ErrorCode.VALIDATION_FAILED.getMessage());

        return ResponseEntity
                .status(ErrorCode.VALIDATION_FAILED.getStatus())
                .body(ApiResponse.fail(ErrorCode.VALIDATION_FAILED, errorMessage));
    }

    // JSON 구조 오류/타입 불일치 등
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(ApiResponse.fail(ErrorCode.INVALID_REQUEST, "요청 형식이 올바르지 않습니다."));
    }

    // 그 외 예상치 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}