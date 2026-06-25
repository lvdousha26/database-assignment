package com.mingbo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static final String SIGN_KEY = "oilwell";
    private static final Long EXPIRE = 43200000L;

    public static String generateJwt(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(System.currentTimeMillis() + EXPIRE);
        Algorithm algorithm = Algorithm.HMAC256(SIGN_KEY);

        return JWT.create()
                .withIssuer("auth0")
                .withClaim("username", (String) claims.get("username"))
                .withClaim("id", (Long) claims.get("id"))
                .withClaim("role", (String) claims.get("role"))
                .withExpiresAt(expiryDate)
                .sign(algorithm);
    }

    public static Map<String, Claim> parseJWT(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SIGN_KEY);
        JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaims();
    }
}
