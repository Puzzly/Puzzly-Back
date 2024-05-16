package com.puzzly.api.service;

import com.puzzly.api.domain.AccountAuthority;
import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.dto.response.UserAttachmentsResponse;
import com.puzzly.api.dto.response.UserResponseDto;
import com.puzzly.api.entity.User;
import com.puzzly.api.entity.UserAccountAuthority;
import com.puzzly.api.entity.UserAttachments;
import com.puzzly.api.entity.UserExtension;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.repository.jpa.UserAccountAuthorityJpaRepository;
import com.puzzly.api.repository.jpa.UserAttachmentsJpaRepository;
import com.puzzly.api.repository.jpa.UserExtensionJpaRepository;
import com.puzzly.api.repository.jpa.UserJpaRepository;
import com.puzzly.api.repository.mybatis.UserMybatisRepository;
import com.puzzly.api.util.CustomUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserJpaRepository userJpaRepository;
    private final UserExtensionJpaRepository userExtensionJpaRepository;
    private final UserAccountAuthorityJpaRepository userAccountAuthorityJpaRepository;

    private final UserAttachmentsJpaRepository userAttachmentsJpaRepository;
    private final UserMybatisRepository userMybatisRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomUtils customUtils;
    private final String context = "user";

    /** 사용자 추가 */
    @Transactional
    public HashMap<String, Object> createUser(UserRequestDto userRequestDto) throws FailException{
        HashMap<String, Object> resultMap = new HashMap<>();

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
        // TODO FE와 별도로 상의하여 통신구간 암호화를 구현하고, 복호화 > 암호화 혹은 그대로 때려박기 등을 구현해야 한다.
        userRequestDto.setPassword(bCryptPasswordEncoder.encode(userRequestDto.getPassword()));
        UserExtension userExtension = UserExtension.builder()
                .firstTermAgreement(userRequestDto.getFirstTermAgreement())
                .secondTermAgreement(userRequestDto.getSecondTermAgreement())
                .build();
        userExtensionJpaRepository.save(userExtension);
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
                .userExtension(userExtension)
                .isDeleted(false)
                .build();
        userJpaRepository.save(user);

        UserAccountAuthority userAccountAuthority = UserAccountAuthority.builder()
                .user(user)
                .accountAuthority(ObjectUtils.isEmpty(userRequestDto.getAccountAuthority()) ? AccountAuthority.ROLE_USER : userRequestDto.getAccountAuthority())
                .build();
        userAccountAuthorityJpaRepository.save(userAccountAuthority);

        if(userAccountAuthority.getAccountAuthority().equals(AccountAuthority.ROLE_ADMIN)){
            UserAccountAuthority adminAccountAuthority = UserAccountAuthority.builder()
                    .user(user)
                    .accountAuthority(AccountAuthority.ROLE_USER)
                    .build();
            userAccountAuthorityJpaRepository.save(adminAccountAuthority);
        }
        UserResponseDto userResult = buildUserResponseDtoFromUser(user, userExtension);

        resultMap.put("user", userResult);
        return resultMap;
    }

    /** 사용자 조회 */
    public HashMap<String, Object> getUser(Long userId){
        HashMap<String, Object> resultMap = new HashMap<>();

        UserResponseDto user = userMybatisRepository.selectUser(userId);
        user.setAccountAuthority(userMybatisRepository.selectUserAuthority(userId));

        resultMap.put("user", user);
        return resultMap;
    }

    /** 사용자 수정 */
    @Transactional
    public HashMap<String, Object> modifyUser(Long userId, UserRequestDto userRequestDto){
        HashMap<String, Object> resultMap = new HashMap<>();
        User user = userJpaRepository.findById(userId).orElse(null);
        UserExtension userExtension = user.getUserExtension();

        if(userRequestDto.getUserName() != null) user.setUserName(StringUtils.trim(userRequestDto.getUserName()));
        if(userRequestDto.getNickName() != null) user.setNickName(StringUtils.trim(userRequestDto.getNickName()));
        if(userRequestDto.getPassword() != null) user.setPassword(bCryptPasswordEncoder.encode(StringUtils.trim(userRequestDto.getPassword())));
        if(userRequestDto.getPhoneNumber() != null) user.setPhoneNumber(StringUtils.trim(userRequestDto.getPhoneNumber()));
        if(userRequestDto.getBirth() != null) user.setBirth(userRequestDto.getBirth());
        if(userRequestDto.getGender() != null) user.setGender(userRequestDto.getGender());
        if(userRequestDto.getFirstTermAgreement() != null) userExtension.setFirstTermAgreement(userRequestDto.getFirstTermAgreement());
        if(userRequestDto.getSecondTermAgreement() != null) userExtension.setSecondTermAgreement(userRequestDto.getSecondTermAgreement());
        if(userRequestDto.getStatusMessage() != null) userExtension.setStatusMessage(userRequestDto.getStatusMessage());
        //profile은 나중에
        user.setModifyDateTime(LocalDateTime.now());

        userJpaRepository.save(user);
        userExtensionJpaRepository.save(userExtension);

        if(userRequestDto.getCreateAttachmentsId() != null && userRequestDto.getCreateAttachmentsId() != 0){
            userAttachmentsJpaRepository.bulkUpdateIsDeleted(user, false, true, LocalDateTime.now());
            UserAttachments userAttachments = userAttachmentsJpaRepository.findById(userRequestDto.getCreateAttachmentsId()).orElse(null);
            if(userAttachments == null){
                throw new FailException("SERVER_MESSAGE_ATTACHMENTS_NOT_EXISTS",400);
            }
            userAttachments.setUser(user);
            userAttachmentsJpaRepository.save(userAttachments);
        }

        UserResponseDto userResult = buildUserResponseDtoFromUser(user, userExtension);
        resultMap.put("user", userResult);

        return resultMap;
    }

    /** 사용자 정보 조회 */
    @Deprecated
    public UserResponseDto selectUser(Long userId){
        // jpa 전용 failException 만들어서 orelseThrow 사용을 고려할것
        User userEntity = userJpaRepository.findById(userId).orElse(null);
        UserExtension userExEntity = userEntity.getUserExtension();
        UserResponseDto user  = UserResponseDto.builder().userId(userEntity.getUserId()).userName(userEntity.getUserName()).nickName(userEntity.getNickName())
                .email(userEntity.getEmail()).phoneNumber(userEntity.getPhoneNumber()).birth(userEntity.getBirth()).gender(userEntity.getGender())
                .accountAuthority(userAccountAuthorityJpaRepository.findByUser(userEntity).stream().map((ua) -> {
                    return ua.getAccountAuthority().getText();
                }).collect(Collectors.toList())).createDateTime(userEntity.getCreateDateTime()).status(userEntity.getStatus())
                .firstTermAgreement(userExEntity.getFirstTermAgreement())
                .secondTermAgreement(userExEntity.getSecondTermAgreement())
                //.profileFilePath(userExEntity.getProfileFilePath())
                .statusMessage(userExEntity.getStatusMessage())
                .build();
        return user;
    }


    /** 사용자 첨부파일 (프로필) 업로드*/
    public HashMap<String, Object> uploadUserAttachments(SecurityUser securityUser, MultipartFile attachments){
        HashMap<String, Object> resultMap = new HashMap<>();
        User user = findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        Long attachmentsId = 0L;
        try {
            HashMap<String, Object> fileResult = customUtils.uploadFile(context, attachments);
            UserAttachments userAttachments = UserAttachments.builder()
                    .extension(MapUtils.getString(fileResult, "extension"))
                    .filePath(MapUtils.getString(fileResult, "dirPath") + MapUtils.getString(fileResult, "fileName"))
                    .fileSize(MapUtils.getLong(fileResult, "fileSize"))
                    .createDateTime(LocalDateTime.now())
                    .originName(MapUtils.getString(fileResult, "originName"))
                    .isDeleted(false)
                    .createUser(user)
                    .build();
            userAttachmentsJpaRepository.save(userAttachments);
            attachmentsId = userAttachments.getAttachmentsId();
        }catch(Exception e){
            e.printStackTrace();
            throw new FailException("SERVER_MESSAGE_EXCEPTION_ON_USER_ATTACHMENTS", 500);
        }

        resultMap.put("attachmentsId", attachmentsId);
        return resultMap;
    }

    /** User , UserExtension Entity로부터 user responseDto 생성*/
    private UserResponseDto buildUserResponseDtoFromUser(User user, UserExtension userExtension){
        UserAttachments userAttachments = userAttachmentsJpaRepository.findByUserAndIsDeleted(user, false);
        UserAttachmentsResponse userAttachmentsResponse = null;
        if(userAttachments != null){
            userAttachmentsResponse = UserAttachmentsResponse.builder()
                                        .attachmentsId(userAttachments.getAttachmentsId())
                                        .createDateTime(userAttachments.getCreateDateTime())
                                        .originName(userAttachments.getOriginName())
                                        .filePath(userAttachments.getFilePath())
                                        .fileSize(userAttachments.getFileSize())
                                        .build();
        }
        return UserResponseDto.builder().userId(user.getUserId()).userName(user.getUserName()).email(user.getEmail()).
                createDateTime(user.getCreateDateTime()).status(user.getStatus()).nickName(user.getNickName()).phoneNumber(user.getPhoneNumber())
                .birth(user.getBirth()).gender(user.getGender())
                .extensionId(userExtension.getExtensionId())
                .firstTermAgreement(userExtension.getFirstTermAgreement())
                .secondTermAgreement(userExtension.getSecondTermAgreement())
                .accountAuthority(userAccountAuthorityJpaRepository.findByUser(user).stream().map((ua) -> {
                    return ua.getAccountAuthority().getText();
                }).collect(Collectors.toList()))
                .userAttachments(userAttachmentsResponse == null ? null : userAttachmentsResponse)
                .build();
    }

    public User findByEmail(String email){
        return userJpaRepository.findByEmail(email);
    }
    public Optional<User> findById(Long userId) {return userJpaRepository.findById(userId);}

    public List<UserResponseDto> selectUserByCalendar(Long calendarId){
        return userMybatisRepository.selectUserByCalendar(calendarId);
    }

    public List<UserAccountAuthority> findAccountAuhorityByUser(User user){
        return userAccountAuthorityJpaRepository.findByUser(user);
    }
}
