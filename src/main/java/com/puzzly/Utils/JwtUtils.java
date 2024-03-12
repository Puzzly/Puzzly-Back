package com.puzzly.Utils;

import com.puzzly.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils implements InitializingBean {
    //private static final String jwtSecretKey = "89e3d9b82f6908ddae0f2f20ce7bbcd7307b14e6938fa54e2990e8ee498632e7680474fd37098932f1d4605b7b3768bca12bf8b5454ed26f8316ee1de8a6948b";
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;
    //private final Key key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    // @constructorBinding?
    private Key key;
    private final String principal = "email";
    private final String authority = "authority";

    private final int expiredMilsForAccess = 10 * 60 * 1000;
    private final int expiredMilsForRefresh = 6 * 60 * 60 * 1000;

    @Override
    public void afterPropertiesSet() {
        this.key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }
    public String generateJwtToken(User user) {
        Claims claims = Jwts.claims();
        claims.put("email", user.getEmail());
        claims.put("authority", user.getAuthority());

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)                           // Payload - Claims구성
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setIssuer("puzzly")                                       // Issuer 구성
                .signWith(key, SignatureAlgorithm.HS256)                    // Signature 구성 : 이 키를 사용하여 JWT 토큰에 서명을 추가한다. 이 서명은 토큰의 무결성을 보장하는 데 사용된다.
                .setExpiration(new Date(System.currentTimeMillis() + expiredMilsForAccess));

        return builder.compact();
    }

    public String generateRefreshToken(String str){
        Claims claims = Jwts.claims();
        claims.put("issuer", "puzzly");

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setIssuer("puzzly")
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + expiredMilsForRefresh));

        return builder.compact();
    }

    public boolean isValidToken(String token) {
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

    private Claims getClaimsFormToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(token).getBody();
    }

    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFormToken(token);
        return claims.get(principal).toString();
    }

    public String getAuthorityFromToken(String token){
        Claims claims = getClaimsFormToken(token);
        return claims.get(authority).toString();
    }

    public Boolean isExpired(String token){
        Claims claims = getClaimsFormToken(token);
        return claims.getExpiration().before(new Date());
    }

}
