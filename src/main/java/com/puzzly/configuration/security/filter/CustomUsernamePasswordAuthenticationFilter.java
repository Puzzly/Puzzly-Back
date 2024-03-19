package com.puzzly.configuration.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzly.api.member.dto.UserDTORequest;
import com.puzzly.api.cmm.exception.FailException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 필터 자체는 class로만 만들고, securityConfig에서 추가하여 처리
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {

        UsernamePasswordAuthenticationToken attemptToken;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            UserDTORequest user = objectMapper.readValue(request.getInputStream(), UserDTORequest.class);
            attemptToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
            setDetails(request, attemptToken);
        } catch (Exception e) {
            throw new FailException("Attempt Token create failed", 400);
        }

        // Authentication 객체를 반환한다.
        return this.getAuthenticationManager().authenticate(attemptToken);
    }
}
