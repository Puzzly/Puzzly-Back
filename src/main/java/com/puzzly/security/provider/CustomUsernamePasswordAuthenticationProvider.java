package com.puzzly.security.provider;

import com.puzzly.security.details.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@RequiredArgsConstructor
public class CustomUsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    // 인증 대상 조회
    private final UserDetailsService userDetailsService;
    // 암호화 처리
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("2.CustomAuthenticationProvider");

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        // AuthenticationFilter에서 생성된 토큰으로부터 ID, PW를 조회
        String email = (String)token.getPrincipal();
        String userPassword = (String) token.getCredentials();

        SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(email);
        log.error("securityUser : " + securityUser.toString());
        if (!bCryptPasswordEncoder().matches(userPassword, securityUser.getPassword())) {
            log.error("pw : " + securityUser.getPassword());
            log.error("invalid password?");
            throw new BadCredentialsException(securityUser.getEmail() + "Invalid password");
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(securityUser, userPassword, securityUser.getAuthorities());
        return authToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
