package com.szs.assignment.configure.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtAuthentication {

    @Schema(description = "사용자를 구별하는 정수형의 PK", example = "1")
    public final Long seq;
    @Schema(description = "로그인 아이디", example = "eunluck")
    public final String userId;
    @Schema(description = "이름", example = "은행운")
    public final String name;

}