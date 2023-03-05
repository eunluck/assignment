package com.szs.assignment.configure.security;

import com.szs.assignment.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigure {

    private final Jwt jwt;

    private final JwtTokenConfigure jwtTokenConfigure;

    private final JwtAccessDeniedHandler accessDeniedHandler;

    private final EntryPointUnauthorizedHandler unauthorizedHandler;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final AuthenticationConfiguration authenticationConfiguration;
    private static final String[] SWAGGER_AUTH_WHITELIST = {
        // -- swagger ui
        "/v3/api-docs/**",
        "/swagger-resources",
        "/swagger*/**",
        "/webjars/**",
        "/swagger-ui/**",
        "/resources/**"
    };


    public SecurityConfigure(Jwt jwt, JwtTokenConfigure jwtTokenConfigure,
                             JwtAccessDeniedHandler accessDeniedHandler,
                             EntryPointUnauthorizedHandler unauthorizedHandler,
                             @Lazy JwtAuthenticationProvider jwtAuthenticationProvider,
                             AuthenticationConfiguration authenticationConfiguration) {
        this.jwtTokenConfigure = jwtTokenConfigure;
        this.jwt = jwt;
        this.accessDeniedHandler = accessDeniedHandler;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter(jwtTokenConfigure.getHeader(), jwt);
    }
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .cors()
            .and()
            .authenticationManager(authenticationManager())
            .authenticationProvider(jwtAuthenticationProvider)
            .csrf()
            .disable()
            .headers()
            .disable()
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler)
            .authenticationEntryPoint(unauthorizedHandler)
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/szs/signup").permitAll()
                .antMatchers("/szs/login").permitAll()
                .antMatchers(SWAGGER_AUTH_WHITELIST).permitAll()
                .anyRequest().hasRole(Role.USER.name())
            .and()
            .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
            .formLogin()
            .disable().build();
    }

}