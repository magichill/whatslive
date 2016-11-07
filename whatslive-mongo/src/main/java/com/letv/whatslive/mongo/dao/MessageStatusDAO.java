package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-8-7.
 */
@Repository
public class MessageStatusDAO extends BaseDAO {

    protected String collectionName = "messageStatus";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    public List<DBObject> getAllMessage(Long userId,Integer start,Integer limit){
        Map<String,Object> params = Maps.newHashMap();
        params.put("status",new BasicDBObject("$ne",Constants.MESSAGE_DELETE));
        params.put("recId",userId);
        DBObject order = new BasicDBObject();
        order.put("createTime",-1);
        List<DBObject> messageList = selectAll(start,limit,params,order);
        return messageList;
    }
}
