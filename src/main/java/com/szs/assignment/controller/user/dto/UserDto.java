package com.szs.assignment.controller.user.dto;

import com.szs.assignment.configure.security.JwtAuthentication;
import com.szs.assignment.controller.BaseDto;
import com.szs.assignment.model.entity.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UserDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class JoinRequest {

        @Schema(description = "사용자 로그인 ID", example = "eunluck")
        @NotBlank(message = "아이디를 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9_]{3,15}$", message = "3~15자의 영어와 숫자만 허용됩니다.")
        private String userId;

        @Schema(description = "이름", example = "은행운")
        @NotBlank(message = "이름을 입력해주세요.")
        private String name;

        @Schema(description = "주민등록번호", example = "920812-1234567")
        @Pattern(regexp = "^[0-9]{6}-[1-4][0-9]{6}$", message = "주민등록번호 형식이 올바르지 않습니다.")
        private String regNo;

        @Schema(description = "패스워드", example = "123456")
        @NotBlank(message = "패스워드를 입력해주세요.")
        private String password;

        public UserInfo newUser() {
            return UserInfo.join(userId,password, name, regNo);
        }

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString(callSuper = true)
    public static class Response extends BaseDto {

        @Schema(description = "사용자 로그인 ID", example = "eunluck")
        private String userId;

        @Schema(description = "이름", example = "은행운")
        private String name;

        @Schema(description = "주민등록번호", example = "920812-1234567")
        private String regNo;


        public static Response fromJwtAuthentication(JwtAuthentication jwtAuthentication) {
            return new Response(
                    jwtAuthentication.seq,
                    jwtAuthentication.getUserId(),
                    jwtAuthentication.getName());
        }

        public Response(UserInfo userInfoEntity) {
            super(userInfoEntity);
            this.userId = userInfoEntity.getUserId();
            this.name = userInfoEntity.getName();
            this.regNo = userInfoEntity.getRegNo();
        }

        public Response(Long seq, String userId, String name) {
            super(seq);
            this.userId = userId;
            this.name = name;
        }


    }

}