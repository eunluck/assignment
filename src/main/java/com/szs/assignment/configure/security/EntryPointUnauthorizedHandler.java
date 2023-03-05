package com.szs.assignment.configure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szs.assignment.controller.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {

    static ApiResult<?> E401 = ApiResult.ERROR("인증 오류 (message:'인증되지 않은 사용자입니다.')",
        HttpStatus.UNAUTHORIZED);

    private final ObjectMapper om;

    public EntryPointUnauthorizedHandler(ObjectMapper om) {
        this.om = om;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
        throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("content-type", "application/json");
        response.getWriter().write(om.writeValueAsString(E401));
        response.getWriter().flush();
        response.getWriter().close();
    }

}