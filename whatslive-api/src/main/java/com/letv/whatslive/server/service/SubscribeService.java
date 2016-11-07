package com.letv.whatslive.server.service;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Subscribe;
import com.letv.whatslive.model.convert.SubscribeConvert;
import com.letv.whatslive.model.utils.ObjUtils;
import com.letv.whatslive.mongo.dao.SubscribeDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-7-23.
 */
@Service
public class SubscribeService {

    private final static Logger logger = LoggerFactory.getLogger(SubscribeService.class);

    @Resource
    private SubscribeDAO subscribeDAO;

    public void saveSubscribe(Subscribe subscribe){
        Subscribe target = checkSubscribe(subscribe.getUserId(),subscribe.getProgramId());
        if(target != null){
            DBObject query = new BasicDBObject();
            query.put("_id",target.getId());
            DBObject update = new BasicDBObject();
            update.put("status",subscribe.getStatus());
            subscribeDAO.update(query, new BasicDBObject("$set",update));
        }else{
            subscribeDAO.insertSubscribe(subscribe);
        }

    }

    public Subscribe checkSubscribe(Long userId,Long programId){
        DBObject query = new BasicDBObject();
        query.put("userId",userId);
        query.put("programId",programId);
        DBObject subscribe = subscribeDAO.find(query);
        if(subscribe != null){
            return SubscribeConvert.castDBObjectToSubscribe(subscribe);
        }else{
            return null;
        }

    }

    public List<Long> getAllSubscribeId(DBObject query){
        return subscribeDAO.getAllSubscribe(query);
    }



    public List<DBObject> getStartSubscribe(Long userId){
        List<DBObject> subList = subscribeDAO.getAllSubScribeStartByUser(userId);
        List<DBObject> result = Lists.newArrayList();
        for(DBObject dbObject : subList){
            DBRef proRef = (DBRef)dbObject.get("program");
            DBObject program = proRef.fetch();
            if(program != null) {
                Integer type = ObjectUtils.toInteger(program.get("pType"));
                Integer status = ObjUtils.toInteger(program.get("status"));
                if (type == 1 && status == 1) {
                    result.add(program);
                }
            }
        }
        return result;
    }
    /**
     * 获取订阅过某个直播的用户列表
      */
    public Map<Long,DBObject> getAllUser(Long programId){
        DBObject query = new BasicDBObject();
        query.put("programId",programId);
        Map<Long,DBObject> users = subscribeDAO.getAllSubscribeUser(programId);
        return users;
    }

    /**
     * 节目被取消后更新订阅状态
     * @param programId
     */
    public void cancelSubscribe(Long programId){
        DBObject query = new BasicDBObject();
        query.put("programId",programId);
        DBObject update = new BasicDBObject();
        update.put("status",0);
        subscribeDAO.update(query,new BasicDBObject("$set",update));
    }
}
