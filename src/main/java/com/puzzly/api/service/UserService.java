package com.puzzly.api.service;

import com.puzzly.api.domain.AccountAuthority;
import com.puzzly.api.domain.JoinType;
import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.dto.response.UserResponseDto;
import com.puzzly.api.entity.User;
import com.puzzly.api.entity.UserAccountAuthority;
import com.puzzly.api.entity.UserExtension;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.repository.jpa.UserAccountAuthorityRepository;
import com.puzzly.api.repository.jpa.UserExtensionRepository;
import com.puzzly.api.repository.jpa.UserRepository;
import com.puzzly.api.util.CustomUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.Map;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    //TODO DID
    // 1. Attachments, userDel 제거
    // 2. Param ID 추가
    // 3. API 정리
    // 4. DTO 정리 ( time계열 정리 )
    // 5. Id 체크 API 추가
    // 6. Repository Level Jpa 단어 제거
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserExtensionRepository userExtensionRepository;
    private final UserAccountAuthorityRepository userAccountAuthorityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomUtils customUtils;
    private final RedisService redisService;
    private final String context = "user";

    /** 회원가입 (NATIVE) */
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) throws FailException{
        // Parameter Check
        if(ObjectUtils.isEmpty(userRequestDto.getUserName()) || ObjectUtils.isEmpty(userRequestDto.getNickName()) ||
                ObjectUtils.isEmpty(userRequestDto.getMemberId()) ||
                ObjectUtils.isEmpty(userRequestDto.getEmail()) || ObjectUtils.isEmpty(userRequestDto.getPassword()) ||
                ObjectUtils.isEmpty(userRequestDto.getPhoneNumber()) || ObjectUtils.isEmpty(userRequestDto.getBirth()) ||
                ObjectUtils.isEmpty(userRequestDto.getGender())){
            throw new FailException("SERVER_MESSAGE_BASIC_USER_PARAMETER_MISSING", 400);
        }
        if(ObjectUtils.isEmpty(userRequestDto.getFirstTermAgreement() || ObjectUtils.isEmpty(userRequestDto.getSecondTermAgreement()))){
            throw new FailException("SERVER_MESSAGE_TERM_AGREEMENT_NOT_EXISTS", 400);
        }
        if(userRepository.selectExistsEmailAndIsDeleted(userRequestDto.getEmail(), null)){
            throw new FailException("SERVER_MESSAGE_EMAIL_ALREADY_EXISTS", 400);
        }
        if(selectExistsMemberIdAndIsDeleted(userRequestDto.getMemberId(), null)){
            throw new FailException("SERVER_MESSAGE_ID_ALREADY_EXISTS", 400);
        }
        // TODO FE와 별도로 상의하여 통신구간 암호화를 구현하고, 복호화 > 암호화 혹은 그대로 때려박기 등을 구현해야 한다.
        userRequestDto.setPassword(bCryptPasswordEncoder.encode(userRequestDto.getPassword()));

        // 확장정보 저장
        // TODO Oauth 추가시, JoinType 강제지정 풀어야함.
        UserExtension userExtension = UserExtension.builder()
                .joinType(JoinType.NATIVE).firstTermAgreement(userRequestDto.getFirstTermAgreement()).secondTermAgreement(userRequestDto.getSecondTermAgreement())
                .build();
        userExtensionRepository.save(userExtension);

        // 기초정보 저장
        User user = User.builder()
                .memberId(userRequestDto.getMemberId()).userName(userRequestDto.getUserName()).nickName(userRequestDto.getUserName()).phoneNumber(userRequestDto.getPhoneNumber())
                .birth(userRequestDto.getBirth()).gender(userRequestDto.getGender()).email(userRequestDto.getEmail()).password(userRequestDto.getPassword())
                .createDateTime(LocalDateTime.now()).isDeleted(false).userExtension(userExtension)
                .build();
        userRepository.save(user);

        // 계정권한 저장
        UserAccountAuthority userAccountAuthority = UserAccountAuthority.builder()
                .user(user)
                .accountAuthority(ObjectUtils.isEmpty(userRequestDto.getAccountAuthority()) ? AccountAuthority.ROLE_USER : userRequestDto.getAccountAuthority())
                .build();
        userAccountAuthorityRepository.save(userAccountAuthority);

        // 계정이 ADMIN일 경우 사용자 권한도 추가
        if(userAccountAuthority.getAccountAuthority().equals(AccountAuthority.ROLE_ADMIN)){
            UserAccountAuthority adminAccountAuthority = UserAccountAuthority.builder()
                    .user(user).accountAuthority(AccountAuthority.ROLE_USER)
                    .build();
            userAccountAuthorityRepository.save(adminAccountAuthority);
        }

        // 결과 return
        UserResponseDto result = buildUserResponseDtoFromUser(user, userExtension);
        return result;
    }

    /** 사용자 첨부파일 (프로필) 업로드*/
    public UserResponseDto uploadUserProfile(SecurityUser securityUser, MultipartFile attachments) throws FailException{

        User user = findById(securityUser.getUser().getUserId()).orElse(null);
        UserExtension userExtension = user.getUserExtension();
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        if(StringUtils.isNotEmpty(userExtension.getProfilePath())){
            customUtils.deleteFile(context , userExtension.getProfilePath());
            userExtension.setProfilePath(null);
            userExtension.setFileSize(null);
            userExtension.setExtension(null);
            userExtension.setOriginName(null);
        }

        HashMap<String, Object> fileResult = customUtils.uploadFile(context, attachments);
        userExtension.setOriginName(MapUtils.getString(fileResult, "originName"));
        userExtension.setProfilePath(MapUtils.getString(fileResult, "dirPath") + MapUtils.getString(fileResult, "fileName") + "." + MapUtils.getString(fileResult, "extension"));
        userExtension.setFileSize(MapUtils.getLong(fileResult, "fileSize"));
        userExtension.setExtension(MapUtils.getString(fileResult, "extension"));

        user.setModifyDateTime(LocalDateTime.now());
        userExtensionRepository.save(userExtension);
        userRepository.save(user);

        return buildUserResponseDtoFromUser(user, userExtension);
    }

    /** 이메일 중복여부 조회*/
    public boolean selectExistsEmail(String email) throws FailException{
        if(userRepository.selectExistsEmailAndIsDeleted(email, null)){
            throw new FailException("SERVER_MESSAGE_EMAIL_ALREADY_EXISTS", 400);
        } else {
            return false;
        }
    }

    /** ID 중복여부 조회 */
    public boolean selectExistsMemberId(String memberId) throws FailException{
        if(selectExistsMemberIdAndIsDeleted(memberId, null)){
            throw new FailException("SERVER_MESSAGE_MEMBER_ID_ALREADY_EXISTS", 400);
        }{
            return false;
        }
    }


    /** 사용자 조회 */
    public UserResponseDto getUser(Long userId){
        UserResponseDto user = userRepository.selectUserByUserId(userId, false);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_INFO_NOT_FOUND", 400);
        }
        user.setAccountAuthority(getAccountAuthority(userId));
        return user;
    }

    /** 사용자 프로필 사진 다운로드*/
    public void downloadUserProfile(SecurityUser securityUser, Long userId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(ObjectUtils.isEmpty(userId)){
            throw new FailException("SERVER_MESSAGE_PARAMETER_NOT_FOUND", 400);
        }
        User user = userRepository.findByUserIdAndIsDeleted(userId, false);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        UserExtension userExtension = user.getUserExtension();
        customUtils.downloadFile(userExtension.getProfilePath(), userExtension.getOriginName(), userExtension.getExtension(), request, response);
    }


    /** 사용자 수정 */
    @Transactional
    public UserResponseDto modifyUser(Long userId, UserRequestDto userRequestDto){
        User user = userRepository.findByUserIdAndIsDeleted(userId, false);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        UserExtension userExtension = user.getUserExtension();

        if(userRequestDto.getNickName() != null) user.setNickName(StringUtils.trim(userRequestDto.getNickName()));
        if(userRequestDto.getStatusMessage() != null) userExtension.setStatusMessage(userRequestDto.getStatusMessage());
        if(userRequestDto.getFirstTermAgreement() != null) userExtension.setFirstTermAgreement(userRequestDto.getFirstTermAgreement());
        if(userRequestDto.getSecondTermAgreement() != null) userExtension.setSecondTermAgreement(userRequestDto.getSecondTermAgreement());
        user.setModifyDateTime(LocalDateTime.now());

        userRepository.save(user);
        userExtensionRepository.save(userExtension);
        UserResponseDto modifiedUser = buildUserResponseDtoFromUser(user, userExtension);

        return modifiedUser;
    }

    /** 회원탈퇴 */
    public Boolean deleteUser(SecurityUser securityUser){
            long userId = securityUser.getUser().getUserId();
            User user = userRepository.findByUserId(userId);
            user.setIsDeleted(true);
            userRepository.save(user);
        return true;
    }

    /* FCM 토큰 변경 */
    public HashMap<String, Object> modifyFcmToken(SecurityUser securityUser, Long userId, String appToken, String webToken){
        HashMap<String, Object> resultMap = new HashMap<>();


        Map<String, String> requestMap = new HashMap<>();
        if(appToken != null){
            requestMap.put("app_token", appToken);
        }
        if(webToken != null){
            requestMap.put("web_token", webToken);
        }

        if(userId == null){
            userId = securityUser.getUser().getUserId();
            User user = userRepository.findByUserId(userId);
            if(user == null){
                throw new FailException("SERVER_MESSAGE_USER_NOT_FOUND", 400);
            }
            if(user.getIsDeleted() == true){
                throw new FailException("SERVER_MESSAGE_DELETED_USER", 400);
            }
        } else {
            if(!securityUser.getAuthorities().contains("ROLE_ADMIN")) {
                throw new FailException("SERVER_MESSAGE_ONLY_ADMIN_CAN_DO_THIS_OPERATION", 400);
            }
        }

        redisService.setHash(String.valueOf(userId), requestMap, 5184000000L);
        resultMap.put("result", "SUCCEED");
        return resultMap;
    }

    /** User , UserExtension Entity로부터 user responseDto 생성*/
    private UserResponseDto buildUserResponseDtoFromUser(User user, UserExtension userExtension){

        return UserResponseDto.builder()
                .userId(user.getUserId()).memberId(user.getMemberId()).userName(user.getUserName())
                .nickName(user.getNickName()).phoneNumber(user.getPhoneNumber())
                .birth(user.getBirth()).gender(user.getGender())
                .email(user.getEmail()).createDateTime(user.getCreateDateTime())

                .joinType(userExtension.getJoinType())
                .statusMessage(userExtension.getStatusMessage())
                .profilePath(userExtension.getProfilePath()).extension(userExtension.getExtension()).originName(userExtension.getOriginName())
                .fileSize(userExtension.getFileSize())
                .firstTermAgreement(userExtension.getFirstTermAgreement())
                .secondTermAgreement(userExtension.getSecondTermAgreement())
                .personalSetting(userExtension.getPersonalSetting())
                .accountAuthority(userAccountAuthorityRepository.findByUser(user).stream().map((ua) -> {
                    return ua.getAccountAuthority().getText();
                }).collect(Collectors.toList()))
                .build();
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }
    public Optional<User> findById(Long userId) {return userRepository.findById(userId);}

    public Boolean selectExistsEmailAndIsDeleted(String email, Boolean isDeleted){
        return userRepository.selectExistsEmailAndIsDeleted(email, isDeleted);
    }
    public Boolean selectExistsMemberIdAndIsDeleted(String memberId, Boolean isDeleted){
        return userRepository.selectExistsMemberIdAndIsDeleted(memberId, isDeleted);
    }

    public User findByUserId(Long userId) {return userRepository.findByUserId(userId);}

    public User findByMemberIdAndIsDeleted(String memberId, Boolean isDeleted) {
        return userRepository.findByMemberIdAndIsDeleted(memberId, isDeleted);
    }

    public List<UserResponseDto> selectUserByCalendar(Long calendarId, Boolean isDeleted){
        //return userMybatisRepository.selectUserByCalendar(calendarId);
        return userRepository.selectUserByCalendar(calendarId, isDeleted);
    }


    /** 사용자 정보 조회 */
    @Deprecated(forRemoval = true)
    public UserResponseDto selectUser(Long userId){

        // jpa 전용 failException 만들어서 orelseThrow 사용을 고려할것
        User userEntity = userRepository.findById(userId).orElse(null);
        UserExtension userExEntity = userEntity.getUserExtension();
        UserResponseDto user  = UserResponseDto.builder().userId(userEntity.getUserId()).userName(userEntity.getUserName()).nickName(userEntity.getNickName())
                .email(userEntity.getEmail()).phoneNumber(userEntity.getPhoneNumber()).birth(userEntity.getBirth()).gender(userEntity.getGender())
                .accountAuthority(userAccountAuthorityRepository.findByUser(userEntity).stream().map((ua) -> {
                    return ua.getAccountAuthority().getText();
                }).collect(Collectors.toList())).createDateTime(userEntity.getCreateDateTime())
                .firstTermAgreement(userExEntity.getFirstTermAgreement())
                .secondTermAgreement(userExEntity.getSecondTermAgreement())
                //.profileFilePath(userExEntity.getProfileFilePath())
                .statusMessage(userExEntity.getStatusMessage())
                .build();
        return user;

    }

    public List<UserResponseDto> selectUserByCalendarContentRelation(Long contentId, Boolean isDeleted) {
        /*
        // TODO 이 위치에서 사용자 프로필 사진 정보 필요한지 협의필요
        //return userMybatisRepository.selectUserByCalendarContentRelation(calendarId, isDeleted);
        //return userJpaRepository.selectUserByCalendarContentRelation(contentId, isDeleted);
        List<UserResponseDto> userList = userJpaRepository.selectUserByCalendarContentRelation(contentId, isDeleted);
        userList.stream().forEach((user) -> {
            user.setUserAttachments(userAttachmentsJpaRepository.selectUserAttachmentsByUserId(user.getUserId(), false));
        });
        return userList;

         */
        return null;
    }

    public List<UserAccountAuthority> findAccountAuhorityByUser(User user){
        return userAccountAuthorityRepository.findByUser(user);
    }

    public List<String> getAccountAuthority(Long userId){
        List<String> accountAuthorityList = new ArrayList<>();
        userAccountAuthorityRepository.selectUserAuthority(userId).stream().forEach(
                (authority) -> {
                    accountAuthorityList.add(authority.getText());
                });
        return accountAuthorityList;
    }
}
