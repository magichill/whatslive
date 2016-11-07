package com.letv.whatslive.web.service.activity;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.common.utils.PriorityUtils;
import com.letv.whatslive.model.Activity;
import com.letv.whatslive.model.ActivityContent;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.convert.ActivityContentConvert;
import com.letv.whatslive.mongo.dao.ActivityContentDAO;
import com.letv.whatslive.mongo.dao.ActivityDAO;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.constant.ServiceConstants;
import com.letv.whatslive.web.service.common.ApiInnerService;
import com.letv.whatslive.web.util.String.StringUtils;
import com.letv.whatslive.web.util.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/10/10.
 */
@Component
public class ActivityService {
    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    @Autowired
    private ActivityDAO activityDAO;

    @Autowired
    private ActivityContentDAO activityContentDAO;

    @Autowired
    private ApiInnerService apiInnerService;

    /**
     * 根据直播查询参数获取直播列表
     * @param order 排序参数
     * @param params 查询参数
     * @param start 查询开始记录行数
     * @param limit 本次要查询的记录数
     * @return
     */
    public List<Activity> getActivityListByParams(Map order, Map params, Integer start, Integer limit) {
        List<Activity> programsList = activityDAO.getActivityListByParams(order, params, start, limit);
        return programsList;
    }

    /**
     * 根据参数查询活动的总记录数
     * @param params
     * @return
     */
    public Long countActivityByParams(Map params) {
        return activityDAO.countActivityByParams(params);
    }

    public Activity getActivityById(Long aid) {
        return activityDAO.getActivityById(aid);
    }

    /**
     * 更新直播信息
     * @param activity
     * @param vPic
     * @param sPic
     * @return
     */
    public ResultBean updateActivity(Activity activity, String vPic, String sPic) {
        ResultBean res = ResultBean.getTrueInstance();
        activityDAO.updateActivity(activity);
        String message = saveImg(activity, vPic, sPic);
        if(null != message){
            res.setFalseAndMsg(message);
        }
        return res;

    }

    public ResultBean saveActivity(Activity activity, String vPic, String sPic) {
        ResultBean res = ResultBean.getTrueInstance();
        Long id = activityDAO.insertActivity(activity);
        activity.setId(id);
        String message = saveImg(activity, vPic, sPic);
        if(null != message){
            res.setFalseAndMsg(message);
        }
        return res;

    }

    public List<Long> getProgramIdsByActivityId(Long activityId, Integer start, Integer limit){
        Map<String, Object> queryMaps = Maps.newHashMap();
        queryMaps.put("actId", activityId);
        Map<String, Object> orderMaps = Maps.newLinkedHashMap();
        orderMaps.put("priority", -1);
        orderMaps.put("createTime", -1);
        return activityContentDAO.getProgramIdsByParams(queryMaps , orderMaps, start, limit);
    }

    public ActivityContent getActivityContentByActIdAndPId(Long activityId, Long programId){
        Map<String, Object> queryParams = Maps.newHashMap();
        queryParams.put("actId", activityId);
        queryParams.put("programId", programId);
        return activityContentDAO.getActivityContent(queryParams);
    }

    public Long countProgramIdsByActivityId(Long activityId){
        Map<String, Object> queryMaps = Maps.newHashMap();
        queryMaps.put("actId", activityId);
        return activityContentDAO.countProgramIdsByParams(queryMaps);
    }

    public void saveActivityContent(Long actId, boolean first, Program program){
        ActivityContent activityContent = new ActivityContent();
        activityContent.setActId(actId);
        activityContent.setProgramId(program.getId());
        activityContent.setStatus(1);
        activityContent.setPriority(PriorityUtils.getPriority(program.getPType(), first ,program.getStartTime()));
        activityContentDAO.insertActivityContent(activityContent);
    }

    public void deleteActivityContent(Long actId, Long programId){
        Map<String, Object> queryParams = Maps.newHashMap();
        queryParams.put("actId", actId);
        queryParams.put("programId", programId);
        activityContentDAO.delActivityContent(queryParams);
    }

    /**
     * 保存活动封面信息
     * @param activity
     * @param vPic
     * @param sPic
     * @return
     */
    private String saveImg(Activity activity, String vPic, String sPic){
        String message = null;
        if (!StringUtils.isBlank(vPic) && !StringUtils.isBlank(sPic)) {
            File file = new File(vPic);
            if (file.exists()) {
                //生成上传图片的key值，前缀+文件MD5值
                String md5= MD5Util.fileMd5(file);
                String key = apiInnerService.getAbstractUploadService()
                        .getKey(ServiceConstants.UPLOAD_CODE_ACTIVITY_PIC, md5, ObjectUtils.toString(activity.getId()));
                if(!apiInnerService.getAbstractUploadService().uploadFile(md5, file.length(), vPic, sPic, key)){
                    message = "封面图片上传服务器失败！请重新修改!";
                }else{
                    //如果使用的是AWS_S3服务则需要将封面设置为AWS_S3的URL
                    if(ServiceConstants.FILE_UPLOAD_TYPE_AWS_S3.equals(ServiceConstants.FILE_UPLOAD_TYPE)){
                        activity.setPicture(ServiceConstants.AWS_S3_URL_PREX+key);
                    }else{

                    }
                }
            } else {
                message = "封面图片文件不存在！";
            }
        }
        return message;

    }
}
