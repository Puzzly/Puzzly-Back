package com.puzzly.api.service;

import com.puzzly.api.domain.AccountAuthority;
import com.puzzly.api.dto.request.UserExRequestDto;
import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.dto.response.UserExResponseDto;
import com.puzzly.api.dto.response.UserResponseDto;
import com.puzzly.api.entity.User;
import com.puzzly.api.entity.UserEx;
import com.puzzly.api.repository.jpa.UserExJpaRepository;
import com.puzzly.api.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserJpaRepository userJpaRepository;
    private final UserExJpaRepository userExJpaRepository;

    public UserResponseDto insertUser(UserRequestDto userRequestDto){

        if(userRequestDto.getCreateDateTime() == null) userRequestDto.setCreateDateTime(LocalDateTime.now());
        //if(userRequestDto.getAccountAuthority() == null) userRequestDto.setAccountAuthority(AccountAuthority.ROLE_USER);
        // TODO FE와 별도로 상의하여 통신구간 암호화를 구현하고, 복호화 > 암호화 혹은 그대로 때려박기 등을 구현해야 한다.
        //userRequestDto.setPassword(bCryptPasswordEncoder.encode(userRequestDto.getPassword()));

        /*
        UserExRequestDto tmpEx = userDTO.getUserExRequestDto();
        userDTO.setUserExRequestDto(null);

         */
        UserExRequestDto userExDto = userRequestDto.getUserExRequestDto();


        User user = User.builder()
                .userId((long)0)
                .userName(userRequestDto.getUserName())
                .nickName(userRequestDto.getNickName())
                .email(userRequestDto.getEmail())
                .password(userRequestDto.getPassword())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .birth(userRequestDto.getBirth())
                .gender(userRequestDto.isGender())
                .accountAuthority(userRequestDto.getAccountAuthority() == null ? AccountAuthority.ROLE_USER : userRequestDto.getAccountAuthority())
                .createDateTime(userRequestDto.getCreateDateTime() == null ? LocalDateTime.now() : userRequestDto.getCreateDateTime())
                .status("CREATED")
                .build();

        User savedEntity = userJpaRepository.save(user);
        log.error("savedEntity : USER" + savedEntity);

        UserEx userEx = UserEx.builder()
                //.profileFilePath(userExDto.getProfileFilePath())
                .firstTermAgreement(userExDto.isFirstTermAgreement())
                .secondTermAgreement(userExDto.isSecondTermAgreement())
                .user(userJpaRepository.findById(savedEntity.getUserId()).get())
                .build();
        UserEx savedExEntity = userExJpaRepository.save(userEx);
        logger.info("SAVEDENTITY : " + savedExEntity);

        return UserResponseDto.builder().userId(savedEntity.getUserId()).userName(user.getUserName()).email(savedEntity.getEmail()).
                createDateTime(savedEntity.getCreateDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).
                accountAuthority(savedEntity.getAccountAuthority()).build();
    }

    public List<UserResponseDto> selectUser(Long userId){
        if(userId == null){
            List<UserResponseDto> userList = userJpaRepository.findAll().stream().map((user) -> {
                UserResponseDto dto = UserResponseDto.builder().userId(user.getUserId()).userName(user.getUserName()).nickName(user.getNickName())
                        .email(user.getEmail()).phoneNumber(user.getPhoneNumber()).birth(user.getBirth()).gender(user.isGender())
                        .accountAuthority(user.getAccountAuthority()).createDateTime(user.getCreateDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).status(user.getStatus())
                        .build();
                UserEx userEx = user.getUserEx();
                UserExResponseDto exDto = UserExResponseDto.builder().profileFilePath(userEx.getProfileFilePath())
                        .firstTermAgreement(userEx.isFirstTermAgreement())
                        .secondTermAgreement(userEx.isSecondTermAgreement())
                        .statusMessage(userEx.getStatusMessage()).build();
                dto.setUserExResponseDto(exDto);
                return dto;
                }
            ).collect(Collectors.toList());
            return userList;
        } else {
            List<UserResponseDto> userList = userJpaRepository.findById(userId).stream().map((user) -> {
                        UserResponseDto dto = UserResponseDto.builder().userId(user.getUserId()).userName(user.getUserName()).nickName(user.getNickName())
                                .email(user.getEmail()).phoneNumber(user.getPhoneNumber()).birth(user.getBirth()).gender(user.isGender())
                                .accountAuthority(user.getAccountAuthority()).createDateTime(user.getCreateDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).status(user.getStatus())
                                .build();
                        UserEx userEx = user.getUserEx();
                        UserExResponseDto exDto = UserExResponseDto.builder().profileFilePath(userEx.getProfileFilePath())
                                .firstTermAgreement(userEx.isFirstTermAgreement())
                                .secondTermAgreement(userEx.isSecondTermAgreement())
                                .statusMessage(userEx.getStatusMessage()).build();
                        dto.setUserExResponseDto(exDto);
                        return dto;
                    }
            ).collect(Collectors.toList());
            return userList;
        }
    }
}
