package com.szs.assignment.configure.security;

import com.szs.assignment.controller.user.dto.LoginDto;
import com.szs.assignment.controller.user.dto.UserDto;
import com.szs.assignment.model.Role;
import com.szs.assignment.model.entity.UserInfo;
import com.szs.assignment.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.ClassUtils.isAssignable;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final Jwt jwt;
    private final UserService userService;

    public JwtAuthenticationProvider(Jwt jwt, UserService userService) {
        this.jwt = jwt;
        this.userService = userService;
    }

    public static List<GrantedAuthority> createAuthorityList(Set<String> authorities) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }
        return grantedAuthorities;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        LoginDto.Request request = authenticationToken.toLoginRequest();
        return processUserAuthentication(request.getLoginId(), request.getPassword());
    }

    private Authentication processUserAuthentication(String loginId, String password) {

            UserInfo userInfo = userService.login(loginId, password);

            JwtAuthenticationToken authenticated =
                new JwtAuthenticationToken(
                    userInfo.getSeq(),
                    null,
                    createAuthorityList(Collections.singleton(Role.USER.value())));

            String apiToken = userInfo.newApiToken(jwt, new String[]{Role.USER.value()});

            authenticated.setDetails(
                    new LoginDto.Response(
                            apiToken,
                            new UserDto.Response(userInfo))
            );

            return authenticated;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return isAssignable(JwtAuthenticationToken.class, authentication);
    }


}