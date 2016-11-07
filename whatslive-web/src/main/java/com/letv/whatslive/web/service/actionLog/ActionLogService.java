package com.letv.whatslive.web.service.actionLog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.*;
import com.letv.whatslive.model.redis.chat.protocol.LoginEvent;
import com.letv.whatslive.mongo.dao.*;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.constant.ServiceConstants;
import com.letv.whatslive.web.constant.WebConstants;
import com.letv.whatslive.web.service.common.ApiInnerService;
import com.letv.whatslive.web.util.String.StringUtils;
import com.letv.whatslive.web.util.util.MD5Util;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by wangjian7 on 2015/10/10.
 */
@Component
public class ActionLogService {
    private static final Logger logger = LoggerFactory.getLogger(ActionLogService.class);

    @Autowired
    private ActionLogDAO actionLogDAO;

    @Autowired
    ProgramDAO programDAO;

    @Autowired
    private ProgramReplayDAO programReplayDAO;

    @Autowired
    private ApiInnerService apiInnerService;

    @Autowired
    private JedisDAO jedisDAO;

    @Autowired
    private UserDAO userDAO;

    private int everyQueryNum = 1000;


    /**
     * 根据直播id获取直播的回放活动并生成文件上传cdn
     * @param id
     * @return
     */
    public ResultBean createProgramActionLogFile(Long id, long programStartTime) {
        ResultBean resultBean = ResultBean.getTrueInstance();
        Map<String, Object> params = Maps.newHashMap();
        params.put("roomId", id);
        Long actionNum = actionLogDAO.countActionLogByParams(params);
        if(actionNum>0){
            logger.info("program have actionLog count: "+actionNum + " id:" +id);
            try {
                //创建日志文件
                Long createTime = System.currentTimeMillis();
                String fileName = String.format(WebConstants.UPLOAD_ACTION_LOG, id, createTime);
                File logFile = new File(WebConstants.UPLOAD_PATH_ROOT + fileName);
                List<String> data = Lists.newArrayList();
                //写入用户列表信息
                data.add("part_start_1");
                Set<String> userSet = jedisDAO.getJedisReadTemplate().smembers(Constants.LIVE_ONLINE_TOTALUSER_KEY+id);
                String programUserJson = getProgramUserJson(id);
                if(null != programUserJson){
                    userSet.add(programUserJson);
                }
                Map<String, JSONObject> userMap = Maps.newHashMap();
                Iterator<String> setIt = userSet.iterator();
                while (setIt.hasNext()) {
                    JSONObject jsonObject = JSON.parseObject(setIt.next());
                    jsonObject.remove("date");
                    userMap.put(jsonObject.getString("uid"), jsonObject);
                }
                Iterator<String> mapIt = userMap.keySet().iterator();
                while (mapIt.hasNext()) {
                    data.add(userMap.get(mapIt.next()).toString());
                }
                data.add("part_end_1");

                //写入用户行为信息
                data.add("part_start_2");
                Map<String, Object> orders = Maps.newHashMap();
                orders.put("date",1);
                long queryTime = actionNum%everyQueryNum==0?actionNum/everyQueryNum:actionNum/everyQueryNum+1;

                for(int i=0;i<queryTime;i++){
                    List<String> actionLogList = actionLogDAO.getActionLogJsonStrListByParams(orders, params,
                            i * everyQueryNum, everyQueryNum, programStartTime);
                    data.addAll(actionLogList);
                }
                data.add("part_end_2");
                FileUtils.writeLines(logFile, "UTF-8", data);
                ProgramReplay programReplay = saveOrUpdateProgramReplay(id, 1);
                if(null == saveLogFile(programReplay, WebConstants.UPLOAD_PATH_ROOT + fileName,
                        WebConstants.UPLOAD_SERVER_HOST + fileName)){
                    saveOrUpdateProgramReplay(id, 2);
                }else{
                    saveOrUpdateProgramReplay(id, 4);
                }

            } catch (IOException e) {
                logger.error("program write actionLog to file error! " + id, e);
                saveOrUpdateProgramReplay(id, 4);
            }
        }else{
            saveOrUpdateProgramReplay(id, 5);
            logger.info("program have no actionLog! "+ id);
        }

        return resultBean;
    }

    private ProgramReplay saveOrUpdateProgramReplay(Long id, int status){
        ProgramReplay programReplay = programReplayDAO.queryReplayByPid(id);
        if(null != programReplay){
            programReplay.setPid(id);
            programReplay.setStatus(status);
            programReplayDAO.updateReplay(programReplay);
        }else{
            programReplay = new ProgramReplay();
            programReplay.setPid(id);
            programReplay.setStatus(status);
            programReplay.setId(programReplayDAO.insertReplay(programReplay));
        }
        return programReplay;
    }

    private String getProgramUserJson(Long id){
        Program program = programDAO.getProgramById(id);
        if(null != program){
            User user = userDAO.getUserById(program.getUserId());
            if(null != user){
                LoginEvent loginEvent = new LoginEvent(user.getUserId().toString(), user.getPicture(), user.getNickName(), 0L);
                return JSON.toJSONString(loginEvent);
            }
        }
        return null;


    }

    /**
     * 保存直播回放信息
     * @param programReplay
     * @param vLogFile
     * @param slogFile
     * @return
     */
    private String saveLogFile(ProgramReplay programReplay, String vLogFile, String slogFile){
        String message = null;
        if (!StringUtils.isBlank(vLogFile) && !StringUtils.isBlank(vLogFile)) {
            String pictureUrl = null;
            File file = new File(vLogFile);
            if (file.exists()) {
                //生成上传图片的key值，前缀+文件MD5值
                String md5= MD5Util.fileMd5(file);
                String key = apiInnerService.getAbstractUploadService()
                        .getKey(ServiceConstants.UPLOAD_CODE_PROGRAM_REPLAY_LOG, md5, ObjectUtils.toString(programReplay.getPid()));
                if(!apiInnerService.getAbstractUploadService().uploadFile(md5, file.length(), vLogFile, slogFile, key)){
                    message = "日志文件上传服务器失败！请重新修改!";
                }else{
                    //如果使用的是AWS_S3服务则需要将用户头像设置为AWS_S3的URL
                    if(ServiceConstants.FILE_UPLOAD_TYPE_AWS_S3.equals(ServiceConstants.FILE_UPLOAD_TYPE)){
                        programReplay.setLogURL(ServiceConstants.AWS_S3_URL_PREX+key);
                    }else{

                    }
                }
            } else {
                message = "日志文件不存在！";
            }
        }
        if(null != message){
            logger.error("programReplay upload log file error! program id:" + programReplay.getPid());
        }
        return message;

    }

}
