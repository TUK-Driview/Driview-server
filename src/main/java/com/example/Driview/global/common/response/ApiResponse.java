package com.example.Driview.global.common.response;

import com.example.Driview.global.common.exception.BaseErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"isSuccess", "errorCode", "message", "data"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final boolean success;

    private final String errorCode;

    private final String message;

    private final T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, null, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, "요청이 성공적으로 처리되었습니다.", data);
    }

    public static <T> ApiResponse<T> fail(BaseErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> ApiResponse<T> fail(BaseErrorCode errorCode, String message) {
        return new ApiResponse<>(false, errorCode.getCode(), message, null
        );
    }
}