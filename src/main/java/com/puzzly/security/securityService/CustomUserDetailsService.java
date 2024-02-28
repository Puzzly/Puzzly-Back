package com.puzzly.security.securityService;

import com.puzzly.entity.User;
import com.puzzly.exception.FailException;
import com.puzzly.repository.UserRepository;
import com.puzzly.security.details.SecurityUser;
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
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if(user == null){
            throw new UsernameNotFoundException("Failed : Email invalid");
        }

        SecurityUser securityUser = new SecurityUser(user);

        return securityUser;
    }
}
