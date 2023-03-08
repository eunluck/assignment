package com.szs.assignment.controller;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
@Getter
@Schema(description = "API 응답 형식")
public class ApiResult<T> {

    @Schema(description = "API 요청 처리 결과")
    private final boolean success;

    @Schema(description = "success가 true라면, API 요청 처리 응답값")
    private final T response;

    @Schema(description = "success가 false라면, API 요청 처리 응답값")
    private final ApiError error;

    private ApiResult(boolean success, T response, ApiError error) {
        this.success = success;
        this.response = response;
        this.error = error;
    }

    public static <T> ApiResult<T> OK(T response) {
        return new ApiResult<>(true, response, null);
    }

    public static ApiResult<?> ERROR(Throwable throwable, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(throwable, status));
    }

    public static ApiResult<?> ERROR(String errorMessage, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(errorMessage, status));
    }


}