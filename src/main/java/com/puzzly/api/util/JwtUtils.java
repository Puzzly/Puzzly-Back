package com.puzzly.api.util;

import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.entity.User;
import com.puzzly.api.entity.UserAccountAuthority;
import com.puzzly.api.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private final UserService userService;

    private Key key;
    private final String principal = "email";
    private final String authority = "authorities";

    private final String userId = "userId";

    private final int expiredMilsForAccess = 100 * 60 * 1000;
    private final int expiredMilsForRefresh = 6 * 60 * 60 * 1000;

    @Override
    public void afterPropertiesSet() {
        this.key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }
    public String generateJwtToken(User user) {
        Claims claims = Jwts.claims();
        claims.put("email", user.getEmail());
        claims.put("authorities", getUserAccountAuthorityList(userService.findAccountAuhorityByUser(user)));
        claims.put("userId", user.getUserId());

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

    public boolean isValidToken(String token) throws Exception{
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

    public Long getUserIdFromToken(String token){
        Claims claims = getClaimsFormToken(token);
        return Long.parseLong(claims.get(userId).toString());
    }


    public List<String> getAuthorityFromToken(String token){
        Claims claims = getClaimsFormToken(token);
        ArrayList<String> authorityList = (ArrayList<String>) claims.get(authority);
        return authorityList;
    }

    public Boolean isExpired(String token){
        Claims claims = getClaimsFormToken(token);
        return claims.getExpiration().before(new Date());
    }

    public String getJwtSecretKey(){
        return jwtSecretKey;
    }

    public List<String> getUserAccountAuthorityList(List<UserAccountAuthority> accountAuthorityArrayList) {
        return accountAuthorityArrayList.stream().map((authority) -> {
            return authority.getAccountAuthority().getText();
        }).collect(Collectors.toList());
    }
}
