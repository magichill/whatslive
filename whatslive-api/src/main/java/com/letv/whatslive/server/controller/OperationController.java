package com.letv.whatslive.server.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Activity;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.dto.OperationDTO;
import com.letv.whatslive.model.dto.ProgramDTO;
import com.letv.whatslive.server.service.ActivityContentService;
import com.letv.whatslive.server.service.ActivityService;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-9-23.
 */
@Service
public class OperationController  extends BaseController{


    @Resource
    private ActivityService activityService;

    @Resource
    private ActivityContentService activityContentService;

    public ResponseBody addActivity(Map<String, Object> params, String sid, RequestHeader header) {
        Activity activity = new Activity();
        activity.setTag(ObjectUtils.toString(params.get("tag")));
        activity.setStatus(1);
        activity.setTitle(ObjectUtils.toString(params.get("title")));
        activity.setPicture(ObjectUtils.toString(params.get("picture")));
        activity.setUrl(ObjectUtils.toString(params.get("url")));
        activityService.insertActivity(activity);
        return getResponseBody(sid,"ok");
    }
    /**
     * 首页活动接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody activityList(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        if(userId == null){
            return getErrorResponse(sid, Constant.PARAMS_ERROR_CODE);
        }
        try {
            List<Activity> result = activityService.getActivityList();
            return getResponseBody(sid, result);
        }catch (Exception e){
            LogUtils.logError("fail to get message count,[exception] ", e);
            return getErrorResponse(sid,Constant.MESSAGE_ERROR_CODE);
        }
    }

    /**
     * 首页活动接口新版
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody activityListV2(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        if(userId == null){
            return getErrorResponse(sid, Constant.PARAMS_ERROR_CODE);
        }
        Integer start = ObjectUtils.toInteger(params.get("start"),1);
        Integer limit = ObjectUtils.toInteger(params.get("limit"),10);
        Long timestamp = ObjectUtils.toLong(params.get("timestamp"),0l);
        if(start == 1){
            timestamp = System.currentTimeMillis();
        }
        try {
            List<Activity> result = activityService.getActivityList(start,limit,timestamp);
            return getTimeStampResponseBody(sid,result,timestamp);
        }catch (Exception e){
            LogUtils.logError("fail to get message count,[exception] ", e);
            return getErrorResponse(sid,Constant.MESSAGE_ERROR_CODE);
        }
    }

    /**
     * 活动内的视频列表
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody activityContent(Map<String, Object> params, String sid, RequestHeader header) {
        Long now = System.currentTimeMillis();
        Integer start = ObjectUtils.toInteger(params.get("start"),1);
        Integer limit = ObjectUtils.toInteger(params.get("limit"),10);
        Long actId = ObjectUtils.toLong(params.get("actId"));
        try {
            List<Program> result = activityContentService.getProgramListByActId(actId, start, limit);
            Long timestamp = ObjectUtils.toLong(params.get("timestamp"),0l);
            Map<String,Object> data = Maps.newHashMap();
            List<ProgramDTO> programDTOList = Lists.newArrayList();
            if (result != null && result.size() > 0) {
                for (Program program : result) {
                    programDTOList.add(new ProgramDTO(program));
                }
            }
            Activity activity = activityService.getActivityById(actId);
            data.put("activity", activity);
            data.put("programList", programDTOList);
            if(start == 1){
                timestamp = now;
            }
            return getTimeStampResponseBody(sid, data,timestamp);
        }catch(Exception e){
            LogUtils.logError("fail to get activityContent,[exception] ", e);
            return getErrorResponse(sid,Constant.OTHER_ERROR_CODE);
        }

    }
}
