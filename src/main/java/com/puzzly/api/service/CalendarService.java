package com.puzzly.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.dto.request.CalendarContentCommentRequestDto;
import com.puzzly.api.dto.request.CalendarContentRequestDto;
import com.puzzly.api.dto.request.CalendarLabelRequestDto;
import com.puzzly.api.dto.request.CalendarRequestDto;
import com.puzzly.api.dto.response.*;
import com.puzzly.api.entity.Calendar;
import com.puzzly.api.entity.*;
import com.puzzly.api.enums.AlarmType;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.repository.jpa.*;
import com.puzzly.api.util.CustomUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Text;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {
    private static final Logger logger = LoggerFactory.getLogger(CalendarService.class);
    private final CalendarJpaRepository calendarJpaRepository;

    private final CalendarUserRelationJpaRepository calendarUserRelationJpaRepository;

    private final CalendarContentJpaRepository calendarContentJpaRepository;

    private final CalendarContentAttachmentsJpaRepository calendarContentAttachmentsJpaRepository;

    private final CalendarLabelJpaRepository calendarLabelJpaRepository;

    private final CalendarContentCommentJpaRepository calendarContentCommentJpaRepository;

    private final CustomUtils customUtils;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final JobService jobService;
    private final HttpClientService httpClientService;

    private final CalendarContentUserRelationJpaRepository calendarContentUserRelationJpaRepository;
    private final CalendarContentRecurringInfoJpaRepository calendarContentRecurringInfoJpaRepository;

    private final CommonCalendarContentJpaRepository commonCalendarContentJpaRepository;
    private final CommonCalendarSyncJpaRepository commonCalendarSyncJpaRepository;
    private final String context = "calendar";

    @Value("${puzzly.datago.decoding}")
    private String DATAGO_DECODE_KEY;
    @Value("${puzzly.datago.encoding}")
    private String DATAGO_ENCODE_KEY;

    private final String DATAGO_URI_PATH = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService";
    private final String DATAGO_PATH_HOLIDAY = "/getRestDeInfo";

    /** 초대 */
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

    /** 캘린더 */
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
        resultMap.put("calnendar", calendarResponseDto);
        return resultMap;
    }

    /** 캘린더 리스트 조회*/
    public HashMap<String, Object> getCalendarList(SecurityUser securityUser, int offset, int pageSize, boolean isDeleted){
        HashMap<String, Object> resultMap = new HashMap<>();
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        //List<CalendarResponseDto> calendarList = calendarMybatisRepository.selectCalendarList(user.getUserId(), offset, pageSize, isDeleted);
        List<CalendarResponseDto> calendarList = calendarJpaRepository.selectCalendarList(securityUser.getUser().getUserId(), offset*pageSize, pageSize, isDeleted);
        calendarList.stream().forEach((calendarResponseDto -> {
            calendarResponseDto.setUserList(userService.selectUserByCalendar(calendarResponseDto.getCalendarId(), false));
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
    //TODO 캘린더 삭제에서 컨텐츠 반복정보 삭제안됨
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
            // TODO 이렇게 삭제하지 말고, 캘린더에만 soft delete걸고 스케쥴에서 전체 처리하는 방향 고민. 대신 이경우, SELECT에서 반드시 조회가 안됨이 보장되어야함
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

    /** 컨텐트 */
    /** 캘린더 컨텐트 (일정) 생성 */
    public HashMap<String, Object> createCalendarContent(SecurityUser securityUser, CalendarContentRequestDto contentRequestDto)
        throws SchedulerException {
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
        CalendarLabel calendarLabel = calendarLabelJpaRepository.findById(contentRequestDto.getLabelId()).orElse(null);
        if(contentRequestDto.getLabelId() != null && contentRequestDto.getLabelId() != 0 && calendarLabel == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_LABEL_NOT_EXISTS", 400);
        }

        LocalDateTime notifyDate = null;

        // 다음 알림 울릴 시간
        if(contentRequestDto.getIsNotify()){
            LocalDateTime startDate = contentRequestDto.getStartDateTime();
            notifyDate = subtractTime(startDate, contentRequestDto.getNotifyIntervalUnit(), contentRequestDto.getNotifyInterval());
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
                .isRecurrable(contentRequestDto.getIsRecurrable())
                .isNotify(contentRequestDto.getIsNotify())
                .notifyIntervalUnit(contentRequestDto.getIsNotify() ? contentRequestDto.getNotifyIntervalUnit() : AlarmType.NONE)
                .notifyInterval(contentRequestDto.getIsNotify() ? contentRequestDto.getNotifyInterval() : 0)
                .notifyType(contentRequestDto.getIsNotify() ? contentRequestDto.getNotifyType() : 0)
                .notifyDate(notifyDate)
                //.notifyTime(contentRequestDto.getNotify() ? contentRequestDto.getNotifyTime() == null ? null : contentRequestDto.getNotifyTime(): null)
                .memo(contentRequestDto.getMemo())
                .label(calendarLabel)
                .build();
        calendarContentJpaRepository.save(calendarContent);
        // 캘린더 컨텐트 응답 생성
        CalendarContentResponseDto contentResponseDto = buildCalendarContentResponseDto(calendarContent, user);

        // TODO: Fe fcm key 발급 후 job 등록 테스트
//        if(contentRequestDto.getIsNotify()){
//            jobService.scheduleAlarm(calendarContent);
//        }

        // 켈린더 첨부파일 등록
        if(contentRequestDto.getCreateAttachmentsList() != null && ObjectUtils.isNotEmpty(contentRequestDto.getCreateAttachmentsList())) {
            ArrayList<CalendarContentAttachmentsResponseDto> attachmentsList = new ArrayList<>();
            contentRequestDto.getCreateAttachmentsList().forEach( attachmentsId -> {
                CalendarContentAttachments calendarContentAttachments = calendarContentAttachmentsJpaRepository.findById(attachmentsId).orElse(null);

                if(calendarContentAttachments != null) {
                    calendarContentAttachments.setCalendarContent(calendarContent);
                    calendarContentAttachmentsJpaRepository.save(calendarContentAttachments);
                    attachmentsList.add(CalendarContentAttachmentsResponseDto.builder().contentId(calendarContentAttachments.getCalendarContent().getContentId())
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
        // 반복 정보 입력
        if(calendarContent.getIsRecurrable()){
            CalendarContentRecurringInfo recurringInfo = CalendarContentRecurringInfo.builder()
                    .calendarContent(calendarContent)
                    .recurringType(contentRequestDto.getRecurringInfo().getRecurringType())
                    .period(contentRequestDto.getRecurringInfo().getPeriod())
                    .recurringDate(contentRequestDto.getRecurringInfo().getRecurringDate())
                    .recurringDay(contentRequestDto.getRecurringInfo().getRecurringDay())
                    .conditionCount(contentRequestDto.getRecurringInfo().getConditionCount())
                    .currentCount((contentRequestDto.getRecurringInfo().getConditionCount()) != null ? 0 : null)
                    .conditionEndDate((contentRequestDto.getRecurringInfo().getConditionEndDate()))
                    .isDeleted(false)
                    .build();
            //TODO EndDate랑 Count가 동시에 들어올 수도 있음, 이때 endDate == null? 로 처리하는건 부족해보임
            recurringInfo.setConditionEndDate(calculateEndConditionDate(calendarContent.getStartDateTime(), recurringInfo));
            calendarContentRecurringInfoJpaRepository.save(recurringInfo);
            contentResponseDto.setRecurringInfo(CalendarContentRecurringInfoResponseDto.builder()
                    .calendarContentId(calendarContent.getContentId())
                    .conditionCount(recurringInfo.getConditionCount())
                    .period(recurringInfo.getPeriod())
                    .recurringType(recurringInfo.getRecurringType())
                    .recurringDate(recurringInfo.getRecurringDate())
                    .recurringDay(recurringInfo.getRecurringDay())
                    .conditionEndDate(recurringInfo.getConditionEndDate()).build());
        }

        if(contentRequestDto.getCreateUserIdList() != null || ObjectUtils.isNotEmpty(contentRequestDto.getCreateUserIdList()) || !contentRequestDto.getCreateUserIdList().contains(user.getUserId())) {
            contentRequestDto.setCreateUserIdList(new ArrayList<Long>(){{
                    add(user.getUserId());
            }});
        }
        // 캘린더 참여자 입력
        if(contentRequestDto.getCreateUserIdList() != null || ObjectUtils.isNotEmpty(contentRequestDto.getCreateUserIdList())){
            List<UserResponseDto> userList = new ArrayList<>();
                contentRequestDto.getCreateUserIdList().stream().forEach((userId) -> {
                if(userService.findByUserId(userId) == null){
                    throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
                }
                User selectedUser = userService.findByUserId(userId);
                CalendarContentUserRelation contentUserRelation = CalendarContentUserRelation.builder()
                                .calendarContent(calendarContent).user(selectedUser)
                                .isDeleted(false)
                                .build();
                calendarContentUserRelationJpaRepository.save(contentUserRelation);
//                UserAttachments userAttachments = userService.selectUserAttachmentsByUser(selectedUser, false).orElse(null);
//                userList.add(UserResponseDto.builder().userId(selectedUser.getUserId()).nickName(selectedUser.getNickName()).userName(selectedUser.getUserName()).userAttachments(
//                        UserAttachmentsResponseDto.builder().attachmentsId(userAttachments != null ? userAttachments.getAttachmentsId() : null).build()
//                ).build());
            });
            contentResponseDto.setUserList(userList);
        }



        resultMap.put("content", contentResponseDto);
        return resultMap;
    }

    /** 캘린더 컨텐트(일정) 리스트 조회 */
    public HashMap<String, Object> getCalendarContentList(SecurityUser securityUser, ArrayList<Long> calendarIds, LocalDateTime startTargetDateTime, LocalDateTime limitTargetDateTime, boolean isDeleted) throws FailException{
        HashMap<String, Object> resultMap = new HashMap<>();

        if(calendarIds != null && calendarIds.size() >=0) {
            List<CalendarContentResponseDto> responseCalendarContentList = new ArrayList<>();

            /** CUSTOM 캘린더 일정 조회 */
            for (Long calendarId : calendarIds) {
                // 캘린더 존재여부 조회
                Calendar calendar = calendarJpaRepository.findById(calendarId).orElse(null);
                if (calendar == null) {
                    throw new FailException("SERVER_MESSAGE_CALENDAR_NOT_EXISTS", 400);
                }
                // 캘린더 참여여부 조회
                CalendarUserRelation calendarUserRel = calendarUserRelationJpaRepository.findCalendarUserRelation(calendar, securityUser.getUser(), false);
                if (calendarUserRel == null) {
                    throw new FailException("SERVER_MESSAGE_USER_NOT_PARTICIPATE_IN", 404);
                }

                // 캘린더 조회 -> 반복 X
                List<CalendarContentResponseDto> calendarContentList = calendarContentJpaRepository.selectCalendarContentByDateTimeAndCalendarAndIsRecurrable(securityUser.getUser().getUserId(), calendarId, startTargetDateTime, limitTargetDateTime, isDeleted, false);
                calendarContentList.forEach((calendarContent) -> {
                    calendarContent.setAttachmentsList(calendarContentAttachmentsJpaRepository.selectCalendarContentAttachmentsByContentId(calendarContent.getContentId(), false));
                    // 참가자 정보
                    calendarContent.setUserList(userService.selectUserByCalendarContentRelation(calendarContent.getContentId(), false));
/*                    // 반복정보
                    calendarContent.setRecurringInfo(calendarContentRecurringInfoJpaRepository.selectCalendarContentRecurringInfo(calendarContent.getContentId(), false));
                    responseCalendarContentList.add(calendarContent);*/
                });

                // 캘린더 조회 -> 반복 O
                List<CalendarContentResponseDto> rec = calendarContentJpaRepository.selectCalendarContentByCalendarAndIsRecurrableAndBeforeEndConditionDate(securityUser.getUser().getUserId(), calendarId, startTargetDateTime, limitTargetDateTime, isDeleted, true);
                rec.forEach((calendarContent) -> {
                    calendarContent.setAttachmentsList(calendarContentAttachmentsJpaRepository.selectCalendarContentAttachmentsByContentId(calendarContent.getContentId(), false));
                    // 참가자 정보
                    calendarContent.setUserList(userService.selectUserByCalendarContentRelation(calendarContent.getContentId(), false));
                    // 반복정보
                    calendarContent.setRecurringInfo(calendarContentRecurringInfoJpaRepository.selectCalendarContentRecurringInfo(calendarContent.getContentId(), false));
                    responseCalendarContentList.add(calendarContent);
                });

            }

            resultMap.put("contentList", responseCalendarContentList);
        }
        /** 공휴일 정보 */
        long monthDiff = startTargetDateTime.until(limitTargetDateTime, ChronoUnit.MONTHS);
            int idx = 0;
            // 기존 싱크 기록 조회
            do {
                LocalDateTime investingDateTime = startTargetDateTime.plusMonths(idx);
                int year = investingDateTime.getYear();
                int month =  investingDateTime.getMonth().getValue();
                CommonCalendarSync sync = commonCalendarSyncJpaRepository.findBySyncYearAndSyncMonth(year, month);
                // 싱크 기록 없다면 당장 싱크
                if(sync == null){
                    pullOpenCalendar(Integer.toString(year), month < 10 ? "0"+ Integer.toString(month) : Integer.toString(month));
                }
            } while(idx++ < monthDiff);
            // 싱크기록내에서 조회
                List<CommonCalendarContentResponseDto> commonCalendarContentList = commonCalendarContentJpaRepository.selectCommonContentByDateTime(startTargetDateTime, limitTargetDateTime, isDeleted);
            // 추가
            resultMap.put("commonList", commonCalendarContentList);
        //}
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

        CalendarContentResponseDto calendarContentResponseDto = calendarContentJpaRepository.selectCalendarContentByContentId(contentId, false);
        calendarContentResponseDto.setAttachmentsList(calendarContentAttachmentsJpaRepository.selectCalendarContentAttachmentsByContentId(contentId, false));
        // 참가자 정보
        calendarContentResponseDto.setUserList(userService.selectUserByCalendarContentRelation(calendarContent.getContentId(), false));
        // 반복정보
        calendarContentResponseDto.setRecurringInfo(calendarContentRecurringInfoJpaRepository.selectCalendarContentRecurringInfo(calendarContent.getContentId(), false));
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
        CalendarLabel calendarLabel = calendarLabelJpaRepository.findById(calendarContentRequestDto.getLabelId()).orElse(null);
        if(calendarContentRequestDto.getLabelId() != null && calendarContentRequestDto.getLabelId() != 0 && calendarLabel == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_LABEL_NOT_EXISTS", 400);
        }

        calendarContent.setModifyUser(user);
        calendarContent.setModifyDateTime(LocalDateTime.now());
        if(calendarContentRequestDto.getStartDateTime() != null) calendarContent.setStartDateTime(calendarContentRequestDto.getStartDateTime());
        if(calendarContentRequestDto.getEndDateTime() != null) calendarContent.setEndDateTime(calendarContentRequestDto.getEndDateTime());
        if(calendarContentRequestDto.getTitle() != null) calendarContent.setTitle(calendarContentRequestDto.getTitle());
        if(calendarContentRequestDto.getMemo() != null) calendarContent.setMemo(calendarContentRequestDto.getMemo());
        if(calendarContentRequestDto.getLocation() != null)calendarContent.setLocation(calendarContentRequestDto.getLocation());
        if(calendarContentRequestDto.getIsNotify() != null) calendarContent.setIsNotify(calendarContentRequestDto.getIsNotify());
        if(calendarContentRequestDto.getIsNotify() != null && calendarContentRequestDto.getIsNotify()) {
            // TODO: 기존 알림: NONE -> 반복 일정인 경우 notifyDate 설정 계산식 필요. 현재 null 값 들어감
            LocalDateTime originNotifyDate = plusTime(calendarContent.getNotifyDate(), calendarContent.getNotifyIntervalUnit(), calendarContent.getNotifyInterval());
            LocalDateTime notifyDate = null;
            if(originNotifyDate!=null) notifyDate = subtractTime(originNotifyDate, calendarContentRequestDto.getNotifyIntervalUnit(), calendarContentRequestDto.getNotifyInterval());
            calendarContent.setNotifyIntervalUnit(calendarContentRequestDto.getNotifyIntervalUnit());
            calendarContent.setNotifyInterval(calendarContentRequestDto.getNotifyInterval());
            calendarContent.setNotifyType(calendarContentRequestDto.getNotifyType());
            calendarContent.setNotifyDate(notifyDate);
        }else{
            calendarContent.setNotifyIntervalUnit(AlarmType.NONE);
            calendarContent.setNotifyInterval(0);
            calendarContent.setNotifyType(0);
            calendarContent.setNotifyDate(null);
        }

        if(calendarContentRequestDto.getIsStopRecurrable()) {
            calendarContent.setIsRecurrable(false);
            calendarContentRecurringInfoJpaRepository.deleteByCalendarContent(calendarContent);
        } else if (calendarContent.getIsRecurrable() && calendarContentRequestDto.getRecurringInfo() != null){
            //calendarContent.setIsRecurrable(true);
            CalendarContentRecurringInfo recurringInfo = CalendarContentRecurringInfo.builder()
                    .calendarContent(calendarContent)
                    .recurringType(calendarContentRequestDto.getRecurringInfo().getRecurringType())
                    .period(calendarContentRequestDto.getRecurringInfo().getPeriod())
                    .recurringDate(calendarContentRequestDto.getRecurringInfo().getRecurringDate())
                    .recurringDay(calendarContentRequestDto.getRecurringInfo().getRecurringDay())
                    .conditionCount(calendarContentRequestDto.getRecurringInfo().getConditionCount())
                    .currentCount((calendarContentRequestDto.getRecurringInfo().getConditionCount()) != null ? 0 : null)
                    .conditionEndDate((calendarContentRequestDto.getRecurringInfo().getConditionEndDate()))
                    .isDeleted(false)
                    .build();
            recurringInfo.setConditionEndDate(calculateEndConditionDate(calendarContent.getStartDateTime(), recurringInfo));
            calendarContentRecurringInfoJpaRepository.save(recurringInfo);
        }
        if(calendarLabel != null || calendarContentRequestDto.getLabelId() == 0) calendarContent.setLabel(calendarLabel);
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
        // 참여자 정보 변경
        if(calendarContentRequestDto.getCreateUserIdList() != null){
            calendarContentRequestDto.getCreateUserIdList().forEach((userId) -> {
                calendarContentUserRelationJpaRepository.save(CalendarContentUserRelation.builder()
                        .calendarContent(calendarContent)
                        .user(userService.findByUserId(userId))
                        .isDeleted(false)
                        .build());
            });
        }
        if(calendarContentRequestDto.getDeleteUserIdList() != null){
            calendarContentRequestDto.getDeleteUserIdList().forEach((userId) -> {
               calendarContentUserRelationJpaRepository.updateIsDeletedCalendarContentUserRelation(calendarContent, userId, true);
            });
        }
        // 반복 정보 변경
        if(calendarContent.getIsRecurrable() && calendarContentRequestDto.getRecurringInfo() != null){
            calendarContentRecurringInfoJpaRepository.deleteByCalendarContent(calendarContent);
            if(calendarContent.getIsRecurrable()){
                CalendarContentRecurringInfo recurringInfo = CalendarContentRecurringInfo.builder()
                        .calendarContent(calendarContent)
                        .recurringType(calendarContentRequestDto.getRecurringInfo().getRecurringType())
                        .period(calendarContentRequestDto.getRecurringInfo().getPeriod())
                        .recurringDate(calendarContentRequestDto.getRecurringInfo().getRecurringDate())
                        .recurringDay(calendarContentRequestDto.getRecurringInfo().getRecurringDay())
                        .conditionCount(calendarContentRequestDto.getRecurringInfo().getConditionCount())
                        .currentCount((calendarContentRequestDto.getRecurringInfo().getConditionCount()) != null ? 0 : null)
                        .conditionEndDate((calendarContentRequestDto.getRecurringInfo().getConditionEndDate()))
                        .isDeleted(false)
                        .build();

                calendarContentRecurringInfoJpaRepository.save(recurringInfo);
            }

        }


        List<CalendarContentAttachmentsResponseDto> attachmentsList =
                calendarContentAttachmentsJpaRepository.findByCalendarContentAndIsDeleted(calendarContent, false).stream().map(
                        attachments -> {
                            return CalendarContentAttachmentsResponseDto.builder().contentId(attachments.getCalendarContent().getContentId())
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
                .contentId(calendarContent.getContentId())
                .isNotify(calendarContent.getIsNotify())
                .notifyIntervalUnit(calendarContent.getNotifyIntervalUnit())
                .notifyInterval(calendarContent.getNotifyInterval())
                .notifyType(calendarContent.getNotifyType())
                .notifyDate(calendarContent.getNotifyDate())
            .labelId(calendarContent.getLabel() != null ? calendarContent.getLabel().getLabelId() : null)
                .labelName(calendarContent.getLabel() != null ? calendarContent.getLabel().getLabelName() : null)
                .colorCode(calendarContent.getLabel() != null ? calendarContent.getLabel().getColorCode() : null)
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

        // 반복정보 삭제
        calendarContentRecurringInfoJpaRepository.deleteByCalendarContent(calendarContent);
        // 참여자정보 삭제
        calendarContentUserRelationJpaRepository.deleteByCalendarContent(calendarContent);

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

    /** 캘린더 라벨 생성*/
    @Transactional
    public CalendarLabelResponseDto createCalendarLabel(SecurityUser securityUser, CalendarLabelRequestDto calendarLabelRequestDto){
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        if(StringUtils.isEmpty(StringUtils.trim(calendarLabelRequestDto.getLabelName()))){
            throw new FailException("SERVER_MESSAGE_CALENDAR_LABEL_NAME_EMPTY", 400);
        }
        if(StringUtils.isEmpty(StringUtils.trim(calendarLabelRequestDto.getColorCode()))){
            throw new FailException("SERVER_MESSAGE_CALENDAR_LABEL_COLOR_CODE_EMPTY", 400);
        }
        if(calendarLabelJpaRepository.findByLabelName(calendarLabelRequestDto.getLabelName()).isPresent()) {
            throw new FailException("SERVER_MESSAGE_CALENDAR_LABEL_NAME_DUPLICATE", 400);
        }
        if (!calendarLabelRequestDto.getColorCode().matches("^#[0-9A-Fa-f]{6}$")) {
            throw new FailException("SERVER_MESSAGE_CALENDAR_COLOR_CODE_FORMAT_DENY", 400);
        }

        Calendar calendar = calendarJpaRepository.findById(calendarLabelRequestDto.getCalendarId()).orElse(null);

        if(calendar == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_NOT_EXISTS", 400);
        }

        // max 순서
        Integer maxOrder = calendarLabelJpaRepository.getMaxOrder(Objects.requireNonNull(calendar).getCalendarId());
        if(maxOrder == null) maxOrder = 0;

        CalendarLabel calendarLabel = CalendarLabel.builder().labelName(calendarLabelRequestDto.getLabelName())
                .colorCode(calendarLabelRequestDto.getColorCode())
                .orderNum(maxOrder+1)
                .createUser(user)
                .calendar(calendar)
                .createDateTime(LocalDateTime.now())
                .build();

        // 캘린더 라벨 생성
        calendarLabelJpaRepository.save(calendarLabel);

        return CalendarLabelResponseDto.builder().labelId(calendarLabel.getLabelId())
                .labelName(calendarLabel.getLabelName())
                .colorCode(calendarLabel.getColorCode())
                .orderNum(calendarLabel.getOrderNum())
                .createId(user.getUserId())
                .createNickName(user.getNickName())
                .createDateTime(calendarLabel.getCreateDateTime())
                .build();
    }

    /** 캘린더 라벨 리스트 조회*/
    public HashMap<String, Object> getCalendarLabelList(SecurityUser securityUser, Long calendarId, int offset, int pageSize){
        HashMap<String, Object> resultMap = new HashMap<>();
        List<CalendarLabelResponseDto> calendarList = calendarLabelJpaRepository.selectCalendarLabelList(calendarId, offset*pageSize, pageSize);

        resultMap.put("calendarList", calendarList);
        return resultMap;
    }

    /** 캘린더 라벨 수정*/
    @Transactional
    public HashMap<String, Object> modifyCalendarlabel(SecurityUser securityUser, CalendarLabelRequestDto calendarLabelRequestDto){
        HashMap<String, Object> resultMap = new HashMap<>();

        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        CalendarLabel calendarLabelName = calendarLabelJpaRepository.findByLabelName(calendarLabelRequestDto.getLabelName()).orElse(null);
        if(Objects.requireNonNull(calendarLabelName).getLabelId() != calendarLabelRequestDto.getLabelId()) {
            throw new FailException("SERVER_MESSAGE_CALENDAR_LABEL_NAME_DUPLICATE", 400);
        }
        if (!calendarLabelRequestDto.getColorCode().matches("^#[0-9A-Fa-f]{6}$")) {
            throw new FailException("SERVER_MESSAGE_CALENDAR_COLOR_CODE_FORMAT_DENY", 400);
        }
        CalendarLabel calendarLabel = calendarLabelJpaRepository.findById(calendarLabelRequestDto.getLabelId()).orElse(null);
        if(calendarLabel == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_LABEL_NOT_EXISTS", 400);
        }
        if(calendarLabelRequestDto.getOrderNum() != null){
            Integer maxOrder = calendarLabelJpaRepository.getMaxOrder(calendarLabelRequestDto.getCalendarId());
            if(maxOrder == null) maxOrder = 0;

            if(calendarLabelRequestDto.getOrderNum() <= 0 || calendarLabelRequestDto.getOrderNum()>maxOrder){
                throw new FailException("SERVER_MESSAGE_CALENDAR_LABEL_ORDER_NUM_OUT_OF_LANGE", 400);
            }
        }

        // 순서 변경
        if(!Objects.equals(calendarLabel.getOrderNum(), calendarLabelRequestDto.getOrderNum())){
            // 4 -> 2 로 변경
            if(calendarLabel.getOrderNum() > calendarLabelRequestDto.getOrderNum()){
                int move = calendarLabel.getOrderNum() - calendarLabelRequestDto.getOrderNum();
                for(int i=1; i<=move; i++){
                    CalendarLabel calendarLabelOrderNum = calendarLabelJpaRepository.findByOrderNum(calendarLabel.getOrderNum()-i).orElse(null);
                    if(calendarLabelOrderNum == null){
                        throw new FailException("SERVER_MESSAGE_CALENDAR_LABEL_NOT_EXISTS", 400);
                    }
                    calendarLabelOrderNum.setOrderNum(calendarLabelOrderNum.getOrderNum()+1);
                }
            }else{
                // 4 -> 6 로 변경
                int move = calendarLabelRequestDto.getOrderNum() - calendarLabel.getOrderNum();
                for(int i=1; i<=move; i++){
                    CalendarLabel calendarLabelOrderNum = calendarLabelJpaRepository.findByOrderNum(calendarLabel.getOrderNum()+i).orElse(null);
                    if(calendarLabelOrderNum == null){
                        throw new FailException("SERVER_MESSAGE_CALENDAR_LABEL_NOT_EXISTS", 400);
                    }
                    calendarLabelOrderNum.setOrderNum(calendarLabelOrderNum.getOrderNum()-1);
                }
            }
        }

        // 수정
        calendarLabel.setModifyUser(user);
        calendarLabel.setModifyDateTime(LocalDateTime.now());
        if(calendarLabelRequestDto.getLabelName() != null) calendarLabel.setLabelName(calendarLabelRequestDto.getLabelName());
        if(calendarLabelRequestDto.getColorCode() != null) calendarLabel.setColorCode(calendarLabelRequestDto.getColorCode());
        if(calendarLabelRequestDto.getOrderNum() != null) calendarLabel.setOrderNum(calendarLabelRequestDto.getOrderNum());

        CalendarLabelResponseDto labelResponseDto = CalendarLabelResponseDto.builder()
                .labelId(calendarLabel.getLabelId())
                .labelName(calendarLabel.getLabelName())
                .colorCode(calendarLabel.getColorCode())
                .orderNum(calendarLabel.getOrderNum())
                .createId(calendarLabel.getCreateUser().getUserId())
                .createNickName(calendarLabel.getCreateUser().getNickName())
                .createDateTime(calendarLabel.getCreateDateTime())
                .modifyId(calendarLabel.getModifyUser().getUserId())
                .modifyNickName(calendarLabel.getModifyUser().getNickName())
                .modifyDateTime((calendarLabel.getModifyDateTime()))
                .build();

        resultMap.put("label", labelResponseDto);
        return resultMap;
    }

    /** 캘린더 라벨 삭제*/
    @Transactional
    public HashMap<String, Object> removeCalendarLabel(SecurityUser securityUser, Long labelId) throws FailException{
        HashMap<String, Object> resultMap = new HashMap<>();

        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }

        CalendarLabel calendarLabel = calendarLabelJpaRepository.findById(labelId).orElse(null);

        if(calendarLabel == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_LABEL_NOT_EXISTS", 400);
        }

        // max order
        Integer maxOrder = calendarLabelJpaRepository.getMaxOrder(calendarLabel.getCalendar().getCalendarId());
        if(maxOrder == null) maxOrder = 0;

        // 순서 변경
        for(int i=calendarLabel.getOrderNum()+1; i<=maxOrder; i++){
            CalendarLabel calendarLabelOrderNum = calendarLabelJpaRepository.findByOrderNum(i).orElse(null);
            if(calendarLabelOrderNum == null){
                throw new FailException("SERVER_MESSAGE_CALENDAR_LABEL_NOT_EXISTS", 400);
            }
            calendarLabelOrderNum.setOrderNum(calendarLabelOrderNum.getOrderNum()-1);
        }

        // 캘린더 라벨 삭제
        calendarLabel.setDeleteUser(user);
        calendarLabel.setDeleteDateTime(LocalDateTime.now());

        resultMap.put("labelId", labelId);
        return resultMap;
    }


    /** 캘린더 일정 댓글 생성*/
    @Transactional
    public CalendarContentCommentResponseDto createCalendarContentComment(SecurityUser securityUser, CalendarContentCommentRequestDto calendarContentCommentRequestDto){
        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        if(user == null){
            throw new FailException("SERVER_MESSAGE_USER_NOT_EXISTS", 400);
        }
        if(StringUtils.isEmpty(StringUtils.trim(calendarContentCommentRequestDto.getComment()))){
            throw new FailException("SERVER_MESSAGE_CALENDAR_CONTENT_COMMENT_EMPTY", 400);
        }

        CalendarContent calendarContent = calendarContentJpaRepository.findById(calendarContentCommentRequestDto.getContentId()).orElse(null);

        if(calendarContent == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_CONTENT_NOT_EXISTS", 400);
        }

        List<CalendarContentUserRelation> calendarContentUserRelations = calendarContentUserRelationJpaRepository.findByUserAndCalendarContent(user, calendarContent);

        if(calendarContentUserRelations.size() == 0){
            throw new FailException("SERVER_MESSAGE_CALENDAR_CONTENT_NOT_INVITATION", 400);
        }

        CalendarContentComment calendarContentComment = CalendarContentComment.builder().comment(calendarContentCommentRequestDto.getComment())
            .calendarContent(calendarContent)
            .createDateTime(LocalDateTime.now())
            .createUser(user)
            .isSystem(calendarContentCommentRequestDto.getIsSystem())
            .build();

        // 캘린더 라벨 생성
        calendarContentCommentJpaRepository.save(calendarContentComment);

        return CalendarContentCommentResponseDto.builder().commentId(calendarContentComment.getCommentId())
            .comment(calendarContentComment.getComment())
            .createId(user.getUserId())
            .createNickName(user.getNickName())
            .createDateTime(calendarContentComment.getCreateDateTime())
            .build();
    }

    /** 캘린더 일정 댓글 리스트 조회*/
    public HashMap<String, Object> getCalendarContentCommentList(SecurityUser securityUser, Long contentId, Long beforeCommentId, int pageSize){
        HashMap<String, Object> resultMap = new HashMap<>();

        User user = userService.findById(securityUser.getUser().getUserId()).orElse(null);
        CalendarContent calendarContent = calendarContentJpaRepository.findById(contentId).orElse(null);

        if(calendarContent == null){
            throw new FailException("SERVER_MESSAGE_CALENDAR_CONTENT_NOT_EXISTS", 400);
        }

        List<CalendarContentUserRelation> calendarContentUserRelations = calendarContentUserRelationJpaRepository.findByUserAndCalendarContent(user, calendarContent);

        if(calendarContentUserRelations.size() == 0){
            throw new FailException("SERVER_MESSAGE_CALENDAR_CONTENT_NOT_INVITATION", 400);
        }

        if(beforeCommentId == null || beforeCommentId == 0){
            beforeCommentId = calendarContentCommentJpaRepository.findTopByOrderByCommentIdDesc().getCommentId();
        }

        List<CalendarContentCommentResponseDto> calendarList = calendarContentCommentJpaRepository.selectCalendarContentCommentList(contentId, beforeCommentId, pageSize);

        resultMap.put("calendarList", calendarList);
        return resultMap;
    }

    @Transactional
    public void pullOpenCalendarSchedule(LocalDate currentDate, int pullingDuration) {
    // 스케쥴 캘린더 싱크
        for(int i=0; i<pullingDuration; i++){
            LocalDate targetDate = currentDate.plusMonths(i);
            int year = targetDate.getYear();
            int month = targetDate.getMonth().getValue();
            try {
                pullOpenCalendar(Integer.toString(year), month < 10 ? "0"+Integer.toString(month) : Integer.toString(month));
            }catch(FailException fe) {
                fe.printStackTrace();
                continue;
            } catch(Exception e ){
                e.printStackTrace();
            }
        }

    }
    /** 공공 API 호출 */
    public String pullOpenCalendar(String year, String month) throws FailException{
        /** Right Way to use http5Core (Migration GUIDE) */
        //HttpGet httpGet = new HttpGet(new URIBuilder())
        List<NameValuePair> params = new ArrayList<>();

        //params.add(new BasicNameValuePair("serviceKey", URLDecoder.decode(DATAGO_ENCODE_KEY, "UTF-8")));
        params.add(new BasicNameValuePair("serviceKey", DATAGO_DECODE_KEY));
        params.add(new BasicNameValuePair("solYear", year));
        params.add(new BasicNameValuePair("solMonth", month));
        params.add(new BasicNameValuePair("_type", "json"));
        params.add(new BasicNameValuePair("numOfRows", "200"));
        try {
            Map resultMap = httpClientService.httpGet(DATAGO_URI_PATH + DATAGO_PATH_HOLIDAY, params);
            Map body = MapUtils.getMap(MapUtils.getMap(resultMap, "response"), "body");
            int totalCount = MapUtils.getInteger(body, "totalCount");
            int pageSize = MapUtils.getInteger(body, "numOfRows");
            int page = MapUtils.getInteger(body, "pageNo");

            if (totalCount > 1) {
                ArrayList<Map<String, Object>> items = (ArrayList<Map<String, Object>>) MapUtils.getObject(MapUtils.getMap(body, "items"), "item");
                for (Map<String, Object> item : items) {
                    CommonCalendarContent content = CommonCalendarContent.builder()
                            .title(MapUtils.getString(item, "dateName"))
                            .startDateTime(customUtils.localDateFromNoneDashedDateString(MapUtils.getString(item, "locdate")).atStartOfDay())
                            .endDateTime(customUtils.localDateFromNoneDashedDateString(MapUtils.getString(item, "locdate")).atStartOfDay().plusDays(1).minusMinutes(1))
                            .isHoliday(MapUtils.getString(item, "isHoliday").equals("Y") ? true : false)
                            .build();
                    commonCalendarContentJpaRepository.save(content);

                }
            } else if (totalCount == 1) {
                Map item = MapUtils.getMap(MapUtils.getMap(body, "items"), "item");
                CommonCalendarContent content = CommonCalendarContent.builder()
                        .title(MapUtils.getString(item, "dateName"))
                        .startDateTime(customUtils.localDateFromNoneDashedDateString(MapUtils.getString(item, "locdate")).atStartOfDay())
                        .endDateTime(customUtils.localDateFromNoneDashedDateString(MapUtils.getString(item, "locdate")).atStartOfDay().plusDays(1).minusMinutes(1))
                        .isHoliday(MapUtils.getString(item, "isHoliday").equals("Y") ? true : false)
                        .build();
                commonCalendarContentJpaRepository.save(content);

            }
            // 기존 Sync 기록 있다면 제거
            CommonCalendarSync oldSync = commonCalendarSyncJpaRepository.findBySyncYearAndSyncMonth(Integer.valueOf(year), Integer.valueOf(month));
            if (oldSync != null) {
                commonCalendarSyncJpaRepository.delete(oldSync);
            }
            CommonCalendarSync sync = CommonCalendarSync.builder()
                    .syncDateTime(LocalDateTime.now())
                    .syncMonth(Integer.valueOf(month))
                    .syncYear(Integer.valueOf(year))
                    .build();
            commonCalendarSyncJpaRepository.save(sync);
        }catch(Exception e){
            e.printStackTrace();
            throw new FailException(e.getMessage(), 500);
        }
        return "SUCCESS";

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
                .contentId(calendarContent.getContentId())
                .isNotify(calendarContent.getIsNotify())
                .notifyIntervalUnit(calendarContent.getNotifyIntervalUnit())
                .notifyInterval(calendarContent.getNotifyInterval())
                .notifyType(calendarContent.getNotifyType())
                .notifyDate(calendarContent.getNotifyDate())
                .labelId(calendarContent.getLabel() != null ? calendarContent.getLabel().getLabelId() : null)
                .labelName(calendarContent.getLabel() != null ? calendarContent.getLabel().getLabelName() : null)
                .colorCode(calendarContent.getLabel() != null ? calendarContent.getLabel().getColorCode() : null)
                //.notifyTime(calendarContent.getNotifyTime())
                .memo(calendarContent.getMemo())
                //.calendarLabel(null)
                .build();
    }

    public Calendar getCalendarForDummy(Long calendarId){
        return calendarJpaRepository.findById(calendarId).orElse(null);
    }
    public LocalDateTime subtractTime(LocalDateTime dateTime, AlarmType type, int time) {
        switch (type) {
            case MINUTE:
                return dateTime.minusMinutes(time);
            case HOUR:
                return dateTime.minusHours(time);
            case DAY:
                return dateTime.minusDays(time);
            case NONE:
                return null;
            default:
                throw new FailException("SERVER_MESSAGE_CALENDAR_CONTENT_UNSUPPORTED_NOTIFY_INTERVAL_UNIT", 400);
        }
    }

    public LocalDateTime plusTime(LocalDateTime dateTime, AlarmType type, int time) {
        switch (type) {
            case MINUTE:
                return dateTime.plusMinutes(time);
            case HOUR:
                return dateTime.plusHours(time);
            case DAY:
                return dateTime.plusDays(time);
            case NONE:
                return null;
            default:
                throw new FailException("SERVER_MESSAGE_CALENDAR_CONTENT_UNSUPPORTED_NOTIFY_INTERVAL_UNIT", 400);
        }
    }

    public LocalDate calculateEndConditionDate(LocalDateTime startDateTime, CalendarContentRecurringInfo recurringInfo){
        logger.info("[DEV] 종료날짜 계산");
        // 종료 일정 지정시 연산 필요 X
        if(recurringInfo.getConditionEndDate() != null){
            logger.info("[DEV] 종료날짜 지정 : " + customUtils.localDateStringFromLocalDate(recurringInfo.getConditionEndDate()));
            return recurringInfo.getConditionEndDate();
        }
        int conditionCount = recurringInfo.getConditionCount();
        LocalDate endConditionDate = null;
        int endDateDayCount = 0;
        Integer period = recurringInfo.getPeriod();
        switch(recurringInfo.getRecurringType()){
            /** 일 기준 반복*/
            case "D":
                // 반복횟수 * 간격 = 반복종료까지의 날짜
                /** 연산부 */
                // 첫날도 반복 1일차
                endDateDayCount = period * (conditionCount-1);
                endConditionDate = startDateTime.toLocalDate().plusDays(endDateDayCount);

                /** 검증부 */
                logger.info("[DEV] * 일단위 * 종료날짜 계산 : " + customUtils.localDateStringFromLocalDate(endConditionDate));
                logger.info("[DEV] * 일단위 *검증");
                for(int i=0; i<conditionCount; i++){
                    logger.info(String.format("[DEV]   %d 회차 반복 : %s 일", (i+1), startDateTime.toLocalDate().plusDays(i*period)));
                }
                logger.info("[DEV] 검증 종료");
                return endConditionDate;
            /** 주 기준 반복*/
            case "W" :
                /** 연산부 */
                // 일정을 반복 수행할 요일들
                ArrayList<Integer> recurringDate = (ArrayList<Integer>) Arrays.stream(StringUtils.split(recurringInfo.getRecurringDate(), ","))
                        .map(dateString -> Integer.valueOf(dateString))
                        .sorted()
                        .collect(Collectors.toList());
                // 반복요일 중 시작날짜 요일 찾기
                int recurringDateIdx = recurringDate.indexOf(startDateTime.getDayOfWeek().getValue());
                if(recurringDateIdx == -1) {
                    // 만약 시작 날이 반복주차에 포함이 안되어있다면
                    // TODO 예외처리 따로 필요함.
                    throw new FailException("SERVER_MESSAGE_START_DATE_NOT_IN_RECURRING_DATE", 400);
                }

                // 몇주 반복해야 하는가?
                // 시작날도 1회 반복임
                int totalWeek = (conditionCount -1 ) / recurringDate.size();
                // TODO 시작날짜 + 주반복
                // 종료날짜 = 시작날짜 + (주반복)주 + (나머지 일로 발생하는 초과 주) + 나머지 요일 반복
                // 시작요일로부터 + 꽉 채운 주를 넘어서서 몇번 더 해야하는가?
                int countOverWeek = (recurringDateIdx + ((conditionCount -1) % recurringDate.size())) / recurringDate.size();
                // 시작 요일로부터 + 몇 번째 반복 요일 idx를 참조해야 하는가?
                int countOverDayIdx = (recurringDateIdx + ((conditionCount -1) % recurringDate.size())) % recurringDate.size();
                // 주기 * (몇주 반복 + 꽉 채운 주를 넘어서 다음주 넘어가는지)
                if(totalWeek == 0){
                    // 꽉 채운 주차가 없다면 ?
                    int plusDays = (period * countOverWeek * 7) - startDateTime.getDayOfWeek().getValue() + recurringDate.get(countOverDayIdx);
                    //int plusDays = (period * countOverWeek) - startDateTime.getDayOfWeek().getValue() + recurringDate.get(countOverDayIdx);
                    endConditionDate = startDateTime.toLocalDate().plusDays(plusDays);
                } else {
                    // 종료일 = 시작일 + (주 반복일) + (주차넘길경우 ? 그주의 일요일 + 초과해서 반복하는 요일 : 전주의 일요일 + 초과날 idx)
                    int plusDays = (totalWeek * period * 7) + (countOverWeek * period * 7) - startDateTime.getDayOfWeek().getValue() + recurringDate.get(countOverDayIdx);
                    //plusDays = countOverWeek == 1 ? plusDays + 7 - startDateTime.getDayOfWeek().getValue() + recurringDate.get(countOverDayIdx) : plusDays + recurringDate.get(countOverDayIdx) - startDateTime.getDayOfWeek().getValue();
                    endConditionDate = startDateTime.toLocalDate().plusDays(plusDays);
                }
                logger.info("[DEV] *주단위* 종료날짜 계산 : " + customUtils.localDateStringFromLocalDate(endConditionDate));

                /** 검증부 */
                logger.info("[DEV] *주단위* 검증");
                logger.info("[DEV] period (n주마다) : " + period);
                logger.info("[DEV] 반복 일 : " + ArrayUtils.toString(recurringDate));

                int verifyrecurringDateIdx = recurringDate.indexOf(startDateTime.getDayOfWeek().getValue());
                LocalDate verifyDate = startDateTime.toLocalDate().minusDays(startDateTime.getDayOfWeek().getValue());
                if(verifyrecurringDateIdx != -1){
                    for(int i=1; i<=conditionCount; i++){
                        // 주차가산
                        int verifyWeekCof = ((recurringDateIdx + (i-1)) / recurringDate.size()) *7;
                        // Date 가산
                        int verifyDateCof = recurringDate.get(((recurringDateIdx + (i-1))% recurringDate.size()));
                        int verifyPlusDays = verifyDateCof + verifyWeekCof * period;
                        // 시작 기준점을 그냥 전주 일요일로 잡아버리자
                        logger.info(String.format("[DEV] %d 회차 반복 : %s 일. %s", i, verifyDate.plusDays(verifyPlusDays),verifyDate.plusDays(verifyPlusDays).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREA)));
                        //logger.info(String.format("[DEV] %d 회차 반복 : %s 일. %s", i, customUtils.localDateStringFromLocalDate(startDateTime.toLocalDate().plusDays(dateCoefficient)), startDateTime.toLocalDate().plusDays(dateCoefficient).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREA)));
                    }
                    logger.info("[DEV] 검증 종료");
                } else {
                    // 만약 시작 날이 반복주차에 포함이 안되어있다면
                    // TODO 예외처리 따로 필요함.
                    throw new FailException("SERVER_MESSAGE_START_DATE_NOT_IN_RECURRING_DATE", 400);
                }
                return endConditionDate;
            /** 월 단위 반복*/
            case "M" :
                //LocalDate temporal = startDateTime.toLocalDate().plusMonths(recurringInfo.getConditionCount());
                if(recurringInfo.getPeriod().equals(0)) {
                    /** 매달 특정 일에 반복*/
                    /** 연산부 */
                    // 첫 일정 시작일도 1회차 반복임
                    endDateDayCount = conditionCount-1 ;
                    // 매 달 n 일 반복   Integer recurringDay = 달에 한번
                    endConditionDate = startDateTime.toLocalDate().plusMonths(endDateDayCount);

                    /** 검증부 */
                    logger.info("[DEV] * 달단위 특정일반복 * 종료날짜 계산 : " + customUtils.localDateStringFromLocalDate(endConditionDate));
                    logger.info("[DEV] * 달단위 *검증");
                    for(int i=0; i<conditionCount; i++){
                        logger.info(String.format("[DEV] %d 회차 반복 : %s 일. %s", (i+1), startDateTime.toLocalDate().plusMonths(i), startDateTime.toLocalDate().plusMonths(i).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREA)));
                    }
                    logger.info("[DEV] 검증 종료");
                } else {
                    /** 연산부 */
                    // 반복 횟수 == 경과 개월수, 첫 일정 수행일도 첫번째 반복
                    int totalMonth = conditionCount -1;
                    // 시작 요일
                    int startDateInteger = startDateTime.getDayOfWeek().getValue();
                    // 시작 요일의 n번째 수 반복
                    int startDateCount = (int) Math.ceil((double)startDateTime.toLocalDate().getDayOfMonth()/(double)7) -1;
                    int endDayCoefficient = 0;
                    if(startDateCount != 4) {
                        // 반복 종료 월
                        //LocalDate endMonth = LocalDate.of(startDateTime.toLocalDate().plusMonths(totalMonth).getYear(), startDateTime.toLocalDate().plusMonths(totalMonth).getMonth(), 1);
                        LocalDate endMonth = LocalDate.of(startDateTime.toLocalDate().plusMonths(totalMonth).getYear(), startDateTime.toLocalDate().plusMonths(totalMonth).getMonth(), 1);
                        // 반복 종료 월 1일의 요일
                        int endMonthdateInteger = endMonth.getDayOfWeek().getValue();
                        // 반복 종료 월의 1일로부터의 날짜계수
                        endDayCoefficient = startDateInteger < endMonthdateInteger ? 7 - (endMonthdateInteger - startDateInteger) : endMonthdateInteger == startDateInteger ? 0 : (endMonthdateInteger - startDateInteger);
                        endConditionDate = endMonth.plusDays(endDayCoefficient).plusWeeks(startDateCount);
                    } else {
                        // 5번째 일 경우 -> 마지막 해당 요일로 가야함.
                        LocalDate endMonth = LocalDate.of(startDateTime.toLocalDate().plusMonths(totalMonth).getYear(), startDateTime.toLocalDate().plusMonths(totalMonth).getMonth(), 1);
                        // 마지막 날의 요일
                        int endMonthLastdateInteger = endMonth.withDayOfMonth(endMonth.lengthOfMonth()).getDayOfWeek().getValue();
                        // 마지막 날로부터 dateIntger
                        // 마지막날이 목요일이면 월 (-3), 화(-2), 수(-1), 목(-0), 금(-6), 토(-5), 일(-4)
                        endDayCoefficient = startDateInteger < endMonthLastdateInteger ? (endMonthLastdateInteger - startDateInteger) : startDateInteger == endMonthLastdateInteger ? 0 : (11 - startDateInteger);
                        endConditionDate = LocalDate.of(endMonth.getYear(), endMonth.getMonth(), endMonth.withDayOfMonth(endMonth.lengthOfMonth()).getDayOfMonth() - endDayCoefficient);
                    }
                    logger.info("[DEV] * 달단위 특정주차 특정요일 * 종료날짜 계산 : " + customUtils.localDateStringFromLocalDate(endConditionDate));

                    /** 검증부 */
                    logger.info("[DEV] * 달단위 *검증");
                    logger.info(String.format("[DEV] %d 회차 반복 : %s 일. %s", 1, customUtils.localDateStringFromLocalDate(startDateTime.toLocalDate())), startDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREA));
                    LocalDate devVerify2 = null;
                    for(int i=2; i<conditionCount; i++){
                        devVerify2 = LocalDate.of(devVerify2.plusMonths(i-1).getYear(),devVerify2.plusMonths(i-1).getMonth(), 1);
                        int devVefDate = devVerify2.getDayOfWeek().getValue();
                        if(startDateCount != 4) {
                            devVerify2 = LocalDate.of(devVerify2.getYear(), devVerify2.getMonth(), devVerify2.plusDays(endDayCoefficient).plusWeeks(startDateCount).getDayOfMonth());
                        }else {
                            devVerify2 = LocalDate.of(devVerify2.getYear(), devVerify2.getMonth(), devVerify2.withDayOfMonth(devVerify2.lengthOfMonth()).getDayOfMonth() - endDayCoefficient);
                        }
                        // 2번째반복
                        logger.info(String.format("[DEV] %d 회차 반복, %s : %s 일. %s", i, startDateCount != 4 ? "마지막 요일에 반복" : startDateCount+" 번째 요일에 반복" ,devVerify2, devVerify2.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREA)));
                    }
                    logger.info("[DEV] 검증 종료");
                }
                return endConditionDate;
            case "Y":
                // 첫 일정일도 1회차 반복임
                endDateDayCount = conditionCount-1;
                // 매년 지정일 반복
                endConditionDate = startDateTime.toLocalDate().plusYears(recurringInfo.getConditionCount());
                logger.info("[DEV] *연단위* 종료날짜 계산 : " + customUtils.localDateStringFromLocalDate(endConditionDate));
                logger.info("[DEV] *연단위* 검증");
                for(int i=0; i<conditionCount; i++){
                    logger.info("[DEV] %d 회차 반복 : %s 일. %s", (i+1), startDateTime.plusYears(i));
                }
                return endConditionDate;
            default:
                throw new FailException("SERVER_MESSAGE_UNVERIFIABLE_RECURRING_TYPE", 400);
        }
    }

    // 검증용 인데 개발에 써도 되겠는데..
    public int getCurrentWeekOfMonth(LocalDate localDate) {
        // 한 주의 시작은 월요일이고, 첫 주에 4일이 포함되어있어야 첫 주 취급 (목/금/토/일)
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 4);

        int weekOfMonth = localDate.get(weekFields.weekOfMonth());

        // 첫 주에 해당하지 않는 주의 경우 전 달 마지막 주차로 계산
        if (weekOfMonth == 0) {
            // 전 달의 마지막 날 기준
            LocalDate lastDayOfLastMonth = localDate.with(TemporalAdjusters.firstDayOfMonth()).minusDays(1);
            return getCurrentWeekOfMonth(lastDayOfLastMonth);
        }

        // 이번 달의 마지막 날 기준
        LocalDate lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());
        // 마지막 주차의 경우 마지막 날이 월~수 사이이면 다음달 1주차로 계산
        if (weekOfMonth == lastDayOfMonth.get(weekFields.weekOfMonth()) && lastDayOfMonth.getDayOfWeek().compareTo(DayOfWeek.THURSDAY) < 0) {
            LocalDate firstDayOfNextMonth = lastDayOfMonth.plusDays(1); // 마지막 날 + 1일 => 다음달 1일
            return getCurrentWeekOfMonth(firstDayOfNextMonth);
        }
        return weekOfMonth;
        //return localDate.getMonthValue() + "월 " + weekOfMonth + "주차";
    }



}

