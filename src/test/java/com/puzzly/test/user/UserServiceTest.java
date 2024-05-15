package com.puzzly.test.user;

import com.puzzly.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceTest {

    Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    //UserRepository userRepository;
    UserService userService;

    /*
    @BeforeEach
    public void beforeEach(){
        userService = new UserService(new BCryptPasswordEncoder(), new ModelMapper(), userRepository);
    }


     */
    // TODO 생성자 주입을 사용하면, new를 사용해서 넣을수 있는게 장점이다
    // 를 아직 이해 못함. 찾아서 공부해야해
    @Test
    public void addUser(){
        /*
        logger.info("addUser Started");
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .userName("tester")
                .email("UserServiceTester@puzzly.com")
                .password("userServiceTester123!")
                .nickName("tester")
                .build();
        userService.insertUser(userRequestDto);
        logger.info("insert completed");

        logger.info("validation");
        User user = userService.getUser("tester");
        logger.info(user.toString());


        logger.info("user insert done");


         */

    }
}
