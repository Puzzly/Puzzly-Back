package com.puzzly.configuration.security.securityService;

import com.puzzly.api.entity.User;
import com.puzzly.configuration.security.details.SecurityUser;
import com.puzzly.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("Failed : Email invalid");
        }
        SecurityUser securityUser = new SecurityUser(user);

        return securityUser;
    }
}
