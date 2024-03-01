package com.puzzly.Utils;

import com.puzzly.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtils {
    private static final String jwtSecretKey = "89e3d9b82f6908ddae0f2f20ce7bbcd7307b14e6938fa54e2990e8ee498632e7680474fd37098932f1d4605b7b3768bca12bf8b5454ed26f8316ee1de8a6948b";
    private static final Key key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    private static final String principal = "email";
    private static final String authority = "authority";

    private static final int expiredMils = 6 * 60 * 60 * 1000;
    public static String generateJwtToken(User user) {
        Claims claims = Jwts.claims();
        claims.put("email", user.getEmail());
        claims.put("authority", user.getAuthority());

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)                           // Payload - Claims구성
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setIssuer("Puzzly")                                       // Issuer 구성
                .signWith(key, SignatureAlgorithm.HS256)                    // Signature 구성 : 이 키를 사용하여 JWT 토큰에 서명을 추가한다. 이 서명은 토큰의 무결성을 보장하는 데 사용된다.
                .setExpiration(new Date(System.currentTimeMillis() + expiredMils));

        return builder.compact();
    }

    public static boolean isValidToken(String token) {
        try {
            Claims claims = getClaimsFormToken(token);

            log.info("expireTime : " + claims.getExpiration());
            log.info("email : " + claims.get(principal));

            return true;
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("Token Expired", expiredJwtException);
            return false;
        } catch (JwtException jwtException) {
            log.error("Token Tampered", jwtException);
            return false;
        } catch (NullPointerException npe) {
            log.error("Token is null", npe);
            return false;
        }
    }

    private static Claims getClaimsFormToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(token).getBody();
    }

    public static String getEmailFromToken(String token) {
        Claims claims = getClaimsFormToken(token);
        return claims.get(principal).toString();
    }

    public static String getAuthorityFromToken(String token){
        Claims claims = getClaimsFormToken(token);
        return claims.get(authority).toString();
    }

    public static Boolean isExpired(String token){
        Claims claims = getClaimsFormToken(token);
        return claims.getExpiration().before(new Date());
    }

}