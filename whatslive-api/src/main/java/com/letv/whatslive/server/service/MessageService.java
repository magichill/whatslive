package com.letv.whatslive.server.service;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Message;
import com.letv.whatslive.model.MessageStatus;
import com.letv.whatslive.model.convert.MessageConvert;
import com.letv.whatslive.mongo.dao.MessageDAO;
import com.letv.whatslive.mongo.dao.MessageStatusDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by gaoshan on 15-8-7.
 */
@Service
public class MessageService {

    @Resource
    private MessageDAO messageDAO;

    @Resource
    private MessageStatusDAO messageStatusDAO;

    public void userSendMessage(Long fromId,Long recId,String content){
//        Message message = new Message();
//        message.setSenderId(fromId);
//        message.setContent(content);
//        message.setStatus(1);
//        message.setType(2);
//        message.setCreateTime(System.currentTimeMillis());
//        message.setUpdateTime(System.currentTimeMillis());
//        DBObject mObj = MessageConvert.castMessageToDBObject(message);
//        Long mId = messageDAO.insert(mObj);
        Long mId = insertMessage(fromId,content);

//        MessageStatus mStatus = new MessageStatus();
//        mStatus.setMid(new DBRef("message",mId));
//        mStatus.setRecId(recId);
//        mStatus.setStatus(Constants.MESSAGE_READ_NO);
//        mStatus.setCreateTime(System.currentTimeMillis());
//        mStatus.setUpdateTime(System.currentTimeMillis());
//        DBObject sObj = MessageConvert.castMessageStatusToDBObject(mStatus);
//        messageStatusDAO.insert(sObj);
        insertStatus(mId,recId);
    }

    public Integer newMessageCount(Long userId){
        Integer count = 0;
        DBObject order = new BasicDBObject();
        order.put("createTime",-1);
        DBObject query = new BasicDBObject();
        query.put("recId",userId);
        //查询系统消息
        DBObject sysQuery = new BasicDBObject();
        sysQuery.put("type",1);
        sysQuery.put("status",1);
        List<DBObject> sysMessageList = messageDAO.findAll(sysQuery,order);
        for(DBObject sys : sysMessageList){
            DBObject q = new BasicDBObject();
            q.put("recId",userId);
            q.put("mId.$id",ObjectUtils.toLong(sys.get("_id")));
            DBObject obj = messageStatusDAO.find(q);
            if(obj == null){
                insertStatus(ObjectUtils.toLong(sys.get("_id")),userId);
            }
        }
        query.put("status",Constants.MESSAGE_READ_NO);
        List<DBObject> statusList = messageStatusDAO.selectAll(query,order);
        if(statusList != null){
            count = statusList.size();
        }

        return count;
    }

    public List<MessageStatus> messageList(Long userId,Integer start,Integer limit){
        if(start == 1){
            DBObject query = new BasicDBObject();
            query.put("recId",userId);
            query.put("status",Constants.MESSAGE_READ_NO);
            DBObject update = new BasicDBObject();
            update.put("status",Constants.MESSAGE_READ_ALREADY);
            messageStatusDAO.update(query,new BasicDBObject("$set",update));
        }
        List<DBObject> messageList = messageStatusDAO.getAllMessage(userId,start,limit);
        List<MessageStatus> result = Lists.newArrayList();
        for(DBObject dbObject : messageList){
            MessageStatus status = MessageConvert.castDBObjectToMessageStatus(dbObject);
            result.add(status);
        }
        return result;
    }

    public void insertStatus(Long mId,Long recId){
        MessageStatus mStatus = new MessageStatus();
        mStatus.setMid(new DBRef("message",mId));
        mStatus.setRecId(recId);
        mStatus.setStatus(Constants.MESSAGE_READ_NO);
        mStatus.setCreateTime(System.currentTimeMillis());
        mStatus.setUpdateTime(System.currentTimeMillis());
        DBObject sObj = MessageConvert.castMessageStatusToDBObject(mStatus);
        messageStatusDAO.insert(sObj);
    }

    public Long insertMessage(Long fromId,String content){
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
}
