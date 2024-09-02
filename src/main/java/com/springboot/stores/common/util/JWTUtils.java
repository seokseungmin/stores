package com.springboot.stores.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.springboot.stores.common.model.UserLoginToken;
import com.springboot.stores.entity.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Date;

@UtilityClass
public class JWTUtils {

    private static final String KEY = "stores";
    private static final String CLAIM_USER_ID = "user_id";


    public static UserLoginToken createToken(User user) {

        if (user == null) {
            return null;
        }

        LocalDateTime expiredDateTime = LocalDateTime.now().plusMonths(1);
        Date expiredDate = java.sql.Timestamp.valueOf(expiredDateTime);

        String token = JWT.create()
                .withExpiresAt(expiredDate)
                .withClaim(CLAIM_USER_ID, user.getId())
                .withClaim("role", user.getRole().name())
                .withSubject(user.getUsername())
                .withIssuer(user.getEmail())
                .sign(Algorithm.HMAC512(KEY.getBytes()));

        return UserLoginToken.builder()
                .token(token)
                .build();
    }

    public static String getIssuer(String token) {

        return JWT.require(Algorithm.HMAC512(KEY.getBytes()))
                .build()
                .verify(token)
                .getIssuer();

    }

    public static boolean validateToken(String token, UserDetails userDetails) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(KEY.getBytes())).build();
            DecodedJWT jwt = verifier.verify(token);
            String username = jwt.getSubject();

            return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private static boolean isTokenExpired(DecodedJWT jwt) {
        return jwt.getExpiresAt().before(new Date());
    }

    public static DecodedJWT verifyToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(KEY.getBytes())).build();
        return verifier.verify(token);
    }

    public static String getEmailFromToken(String token) throws JWTVerificationException {
        return verifyToken(token).getClaim("iss").asString();
    }

    public static String getRoleFromToken(String token) throws JWTVerificationException {
        return verifyToken(token).getClaim("role").asString();
    }
}
