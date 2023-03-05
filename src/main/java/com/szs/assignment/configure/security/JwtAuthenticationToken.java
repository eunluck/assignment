package com.szs.assignment.configure.security;

import com.szs.assignment.controller.user.dto.LoginDto;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private String credentials;


    public static JwtAuthenticationToken fromLogin(String loginId, String password) {
        return new JwtAuthenticationToken(loginId,password);
    }

    public JwtAuthenticationToken(String loginId, String password) {
        super(null);
        super.setAuthenticated(false);

        this.principal = loginId;
        this.credentials = password;
    }


    JwtAuthenticationToken(Object seq, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);

        this.principal = seq;
        this.credentials = credentials;
    }


    LoginDto.Request toLoginRequest() {
        return new LoginDto.Request(String.valueOf(principal), credentials);
    }


    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public String getCredentials() {
        return credentials;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("principal", principal)
            .append("credentials", "[PROTECTED]")
            .toString();
    }

}