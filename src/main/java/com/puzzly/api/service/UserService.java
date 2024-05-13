package com.puzzly.api.service;

import com.puzzly.api.domain.AccountAuthority;
import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.dto.response.UserResponseDto;
import com.puzzly.api.entity.User;
import com.puzzly.api.entity.UserAccountAuthority;
import com.puzzly.api.entity.UserEx;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.repository.jpa.UserAccountJpaRepository;
import com.puzzly.api.repository.jpa.UserExJpaRepository;
import com.puzzly.api.repository.jpa.UserJpaRepository;
import com.puzzly.api.repository.mybatis.UserExMybatisRepository;
import com.puzzly.api.repository.mybatis.UserMybatisRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserJpaRepository userJpaRepository;
    private final UserExJpaRepository userExJpaRepository;

    private final UserAccountJpaRepository userAccountJpaRepository;

    private final UserMybatisRepository userMybatisRepository;

    private final UserExMybatisRepository userExMybatisRepository;


    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public UserResponseDto insertUser(UserRequestDto userRequestDto) throws FailException{
        if(ObjectUtils.isEmpty(userRequestDto.getUserName()) || ObjectUtils.isEmpty(userRequestDto.getNickName()) ||
        ObjectUtils.isEmpty(userRequestDto.getEmail()) || ObjectUtils.isEmpty(userRequestDto.getPassword()) ||
                ObjectUtils.isEmpty(userRequestDto.getPhoneNumber()) || ObjectUtils.isEmpty(userRequestDto.getBirth())){
            throw new FailException("SERVER_MESSAGE_BASIC_USER_PARAMETER_MISSING", 400);
        }
        if(ObjectUtils.isEmpty(userRequestDto.getGender())) {
            throw new FailException("SERVER_MESSAGE_GENDER_NOT_EXISTS", 400);
        }
        if(ObjectUtils.isEmpty(userRequestDto.getFirstTermAgreement() || ObjectUtils.isEmpty(userRequestDto.getSecondTermAgreement()))){
            throw new FailException("SERVER_MESSAGE_TERM_AGREEMENT_NOT_EXISTS", 400);
        }
        if(userMybatisRepository.selectUserByEmail(userRequestDto.getEmail()) != null){
            throw new FailException("SERVER_MESSAGE_EMAIL_ALREADY_EXISTS", 400);
        }
        //if(ObjectUtils.isEmpty(userRequestDto.getCreateDateTime())) userRequestDto.setCreateDateTime(LocalDateTime.now());
        // TODO FE와 별도로 상의하여 통신구간 암호화를 구현하고, 복호화 > 암호화 혹은 그대로 때려박기 등을 구현해야 한다.
        userRequestDto.setPassword(bCryptPasswordEncoder.encode(userRequestDto.getPassword()));

        User user = User.builder()
                .userName(userRequestDto.getUserName())
                .nickName(userRequestDto.getNickName())
                .email(userRequestDto.getEmail())
                .password(userRequestDto.getPassword())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .birth(userRequestDto.getBirth())
                .gender(userRequestDto.getGender())
                .createDateTime(LocalDateTime.now())
                .status(StringUtils.isEmpty(userRequestDto.getStatus()) ? "CREATE" : userRequestDto.getStatus())
                .isDeleted(false)
                .build();

        User savedEntity = userJpaRepository.save(user);
        log.error("savedEntity : USER" + savedEntity);

        UserEx userEx = UserEx.builder()
                .firstTermAgreement(userRequestDto.getFirstTermAgreement())
                .secondTermAgreement(userRequestDto.getSecondTermAgreement())
                .user(userJpaRepository.findById(savedEntity.getUserId()).orElse(null))
                .build();
        UserEx savedExEntity = userExJpaRepository.save(userEx);
        logger.info("SAVEDENTITY : " + savedExEntity);

        UserAccountAuthority userAccountAuthority = UserAccountAuthority.builder()
                .user(savedEntity)
                .accountAuthority(ObjectUtils.isEmpty(userRequestDto.getAccountAuthority()) ? AccountAuthority.ROLE_USER : userRequestDto.getAccountAuthority())
                .build();

        userAccountJpaRepository.save(userAccountAuthority);
        if(userAccountAuthority.getAccountAuthority().equals(AccountAuthority.ROLE_ADMIN)){
            UserAccountAuthority adminAccountAuthority = UserAccountAuthority.builder()
                    .user(savedEntity)
                    .accountAuthority(AccountAuthority.ROLE_USER)
                    .build();
            userAccountJpaRepository.save(adminAccountAuthority);
        }
        return UserResponseDto.builder().userId(savedEntity.getUserId()).userName(user.getUserName()).email(savedEntity.getEmail()).
                createDateTime(savedEntity.getCreateDateTime()).status(user.getStatus()).nickName(user.getNickName()).phoneNumber(user.getPhoneNumber())
                .birth(user.getBirth()).gender(user.getGender())
                //.userExResponseDto(UserExResponseDto.builder().userExId(userEx.getUserExId())
                        .firstTermAgreement(userEx.getFirstTermAgreement())
                        .secondTermAgreement(userEx.getSecondTermAgreement())
                 //       .build())
                //accountAuthority(savedEntity.getAccountAuthority()).build();
                .accountAuthority(userAccountJpaRepository.findByUser(user).stream().map((ua) -> {
                    return ua.getAccountAuthority().getText();
                }).collect(Collectors.toList()))
                .build();
    }


    //public UserResponseDto

    public UserResponseDto selectUserMybatis(Long userId){
        UserResponseDto user = userMybatisRepository.selectUser(userId);
        user.setAccountAuthority(userMybatisRepository.selectUserAuthority(userId));

        /*
        List<UserResponseDto> userList = userMybatisRepository.selectUser(userId).stream().map((user) -> {
                    UserResponseDto dto = UserResponseDto.builder().userId(user.getUserId()).userName(user.getUserName()).nickName(user.getNickName())
                            .email(user.getEmail()).phoneNumber(user.getPhoneNumber()).birth(user.getBirth()).gender(user.getGender())
                            .accountAuthority(userAccountJpaRepository.findByUser(user).stream().map((ua) -> {
                                return ua.getAccountAuthority().getText();
                            }).collect(Collectors.toList())).createDateTime(user.getCreateDateTime()).status(user.getStatus())
                            .build();
                    UserEx userEx = userExMybatisRepository.selectUserEx(dto.getUserId());
                    UserExResponseDto exDto = UserExResponseDto.builder()
                            .userExId(userEx.getUserExId())
                            .profileFilePath(userEx.getProfileFilePath())
                            .firstTermAgreement(userEx.getFirstTermAgreement())
                            .secondTermAgreement(userEx.getSecondTermAgreement())
                            .statusMessage(userEx.getStatusMessage()).build();
                    dto.setUserExResponseDto(exDto);
                    return dto;
                }
        ).collect(Collectors.toList());
         */
        return user;
    }

    public UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto){
        User userEntity = userJpaRepository.findById(userId).orElse(null);
        UserEx userExEntity = userEntity.getUserEx();

        if(userRequestDto.getUserName() != null) userEntity.setUserName(StringUtils.trim(userRequestDto.getUserName()));
        if(userRequestDto.getNickName() != null) userEntity.setNickName(StringUtils.trim(userRequestDto.getNickName()));
        if(userRequestDto.getPassword() != null) userEntity.setPassword(bCryptPasswordEncoder.encode(StringUtils.trim(userRequestDto.getPassword())));
        if(userRequestDto.getPhoneNumber() != null) userEntity.setPhoneNumber(StringUtils.trim(userRequestDto.getPhoneNumber()));
        if(userRequestDto.getBirth() != null) userEntity.setBirth(userRequestDto.getBirth());
        if(userRequestDto.getGender() != null) userEntity.setGender(userRequestDto.getGender());
        if(userRequestDto.getFirstTermAgreement() != null) userExEntity.setFirstTermAgreement(userRequestDto.getFirstTermAgreement());
        if(userRequestDto.getSecondTermAgreement() != null) userExEntity.setSecondTermAgreement(userRequestDto.getSecondTermAgreement());
        if(userRequestDto.getStatusMessage() != null) userExEntity.setStatusMessage(userRequestDto.getStatusMessage());
        //profile은 나중에
        userEntity.setModifyDateTime(LocalDateTime.now());

        userJpaRepository.save(userEntity);
        userExJpaRepository.save(userExEntity);

        UserResponseDto user = selectUser(userId);

        return user;
    }

    /** Interal Use (for update)*/
    public UserResponseDto selectUser(Long userId){
        // jpa 전용 failException 만들어서 orelseThrow 사용을 고려할것
        User userEntity = userJpaRepository.findById(userId).orElse(null);
        UserEx userExEntity = userEntity.getUserEx();
        UserResponseDto user  = UserResponseDto.builder().userId(userEntity.getUserId()).userName(userEntity.getUserName()).nickName(userEntity.getNickName())
                .email(userEntity.getEmail()).phoneNumber(userEntity.getPhoneNumber()).birth(userEntity.getBirth()).gender(userEntity.getGender())
                .accountAuthority(userAccountJpaRepository.findByUser(userEntity).stream().map((ua) -> {
                    return ua.getAccountAuthority().getText();
                }).collect(Collectors.toList())).createDateTime(userEntity.getCreateDateTime()).status(userEntity.getStatus())
                .firstTermAgreement(userExEntity.getFirstTermAgreement())
                .secondTermAgreement(userExEntity.getSecondTermAgreement())
                .profileFilePath(userExEntity.getProfileFilePath())
                .statusMessage(userExEntity.getStatusMessage())
                .build();
        return user;
        /*
        if(userId == null){
            List<UserResponseDto> userList = userJpaRepository.findAll().stream().map((user) -> {
                        UserResponseDto dto = UserResponseDto.builder().userId(user.getUserId()).userName(user.getUserName()).nickName(user.getNickName())
                                .email(user.getEmail()).phoneNumber(user.getPhoneNumber()).birth(user.getBirth()).gender(user.getGender())
                                .accountAuthority(userAccountJpaRepository.findByUser(user).stream().map((ua) -> {
                                    return ua.getAccountAuthority().getText();
                                }).collect(Collectors.toList())).createDateTime(user.getCreateDateTime()).status(user.getStatus())
                                .build();
                        UserEx userEx = user.getUserEx();
                        UserExResponseDto exDto = UserExResponseDto.builder()
                                .userExId(userEx.getUserExId())
                                .profileFilePath(userEx.getProfileFilePath())
                                .firstTermAgreement(userEx.getFirstTermAgreement())
                                .secondTermAgreement(userEx.getSecondTermAgreement())
                                .statusMessage(userEx.getStatusMessage()).build();
                        dto.setUserExResponseDto(exDto);
                        return dto;
                    }
            ).collect(Collectors.toList());
            return userList;
        } else {
            List<UserAccountAuthority> ac = userAccountJpaRepository.findByUser(userJpaRepository.findById(userId).orElse(null));
            List<UserResponseDto> userList = userJpaRepository.findById(userId).stream().map((user) -> {
                        UserResponseDto dto = UserResponseDto.builder().userId(user.getUserId()).userName(user.getUserName()).nickName(user.getNickName())
                                .email(user.getEmail()).phoneNumber(user.getPhoneNumber()).birth(user.getBirth()).gender(user.getGender())
                                .accountAuthority(userAccountJpaRepository.findByUser(user).stream().map((ua) -> {
                                    return ua.getAccountAuthority().getText();
                                }).collect(Collectors.toList())).createDateTime(user.getCreateDateTime()).status(user.getStatus())
                                .build();
                        UserEx userEx = user.getUserEx();
                        UserExResponseDto exDto = UserExResponseDto.builder().profileFilePath(userEx.getProfileFilePath())
                                .firstTermAgreement(userEx.getFirstTermAgreement())
                                .secondTermAgreement(userEx.getSecondTermAgreement())
                                .statusMessage(userEx.getStatusMessage()).build();
                        dto.setUserExResponseDto(exDto);
                        return dto;
                    }
            ).collect(Collectors.toList());
            return userList;
        }

         */
    }


    public User findByEmail(String email){
        return userJpaRepository.findByEmail(email);
    }

    public Optional<User> findById(Long userId) {return userJpaRepository.findById(userId);}

    public List<UserResponseDto> findByCalendarRel(Long calendarId){
        return userMybatisRepository.selectUserByCalendarId(calendarId);
    }
}
