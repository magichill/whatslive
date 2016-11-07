package com.letv.whatslive.web.service.video;

import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.*;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.model.convert.MessageConvert;
import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
import com.letv.whatslive.mongo.dao.*;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.constant.ServiceConstants;
import com.letv.whatslive.web.service.common.ApiInnerService;
import com.letv.whatslive.web.util.String.StringUtils;
import com.letv.whatslive.web.util.util.MD5Util;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 直播信息操作服务
 * Created by wangjian7 on 2015/7/8.
 */
@Component
public class VideoService {

    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private ProgramReplayDAO programReplayDAO;

    @Autowired
    private SubscribeDAO subscribeDAO;

    @Autowired
    private TagDAO tagDAO;

    @Autowired
    private JedisDAO jedisDAO;

    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private MessageStatusDAO messageStatusDAO;

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
    public List<Program> getProgramListByParams(Map order, Map params, Integer start, Integer limit) {
        List<Program> programsList = programDAO.getProgramListByParams(order, params, start, limit);
        setValueToPrograms(programsList);
        return programsList;
    }

    /**
     * 根据直播查询参数获取直播id List
     * @param order 排序参数
     * @param params 查询参数
     * @return
     */
    public List<Long> getProgramIdsByParams(Map order, Map params) {
        List<Long> programsIds = programDAO.getProgramIdsByParams(params, order);
        return programsIds;
    }

    /**
     * 根据多个Id的字符串查询直播信息
     * @param ids
     * @return
     */
    public List<Program> getProgramListByIds(String ids){
        List<Program> programsList = programDAO.getProgramListByIds(ids);
        setValueToPrograms(programsList);
        return programsList;
    }

    /**
     * 根据参数查询直播的总记录数
     * @param params
     * @return
     */
    public Long countProgramByParams(Map params) {
        return programDAO.countProgramByParams(params);
    }

    /**
     * 根据直播类型获取所有的被举报的直播或者录播
     * @param pTypes
     * @return
     */
    public Long countReportProgramByPTypes (String pTypes) {
        Map params = new HashMap();
        params.put("reportNum", 0L);
        params.put("pType", pTypes);
        params.put("status", ProgramConstants.pStatus_onLine);
        return programDAO.countProgramByParams(params);
    }

    public Program getProgramById(Long id) {
        return programDAO.getProgramById(ObjectUtils.toLong(id));
    }

    /**
     * 更新直播信息
     * @param program
     * @param vPic
     * @param sPic
     * @return
     */
    public ResultBean updateProgram(Program program, String vPic, String sPic) {
        ResultBean res = ResultBean.getTrueInstance();
        program.setUpdateTime(System.currentTimeMillis());
        programDAO.updateProgram(program);
        String message = saveImg(program, vPic, sPic);
        if(null != message){
            res.setFalseAndMsg(message);
        }
        return res;

    }

    public ResultBean saveProgram(Program program, String vPic, String sPic){
        ResultBean resultBean = ResultBean.getTrueInstance();
        program.setCreateTime(System.currentTimeMillis());
        Long id = programDAO.insertProgram(program);
        program.setId(id);
        String message = saveImg(program, vPic, sPic);
        if(null != message){
            resultBean.setFalseAndMsg(message);
        }
        return resultBean;

    }

    /**
     * 根据直播ID强制下线直播信息
     * @param uid
     * @param pid
     */
    public ResultBean endLive(Long uid, Long pid){
        ResultBean res = ResultBean.getTrueInstance();
        //TODO  异常处理
        ChatEvent chatEvent = ChatEvent.createChatEvent("0", ObjectUtils.toString(pid), ServiceConstants.REDIS_TOPIC_OPERATE_FORCE_OFFLINE);
        jedisDAO.getJedisWriteTemplate().publish(ServiceConstants.REDIS_TOPIC_NAME_CHATROOM, chatEvent.toString());
        userDAO.updateDataAfterClose(uid, pid);
        programDAO.updateData(pid, 0L);
        //下线成功后需要将program的状态改为0
        Program program = programDAO.getProgramById(pid);
        program.setStatus(ProgramConstants.pStatus_offLine);
        program.setPriority(0L);
        programDAO.updateProgram(program);

        return res;
    }

    /**
     * 启动聊天室，需要往redis中写数据
     * @param program
     * @param userId
     */
    public ResultBean startChartRoom(Program program, Long userId){
        ResultBean res = ResultBean.getTrueInstance();
        //TODO　异常处理
        jedisDAO.getJedisWriteTemplate().set(Constants.LIVE_ONLINE_LIKE_KEY+program.getId(),"0");
        jedisDAO.getJedisWriteTemplate().set(Constants.LIVE_ONLINE_COMMENT_KEY+program.getId(),"0");
        jedisDAO.getJedisWriteTemplate().set(Constants.LIVE_ONLINE_REAL_LIKE_KEY+program.getId(),"0");
        jedisDAO.getJedisWriteTemplate().set(Constants.LIVE_ONLINE_REAL_COMMENT_KEY+program.getId(),"0");
        program.setStatus(ProgramConstants.pStatus_onLine);
        program.setPType(ProgramConstants.pType_live);
        program.setPriority(0L);
        program.setWatchNum(0L);
        program.setLikeNum(0L);
        program.setCommentNum(0L);
        res = this.updateProgram(program, null, null);
        return res;

    }

    /**
     * 视频下线需要向视频的订阅者发送消息
     * @param programId
     * @param message
     * @return
     */
    public ResultBean sendOffLineMessage(Long programId, Long userId, String message){
        ResultBean res = ResultBean.getTrueInstance();
        User user = userDAO.getAdminUser();
        if(user==null){
            res.setFalseAndMsg("管理员用户不存在，无法发送消息!");
            return res;
        }
        //TODO　异常处理
        cancelSubscribe(programId);
        sendMessage(user.getUserId(), userId, message);
        return res;
    }

    /**
     * 根据指定查询条件，分组条件获得指定项目的合计值。
     * @param queryParams 查询条件参数
     * @param groupParams 分组条件参数
     * @param sumColumnName 计算合计值得列名称
     * @return 以组为单位的合计值
     */
    public Map<String, Long> getTotalNumByParams(Map queryParams, List<String> groupParams, String sumColumnName) {

        // 获取合计值
        Map<String, Long> totalNumMap = programDAO.getTotalNumByParams(queryParams, groupParams, sumColumnName);

        return totalNumMap;
    }

    /**
     * 查询回放历史数据
     * @param id
     * @return
     */
    public ProgramReplay getProgramReplay(Long id){
        return programReplayDAO.queryReplayByPid(id);
    }

    private void cancelSubscribe(Long programId){
        DBObject query = new BasicDBObject();
        query.put("programId",programId);
        DBObject update = new BasicDBObject();
        update.put("status",0);
        subscribeDAO.update(query,new BasicDBObject("$set",update));
    }

    private void sendMessage(Long fromId,Long recId,String content){
        Long mId = insertMessage(fromId, content);
        insertStatus(mId,recId);
    }

    private Long insertMessage(Long fromId,String content){
        Message message = new Message();
        message.setSenderId(fromId);
        message.setContent(content);
        message.setStatus(1);
        message.setType(2);
        message.setCreateTime(System.currentTimeMillis());
        message.setUpdateTime(System.currentTimeMillis());
        DBObject mObj = MessageConvert.castMessageToDBObject(message);
        Long mId = messageDAO.insert(mObj);
        return mId;
    }

    private void insertStatus(Long mId,Long recId){
        MessageStatus mStatus = new MessageStatus();
        mStatus.setMid(new DBRef("message",mId));
        mStatus.setRecId(recId);
        mStatus.setStatus(Constants.MESSAGE_READ_NO);
        mStatus.setCreateTime(System.currentTimeMillis());
        mStatus.setUpdateTime(System.currentTimeMillis());
        DBObject sObj = MessageConvert.castMessageStatusToDBObject(mStatus);
        messageStatusDAO.insert(sObj);
    }

    /**
     * 保存直播封面信息
     * @param program
     * @param vPic
     * @param sPic
     * @return
     */
    private String saveImg(Program program, String vPic, String sPic){
        String message = null;
        if (!StringUtils.isBlank(vPic) && !StringUtils.isBlank(sPic)) {
            String pictureUrl = null;
            File file = new File(vPic);
            if (file.exists()) {
                //生成上传图片的key值，前缀+文件MD5值
                String md5= MD5Util.fileMd5(file);
                String key = apiInnerService.getAbstractUploadService()
                        .getKey(ServiceConstants.UPLOAD_CODE_PROGRAM_PIC, md5, ObjectUtils.toString(program.getId()));
                if(!apiInnerService.getAbstractUploadService().uploadFile(md5, file.length(), vPic, sPic, key)){
                    message = "封面图片上传服务器失败！请重新修改!";
                }else{
                    //如果使用的是AWS_S3服务则需要将用户头像设置为AWS_S3的URL
                    if(ServiceConstants.FILE_UPLOAD_TYPE_AWS_S3.equals(ServiceConstants.FILE_UPLOAD_TYPE)){
                        program.setPicture(ServiceConstants.AWS_S3_URL_PREX+key);
                    }else{

                    }

                }
            } else {
                message = "封面图片文件不存在！";
            }
        }
        return message;

    }

    /**
     * 设置programsList中的值，包括：
     * 1.对于直播的视频从redis中获取programs要在前端显示的数据，目前主要是观看人数和点赞数
     * 2.对于预约的视频从预约记录中获取预约人数
     * @param programsList
     */
    private void setValueToPrograms(List<Program> programsList) {
        for(Program program : programsList){
            //如果是查询结果中有直播视频需要从redis中获取观看人数和点赞数
            if(ProgramConstants.pType_live.equals(program.getPType())){
                program.setWatchNum(jedisDAO.getJedisReadTemplate().llen(Constants.LIVE_ONLINE_USER_LIST_KEY+ program.getId()));
                Long likeNum = jedisDAO.getJedisReadTemplate().getAsLong(Constants.LIVE_ONLINE_LIKE_KEY + program.getId());
                Long commentNum = jedisDAO.getJedisReadTemplate().getAsLong(Constants.LIVE_ONLINE_COMMENT_KEY + program.getId());
                program.setLikeNum(likeNum==null?0L:likeNum);
                program.setCommentNum(commentNum==null?0L:commentNum);
            }else if(ProgramConstants.pType_order.equals(program.getPType()) || ProgramConstants.pType_order_recommend.equals(program.getPType())){
                program.setOrderNum(subscribeDAO.countSubscribeByProgramId(program.getId()));
                if(null != program.getTagList() && program.getTagList().size()>0){
                    Long tagId= program.getTagList().get(0);
                    Tag tag = tagDAO.getTagById(tagId);
                    program.setTagId(tagId);
                    program.setTagValue(tag.getValue());
                }


            }
        }
    }
}
