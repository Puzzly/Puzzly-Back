package com.puzzly.api.coreComponent.securityCore.filter;

import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.entity.User;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.util.JwtUtils;
import com.puzzly.api.util.Utils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final UserDetailsService userDetailsService;

    /*
    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // TODO Check : Security permitAll() 안먹혀서 별도로 처리중
        List<String> list = Arrays.asList(
                "/api/user/login",
                "/api/user/join",
                "/login",
                "/css/**",
                "/js/**",
                "/images/**",
                "/swagger-ui/**",
                "/swagger-ui/index.html",
                "/favicon.ico",
                "/api/auth/refresh"
        );
        if (list.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        if(explicitSwaggerURI(request)){
            filterChain.doFilter(request, response);
            return;
        }
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("token null");
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorization.split(" ")[1];
        try{
            if (jwtUtils.isExpired(token)) {
                log.error("token expired");
                filterChain.doFilter(request, response);
                return;
            }
        } catch (ExpiredJwtException eje){
            throw new FailException("Token Expired", 400);
        }

        String email = jwtUtils.getEmailFromToken(token);
        List<String> authorities = jwtUtils.getAuthorityFromToken(token);

        User user = User.builder().email(email).password("PROTECTED").build();

        SecurityUser securityUser = new SecurityUser(user);
        securityUser.setAuthorities(getSimpleAuthorityListFromJwt(authorities));
        Authentication authToken = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
        /*
        // cookie 방식으로 처리 실패
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        try {
            if (token != null && !token.equalsIgnoreCase("")) {

                if (JwtUtils.isValidToken(token)) {

                    String email = JwtUtils.getEmailFromToken(token);
                    log.debug("[+] loginId Check: " + email);

                    if (email != null && !email.equalsIgnoreCase("")) {
                        SecurityUser securityUser = (SecurityUser)userDetailsService.loadUserByUsername(email);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        filterChain.doFilter(request, response);
                    } else {
                        throw new FailException("user not found", 401);
                    }
                }
                else {
                    throw new FailException("Invalid TOken", 401);
                }
            }
            else {
                throw new FailException("Token not found", 401);
            }
        } catch (Exception e) {
            // 로그 메시지 생성
            e.printStackTrace();

        }

         */
    }
    public boolean explicitSwaggerURI(HttpServletRequest request) {
        ArrayList<Pattern> swaggerUi = new ArrayList<>();
        swaggerUi.add(Pattern.compile("/swagger-ui/*"));
        swaggerUi.add(Pattern.compile("/v3/api-docs/*"));
        for (Pattern pt : swaggerUi) {
            if (pt.matcher(request.getRequestURI()).find()) {
                return true;
            }
        }
        return false;
    }

    public Collection<? extends GrantedAuthority> getSimpleAuthorityListFromJwt(List<String> accountAuthorityArrayList) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for(String authority : accountAuthorityArrayList){
            authorities.add(new SimpleGrantedAuthority(authority));
        }
        return authorities;
    }
}
