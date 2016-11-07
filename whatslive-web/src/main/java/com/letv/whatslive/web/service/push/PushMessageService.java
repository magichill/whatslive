package com.letv.whatslive.web.service.push;

import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.model.Message;
import com.letv.whatslive.model.MessageStatus;
import com.letv.whatslive.model.PushMessage;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.convert.MessageConvert;
import com.letv.whatslive.mongo.dao.MessageDAO;
import com.letv.whatslive.mongo.dao.MessageStatusDAO;
import com.letv.whatslive.mongo.dao.UserDAO;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.mongo.dao.PushMessageDAO;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/7/28.
 */
@Component
public class PushMessageService {
    @Autowired
    private PushMessageDAO pushMessageDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private MessageStatusDAO messageStatusDAO;

    /**
     * 根据条件查询所有推送消息
     *
     * @param params 模糊查询的查询条件
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @return 满足条件的对象的集合
     */
    public List<PushMessage> getPushMessageListByParams(Map params, Integer start, Integer limit) {
        return pushMessageDAO.getPushMessageListByParams(params, start, limit);
    }

    /**
     * 根据指定条件查询推送消息的数量
     *
     * @param params 模糊查询的查询条件
     * @return 满足条件的结果记录数
     */
    public Long countPushMessageByParams(Map params) {
        return pushMessageDAO.countPushMessageByParams(params);
    }


    public PushMessage getPushMessageById(Long kid) {
        return pushMessageDAO.getPushMessageById(kid);
    }

    public ResultBean insertPushMessage(PushMessage pushMessage){
        ResultBean res = ResultBean.getTrueInstance();
        String message = pushMessageDAO.savePushMessage(pushMessage);
        if(null!=message){
            res.setFalseAndMsg(message);
        }
        return res;
    }

    public void updatePushMessage(PushMessage pushMessage) {
        pushMessageDAO.updatePushMessage(pushMessage);
    }

    public void deletePushMessage(Long kid){
        pushMessageDAO.deletePushMessage(kid);
    }

    public ResultBean pushMessage(String message){
        ResultBean res = ResultBean.getTrueInstance();
        User user = userDAO.getAdminUser();
        if(user==null){
            res.setFalseAndMsg("管理员用户不存在，无法发送消息!");
            return res;
        }
        sendMessage(user.getUserId(),message);
        return res;
    }

    private void sendMessage(Long fromId, String content){
        Long mId = insertMessage(fromId, content);
    }

    private Long insertMessage(Long fromId,String content){
        Message message = new Message();
        message.setSenderId(fromId);
        message.setContent(content);
        message.setStatus(1);
        message.setType(1);
        message.setCreateTime(System.currentTimeMillis());
        message.setUpdateTime(System.currentTimeMillis());
        DBObject mObj = MessageConvert.castMessageToDBObject(message);
        Long mId = messageDAO.insert(mObj);
        return mId;
    }


}
