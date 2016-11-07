package com.letv.whatslive.server.service;

import com.letv.whatslive.mongo.dao.DeviceDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by gaoshan on 15-8-28.
 */
@Service
public class DeviceService {

    @Resource
    private DeviceDAO deviceDAO;


    public boolean updateChannelId(String devId,String channelId){
        DBObject query = new BasicDBObject();
        query.put("_id",devId);
        DBObject update = new BasicDBObject();
        update.put("channelId",channelId);
        return deviceDAO.update(query,new BasicDBObject("$set",update));
    }
}
