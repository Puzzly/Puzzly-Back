package com.puzzly.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.dto.request.CalendarContentRequestDto;
import com.puzzly.api.dto.request.CalendarRequestDto;
import com.puzzly.api.dto.response.CalendarContentAttachmentsResponseDto;
import com.puzzly.api.dto.response.CalendarContentResponseDto;
import com.puzzly.api.dto.response.CalendarResponseDto;
import com.puzzly.api.dto.response.UserResponseDto;
import com.puzzly.api.entity.*;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.repository.jpa.CalendarContentAttachmentsJpaRepository;
import com.puzzly.api.repository.jpa.CalendarContentJpaRepository;
import com.puzzly.api.repository.jpa.CalendarJpaRepository;
import com.puzzly.api.repository.jpa.CalendarUserRelationJpaRepository;
import com.puzzly.api.repository.mybatis.CalendarContentMybatisRepository;
import com.puzzly.api.repository.mybatis.CalendarMybatisRepository;
import com.puzzly.api.util.CustomUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {
    private static final Logger logger = LoggerFactory.getLogger(CalendarService.class);
    private final CalendarJpaRepository calendarJpaRepository;
    private final CalendarMybatisRepository calendarMybatisRepository;

    private final CalendarUserRelationJpaRepository calendarUserRelationJpaRepository;

    private final CalendarContentJpaRepository calendarContentJpaRepository;
    private final CalendarContentMybatisRepository calendarContentMybatisRepository;

    private final CalendarContentAttachmentsJpaRepository calendarContentAttachmentsJpaRepository;

    private final CustomUtils customUtils;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    private final String context = "calendar";

    /** 초대코드 생성*/
    public HashMap<String, String> createInviteCode(SecurityUser securityUser, Long calendarId) throws FailException, Exception{
        HashMap<String, String> result = new HashMap<>();
        if(calendarId == null) {
            throw new FailException("SERVER_MESSAGE_PARAMETER_NOT_GIVEN", 400);
        }
        Calendar calendar = calendarJpaRepository.findById(calendarId).orElse(null);
        if(calendar == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_NOT_EXISTS", 404);
        }
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_INVITE_USER_NOT_EXISTS", 404);
        }
        CalendarUserRelation calendarUserRelList = calendarUserRelationJpaRepository.findCalendarUserRelation(calendar, user, false);
        if(ObjectUtils.isEmpty(calendarUserRelList)){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATED_IN_CALENDAR", 404);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("calendarId", calendar.getCalendarId());
        map.put("createId", securityUser.getUser().getUserId());

        JsonNode jsonNode = objectMapper.valueToTree(map);

        String inviteCode = CustomUtils.aesCBCEncode(jsonNode.toString());
        result.put("inviteCode", inviteCode);
        return result;
    }

    /** 초대코드로 캘린더 가입*/
    @Transactional
    public HashMap<String, Object> joinCalendarByInviteCode(SecurityUser securityUser, String inviteCode) throws FailException{
        User invitedUser = securityUser.getUser();
        HashMap<String, Object> resultMap = new HashMap<>();
        Map<String, Object> invitationMap = null;
        String decodedJsonString = CustomUtils.aesCBCDecode(inviteCode);
        try{
            invitationMap = objectMapper.readValue(decodedJsonString, HashMap.class);
        } catch (JsonProcessingException je){
            je.printStackTrace();
            throw new FailException("SERVER_MESSAGE_JSON_PARSING_ERROR_PARAMETER_CHECK_REQUIRED", 400);
        }

        User inviter = userService.findById(MapUtils.getLong(invitationMap, "createId")).orElse(null);
        Calendar calendar = calendarJpaRepository.findById(MapUtils.getLong(invitationMap, "calendarId")).orElse(null);
        if (inviter == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_FOUND", 404);
        }
        if(securityUser.getUser().getUserId().equals(inviter.getUserId())){
            throw new FailException("SERVER_MESSAGE_CANNOT_JOIN_CALENDAR_BY_OWN_INVITATION", 400);
        }
        CalendarUserRelation calendarInviterRel = calendarUserRelationJpaRepository.findCalendarUserRelation(calendar, inviter, false);
        if(calendarInviterRel == null){
            throw new FailException("SERVER_MESSAGE_INVITED_USER_KICKED_OUT_FROM_CALENDAR", 400);
        }
        Boolean isRelationExists = calendarUserRelationJpaRepository.existsCalendarUserRelation(invitedUser.getUserId(), calendar.getCalendarId(), false);
        if(isRelationExists){
            throw new FailException("SERVER_MESSAGE_INVITED_USER_ALREADY_JOINED_IN", 400);
        }
        // 관계생성
        CalendarUserRelation newRel = buildCalendarUserRel(calendar, invitedUser, false);
        calendarUserRelationJpaRepository.save(newRel);
        // 캘린더 타입변경
        calendar.setCalendarType("TEAM");
        calendarJpaRepository.save(calendar);
        // 응답객체생성
        CalendarResponseDto calendarResponseDto = buildCalendarResponseDto(calendar);

        resultMap.put("calendar", calendarResponseDto);
        return resultMap;
    }
    /** 캘린더 생성*/
    @Transactional
    public HashMap<String, Object> createCalendar(SecurityUser securityUser, CalendarRequestDto calendarRequestDto){
        HashMap<String, Object> resultMap = new HashMap<>();
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        if(StringUtils.isEmpty(StringUtils.trim(calendarRequestDto.getCalendarName()))){
            throw new FailException("SERVER_MESSAGE_CALENDAR_NAME_EMPTY", 400);
        }
        Calendar calendar = Calendar.builder().createUser(user).calendarName(calendarRequestDto.getCalendarName()).calendarType("PRIVATE")
                .isDeleted(false)
                .createDateTime(LocalDateTime.now()).build();

        // 캘린더 생성
        calendarJpaRepository.save(calendar);
        // 캘린더 관계 생성
        CalendarUserRelation calendarUserRel = buildCalendarUserRel(calendar, user, false);
        calendarUserRelationJpaRepository.save(calendarUserRel);

        ArrayList<UserResponseDto> userList = new ArrayList<>();

        userList.add(UserResponseDto.builder().userId(user.getUserId()).userName(user.getUserName()).nickName(user.getNickName()).build());
        CalendarResponseDto calendarResponseDto = CalendarResponseDto.builder().calendarId(calendar.getCalendarId())
                .calendarName(calendar.getCalendarName())
                .createId(user.getUserId())
                .createNickName(user.getNickName())
                .calendarType(calendar.getCalendarType())
                .userList(userList)
                .build();
        resultMap.put("calnendar", calendarRequestDto);
        return resultMap;
    }

    /** 캘린더 리스트 조회*/
    public HashMap<String, Object> getCalendarList(SecurityUser securityUser, int offset, int pageSize, boolean isDeleted){
        HashMap<String, Object> resultMap = new HashMap<>();
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        List<CalendarResponseDto> calendarList = calendarMybatisRepository.selectCalendarList(user.getUserId(), offset, pageSize, isDeleted);
        calendarList.stream().forEach((calendarResponseDto -> {
            calendarResponseDto.setUserList(userService.selectUserByCalendar(calendarResponseDto.getCalendarId()));
            // mybatis로 select할경우 entity 관계로 찾는게 아니라 테이블에서 바로찾아야 함.
        }));

        resultMap.put("calendarList", calendarList);
        return resultMap;
    }


    /** 캘린더 수정*/
    @Transactional
    public HashMap<String, Object> modifyCalendar(SecurityUser securityUser, CalendarRequestDto calendarRequestDto){
        HashMap<String, Object> resultMap = new HashMap<>();
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        if(calendarRequestDto.getCalendarName() != null && StringUtils.isEmpty(StringUtils.trim(calendarRequestDto.getCalendarName()))){
            throw new FailException("SERVER_MESSAGE_CALENDAR_NAME_EMPTY", 400);
        }
        Calendar calendar = calendarJpaRepository.findById(calendarRequestDto.getCalendarId()).orElse(null);
        if (calendar == null) {
            throw new FailException("SERVER_MESSAGE_CALENDAR_NOT_EXISTS", 400);
        }
        calendar.setModifyDateTime(LocalDateTime.now());
        calendar.setModifyUser(user);

        if(calendarRequestDto.getCalendarName() != null) calendar.setCalendarName(calendarRequestDto.getCalendarName());
        // 캘린더 생성
        calendarJpaRepository.save(calendar);

        ArrayList<UserResponseDto> userList = new ArrayList<>();
        userList.add(UserResponseDto.builder().userId(user.getUserId()).userName(user.getUserName()).nickName(user.getNickName()).build());
        CalendarResponseDto calendarResponseDto = CalendarResponseDto.builder().calendarId(calendar.getCalendarId())
                .calendarName(calendar.getCalendarName())
                .createId(user.getUserId())
                .createNickName(user.getNickName())
                .calendarType(calendar.getCalendarType())
                .userList(userList)
                .build();
        resultMap.put("calendar", calendarResponseDto);
        return resultMap;
    }

    /** 캘린더 삭제 */
    @Transactional
    public HashMap<String, Object> removeCalendar(SecurityUser securityUser, Long calendarId) throws FailException{
        HashMap<String, Object> resultMap = new HashMap<>();
        Calendar calendar = calendarJpaRepository.findById(calendarId).orElse(null);
        if(calendar == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_NOT_EXISTS", 400);
        }
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        if(!calendar.getCreateUser().getUserId().equals(user.getUserId())){
            throw new FailException("SERVER_MESSAGE_USER_NOT_OWN_CALENDAR", 400);
        }
        try{
            // 첨부파일 삭제
            calendarContentAttachmentsJpaRepository.bulkUpdateIsDeletedCalendarContentAttachments(calendar.getCalendarId());
            // 캘린더 컨텐츠 삭제
            calendarContentJpaRepository.bulkUpdateIsDeletedCalendarContentByCalendar(calendar);
            // 라벨삭제
            //calendarLabelJpaRepository.bulkUpdateCalendarContentByCalendar(calendar);
            // 캘린더 관계 삭제
            calendarUserRelationJpaRepository.bulkUpdateIsDeletedCalendarUserRelByCalendar(calendar);
            // 캘린더 삭제
            calendar.setIsDeleted(true);
            calendarJpaRepository.save(calendar);

        } catch(Exception e) {
            e.printStackTrace();
            throw new FailException(e.getMessage(), 500);
        }
        resultMap.put("calendarId", calendarId);
        return resultMap;
    }

    /** 캘린더 컨텐트 (일정) 생성 */
    @Transactional
    public HashMap<String, Object> createCalendarContent(SecurityUser securityUser, CalendarContentRequestDto contentRequestDto){
        HashMap<String, Object> resultMap = new HashMap<>();
        Calendar calendar = calendarJpaRepository.findById(contentRequestDto.getCalendarId()).orElse(null);
        if(calendar == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_NOT_EXISTS", 400);
        }
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        CalendarUserRelation calendarUserRelation = calendarUserRelationJpaRepository.findCalendarUserRelation(calendar, user, false);
        if(calendarUserRelation == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATE_IN", 404);
        }

        // 켈린더 컨텐트 등록
        CalendarContent calendarContent = CalendarContent.builder()
                .calendar(calendar)
                .createUser(user)
                .title(contentRequestDto.getTitle())
                .startDateTime(contentRequestDto.getStartDateTime())
                .endDateTime(contentRequestDto.getEndDateTime())
                .createDateTime(LocalDateTime.now())
                .location(contentRequestDto.getLocation())
                .isDeleted(false)
                .content(contentRequestDto.getContent())
                .notify(contentRequestDto.getNotify() == null ? false : contentRequestDto.getNotify())
                //.notifyTime(contentRequestDto.getNotify() ? contentRequestDto.getNotifyTime() == null ? null : contentRequestDto.getNotifyTime(): null)
                .memo(contentRequestDto.getMemo())
                //.calendarLabel()
                .build();
        calendarContentJpaRepository.save(calendarContent);

        // 캘린더 컨텐트 응답 생성
        CalendarContentResponseDto contentResponseDto = buildCalendarContentResponseDto(calendarContent, user);

        // 켈린더 첨부파일 등록
        if(contentRequestDto.getCreateAttachmentsList() != null && ObjectUtils.isNotEmpty(contentRequestDto.getCreateAttachmentsList())) {
            ArrayList<CalendarContentAttachmentsResponseDto> attachmentsList = new ArrayList<>();
            contentRequestDto.getCreateAttachmentsList().forEach( attachmentsId -> {
                CalendarContentAttachments calendarContentAttachments = calendarContentAttachmentsJpaRepository.findById(attachmentsId).orElse(null);

                if(calendarContentAttachments != null) {
                    calendarContentAttachments.setCalendarContent(calendarContent);
                    calendarContentAttachmentsJpaRepository.save(calendarContentAttachments);

                    attachmentsList.add(CalendarContentAttachmentsResponseDto.builder().calendarContentId(calendarContentAttachments.getCalendarContent().getContentId())
                            .attachmentsId(calendarContentAttachments.getAttachmentsId())
                            .filePath(calendarContentAttachments.getFilePath())
                            .fileSize(calendarContentAttachments.getFileSize())
                            .originName(calendarContentAttachments.getOriginName())
                            .extension(calendarContentAttachments.getExtension())
                            .createId(calendarContentAttachments.getCreateUser().getUserId())
                            .createNickName(calendarContentAttachments.getCreateUser().getNickName())
                            .createDateTime(calendarContentAttachments.getCreateDateTime())
                            .build()
                    );
                }
            });
            contentResponseDto.setAttachmentsList(attachmentsList);

        }
        resultMap.put("content", contentResponseDto);
        return resultMap;
    }

    /** 캘린더 컨텐트(일정) 리스트 조회 */
    public HashMap<String, Object> getCalendarContentList(SecurityUser securityUser, Long calendarId, LocalDateTime startTargetDateTime, LocalDateTime limitTargetDateTime, boolean isDeleted){
        HashMap<String, Object> resultMap = new HashMap<>();

        Calendar calendar = calendarJpaRepository.findById(calendarId).orElse(null);
        if(calendar == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_NOT_EXISTS", 400);
        }
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        CalendarUserRelation calendarUserRel = calendarUserRelationJpaRepository.findCalendarUserRelation(calendar, user, false);
        if(calendarUserRel == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATE_IN", 404);
        }

        List<CalendarContentResponseDto> calendarContentList = calendarContentMybatisRepository.selectCalendarContentByDateTimeAndCalendar(calendarId, startTargetDateTime, limitTargetDateTime, isDeleted);
        calendarContentList.forEach((calendarContent) -> {
            // TODO createTime Map에서 localDateTime이 timestamp로 매핑되는 현상 고쳐야함
            calendarContent.setAttachmentsList(calendarContentMybatisRepository.selectCalendarContentAttachmentsByContentId(calendarContent.getContentId(), false));
        });

        resultMap.put("contentList", calendarContentList);

        return resultMap;
    }

    /** 특정 캘린더 컨텐트 (일정) 조회*/
    public HashMap<String, Object> getCalendarContent(SecurityUser securityUser, Long contentId){
        HashMap<String, Object> resultMap = new HashMap<>();

        CalendarContent calendarContent = calendarContentJpaRepository.findById(contentId).orElse(null);
        if(calendarContent == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_CONTENT_NOT_EXISTS", 400);
        }
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        CalendarUserRelation calendarUserRel = calendarUserRelationJpaRepository.findCalendarUserRelation(calendarContent.getCalendar(), user, false);
        if(calendarUserRel == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATE_IN", 404);
        }

        CalendarContentResponseDto calendarContentResponseDto = calendarContentMybatisRepository.selectCalendarContentByContentId(contentId, false);
        calendarContentResponseDto.setAttachmentsList(calendarContentMybatisRepository.selectCalendarContentAttachmentsByContentId(contentId, false));

        resultMap.put("content", calendarContentResponseDto);
        return resultMap;
    }

    /** 캘린더 컨텐트 (일정) 수정*/
    @Transactional
    public HashMap<String, Object> modifyCalendarContent(SecurityUser securityUser, CalendarContentRequestDto calendarContentRequestDto){
        HashMap<String, Object> resultMap = new HashMap<>();

        CalendarContent calendarContent = calendarContentJpaRepository.findById(calendarContentRequestDto.getContentId()).orElse(null);
        if(calendarContent == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_CONTENT_NOT_EXISTS", 400);
        }

        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        CalendarUserRelation calendarUserRel = calendarUserRelationJpaRepository.findCalendarUserRelation(calendarContent.getCalendar(), user, false);
        if(calendarUserRel == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATE_IN", 404);
        }

        calendarContent.setModifyUser(user);
        calendarContent.setModifyDateTime(LocalDateTime.now());
        if(calendarContentRequestDto.getStartDateTime() != null) calendarContent.setStartDateTime(calendarContentRequestDto.getStartDateTime());
        if(calendarContentRequestDto.getEndDateTime() != null) calendarContent.setEndDateTime(calendarContentRequestDto.getEndDateTime());
        if(calendarContentRequestDto.getTitle() != null) calendarContent.setTitle(calendarContentRequestDto.getTitle());
        if(calendarContentRequestDto.getContent() != null) calendarContent.setContent(calendarContentRequestDto.getContent());
        if(calendarContentRequestDto.getMemo() != null) calendarContent.setMemo(calendarContentRequestDto.getMemo());
        if(calendarContentRequestDto.getLocation() != null)calendarContent.setLocation(calendarContentRequestDto.getLocation());
        if(calendarContentRequestDto.getNotify() != null) {
            calendarContent.setNotify(calendarContentRequestDto.getNotify());
            /*
            if(calendarContentRequestDto.getNotifyTime() != null){
                calendarContent.setNotifyTime(calendarContentRequestDto.getNotifyTime());
            } else {
                throw new FailException("SERVER_MESSAGE_NOTIFY_TRUE_BUT_NOTIFY_TIME_NULL", 400);
            }

             */
        }
        calendarContentJpaRepository.save(calendarContent);

        ArrayList<Long> deletedFiles = new ArrayList<>();
        if(calendarContentRequestDto.getDeleteAttachmentsList() != null){
            calendarContentRequestDto.getDeleteAttachmentsList().forEach((fileId) -> {
                    if (fileId != 0) {
                        CalendarContentAttachments calendarContentAttachments = calendarContentAttachmentsJpaRepository.findById(fileId).orElse(null);
                        if (calendarContentAttachments != null) {
                            calendarContentAttachments.setIsDeleted(true);
                            calendarContentAttachments.setDeleteDateTime(LocalDateTime.now());
                            calendarContentAttachments.setDeleteUser(user);
                            calendarContentAttachmentsJpaRepository.save(calendarContentAttachments);
                            deletedFiles.add(fileId);
                        }
                    }
                }
            );
        }

        if(calendarContentRequestDto.getCreateAttachmentsList() != null) {
            // 파일처리
            calendarContentRequestDto.getCreateAttachmentsList().forEach(attachmentsId -> {
                CalendarContentAttachments calendarContentAttachments = calendarContentAttachmentsJpaRepository.findById(attachmentsId).orElse(null);
                if(calendarContentAttachments != null) {
                    calendarContentAttachments.setCalendarContent(calendarContent);
                    calendarContentAttachmentsJpaRepository.save(calendarContentAttachments);
                }
            });
        }
        List<CalendarContentAttachmentsResponseDto> attachmentsList =
                calendarContentAttachmentsJpaRepository.findByCalendarContentAndIsDeleted(calendarContent, false).stream().map(
                        attachments -> {
                            return CalendarContentAttachmentsResponseDto.builder().calendarContentId(attachments.getCalendarContent().getContentId())
                                    .attachmentsId(attachments.getAttachmentsId())
                                    .filePath(attachments.getFilePath())
                                    .fileSize(attachments.getFileSize())
                                    .originName(attachments.getOriginName())
                                    .extension(attachments.getExtension())
                                    .createId(attachments.getCreateUser().getUserId())
                                    .createNickName(attachments.getCreateUser().getNickName())
                                    .createDateTime(attachments.getCreateDateTime())
                                    .build();
                        }).collect(Collectors.toList());

        CalendarContentResponseDto contentResponseDto = CalendarContentResponseDto.builder()
                .calendarId(calendarContent.getCalendar().getCalendarId())
                .calendarName(calendarContent.getCalendar().getCalendarName())
                .startDateTime(calendarContent.getStartDateTime())
                .endDateTime(calendarContent.getEndDateTime())
                .createDateTime(calendarContent.getCreateDateTime())
                .createId(user.getUserId())
                .createNickName(user.getNickName())
                .location(calendarContent.getLocation())
                .title(calendarContent.getTitle())
                .content(calendarContent.getContent())
                .contentId(calendarContent.getContentId())
                .notify(calendarContent.getNotify())
                //.notifyTime(calendarContent.getNotifyTime())
                .memo(calendarContent.getMemo())
                .attachmentsList(attachmentsList)
                //.deleteAttachmentsList(deletedFiles)
                //.calendarLabel(null)
                .build();
        resultMap.put("content", contentResponseDto);
        return resultMap;
    }

    /** 캘린더 컨텐트(일정) 삭제*/
    @Transactional
    public HashMap<String, Object> removeCalendarContent(SecurityUser securityUser, Long contentId) throws FailException{
        HashMap<String, Object> resultMap = new HashMap<>();

        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        CalendarContent calendarContent = calendarContentJpaRepository.findById(contentId).orElse(null);
        if(calendarContent == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_CONTENT_NOT_EXISTS", 400);
        }
        CalendarUserRelation calendarUserRel = calendarUserRelationJpaRepository.findCalendarUserRelation(calendarContent.getCalendar(), user, false);
        if(calendarUserRel == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATE_IN", 404);
        }

        // 첨부파일 전체 삭제
        calendarContentAttachmentsJpaRepository.bulkUpdateIsDeletedCalendarContentAttachmentsByContentId(calendarContent.getContentId());

        // 캘린더 컨텐츠 삭제
        calendarContent.setIsDeleted(true);
        calendarContentJpaRepository.save(calendarContent);

        resultMap.put("contentId", contentId);
        return resultMap;
    }

    /** 첨부파일 추가*/
    public HashMap<String, Object> uploadCalendarContentAttachments(SecurityUser securityUser, List<MultipartFile> fileList){
        HashMap<String, Object> resultMap = new HashMap<>();
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        ArrayList<Long> attachmentsIdList = new ArrayList<>();

        try {
            if (fileList != null) {
                // 파일처리
                fileList.forEach((file) -> {
                    HashMap<String, Object> fileResult = customUtils.uploadFile(context, file);
                    CalendarContentAttachments calendarContentAttachments = CalendarContentAttachments.builder()
                            //.calendarContent(calendarContent)
                            .extension(MapUtils.getString(fileResult, "extension"))
                            .filePath(MapUtils.getString(fileResult, "dirPath") + MapUtils.getString(fileResult, "fileName"))
                            .fileSize(MapUtils.getLong(fileResult, "fileSize"))
                            .createDateTime(LocalDateTime.now())
                            .originName(MapUtils.getString(fileResult, "originName"))
                            .isDeleted(false)
                            .createUser(user)
                            .build();
                    calendarContentAttachmentsJpaRepository.save(calendarContentAttachments);
                    attachmentsIdList.add(calendarContentAttachments.getAttachmentsId());
                    // 굳이 DTO까지 만들 필요 없을것같아서 Map으로 진행
                });
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        resultMap.put("attachmentsIdList", attachmentsIdList);
        return resultMap;
    }

    /** 첨부파일 다운로드 */
    public void downloadCalendarContentAttachments(SecurityUser securityUser, Long attachmentsId, HttpServletRequest request, HttpServletResponse response) throws IOException, FailException{
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        CalendarContentAttachments calendarContentAttachments = calendarContentAttachmentsJpaRepository.findById(attachmentsId).orElse(null);
        if(calendarContentAttachments == null){
            throw new FailException("SERVER_MESSAGE_ATTACHMENT_NOT_EXISTS", 400);
        }
        Long contentId = calendarContentAttachments.getCalendarContent().getContentId();
        CalendarContent calendarContent = calendarContentJpaRepository.findById(contentId).orElse(null);
        CalendarUserRelation calendarUserRel = calendarUserRelationJpaRepository.findCalendarUserRelation(calendarContent.getCalendar(), user, false);
        if(calendarUserRel == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATE_IN", 404);
        }

        String fileFullPath = calendarContentAttachments.getFilePath();
        String originName = calendarContentAttachments.getOriginName();
        String extension = calendarContentAttachments.getExtension();
        customUtils.downloadFile(fileFullPath, originName, extension, request, response);
    }

    public HashMap<String, Object> removeCalendarContentAttachments(SecurityUser securityUser, Long attachmentsId){
        HashMap<String, Object> resultMap = new HashMap<>();
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        CalendarContentAttachments calendarContentAttachments = calendarContentAttachmentsJpaRepository.findById(attachmentsId).orElse(null);
        if(calendarContentAttachments == null){
            throw new FailException("SERVER_MESSAGE_ATTACHMENT_NOT_EXISTS", 400);
        }
        Long contentId = calendarContentAttachments.getCalendarContent().getContentId();
        CalendarContent calendarContent = calendarContentJpaRepository.findById(contentId).orElse(null);
        CalendarUserRelation calendarUserRel = calendarUserRelationJpaRepository.findCalendarUserRelation(calendarContent.getCalendar(), user, false);
        if(calendarUserRel == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATE_IN", 404);
        }
        calendarContentAttachments.setIsDeleted(true);
        calendarContentAttachmentsJpaRepository.save(calendarContentAttachments);

        resultMap.put("attachmentsId", attachmentsId);
        return resultMap;
    }

    /** 캘린더 정보로 캘린더 관계 조회 */
    private List<CalendarUserRelation> findCalendarUserRelationByCalendar(Calendar calendar){
        return calendarUserRelationJpaRepository.findCalendarUserRelByCalendar(calendar);
    }

    /** 캘린더-유저 관계 빌드 메서드*/
    private CalendarUserRelation buildCalendarUserRel(Calendar calendar, User user, boolean isDeleted){
        return CalendarUserRelation.builder().user(user).calendar(calendar).authority(32).isDeleted(isDeleted).build();
    }

    /** 캘린더 -> 캘린더 응답 DTO 빌드 메서드*/
    private CalendarResponseDto buildCalendarResponseDto(Calendar calendar){
        List<UserResponseDto> userList = findCalendarUserRelationByCalendar(calendar).stream().map((relation) -> {
            User user = relation.getUser();
            return UserResponseDto.builder().userId(user.getUserId()).userName(user.getUserName()).userName(user.getNickName()).build();
        }).collect(Collectors.toList());

        return CalendarResponseDto.builder().calendarId(calendar.getCalendarId())
                .calendarName(calendar.getCalendarName())
                .createId(calendar.getCreateUser().getUserId())
                .createNickName(calendar.getCreateUser().getNickName())
                .calendarType(calendar.getCalendarType())
                .userList(userList)
                .build();
    }

    /** 캘린더 컨텐트 -> 캘린터 컨텐트 응답 DTO 빌드 메서드*/
    private CalendarContentResponseDto buildCalendarContentResponseDto(CalendarContent calendarContent, User user){
        return CalendarContentResponseDto.builder()
                .calendarId(calendarContent.getCalendar().getCalendarId())
                .contentId(calendarContent.getContentId())
                .calendarName(calendarContent.getCalendar().getCalendarName())
                .startDateTime(calendarContent.getStartDateTime())
                .endDateTime(calendarContent.getEndDateTime())
                .createDateTime(calendarContent.getCreateDateTime())
                .createId(user.getUserId())
                .title(calendarContent.getTitle())
                .createNickName(user.getNickName())
                .location(calendarContent.getLocation())
                .title(calendarContent.getTitle())
                .content(calendarContent.getContent())
                .contentId(calendarContent.getContentId())
                .notify(calendarContent.getNotify())
                //.notifyTime(calendarContent.getNotifyTime())
                .memo(calendarContent.getMemo())
                //.calendarLabel(null)
                .build();
    }
}
