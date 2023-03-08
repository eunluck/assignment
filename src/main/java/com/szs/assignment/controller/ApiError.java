package com.szs.assignment.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;

@Schema(description = "API 오류 응답")
public class ApiError {

  @Schema(description = "오류 메시지")
  private final String message;

  @Schema(description = "HTTP 오류코드")
  private final int status;

  ApiError(Throwable throwable, HttpStatus status) {
    this(throwable.getMessage(), status);
  }

  ApiError(String message, HttpStatus status) {
    this.message = message;
    this.status = status.value();
  }

  public String getMessage() {
    return message;
  }

  public int getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("message", message)
      .append("status", status)
      .toString();
  }

}