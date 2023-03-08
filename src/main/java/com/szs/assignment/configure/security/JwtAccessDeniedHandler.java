package com.szs.assignment.configure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szs.assignment.controller.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    static ApiResult<?> E403 = ApiResult.ERROR("접근 거부 (message:'접근 권한이 없습니다.')",
        HttpStatus.FORBIDDEN);

    private final ObjectMapper om;

    public JwtAccessDeniedHandler(ObjectMapper om) {
        this.om = om;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
        throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setHeader("content-type", "application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(om.writeValueAsString(E403));
        response.getWriter().flush();
        response.getWriter().close();
    }

}