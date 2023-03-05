package com.szs.assignment.controller.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;


public class LoginDto {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @ToString
    public static class Request {

        @Schema(description = "로그인 ID", example = "eunluck")
        @NotBlank(message = "아이디를 입력해주세요.")
        private String loginId;

        @Schema(description = "패스워드", example = "0694123")
        @NotBlank(message = "패스워드를 입력해주세요.")
        private String password;


    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @ToString
    public static class Response {

        @Schema(description = "로그인 ID", example = "eunluck")
        private String apiToken;

        @Schema(description = "사용자")
        private UserDto.Response user;


    }

}