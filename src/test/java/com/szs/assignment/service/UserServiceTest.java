package com.szs.assignment.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.szs.assignment.model.user.CanJoinUser;
import com.szs.assignment.model.user.UserInfo;
import com.szs.assignment.repository.CanJoinUserRepository;
import com.szs.assignment.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(value = MockitoExtension.class)
class UserServiceTest {

    private UserInfo mockUser;
    private UserService userService;

    @Mock
    private CanJoinUserRepository canJoinUserRepository;
    @Mock
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {

        mockUser = new UserInfo(1L, "eunluck", "123456", "김둘리", "921108-1582816");

        passwordEncoder = new BCryptPasswordEncoder();

        userService = new UserService(userRepository, canJoinUserRepository, passwordEncoder);

    }

    @Test
    @DisplayName("회원가입한다.")
    void join() {

        when(canJoinUserRepository.findByNameAndRegNo(mockUser.getName(), mockUser.getRegNo()))
            .thenReturn(Optional.of(new CanJoinUser(1L, mockUser.getName(), mockUser.getRegNo())));
        when(userRepository.findByUserId(mockUser.getUserId()))
            .thenReturn(Optional.empty());
        when(userRepository.save(any()))
            .thenReturn(mockUser);

        UserInfo join =
            userService
                .join(
                    UserInfo.join(
                        mockUser.getUserId(),
                        mockUser.getPassword(),
                        mockUser.getName(),
                        mockUser.getRegNo()));

        assertAll(
            () -> assertNotNull(join.getUserId()),
            () -> assertNotNull(join),
            () -> assertEquals(mockUser.getRegNo(), join.getRegNo()),
            () -> assertEquals(mockUser.getName(), join.getName()));
    }

    @Test
    @DisplayName("로그인한다.")
    void login() {
        UserInfo encodeUser = mockUser;
        encodeUser.encodePassword(passwordEncoder);

        when(userRepository.findByUserId(mockUser.getUserId()))
            .thenReturn(Optional.of(encodeUser));

        UserInfo login = userService.login(mockUser.getUserId(), "123456");

        assertAll(
            () -> assertNotNull(login.getUserId()),
            () -> assertNotNull(login),
            () -> assertEquals(mockUser.getRegNo(), login.getRegNo()),
            () -> assertEquals(mockUser.getName(), login.getName()));
    }

    @Test
    @DisplayName("중복 계정인지 체크한다.")
    void isDuplicatedId() {
        when(userRepository.findByUserId("eunluck")).thenReturn(Optional.of(mockUser));
        assertTrue(userService.isDuplicatedId("eunluck"));
        assertFalse(userService.isDuplicatedId("eunluck2"));
    }

    @Test
    @DisplayName("가입이 허용된 유저인지 체크한다.")
    void isPossibleUser() {
        when(canJoinUserRepository.findByNameAndRegNo(mockUser.getName(), mockUser.getRegNo()))
            .thenReturn(
                Optional.of(
                    new CanJoinUser(1L, mockUser.getName(), mockUser.getRegNo())));

        assertTrue(userService.isPossibleUser(mockUser.getName(), mockUser.getRegNo()));

    }

    @Test
    @DisplayName("login Id로 검색")
    void findByUserId() {
        when(userRepository.findByUserId(mockUser.getUserId()))
            .thenReturn(Optional.of(mockUser));

        assertNotNull(userService.findByUserId(mockUser.getUserId()).get());
    }

    @Test
    @DisplayName("seq로 검색")
    void findBySeq() {
        when(userRepository.findById(mockUser.getSeq()))
            .thenReturn(Optional.of(mockUser));

        assertNotNull(userService.findBySeq(mockUser.getSeq()).get());
    }
}