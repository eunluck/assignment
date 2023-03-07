package com.szs.assignment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szs.assignment.configure.security.JwtAuthenticationProvider;
import com.szs.assignment.configure.security.JwtAuthenticationTokenFilter;
import com.szs.assignment.model.user.Jwt;
import com.szs.assignment.model.user.UserInfo;
import com.szs.assignment.repository.UserRepository;
import com.szs.assignment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {
    protected static final String TEST_TOKEN_SECRET_KEY = "testTokenSecretKey";

    @MockBean
    protected JwtAuthenticationProvider jwtAuthenticationProvider;

    @Mock
    UserService userService;

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

    @BeforeEach
    protected void setUp(WebApplicationContext webApplicationContext) {
        JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter = (JwtAuthenticationTokenFilter) webApplicationContext
                .getBean("jwtAuthenticationTokenFilter");


        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(SecurityMockMvcConfigurers.springSecurity(jwtAuthenticationTokenFilter))
                .build();

        objectMapper = new ObjectMapper();

        jwtAuthenticationProvider = new JwtAuthenticationProvider(
            new Jwt("test",TEST_TOKEN_SECRET_KEY,10),userService);


        doReturn(true).when(jwtAuthenticationProvider).supports(any());
    }
}
