package com.szs.assignment.controller.user;

import static com.szs.assignment.controller.ApiResult.OK;

import com.szs.assignment.configure.security.JwtAuthenticationToken;
import com.szs.assignment.controller.ApiResult;
import com.szs.assignment.controller.user.dto.JwtAuthentication;
import com.szs.assignment.controller.user.dto.LoginDto;
import com.szs.assignment.controller.user.dto.UserDto;
import com.szs.assignment.controller.user.dto.UserDto.JoinRequest;
import com.szs.assignment.error.NotFoundException;
import com.szs.assignment.error.UnauthorizedException;
import com.szs.assignment.model.user.UserInfo;
import com.szs.assignment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("${apiPrefix}")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Tag(name = "회원 API")
    @GetMapping(path = "me")
    @Operation(summary = "내 정보 보기", description = "Jwt 토큰을 통해 내 정보를 조회합니다.")
    public ApiResult<JwtAuthentication> myInfo(
        @AuthenticationPrincipal JwtAuthentication auth
    ) {
        return OK(auth);
    }

    @Tag(name = "회원 API")
    @GetMapping(path = "me/detail")
    @Operation(summary = "내 정보 상세 보기", description = "가입일시, 주민등록번호 등 상세 정보를 조회합니다.")
    public ApiResult<UserDto.Response> myDetailInfo(
        @AuthenticationPrincipal JwtAuthentication auth
    ) {
        return OK(
            userService.findBySeq(auth.seq)
                .map(UserDto.Response::new)
                .orElseThrow(() ->
                    new NotFoundException(UserInfo.class, auth.seq))
        );
    }

    @Tag(name = "회원 API")
    @PostMapping(path = "login")
    @Operation(summary = "사용자 로그인", description = "로그인 후 토큰을 발급받는다.")
    public ApiResult<LoginDto.Response> join(
        @Valid @RequestBody LoginDto.Request request
    ) throws UnauthorizedException {
        try {
            JwtAuthenticationToken authToken = JwtAuthenticationToken.fromLogin(
                request.getLoginId(),
                request.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return OK((LoginDto.Response) authentication.getDetails());

        } catch (AuthenticationException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    @Tag(name = "회원 API")
    @PostMapping(path = "signup")
    @Operation(summary = "사용자 회원가입", description = "회원가입합니다.")
    public ApiResult<UserDto.Response> join(
        @Valid @RequestBody JoinRequest joinRequest
    ) {
        return OK(
            new UserDto.Response(
                userService.join(joinRequest.newUser())
            )
        );
    }
}
