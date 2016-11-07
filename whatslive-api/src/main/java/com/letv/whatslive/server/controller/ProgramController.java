package com.letv.whatslive.server.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.DateUtils;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.common.utils.StringUtil;
import com.letv.whatslive.model.*;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.model.convert.UserConvert;
import com.letv.whatslive.model.dto.ProgramDTO;
import com.letv.whatslive.model.dto.RemindDTO;
import com.letv.whatslive.model.dto.UserDTO;
import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
import com.letv.whatslive.model.redis.push.PushEvent;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.server.live.ApplyLive;
import com.letv.whatslive.server.live.StreamLive;
import com.letv.whatslive.server.service.*;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-7-3.
 */
@Service
public class ProgramController extends BaseController{

    @Resource
    private ProgramService programService;

    @Resource
    private UserService userService;

    @Resource
    private LikeVideoService likeVideoService;

    @Resource
    private ChatService chatService;

    @Resource
    private SubscribeService subscribeService;

    @Resource
    private ReportService reportService;

    @Resource
    private MessageService messageService;

    @Resource
    private FriendService friendService;

    @Resource
    private ActivityService activityService;

    @Resource
    private ActivityContentService activityContentService;

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private JedisDAO jedisDAO;

//    @Resource(name = "redisTemplate")
//    private ValueOperations<String, Object> valOps;

    /**
     * 节目列表
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody programList(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
        Integer type = ObjectUtils.toInteger(params.get("type"));
        params.put("userId",userId);
        Long now = System.currentTimeMillis();
        if(userId==null || start == null || limit == null || type == null){
            return getErrorResponse(sid,"params is invalid!");
        }
        try {
            List<Program> programList = programService.list(params);
            String edition = ObjectUtils.toString(header.getEditionId());

            int editionNum = 0;
            String from = ObjectUtils.toString(header.getFrom(),"1");
            if (edition != null){
                if (edition.indexOf(".") > -1) {
                    edition = edition.replace(".", "");
                    edition = edition + "0";
                }
                editionNum = ObjectUtils.toInteger(edition, 0);
            }
            //大于1.1.0版本视频列表增加一条活动消息
            if(editionNum >= 1100 && from.equals("2")) {
                if (type.intValue() == 1 && start.intValue() == 1) {
                    List<Activity> alist = activityService.getActivityList();
                    if (alist != null && alist.size() > 0) {
                        Program p = new Program();
                        Activity activity = alist.get(0);
                        p.setPType(4);
                        p.setPicture(activity.getPicture());
                        p.setPName(activity.getTag());
                        programList.add(0, p);
                    }
                }
            }
            DBObject query = new BasicDBObject();
            query.put("userId", userId);
            query.put("status", 1);
            List<Long> subscribeIds = subscribeService.getAllSubscribeId(query);
            List<ProgramDTO> data = Lists.newArrayList();
            if (programList != null && programList.size() > 0) {
                for (Program program : programList) {
                    ProgramDTO dto = new ProgramDTO(program);
//                    dto.setShareUrl(Constant.INNER_SHARE_URL+program.getId());
                    if (dto.getPType() == Constant.VIDEO_SUBSCRIBE || dto.getPType() == Constant.VIDEO_RECOMMAND) {
                        if (subscribeIds.indexOf(dto.getId()) > -1) {
                            dto.setStatus(1);
                        } else {
                            dto.setStatus(0);
                        }
                        if(ObjectUtils.toLong(dto.getStartTime(),0l)>=now){
                            if(dto.getUser().getUserId().longValue() == userId.longValue()) {
                                dto.setIsSelf(1);
                            }else{
                                dto.setIsSelf(0);
                            }
                            data.add(dto);
                        }else{
                            programService.cancelProgram(dto.getId());
                            subscribeService.cancelSubscribe(dto.getId());
                            Map<Long,DBObject> userMap = subscribeService.getAllUser(dto.getId());
                            User adminUser = userService.getAdminUser();
                            if(adminUser!=null) {
                                messageService.userSendMessage(adminUser.getUserId(), dto.getUser().getUserId(), "您未在指定时间开启预设直播" + dto.getPName() + "（" + DateUtils.long2YMDHM(ObjectUtils.toLong(dto.getStartTime())) + "），该预设直播已被取消！");
                                for (Map.Entry<Long, DBObject> entry : userMap.entrySet()) {
                                    messageService.userSendMessage(adminUser.getUserId(),entry.getKey(),"您预约的直播:"+dto.getPName()+"已经取消哦！");
                                }
                            }

                        }
                    }else{
                        data.add(dto);
                    }

                }
            }
            Long timestamp = ObjectUtils.toLong(params.get("timestamp"),0l);
            if(start == 1){
                timestamp = now;
            }
            return getTimeStampResponseBody(sid, data,timestamp);
        }catch (Exception e){
            e.printStackTrace();
            return getErrorResponse(sid,e.getMessage());
        }
    }

    /**
     *  获取直播分享id
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody generateProgramId(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        if(userId == null || !checkUser(userId)){
            return getErrorResponse(sid,Constant.USER_INVALID_ERROR_CODE);
        }
        Program program = new Program();
        program.setUserId(userId);
        program.setPType(-1);
        program.setStatus(0);
        try {
            long programId = programService.createEmptyProgram(program);
            Map<String, Object> result = Maps.newHashMap();
            result.put("programId", programId);
            result.put("shareUrl", ProgramConstants.SHARE_URL+programId);
            return getResponseBody(sid, result);
        }catch (Exception e){
            LogUtils.logError("fail to generate empty programId,[exception] "+e.getMessage());
            return getErrorResponse(sid,Constant.CREATE_PROGRAM_ERROR_CODE);
        }
    }

    /**
     * 发起续播推流直播
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody createContinueProgram(Map<String, Object> params, String sid, RequestHeader header) {
        String liveName = ObjectUtils.toString(params.get("liveName"));
        String location = ObjectUtils.toString(params.get("location"),"");
        Integer from = ObjectUtils.toInteger(header.getFrom());
        Long userId = ObjectUtils.toLong(header.getUserId());
        Long programId = ObjectUtils.toLong(params.get("programId"));
        String city = ObjectUtils.toString(params.get("city"),"");
        String province = ObjectUtils.toString(params.get("province"),"");
        //增加活动id
        Long actId = ObjectUtils.toLong(params.get("actId"));
        if(userId == null){
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        if(!checkUser(userId)){
            return getErrorResponse(sid,Constant.USER_INVALID_ERROR_CODE);
        }
        Program program = new Program();
        if(programId != null){
            program = programService.getEmtpyProgramById(programId);
        }
        String desc = ObjectUtils.toString(params.get("desc"));
        Long startTime = System.currentTimeMillis();
        Long endTime = DateUtils.getDateRange(1);

        program.setPName(liveName);
        program.setUserId(userId);
        program.setPType(1);
        program.setPDesc(desc);
        program.setStartTime(startTime);
        program.setEndTime(endTime);
        program.setFrom(from);
        program.setLocation(location);
        program.setIsContinue(1);
        program.setProvince(province);
        program.setCity(city);

        ApplyLive applyLive = new ApplyLive();
        String activityId = "";
        try {
            activityId = applyLive.createLive(liveName, DateUtils.long2DateTime(startTime), DateUtils.long2DateTime(endTime), desc);
            if(StringUtils.isBlank(activityId)){
                LogUtils.logError("fail to create activityId,activityId = "+activityId);
                return getErrorResponse(sid,Constant.CREATE_PROGRAM_ERROR_CODE);
            }
            program.setActivityId(activityId);
            program.setStatus(1);

        }catch (Exception e){
            LogUtils.logError("fail to create activityId",e);
            return getErrorResponse(sid,Constant.CREATE_PROGRAM_ERROR_CODE);
        }

        try{
            Long streamId = 0l;
            if(programId == null) {
                streamId = programService.createProgram(program);
            }else{
                streamId = programId;
                boolean success = programService.startEmptyProgram(program);
                if(!success){
                    LogUtils.logError("stream is not end! userId="+userId);
                    return getErrorResponse(sid,Constant.LIVE_NOT_END_ERROR_CODE);
                }
            }
            if(streamId==null){
                LogUtils.logError("stream is not end! userId="+userId);
                return getErrorResponse(sid,Constant.LIVE_NOT_END_ERROR_CODE);
            }
            User user = userService.getUserById(userId);
            chatService.generateCode(ObjectUtils.toString(streamId),user);
            Map<String,Object> data = Maps.newHashMap();

            StreamLive streamLive = new StreamLive();
            String pushUrl = streamLive.getPushStreamAddress(activityId);
            data.put("pushurl",ObjectUtils.toString(pushUrl,""));

            data.put("roomId",streamId);
            data.put("chatServer",Constant.CHAT_SERVER);
            //将直播加入活动
            if(actId != null) {
                ActivityContent activityContent = new ActivityContent();
                activityContent.setActId(actId);
                activityContent.setProgramId(streamId);
                activityContent.setStatus(1);
                activityContent.setPriority(0L);
                activityContentService.insertActivityContent(activityContent);
            }
            String picture = ObjectUtils.toString(program.getPicture(),"");
            if(StringUtils.isBlank(picture) || picture.indexOf("g3.letv.cn")>=0) {
                data.put("uploadPic", 0);
            }else{
                data.put("uploadPic",1);
            }
            applyLive = null;
            streamLive = null;
            return getResponseBody(sid,data);
        }catch(Exception e){
            LogUtils.logError("fail to create chatroom,userId=="+userId+" [exception] :",e);
            return getErrorResponse(sid,Constant.CREATE_PROGRAM_ERROR_CODE);
        }
    }
    /**
     * 发起直播
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody createProgram(Map<String, Object> params, String sid, RequestHeader header) {
        String liveName = ObjectUtils.toString(params.get("liveName"));
        String location = ObjectUtils.toString(params.get("location"),"");
        Integer from = ObjectUtils.toInteger(header.getFrom());
        Long userId = ObjectUtils.toLong(header.getUserId());
        Long programId = ObjectUtils.toLong(params.get("programId"));
        //增加活动id
        Long actId = ObjectUtils.toLong(params.get("actId"));
        if(userId == null){
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        if(!checkUser(userId)){
            return getErrorResponse(sid,Constant.USER_INVALID_ERROR_CODE);
        }
        Program program = new Program();
        if(programId != null){
            program = programService.getEmtpyProgramById(programId);
        }
        String desc = ObjectUtils.toString(params.get("desc"));
        Long startTime = System.currentTimeMillis();
        Long endTime = DateUtils.getDateRange(1);

        program.setPName(liveName);
        program.setUserId(userId);
        program.setPType(1);
        program.setPDesc(desc);
        program.setStartTime(startTime);
        program.setEndTime(endTime);
        program.setFrom(from);
        program.setLocation(location);

        ApplyLive applyLive = new ApplyLive();
        String activityId = "";
        try {
            activityId = applyLive.createLive(liveName, DateUtils.long2DateTime(startTime), DateUtils.long2DateTime(endTime), desc);
            if(StringUtils.isBlank(activityId)){
                LogUtils.logError("fail to create activityId,activityId = "+activityId);
                return getErrorResponse(sid,Constant.CREATE_PROGRAM_ERROR_CODE);
            }
            program.setActivityId(activityId);
            program.setStatus(1);

        }catch (Exception e){
            LogUtils.logError("fail to create activityId",e);
            return getErrorResponse(sid,Constant.CREATE_PROGRAM_ERROR_CODE);
        }

        try{
            Long streamId = 0l;
            if(programId == null) {
                streamId = programService.createProgram(program);
            }else{
                streamId = programId;
                boolean success = programService.startEmptyProgram(program);
                if(!success){
                    LogUtils.logError("stream is not end! userId="+userId);
                    return getErrorResponse(sid,Constant.LIVE_NOT_END_ERROR_CODE);
                }
            }
            if(streamId==null){
                LogUtils.logError("stream is not end! userId="+userId);
                return getErrorResponse(sid,Constant.LIVE_NOT_END_ERROR_CODE);
            }
            User user = userService.getUserById(userId);
//            user.setBroadCastNum(user.getBroadCastNum()+1);
//            userService.saveOrUpdateUser(user);
            chatService.generateCode(ObjectUtils.toString(streamId),user);
            Map<String,Object> data = Maps.newHashMap();

            StreamLive streamLive = new StreamLive();
            String pushUrl = streamLive.getPushStreamAddress(activityId);
            data.put("pushurl",ObjectUtils.toString(pushUrl,""));
//            data.put("pushurl", Constant.PUSH_STREAM_URL+userId);

            data.put("roomId",streamId);
            data.put("chatServer",Constant.CHAT_SERVER);
            data.put("activityId",activityId);
            //将直播加入活动
            if(actId != null) {
                ActivityContent activityContent = new ActivityContent();
                activityContent.setActId(actId);
                activityContent.setProgramId(streamId);
                activityContent.setStatus(1);
                activityContent.setPriority(0L);
                activityContentService.insertActivityContent(activityContent);
            }
            String picture = ObjectUtils.toString(program.getPicture(),"");
            if(StringUtils.isBlank(picture) || picture.indexOf("g3.letv.cn")>=0) {
                data.put("uploadPic", 0);
            }else{
                data.put("uploadPic",1);
            }
            applyLive = null;
            streamLive = null;
            return getResponseBody(sid,data);
        }catch(Exception e){
            LogUtils.logError("fail to create chatroom,userId=="+userId+" [exception] :",e);
            return getErrorResponse(sid,Constant.CREATE_PROGRAM_ERROR_CODE);
        }
    }

    /**
     * 观看者预约直播
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody subscribeProgram(Map<String, Object> params, String sid, RequestHeader header) {
        Long programId = ObjectUtils.toLong(params.get("programId"));
        Long userId = ObjectUtils.toLong(header.getUserId());
        Integer status = ObjectUtils.toInteger(params.get("status"));
        Map<String,Object> checkParam = Maps.newHashMap();
        checkParam.put("programId",programId);
        checkParam.put("status",status);
        checkParam.put("userId",userId);
        String validateResult = validateParams(checkParam);
        if(validateResult != null){
            LogUtils.logError(validateResult);
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        if(!checkUser(userId)){
            return getErrorResponse(sid,Constant.USER_INVALID_ERROR_CODE);
        }
        Subscribe subscribe = new Subscribe();
        subscribe.setUserId(userId);
        subscribe.setStatus(status);
        subscribe.setProgramId(programId);
        try{
            subscribeService.saveSubscribe(subscribe);
            return getResponseBody(sid,"ok");
        }catch(Exception e){
            LogUtils.logError("fail to subscribe program,[exception] ",e);
        }
        return getErrorResponse(sid,Constant.SUBSCRIBE_ERROR_CODE);
    }

    /**
     * 换台接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody switchProgram(Map<String, Object> params, String sid, RequestHeader header) {
        Program program = programService.getRandomProgram();
        if(program == null){
            return getErrorResponse(sid,Constant.LIVE_NULL_ERROR_CODE);
        }
        try {
            return getResponseBody(sid, new ProgramDTO(program));
        }catch (Exception e){
            return getErrorResponse(sid,Constant.OTHER_ERROR_CODE);
        }
    }

    /**
     * 用户预约直播列表
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody planProgramListByUser(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        if(userId == null || !checkUser(userId)){
            LogUtils.logError("[planProgramListByUser],userId could not be null");
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        Map<String,Object> data = Maps.newHashMap();
        try {
            Long now = System.currentTimeMillis();
            User adminUser = userService.getAdminUser();
            List<ProgramDTO> programList = programService.getPlanProgramByUserId(userId);
            List<ProgramDTO> result = Lists.newArrayList();
            for(ProgramDTO dto : programList) {
                if (ObjectUtils.toLong(dto.getStartTime(), 0l) >= now) {
                    result.add(dto);
                }else{
                    programService.cancelProgram(dto.getId());
                    subscribeService.cancelSubscribe(dto.getId());
                    Map<Long, DBObject> userMap = subscribeService.getAllUser(dto.getId());
                    if (adminUser != null) {
                        messageService.userSendMessage(adminUser.getUserId(), dto.getUser().getUserId(), "您未在指定时间开启预设直播" + dto.getPName() + "（" + DateUtils.long2YMDHM(ObjectUtils.toLong(dto.getStartTime())) + "），该预设直播已被取消！");
                        for (Map.Entry<Long, DBObject> entry : userMap.entrySet()) {
                            messageService.userSendMessage(adminUser.getUserId(), entry.getKey(), "您预约的直播:" + dto.getPName() + "已经取消哦！");
                        }
                    }
                }
            }
            data.put("count", result.size());
            data.put("list", result);
            return getResponseBody(sid, data);
        }catch (Exception e){
            return getErrorResponse(sid,e.getMessage());
        }
    }

    /**
     * 开始预约直播
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody startPlanProgram(Map<String,Object> params,String sid,RequestHeader header){
        Long programId = ObjectUtils.toLong(params.get("programId"));
        Long userId = ObjectUtils.toLong(header.getUserId());
        if(programId == null || userId == null || !checkUser(userId)){
            return getErrorResponse(sid,"params is invalid");
        }
        Program program = programService.getProgramById(programId);
        if(program == null){
            return getErrorResponse(sid,Constant.LIVE_NULL_ERROR_CODE);
        }
        //切换到云直播平台
        ApplyLive apply = new ApplyLive();
        String activityId = apply.createLive(program.getPName(),DateUtils.long2DateTime(System.currentTimeMillis()),DateUtils.long2DateTime(program.getEndTime()),program.getPDesc());
        StreamLive streamLive = new StreamLive();
        String pushUrl = "";
        try {
            pushUrl = streamLive.getPushStreamAddress(activityId);
        } catch (Exception e) {
            LogUtils.logError("fail to get pushUrl,[exception]:",e);
            return getErrorResponse(sid,Constant.PUSH_URL_ERROR_CODE);
        }
        //
        Map<Long,DBObject> users = subscribeService.getAllUser(programId);
        if(programService.checkStartProgram(userId)) {
            programService.startPlanProgram(programId, userId,activityId);
            PushEvent pushEvent = new PushEvent();
            pushEvent.setAction(1);
            pushEvent.setProgramId(programId);
            jedisDAO.getJedisWriteTemplate().publish(Constants.DEFAULT_PUSH_MESSSAGE_EVENT,pushEvent.toString());
            User user = userService.getUserById(userId);
            user.setBroadCastNum(user.getBroadCastNum() + 1);
            userService.saveOrUpdateUser(user);
            chatService.generateCode(ObjectUtils.toString(programId), user);
            Map<String, Object> data = Maps.newHashMap();
            data.put("pushurl", ObjectUtils.toString(pushUrl,""));
//            data.put("pushurl", Constant.PUSH_STREAM_URL + userId);
            data.put("roomId", programId);
            data.put("chatServer", Constant.CHAT_SERVER);
            data.put("activityId",activityId);
            String picture = ObjectUtils.toString(program.getPicture(),"");
            if(StringUtils.isBlank(picture) || picture.indexOf("g3.letv.cn")>=0) {
                data.put("uploadPic", 0);
            }else{
                data.put("uploadPic",1);
            }
            return getResponseBody(sid, data);
        }else{
            return getErrorResponse(sid,Constant.LIVE_NOT_END_ERROR_CODE);
        }
    }

    /**
     * 预约过期结束
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody closePlanProgram(Map<String,Object> params,String sid,RequestHeader header){
        Long programId = ObjectUtils.toLong(params.get("programId"));
        Long userId = ObjectUtils.toLong(header.getUserId());
        Map<String,Object> checkParam = Maps.newHashMap();
        checkParam.put("programId",programId);
        checkParam.put("userId",userId);
        String validateResult = validateParams(checkParam);
        if(validateResult != null){
            return getErrorResponse(sid,validateResult);
        }
        Program program = programService.getProgramById(programId);
        if(program == null){
            return getErrorResponse(sid,Constant.LIVE_NULL_ERROR_CODE);
        }
        try {
            programService.closePlanProgram(programId, userId);
            ApplyLive applyLive = new ApplyLive();
            applyLive.stopLiveActivityById(program.getActivityId());
            return getResponseBody(sid, "ok");
        }catch (Exception e){
            LogUtils.logError("fail to close plan program, [exception] ",e);
            return getErrorResponse(sid,e.getMessage());
        }
    }
    /**
     * 发起预约直播接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody planProgram(Map<String, Object> params, String sid, RequestHeader header) {
        String liveName = ObjectUtils.toString(params.get("liveName"));
        Long userId = ObjectUtils.toLong(header.getUserId());
        String desc = ObjectUtils.toString(params.get("desc"));
        Long now = System.currentTimeMillis();
        Long startTime = ObjectUtils.toLong(params.get("startTime"));
        Long endTime = ObjectUtils.toLong(params.get("endTime"));
        Long programId = ObjectUtils.toLong(params.get("programId"));
        String province = ObjectUtils.toString(params.get("province"));
        String city = ObjectUtils.toString(params.get("city"));
        if(!checkUser(userId)){
            return getErrorResponse(sid,Constant.USER_INVALID_ERROR_CODE);
        }

        Program program = new Program();
        if(programId != null){
            program = programService.getEmtpyProgramById(programId);
        }

        Map<String,Object> checkParam = Maps.newHashMap();
        checkParam.put("liveName",liveName);
        checkParam.put("startTime",startTime);
        checkParam.put("endTime",endTime);
        checkParam.put("userId",userId);
        String validateResult = validateParams(checkParam);
        if(validateResult != null){
            LogUtils.logError(validateResult);
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        if(now.longValue() >= startTime.longValue()){
            return getErrorResponse(sid,Constant.PLAN_STARTTIME_ERROR_CODE);
        }
        if(endTime.longValue() <= startTime.longValue()){
            return getErrorResponse(sid,Constant.PLAN_ENDTIME_ERROR_CODE);
        }

        program.setPName(liveName);
        program.setUserId(userId);
        program.setPType(2);
        program.setPDesc(desc);
        program.setStartTime(startTime);
        program.setEndTime(endTime);
        program.setStatus(1);
        program.setIsShow(0);
        program.setCity(city);
        program.setProvince(province);
        Long streamId = 0l;
        try {

            if(programId == null) {
                streamId = programService.createPlanProgram(program);
            }else{
                streamId = programId;
                boolean success = programService.createEmptyPlanProgram(program);
                if(!success){
                    LogUtils.logError("fail to create plan program,same start time.");
                    return getErrorResponse(sid,Constant.PLAN_REPEAT_ERROR_CODE);
                }
            }
            if(streamId == -1l){
                LogUtils.logError("fail to create plan program,same start time.");
                return getErrorResponse(sid,Constant.PLAN_REPEAT_ERROR_CODE);
            }
            LogUtils.commonLog("user create a plan program,streamId:"+streamId);
        }catch (Exception e){
            LogUtils.logError("预约节目失败！[exception]",e);
            return getErrorResponse(sid,Constant.SUBSCRIBE_ERROR_CODE);
        }
        Map result = Maps.newHashMap();
        result.put("programId",streamId);
        return getResponseBody(sid,result);
    }

    /**
     * 取消直播预约接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody cancelProgram(Map<String, Object> params, String sid, RequestHeader header) {
        Long programId = ObjectUtils.toLong(params.get("programId"));
        Long userId = ObjectUtils.toLong(header.getUserId());

        if(programId == null || userId == null){
            LogUtils.logError("cancelProgram params are invalid");
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        if(!checkUser(userId)){
            return getErrorResponse(sid,Constant.USER_INVALID_ERROR_CODE);
        }
        Program planProgram = programService.getPlanProgramByUserId(programId,userId);
        if(planProgram != null){
            programService.cancelProgram(programId);
            subscribeService.cancelSubscribe(programId);
            Map<Long,DBObject> userMap = subscribeService.getAllUser(programId);
            for (Map.Entry<Long, DBObject> entry : userMap.entrySet()) {
                String nickName = planProgram.getUserRef().fetch()!=null?ObjectUtils.toString(planProgram.getUserRef().fetch().get("nickName")):"";
                messageService.userSendMessage(userId,entry.getKey(),"您预约的"+nickName+"的直播:"+planProgram.getPName()+"已经被"+nickName+"取消！");
            }
            return getResponseBody(sid,"ok");
        }else{
            LogUtils.logError("program is not exist! programId = "+programId);
            return getErrorResponse(sid,Constant.LIVE_NULL_ERROR_CODE);
        }
    }

    public ResponseBody playUrl(Map<String, Object> params, String sid, RequestHeader header) {
        Map<String,Object> result = Maps.newHashMap();
        String userId = header.getUserId();
        result.put("playUrl",Constant.PULL_STREAM_URL+userId);
        result.put("streamId","239-whatslive-"+userId);
        return getResponseBody(sid,result);
    }

    /**
     * 更新节目封面
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody updateProgramPic(Map<String, Object> params, String sid, RequestHeader header) {
        Long programId = ObjectUtils.toLong(params.get("programId"));
        String amzonKey = ObjectUtils.toString(params.get("picKey"));
        Program program = programService.getProgramIngoreStatusById(programId);
        if(program != null){
            program.setPicture(amzonKey);
            boolean status = programService.updateProgramPic(program);
            if(status){
                return getResponseBody(sid,"ok");
            }else{
                return getErrorResponse(sid,"mongodb update fail");
            }
        }else{
            return getErrorResponse(sid,"program is null");
        }


    }
    /**
     * 获取直播节目信息
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody getProgram(Map<String, Object> params, String sid, RequestHeader header) {
        Long id = ObjectUtils.toLong(params.get("id"));
        if(id == null){
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        Program program = programService.getProgramIngoreStatusById(id);

        if(program == null || program.getStatus().intValue()!=1){
            Map<String,Object> result = Maps.newHashMap();
            result.put("error",Constant.LIVE_NULL_ERROR_CODE);
            if(program!=null) {
                result.put("user", new UserDTO(UserConvert.castDBObjectToUser(program.getUserRef().fetch())));
            }
            return getResponseBody(sid,result);
        }else{
            if(program.getPType() == 1) {
                program.setLikeNum(ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_LIKE_KEY + id), 0l));
                program.setWatchNum(ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().llen(Constants.LIVE_ONLINE_USER_LIST_KEY + id)));
                program.setCommentNum(ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_COMMENT_KEY + id), 0l));
            }
            if(program.getPType() == 3){
                program.setActionLogUrl(programService.replayFileUrl(program.getId()));
            }
            ProgramDTO result = new ProgramDTO(program);
//            result.setShareUrl(Constant.SHARE_URL+program.getId());
            Long userId = ObjectUtils.toLong(header.getUserId());
            Integer status = friendService.getFriendShipByUserId(userId,program.getUserId());
            result.setIsFocus(status);
            if(userId != null){
                result.setIsReport(reportService.checkReport(userId,id));
            }
            return getResponseBody(sid,result);
        }

    }

    /**
     * 节目点赞接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody likeProgram(Map<String, Object> params, String sid, RequestHeader header) {
        long userId = ObjectUtils.toLong(header.getUserId());
        long vid = ObjectUtils.toLong(params.get("vid"));
        int status =  ObjectUtils.toInteger(params.get("stauts")); //点赞状态，1点赞，-1取消点赞
        if(!checkUser(userId)){
            return getErrorResponse(sid,Constant.USER_INVALID_ERROR_CODE);
        }
        try{
            programService.incLikeNum(vid,status);
            LikeVideo likeVideo = likeVideoService.getLikeVideo(userId, vid);
            likeVideo.setStatus(status);
            likeVideoService.insert(likeVideo);
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.logError("fail to like program");
            return getResponseBody(sid,"fail");
        }
        return getResponseBody(sid,"ok");
    }

    /**
     * 关闭直播
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody closeProgram(Map<String, Object> params, String sid, RequestHeader header) {
        String userId = header.getUserId();
        Long roomId = ObjectUtils.toLong(params.get("roomId"));
        Map<String,Object> checkParam = Maps.newHashMap();
        checkParam.put("roomId",roomId);
        checkParam.put("userId",userId);
        String validateResult = validateParams(checkParam);
        if(validateResult != null){
            LogUtils.logError(validateResult);
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        try {
            Program program = programService.getProgramById(roomId);
            if(program != null) {
                if(program.getPType() == 3 || StringUtils.isNotBlank(program.getLiveId())){
                    LogUtils.logError("该类型无需关闭流");
                    return getResponseBody(sid,"ok");
                }
                String videoId = "";
                int status = -1;  //录播转码状态  -1正在转码 -2表示直播失败
                int duration = DateUtils.long2Seconds(System.currentTimeMillis() - program.getStartTime());
                if(duration < 30){
                    status = -2;
                }
                programService.closeProgram(ObjectUtils.toLong(roomId),status);
                if(StringUtils.isNotBlank(program.getActivityId())) {
                    if(duration > 30) {
                        ApplyLive applyLive = new ApplyLive();
                        applyLive.stopLiveActivityById(program.getActivityId());
                    }
                }
                userService.updateUserAfterClose(ObjectUtils.toLong(userId), ObjectUtils.toLong(roomId),videoId);
                jedisDAO.getJedisWriteTemplate().publish("chatroom", ChatEvent.createChatEvent(userId, ObjectUtils.toString(roomId), 3).toString());
                return getResponseBody(sid, "ok");
            }else{
                return getErrorResponse(sid,Constant.LIVE_NULL_ERROR_CODE);
            }
        }catch (Exception e){
            LogUtils.logError("fail to close program,[exception] ",e);
            return getErrorResponse(sid,Constant.SERVER_ERROR_CODE);
        }
    }

    /**
     * 用户结束直播返回节目数据
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody programEnd(Map<String, Object> params, String sid, RequestHeader header) {
        Map<String,Object> data = Maps.newHashMap();
        String programId = ObjectUtils.toString(params.get("programId"));
        if(programId == null){
            return getErrorResponse(sid,"programId could not be null");
        }
        try {
            Program program = programService.getEndProgramById(ObjectUtils.toLong(programId));
            if (program == null) {
                data.put("like", 0);
                data.put("duration", "00:00:00");
                data.put("comments", 0);
                data.put("viewers", 0);
                data.put("title","");
                data.put("picture", com.letv.whatslive.model.utils.Constant.DEFAULT_PICTURE);
            } else {
                String duration = DateUtils.continueTime(program.getStartTime(), program.getEndTime());
                data.put("like", ObjectUtils.toInteger(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_LIKE_KEY + programId), 0));
                data.put("duration", duration);
                data.put("comments", ObjectUtils.toInteger(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_COMMENT_KEY + programId), 0));
                data.put("viewers", jedisDAO.getJedisReadTemplate().smembers(Constants.LIVE_ONLINE_TOTALUSER_KEY + programId).size());
                data.put("title",program.getPName());
                data.put("picture",program.getPicture());
            }
            return getResponseBody(sid, data);
        }catch (Exception e){
            LogUtils.logError("fail to get program end",e);
            return getErrorResponse(sid,Constant.OTHER_ERROR_CODE);
        }
    }

    /**
     * 用户发布直播列表接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody privatePrograms(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        Long currentId = ObjectUtils.toLong(params.get("currentId"));
        Integer start = ObjectUtils.toInteger(params.get("start"),1);
        Integer limit = ObjectUtils.toInteger(params.get("limit"),10);
        if(currentId == null){
            currentId = userId;
        }
        try {
            List<ProgramDTO> data = programService.getProgramByUserId(start, limit, currentId,3);
            DBObject query = new BasicDBObject();
            query.put("userId", ObjectUtils.toLong(userId));
            query.put("status", 1);
            List<Long> subscribeIds = subscribeService.getAllSubscribeId(query);
            List<ProgramDTO> result = Lists.newArrayList();
            if (data != null && data.size() > 0) {
                for (ProgramDTO dto : data) {
                    if (dto.getPType() == Constant.VIDEO_SUBSCRIBE) {
                        if (subscribeIds.indexOf(dto.getId()) > -1) {
                            dto.setStatus(1);
                        } else {
                            dto.setStatus(0);
                        }
                    }
                    if (dto != null) {
                        result.add(dto);
                    }
                }
            }
            return getResponseBody(sid, result);
        }catch (Exception e){
            LogUtils.logError("fail to get private program ",e);
            return getErrorResponse(sid,Constant.OTHER_ERROR_CODE);
        }
    }

    /**
     * 拉取用户预约提醒
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody pushSchedule(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        List<ProgramDTO> plans = programService.getPlanProgramByUserId(userId);
        DBObject query = new BasicDBObject();
        query.put("userId",userId);
        List<DBObject> subs = subscribeService.getStartSubscribe(userId);
        Long now = System.currentTimeMillis();
        List<RemindDTO> planList = Lists.newArrayList();
        for(ProgramDTO dto : plans){
            Long leftTime = ObjectUtils.toLong(dto.getStartTime())-now;
            if(leftTime <= Constant.REMIND_TIME){
                RemindDTO remind = new RemindDTO();
                remind.setPName(dto.getPName());
                remind.setProgramId(ObjectUtils.toLong(dto.getId()));
                remind.setStartTime(dto.getStartTime());
                planList.add(remind);
            }
        }
        List<RemindDTO> subList = Lists.newArrayList();
        for(DBObject dbObject : subs){
            RemindDTO remindDTO = new RemindDTO();
            remindDTO.setPName(ObjectUtils.toString(dbObject.get("pName")));
            remindDTO.setProgramId(ObjectUtils.toLong(dbObject.get("_id")));
//            remindDTO.setStreamId("239-whatslive-"+ObjectUtils.toString(dbObject.get("userId")));
            remindDTO.setStreamId(ObjectUtils.toString(dbObject.get("activityId")));
            remindDTO.setStartTime(ObjectUtils.toString(dbObject.get("startTime")));
            subList.add(remindDTO);
        }
        Map<String,Object> result = Maps.newHashMap();
        result.put("planList",planList);
        result.put("subList",subList);
        return getResponseBody(sid,result);
    }

    /**
     * 用户自己的录播视频
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody myReplay(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        Long currentId = ObjectUtils.toLong(params.get("userId"));
        Integer start = ObjectUtils.toInteger(params.get("start"), 1);
        Integer limit = ObjectUtils.toInteger(params.get("limit"), 10);
        String edition = ObjectUtils.toString(header.getEditionId());

        int editionNum = 0;
        if (edition != null){
            if (edition.indexOf(".") > -1) {
                edition = edition.replace(".", "");
                edition = edition + "0";
            }
            editionNum = ObjectUtils.toInteger(edition, 0);
        }

        if(userId == null){
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        if(currentId != null){
            userId = currentId;
        }
        try {
            List<ProgramDTO> result = Lists.newArrayList();
            if(editionNum < 1100){
                result = programService.getProgramByUserId(start, limit, userId, 3);
            }else{
                result = programService.getReplayByUserId(start,limit,userId);
            }

            return getResponseBody(sid, result);
        }catch (Exception e){
            LogUtils.logError("my replay exception:",e);
            return getErrorResponse(sid,Constant.REPLAY_ERROR_CODE);
        }
    }

    /**
     * 删除录播接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody removeReplay(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        Long programId = ObjectUtils.toLong(params.get("programId"));
        if(userId == null || programId == null){
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        try {
            Program program = programService.getReplayProgramByUserId(programId,userId);
            if(program != null){
                subscribeService.cancelSubscribe(programId);
                programService.cancelProgram(programId);
            }
            return getResponseBody(sid, 1);
        }catch (Exception e){
            LogUtils.logError("fail to remove replay ",e);
            return getErrorResponse(sid,Constant.REPLAY_ERROR_CODE);
        }
    }

    /**
     * 用户订阅及预约更新
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody updateCount(Map<String, Object> params, String sid, RequestHeader header) {
        Long timestamp = System.currentTimeMillis();
        Long userId = ObjectUtils.toLong(header.getUserId());
        if(userId == null){
            LogUtils.logError("updateCount api,userId is null");
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        Integer start = 1;
        Integer limit = 100;
        Map paramMap = Maps.newHashMap();
        paramMap.put("start",start);
        paramMap.put("limit",limit);
        paramMap.put("userId",userId);
        paramMap.put("timestamp",timestamp);

        Map result = programService.updateCount(paramMap);
        return getResponseBody(sid,result);
    }

    /**
     * 推荐列表
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody recommend(Map<String, Object> params, String sid, RequestHeader header) {
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
        Integer type = ObjectUtils.toInteger(params.get("type"));
        Long timestamp = ObjectUtils.toLong(params.get("timestamp"));
        List<Program> programList = programService.recommendList(start,limit,type,timestamp);
        Long userId = ObjectUtils.toLong(params.get("userId"));
        Long now = System.currentTimeMillis();
        if (type.intValue() == 1 && start.intValue() == 1) {
            List<Activity> alist = activityService.getActivityList();
            if (alist != null && alist.size() > 0) {
                for(Activity a : alist) {
                    Program p = new Program();
                    p.setPType(4);
                    p.setPicture(a.getPicture());
                    p.setPName(a.getTag());
                    programList.add(0, p);
                }
            }
        }
        DBObject query = new BasicDBObject();
        query.put("userId", userId);
        query.put("status", 1);
        List<Long> subscribeIds = subscribeService.getAllSubscribeId(query);
        List<ProgramDTO> data = Lists.newArrayList();
        if (programList != null && programList.size() > 0) {
            for (Program program : programList) {
                ProgramDTO dto = new ProgramDTO(program);
                if (dto.getPType() == Constant.VIDEO_SUBSCRIBE || dto.getPType() == Constant.VIDEO_RECOMMAND) {
                    if (subscribeIds.indexOf(dto.getId()) > -1) {
                        dto.setStatus(1);
                    } else {
                        dto.setStatus(0);
                    }
                    if(ObjectUtils.toLong(dto.getStartTime(),0l)>=now){
                        if(dto.getUser().getUserId().longValue() == userId.longValue()) {
                            dto.setIsSelf(1);
                        }else{
                            dto.setIsSelf(0);
                        }
                        data.add(dto);
                    }else{
                        programService.cancelProgram(dto.getId());
                        subscribeService.cancelSubscribe(dto.getId());
                        Map<Long,DBObject> userMap = subscribeService.getAllUser(dto.getId());
                        User adminUser = userService.getAdminUser();
                        if(adminUser!=null) {
                            messageService.userSendMessage(adminUser.getUserId(), dto.getUser().getUserId(), "您未在指定时间开启预设直播" + dto.getPName() + "（" + DateUtils.long2YMDHM(ObjectUtils.toLong(dto.getStartTime())) + "），该预设直播已被取消！");
                            for (Map.Entry<Long, DBObject> entry : userMap.entrySet()) {
                                messageService.userSendMessage(adminUser.getUserId(),entry.getKey(),"您预约的直播:"+dto.getPName()+"已经取消哦！");
                            }
                        }

                    }
                }else{
                    data.add(dto);
                }

            }
        }
        if(start == 1){
            timestamp = now;
        }
        return getTimeStampResponseBody(sid, data,timestamp);
    }

    /**
     * 同城节目列表
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody cityProgramList(Map<String, Object> params, String sid, RequestHeader header) {
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
        String city = ObjectUtils.toString(params.get("city"));
        String province = ObjectUtils.toString(params.get("province"));
        if(start == null || limit == null || city == null || province == null){
            return getErrorResponse(sid,"params could not be null");
        }
        List<ProgramDTO> result = programService.getProgramListByCity(start,limit,city,province);
        return getResponseBody(sid,result);
    }

    /**
     * 搜索视频列表接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody programSearchList(Map<String, Object> params, String sid, RequestHeader header) {
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
        Long timestamp = ObjectUtils.toLong(params.get("timestamp"));
        String keyword = ObjectUtils.toString(params.get("keyword"));
        List<ProgramDTO> result = programService.searchProgramList(start,limit,keyword,timestamp);
        if(start == 1){
            timestamp = System.currentTimeMillis();
        }
        return getTimeStampResponseBody(sid,result,timestamp);
    }

    /**
     * 获取活动状态接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody getActivityStatus(Map<String, Object> params, String sid, RequestHeader header) {
        String activityId = ObjectUtils.toString(params.get("activityId"));
        ApplyLive applyLive = new ApplyLive();
        Map<String,Object> result = applyLive.getActivityStatus(activityId);
        if(result != null) {
            return getResponseBody(sid, result);
        }else{
            return getErrorResponse(sid,"activityId ==="+activityId+", no result");
        }
    }
}
