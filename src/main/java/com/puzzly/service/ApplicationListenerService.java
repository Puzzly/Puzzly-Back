package com.puzzly.service;

import com.puzzly.dto.UserDTORequest;
import com.puzzly.entity.User;
import com.puzzly.enums.Authority;
import com.puzzly.enums.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationListenerService implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    UserService userService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("[++] Application ready");

        // test용 ADMIN 추가
        UserDTORequest user = new UserDTORequest();
        user.setEmail("admin@puzzly.com");
        user.setPassword("admin");
        user.setJoinType(JoinType.NATIVE);
        user.setAuthority(Authority.ROLE_ADMIN);
        userService.insertUser(user);
    }
}
