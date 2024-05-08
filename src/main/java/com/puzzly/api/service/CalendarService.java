package com.puzzly.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.dto.request.CalendarContentsRequestDto;
import com.puzzly.api.dto.request.CalendarRequestDto;
import com.puzzly.api.dto.response.CalendarContentsResponseDto;
import com.puzzly.api.dto.response.CalendarResponseDto;
import com.puzzly.api.dto.response.UserResponseDto;
import com.puzzly.api.entity.*;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.repository.jpa.CalendarContentsAttachmentsJpaRepository;
import com.puzzly.api.repository.jpa.CalendarContentsJpaRepository;
import com.puzzly.api.repository.jpa.CalendarJpaRepository;
import com.puzzly.api.repository.jpa.CalendarUserRelJpaRepository;
import com.puzzly.api.repository.mybatis.CalendarContentsMybatisRepository;
import com.puzzly.api.repository.mybatis.CalendarMybatisRepository;
import com.puzzly.api.util.CustomUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {
    Logger logger = LoggerFactory.getLogger(CalendarService.class);
    private final CalendarJpaRepository calendarJpaRepository;
    private final CalendarMybatisRepository calendarMybatisRepository;
    private final CalendarUserRelJpaRepository calendarUserRelJpaRepository;

    private final CalendarContentsJpaRepository calendarContentsJpaRepository;
    private final CalendarContentsMybatisRepository calendarContentsMybatisRepository;

    private final CalendarContentsAttachmentsJpaRepository calendarContentsAttachmentsJpaRepository;

    private final CustomUtils customUtils;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    private final String context = "calendar";
    public List<CalendarResponseDto> getSimpleCalendarList(SecurityUser securityUser, int offset, int pageSize){
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        List<CalendarResponseDto> calendarList = calendarMybatisRepository.getSimpleCalendarDtoListJoinRel(user.getUserId(), offset, pageSize);
        calendarList.stream().forEach((calendarResponseDto -> {
            // JPA로 간단하게 구현할 방법이 생각나지 않아 Mybatis로 구현 수행
            calendarResponseDto.setUserList(userService.findByCalendarRel(calendarResponseDto.getCalendarId()));
        }));
        return calendarList;
    }

    @Transactional
    public CalendarResponseDto createCalendar(SecurityUser securityUser, CalendarRequestDto calendarRequestDto){
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        if(StringUtils.isEmpty(StringUtils.trim(calendarRequestDto.getCalendarName()))){
            throw new FailException("SERVER_MESSAGE_CALENDAR_NAME_EMPTY", 400);
        }
        Calendar calendar = Calendar.builder().createUser(user).calendarName(calendarRequestDto.getCalendarName()).calendarType("PRIVATE")
                .createDateTime(calendarRequestDto.getCreateDateTime()).build();

        // 캘린더 생성≠
        calendarJpaRepository.save(calendar);
        // 캘린더 관계 생성
        CalendarUserRel calendarUserRel = CalendarUserRel.builder().user(user).calendar(calendar).authority(32).build();
        calendarUserRelJpaRepository.save(calendarUserRel);

        ArrayList<UserResponseDto> userList = new ArrayList<>();
        userList.add(UserResponseDto.builder().userId(user.getUserId()).userName(user.getUserName()).nickName(user.getNickName()).build());
        CalendarResponseDto calendarResponseDto = CalendarResponseDto.builder().calendarId(calendar.getCalendarId())
                .calendarName(calendar.getCalendarName())
                .createId(user.getUserId())
                .createNickName(user.getNickName())
                .calendarType(calendar.getCalendarType())
                .userList(userList)
                .build();

        return calendarResponseDto;
    }

    @Transactional
    public CalendarResponseDto updateCalendar(SecurityUser securityUser, CalendarRequestDto calendarRequestDto){
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

        return calendarResponseDto;
    }

    public String createInviteCode(SecurityUser securityUser, Long calendarId) throws FailException, Exception{
        if(calendarId == null) {
            throw new FailException("SERVER_MESSAGE_PARAMETER_NOT_GIVEN", 400);
        }
        // Calendar의 존재 여부를 확인한다.
        Calendar calendar = calendarJpaRepository.findById(calendarId).orElse(null);

        if(calendar == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_NOT_EXISTS", 404);
        }
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_INVITE_USER_NOT_EXISTS", 404);
        }
        CalendarUserRel calendarUserRel = calendarUserRelJpaRepository.findCalendarUserRelByUser(user);
        if(calendarUserRel == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATED_IN_CALENDAR", 404);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("calendarId", calendar.getCalendarId());
        map.put("createId", securityUser.getUser().getUserId());

        JsonNode jsonNode = objectMapper.valueToTree(map);

        String invitationCode = CustomUtils.aesCBCEncode(jsonNode.toString());
        return invitationCode;
    }

    @Transactional
    public CalendarResponseDto joinCalendarByInviteCode(SecurityUser securityUser, String invitationCode) throws Exception{
        String decodedJsonString = CustomUtils.aesCBCDecode(invitationCode);
        Map<String, Object> invitationMap = objectMapper.readValue(decodedJsonString,HashMap.class);

        User user = userService.findById(MapUtils.getLong(invitationMap, "createId")).orElse(null);
        Calendar calendar = calendarJpaRepository.findById(MapUtils.getLong(invitationMap, "calendarId")).orElse(null);
        if (user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_FOUND", 404);
        }
        if(securityUser.getUser().getUserId().equals(user.getUserId())){
            throw new FailException("SERVER_MESSAGE_CANNOT_JOIN_CALENDAR_IN_POSSESSION", 400);
        }
        CalendarUserRel calendarUserRel = calendarUserRelJpaRepository.findCalendarUserRelByUserAndCalendar(user, calendar);
        if(calendarUserRel == null){
            throw new FailException("SERVER_MESSAGE_INVITED_USER_KICKED_OUT_FROM_CALENDAR", 400);
        }
        CalendarUserRel newRel = CalendarUserRel.builder().user(userService.findById(securityUser.getUser().getUserId()).orElse(null))
                .calendar(calendar)
                .authority(32)
                .build();
        calendarUserRelJpaRepository.save(newRel);
        calendar.setCalendarType("TEAM");
        calendarJpaRepository.save(calendar);
        CalendarResponseDto calendarResponseDto = CalendarResponseDto.builder().calendarId(calendar.getCalendarId())
                .calendarName(calendar.getCalendarName())
                .createId(user.getUserId())
                .createNickName(user.getNickName())
                .calendarType(calendar.getCalendarType())
                // JPA로 간단하게 구현할 방법이 마땅히 떠오르지 않아 Mybatis로 구현 수행
                .userList(userService.findByCalendarRel(calendar.getCalendarId()))
                .build();
        return calendarResponseDto;
    }

    @Transactional
    public CalendarContentsResponseDto createCalendarContents(SecurityUser securityUser, CalendarContentsRequestDto calendarContentsRequestDto, List<MultipartFile> fileList){
        Calendar calendar = calendarJpaRepository.findById(calendarContentsRequestDto.getCalendarId()).orElse(null);
        if(calendar == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_NOT_EXISTS", 400);
        }
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        CalendarUserRel calendarUserRel = calendarUserRelJpaRepository.findCalendarUserRelByUserAndCalendar(user, calendar);
        if(calendarUserRel == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATE_IN", 404);
        }

        CalendarContents calendarContents = CalendarContents.builder()
                .calendar(calendar)
                .createUser(user)
                .title(calendarContentsRequestDto.getTitle())
                .startDateTime(calendarContentsRequestDto.getStartDateTime())
                .endDateTime(calendarContentsRequestDto.getEndDateTime())
                .createDateTime(LocalDateTime.now())
                .location(calendarContentsRequestDto.getLocation())
                .contents(calendarContentsRequestDto.getContents())
                .notify(calendarContentsRequestDto.getNotify() == null ? false : calendarContentsRequestDto.getNotify())
                .notifyTime(calendarContentsRequestDto.getNotify() ? calendarContentsRequestDto.getNotifyTime() == null ? null : calendarContentsRequestDto.getNotifyTime(): null)
                .memo(calendarContentsRequestDto.getMemo())
                //.calendarLabel()
                .build();
        calendarContentsJpaRepository.save(calendarContents).getContentsId();
        ArrayList<Map<String, Object>> files = new ArrayList<>();

        if(fileList != null) {
            // 파일처리
            fileList.forEach((file) -> {
                HashMap<String, Object> fileResult = customUtils.uploadFile(context, file);
                CalendarContentsAttachments calendarContentsAttachments = CalendarContentsAttachments.builder()
                        .calendarContents(calendarContents)
                        .extension(MapUtils.getString(fileResult, "extension"))
                        .filePath(MapUtils.getString(fileResult, "dirPath") + "/" + MapUtils.getString(fileResult, "fileName"))
                        .fileSize(MapUtils.getLong(fileResult, "fileSize"))
                        .createDateTime(LocalDateTime.now())
                        .originName(MapUtils.getString(fileResult, "originName"))
                        .isDeleted(false)
                        .createUser(user)
                        .build();
                calendarContentsAttachmentsJpaRepository.save(calendarContentsAttachments);
                fileResult.put("attachmentId", calendarContentsAttachments.getAttachmentId());
                files.add(fileResult);
                // 굳이 DTO까지 만들 필요 없을것같아서 Map으로 진행
            });
        }

        CalendarContentsResponseDto contentsResponseDto = CalendarContentsResponseDto.builder()
                .calendarId(calendar.getCalendarId())
                .contentsId(calendarContents.getContentsId())
                .calendarName(calendar.getCalendarName())
                .startDateTime(calendarContents.getStartDateTime())
                .endDateTime(calendarContents.getEndDateTime())
                .createDateTime(calendarContents.getCreateDateTime())
                .createId(user.getUserId())
                .title(calendarContents.getTitle())
                .createNickName(user.getNickName())
                .location(calendarContents.getLocation())
                .title(calendarContents.getTitle())
                .contents(calendarContents.getContents())
                .contentsId(calendarContents.getContentsId())
                .notify(calendarContents.getNotify())
                .notifyTime(calendarContents.getNotifyTime())
                .memo(calendarContents.getMemo())
                .fileList(files)
                //.calendarLabel(null)
                .build();
        return contentsResponseDto;
    }

    @Transactional
    public CalendarContentsResponseDto updateCalendarContents(SecurityUser securityUser, CalendarContentsRequestDto calendarContentsRequestDto, List<MultipartFile> fileList){
        Calendar calendar = calendarJpaRepository.findById(calendarContentsRequestDto.getCalendarId()).orElse(null);
        if(calendar == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_NOT_EXISTS", 400);
        }
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        CalendarUserRel calendarUserRel = calendarUserRelJpaRepository.findCalendarUserRelByUserAndCalendar(user, calendar);
        if(calendarUserRel == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATE_IN", 404);
        }
        CalendarContents calendarContents = calendarContentsJpaRepository.findById(calendarContentsRequestDto.getContentsId()).orElse(null);
        if(calendarContents == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_CONTENTS_NOT_EXISTS", 400);
        }
        calendarContents.setModifyUser(user);
        calendarContents.setModifyDateTime(LocalDateTime.now());
        if(calendarContentsRequestDto.getStartDateTime() != null) calendarContents.setStartDateTime(calendarContentsRequestDto.getStartDateTime());
        if(calendarContentsRequestDto.getEndDateTime() != null) calendarContents.setEndDateTime(calendarContentsRequestDto.getEndDateTime());
        if(calendarContentsRequestDto.getTitle() != null) calendarContents.setTitle(calendarContentsRequestDto.getTitle());
        if(calendarContentsRequestDto.getContents() != null) calendarContents.setContents(calendarContentsRequestDto.getContents());
        if(calendarContentsRequestDto.getMemo() != null) calendarContents.setMemo(calendarContentsRequestDto.getMemo());
        if(calendarContentsRequestDto.getLocation() != null)calendarContents.setLocation(calendarContentsRequestDto.getLocation());
        if(calendarContentsRequestDto.getNotify() != null) {
            calendarContents.setNotify(calendarContentsRequestDto.getNotify());
            if(calendarContentsRequestDto.getNotifyTime() != null){
                calendarContents.setNotifyTime(calendarContentsRequestDto.getNotifyTime());
            } else {
                throw new FailException("SERVER_MESSAGE_NOTIFY_TRUE_BUT_NOTIFY_TIME_NULL", 400);
            }
        }
        ArrayList<Long> deletedFiles = new ArrayList<>();
        if(calendarContentsRequestDto.getDeleteFileList() != null){
            calendarContentsRequestDto.getDeleteFileList().forEach((fileId) -> {
                    if (fileId != 0) {
                        CalendarContentsAttachments calendarContentsAttachments = calendarContentsAttachmentsJpaRepository.findById(fileId).orElse(null);
                        if (calendarContentsAttachments != null) {
                            calendarContentsAttachments.setIsDeleted(true);
                            calendarContentsAttachments.setDeleteDateTime(LocalDateTime.now());
                            calendarContentsAttachments.setDeleteUser(user);
                            calendarContentsAttachmentsJpaRepository.save(calendarContentsAttachments);
                            deletedFiles.add(fileId);
                        }
                    }
                }
            );
        }

        ArrayList<Map<String, Object>> files = new ArrayList<>();
        if(fileList != null) {
            // 파일처리
            fileList.forEach((file) -> {
                HashMap<String, Object> fileResult = customUtils.uploadFile(context, file);
                CalendarContentsAttachments calendarContentsAttachments = CalendarContentsAttachments.builder()
                        .calendarContents(calendarContents)
                        .extension(MapUtils.getString(fileResult, "extension"))
                        .filePath(MapUtils.getString(fileResult, "dirPath") + "/" + MapUtils.getString(fileResult, "fileName"))
                        .fileSize(MapUtils.getLong(fileResult, "fileSize"))
                        .createDateTime(LocalDateTime.now())
                        .originName(MapUtils.getString(fileResult, "originName"))
                        .isDeleted(false)
                        .createUser(user)
                        .build();
                calendarContentsAttachmentsJpaRepository.save(calendarContentsAttachments);
                files.add(fileResult);
                // 굳이 DTO까지 만들 필요 없을것같아서 Map으로 진행
            });
        }
        //if(calendarContentsRequestDto.getLabelId() != null) calendarContents.setCalendarLabel();
        calendarContentsJpaRepository.save(calendarContents);

        CalendarContentsResponseDto contentsResponseDto = CalendarContentsResponseDto.builder()
                .calendarId(calendar.getCalendarId())
                .calendarName(calendar.getCalendarName())
                .startDateTime(calendarContents.getStartDateTime())
                .endDateTime(calendarContents.getEndDateTime())
                .createDateTime(calendarContents.getCreateDateTime())
                .createId(user.getUserId())
                .createNickName(user.getNickName())
                .location(calendarContents.getLocation())
                .title(calendarContents.getTitle())
                .contents(calendarContents.getContents())
                .contentsId(calendarContents.getContentsId())
                .notify(calendarContents.getNotify())
                .notifyTime(calendarContents.getNotifyTime())
                .memo(calendarContents.getMemo())
                .fileList(files)
                .deleteFileList(deletedFiles)
                //.calendarLabel(null)
                .build();
        return contentsResponseDto;
    }

    //public List<CalendarContentsResponseDto> getCalendarContentsList(SecurityUser securityUser, Long calendarId, Period period, LocalDate targetDate){
    public List<CalendarContentsResponseDto> getCalendarContentsList(SecurityUser securityUser, Long calendarId, LocalDateTime startTargetDateTime, LocalDateTime limitTargetDateTime){
        Calendar calendar = calendarJpaRepository.findById(calendarId).orElse(null);
        if(calendar == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_NOT_EXISTS", 400);
        }
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        CalendarUserRel calendarUserRel = calendarUserRelJpaRepository.findCalendarUserRelByUserAndCalendar(user, calendar);
        if(calendarUserRel == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATE_IN", 404);
        }
        /*
        LocalDateTime startDateTime = null;
        LocalDateTime limitStartDateTime = null;
        switch(period) {
            case DAY -> {
                startDateTime = targetDate.atStartOfDay();
                limitStartDateTime = startDateTime.with(LocalTime.MAX);
            }
            case WEEK -> {
                startDateTime = targetDate.minusDays(targetDate.get(DAY_OF_WEEK)).atStartOfDay();
                limitStartDateTime = startDateTime.plusDays(6).with(LocalTime.MAX);
            }
            case MONTH -> {
                startDateTime = targetDate.withDayOfMonth(1).atStartOfDay();
                limitStartDateTime = targetDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
            }
            default ->{
                startDateTime = null;
            }
        };
        if (startDateTime == null) {
            throw new FailException("Param Missing", 400);
        }

         */
        List<CalendarContentsResponseDto> calendarContentsList = calendarContentsMybatisRepository.selectCalendarContentsByStartDateTimeAndCalendar(calendarId, startTargetDateTime, limitTargetDateTime);
        calendarContentsList.forEach((calendarContents) -> {
            // TODO createTime Map에서 localDateTime이 timestamp로 매핑되는 현상 고쳐야함
            calendarContents.setFileList(calendarContentsMybatisRepository.selectCalendarContentsAttachmentsByContentsId(calendarContents.getContentsId()));
        });
        return calendarContentsList;
    }

    public CalendarContentsResponseDto getCalendarContents(SecurityUser securityUser, Long contentsId){
        //TODO Authentication Verified
        CalendarContentsResponseDto calendarContentsResponseDto = calendarContentsMybatisRepository.selectCalendarContentsByContentsId(contentsId);
        calendarContentsResponseDto.setFileList(calendarContentsMybatisRepository.selectCalendarContentsAttachmentsByContentsId(contentsId));

        return calendarContentsResponseDto;
    }

    public CalendarResponseDto getCalendar(Long calendarId){
        return calendarMybatisRepository.getCalendar(calendarId);
    }

    public void downloadCalendarContentsAttachments(SecurityUser securityUser, Long attachmentId, HttpServletRequest request, HttpServletResponse response) throws IOException, FailException{
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        CalendarContentsAttachments calendarContentsAttachments = calendarContentsAttachmentsJpaRepository.findById(attachmentId).orElse(null);
        if(calendarContentsAttachments == null){
            throw new FailException("SERVER_MESSAGE_ATTACHMENT_NOT_EXISTS", 400);
        }
        Long contentsId = calendarContentsAttachments.getCalendarContents().getContentsId();
        CalendarContents calendarContents = calendarContentsJpaRepository.findById(contentsId).orElse(null);
        CalendarUserRel calendarUserRel = calendarUserRelJpaRepository.findCalendarUserRelByUserAndCalendar(user, calendarContents.getCalendar());
        if(calendarUserRel == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATE_IN", 404);
        }

        String fileFullPath = calendarContentsAttachments.getFilePath();
        String originName = calendarContentsAttachments.getOriginName();
        String extension = calendarContentsAttachments.getExtension();
        customUtils.downloadFile(fileFullPath, originName, extension, request, response);
    }


}
