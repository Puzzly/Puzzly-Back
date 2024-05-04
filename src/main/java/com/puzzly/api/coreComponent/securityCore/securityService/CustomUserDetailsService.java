package com.puzzly.api.coreComponent.securityCore.securityService;

import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.entity.User;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws FailException {
        User user = null;
        try {
            user = userService.findByEmail(email);
            if(ObjectUtils.isEmpty(user)){
                throw new NoSuchElementException();
            }
        }catch(NoSuchElementException e){
            throw new UsernameNotFoundException("SERVER_MESSAGE_USER_INFO_NOT_FOUND");
        }
        SecurityUser securityUser = new SecurityUser(user);

        return securityUser;
    }
}
