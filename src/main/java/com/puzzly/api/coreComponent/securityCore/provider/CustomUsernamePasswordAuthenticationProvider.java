package com.puzzly.api.coreComponent.securityCore.provider;

import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.exception.FailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomUsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    // 인증 대상 조회
    private final UserDetailsService userDetailsService;
    public final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public CustomUsernamePasswordAuthenticationProvider(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    // 암호화 처리

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("2.CustomAuthenticationProvider");

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        // AuthenticationFilter에서 생성된 토큰으로부터 ID, PW를 조회
        String email = (String)token.getPrincipal();
        String userPassword = (String) token.getCredentials();

        SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(email);
        if (!bCryptPasswordEncoder.matches(userPassword, securityUser.getPassword())) {
            throw new BadCredentialsException(securityUser.getEmail() + "Invalid password");
        }
        if(securityUser.getUser().getIsDeleted()){
            throw new FailException("SERVER_MESSAGE_DELETED_USER", 400);
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(securityUser, "el", securityUser.getAuthorities());
        return authToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
