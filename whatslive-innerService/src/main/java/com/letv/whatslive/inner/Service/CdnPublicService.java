package com.letv.whatslive.inner.Service;

import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Activity;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.ProgramReplay;
import com.letv.whatslive.model.User;
import com.letv.whatslive.mongo.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by wangjian7 on 2015/7/20.
 */
@Component
public class CdnPublicService {
    private static final Logger logger = LoggerFactory.getLogger(CdnPublicService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private ActivityDAO activityDAO;

    @Autowired
    private ProgramReplayDAO programReplayDAO;

    @Autowired
    private ActionLogDAO actionLogDAO;

    /**
     * 更新用户头像图片
     *
     * @param uid
     * @param picUrl
     */
    public void updateUserPic(String uid, String picUrl) {
        StringBuilder message = new StringBuilder().append("userId:").append(uid).append(" pic upload ");
        try{
            User user = userDAO.getUserById(ObjectUtils.toLong(uid));
            user.setPicture(picUrl);
            userDAO.updateUser(user);
            message.append("sucess!");
            logger.info(message.toString());
        }catch (Exception e){
            message.append("failed! please check log");
            logger.error(message.toString(),e);
        }


    }

    /**
     * 更新直播封面信息
     * @param pid
     * @param picUrl
     */
    public void updateProgramPic(String pid, String picUrl){
        StringBuilder message = new StringBuilder().append("programId:").append(pid).append(" pic upload ");
        try{
            Program program = programDAO.getProgramById(ObjectUtils.toLong(pid));
            program.setPicture(picUrl);
            programDAO.updateProgram(program);
            message.append("sucess!");
            logger.info(message.toString());
        }catch (Exception e){
            message.append("failed! please check log");
            logger.error(message.toString(),e);
        }
    }

    /**
     * 更新直播回放日志信息
     * @param pid
     * @param logURL
     */
    public void updateProgramReplayLog(String pid, String logURL){
        StringBuilder message = new StringBuilder().append("programReplayId:").append(pid).append(" replay log upload ");
        try{
            ProgramReplay programReplay = programReplayDAO.queryReplayByPid(ObjectUtils.toLong(pid));
            programReplay.setLogURL(logURL);
            programReplay.setStatus(3);
            programReplayDAO.updateReplay(programReplay);
            actionLogDAO.deleteActionLogById(pid);
            message.append("sucess!");
            logger.info(message.toString());
        }catch (Exception e){
            message.append("failed! please check log");
            logger.error(message.toString(),e);
        }
    }

    /**
     * 更新活动封面信息
     * @param aid
     * @param picUrl
     */
    public void updateActivityPic(String aid, String picUrl){
        StringBuilder message = new StringBuilder().append("activityId:").append(aid).append(" pic upload ");
        try{
            Activity activity = activityDAO.getActivityById(ObjectUtils.toLong(aid));
            activity.setPicture(picUrl);
            activityDAO.updateActivity(activity);
            message.append("sucess!");
            logger.info(message.toString());
        }catch (Exception e){
            message.append("failed! please check log");
            logger.error(message.toString(),e);
        }
    }

}
