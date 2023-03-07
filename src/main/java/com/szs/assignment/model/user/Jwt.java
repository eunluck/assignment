package com.szs.assignment.model.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.szs.assignment.configure.security.JwtTokenConfigure;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
@ToString
public class Jwt {

    private final String issuer;

    private final String clientSecret;

    private final int accessTokenExpirySeconds;

    private final Algorithm algorithm;

    private final JWTVerifier jwtVerifier;


    public Jwt(JwtTokenConfigure jwtTokenConfigure) {
        this.issuer = jwtTokenConfigure.getIssuer();
        this.clientSecret = jwtTokenConfigure.getClientSecret();
        this.accessTokenExpirySeconds = jwtTokenConfigure.getExpirySeconds();
        this.algorithm = Algorithm.HMAC512(clientSecret);
        this.jwtVerifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .build();
    }
    public Jwt(String issuer, String clientSecret, int expirySeconds) {
        this.issuer = issuer;
        this.clientSecret = clientSecret;
        this.accessTokenExpirySeconds = expirySeconds;
        this.algorithm = Algorithm.HMAC512(clientSecret);
        this.jwtVerifier = com.auth0.jwt.JWT.require(algorithm)
            .withIssuer(issuer)
            .build();
    }
    public String newToken(Claims claims) {

        Date now = new Date();
        JWTCreator.Builder builder = JWT.create();
        builder.withIssuer(issuer);
        builder.withIssuedAt(now);
        if (accessTokenExpirySeconds > 0) {
            builder.withExpiresAt(new Date(now.getTime() + accessTokenExpirySeconds * 1_000L));
        }
        builder.withClaim("userKey", claims.userKey);
        builder.withClaim("userId", claims.userId);
        builder.withClaim("name", claims.name);
        builder.withArrayClaim("roles", claims.roles);
        return builder.sign(algorithm);
    }




    public String refreshToken(String token) throws JWTVerificationException {
        Claims claims = verify(token);
        claims.eraseIat();
        claims.eraseExp();
        return newToken(claims);
    }


    public Claims verify(String token) throws JWTVerificationException {
        return new Claims(jwtVerifier.verify(token));
    }


    @Getter
    static public class Claims {

        Long userKey;
        String userId;
        String name;
        String[] roles;
        Date iat;
        Date exp;

        public Claims() {
        }

        Claims(DecodedJWT decodedJWT) {
            Claim userKey = decodedJWT.getClaim("userKey");
            if (!userKey.isNull()) {
                this.userKey = userKey.asLong();
            }
            Claim userId = decodedJWT.getClaim("userId");
            if (!userId.isNull()) {
                this.userId = userId.asString();
            }
            Claim name = decodedJWT.getClaim("name");
            if (!name.isNull()) {
                this.name = name.asString();
            }
            Claim roles = decodedJWT.getClaim("roles");
            if (!roles.isNull()) {
                this.roles = roles.asArray(String.class);
            }
            this.iat = decodedJWT.getIssuedAt();
            this.exp = decodedJWT.getExpiresAt();
        }

        public static Claims of(long userKey, String userId, String name, String[] roles) {
            Claims claims = new Claims();
            claims.userKey = userKey;
            claims.userId = userId;
            claims.name = name;
            claims.roles = roles;
            return claims;
        }


        long getIat() {
            return iat != null ? iat.getTime() : -1;
        }


        public long getExp() {
            return exp != null ? exp.getTime() : -1;
        }

        void eraseIat() {
            iat = null;
        }

        void eraseExp() {
            exp = null;
        }

    }

}