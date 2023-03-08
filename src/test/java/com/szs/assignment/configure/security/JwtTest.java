package com.szs.assignment.configure.security;

import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.szs.assignment.model.user.Jwt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtTest {


    private Jwt jwt;

    @BeforeAll
    @DisplayName("JWT 토큰을 생성한다")
    void setUp() {
        String issuer = "szs";
        String clientSecret = "testestesttestestestestestsetsetsetsets";
        int expirySeconds = 10;
        JwtTokenConfigure jwtTokenConfigure = new JwtTokenConfigure();
        jwtTokenConfigure.setIssuer(issuer);
        jwtTokenConfigure.setClientSecret(clientSecret);
        jwtTokenConfigure.setExpirySeconds(expirySeconds);

        jwt = new Jwt(jwtTokenConfigure);
    }

    @Test
    @DisplayName("JWT 토큰을 생성하고 복호화 할수있다")
    void createJWT() {
        Jwt.Claims claims = Jwt.Claims.of(1L, "test", "tester", new String[]{"ROLE_USER"});
        String encodedJWT = jwt.newToken(claims);

        Jwt.Claims decodedJWT = jwt.verify(encodedJWT);

        assertAll(
            () -> assertEquals(claims.getUserKey(), decodedJWT.getUserKey()),
            () -> assertArrayEquals(claims.getRoles(), decodedJWT.getRoles())
        );
    }

    @Test
    @DisplayName("JWT 토큰을 리프레시 할수있다")
    void refreshJWT() throws Exception {
        Jwt.Claims claims = Jwt.Claims.of(1L, "test", "tester", new String[]{"ROLE_USER"});
        String encodedJWT = jwt.newToken(claims);
        // 1초 대기 후 토큰 갱신
        sleep(1000L);

        String encodedRefreshedJWT = jwt.refreshToken(encodedJWT);

        assertThat(encodedJWT, not(encodedRefreshedJWT));

        Jwt.Claims oldJwt = jwt.verify(encodedJWT);
        Jwt.Claims newJwt = jwt.verify(encodedRefreshedJWT);

        long oldExp = oldJwt.getExp();
        long newExp = newJwt.getExp();
        // 1초 후에 토큰을 갱신했으므로, 새로운 토큰의 만료시각이 1초 이후임
        assertTrue(newExp >= oldExp + 1000L);

    }

}