package com.letv.whatslive.server.service;

import com.letv.whatslive.model.BindUser;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.convert.BindUserConvert;
import com.letv.whatslive.mongo.dao.BindUserDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by gaoshan on 15-7-27.
 */
@Service
public class BindUserService {

    private final static Logger logger = LoggerFactory.getLogger(BindUserService.class);

    @Resource
    private BindUserDAO bindUserDAO;

    public BindUser getUserByThirdId(String thirdId,Integer type){
        DBObject query = new BasicDBObject();
        query.put("thirdId",thirdId);
        query.put("bindType",type);
        DBObject obj = bindUserDAO.find(query);
        if(obj != null) {
            BindUser user = BindUserConvert.castDBObjectToUser(obj);
            return user;
        }else{
            return null;
        }
    }

    public void saveBindUser(User user){
        BindUser bindUser = new BindUser();
        bindUser.setBindToken(user.getAccessToken());
        bindUser.setBindTokenSecret(user.getTokenSecret());
        bindUser.setBindType(user.getUserType());
        bindUser.setBindThirdId(user.getThirdId());
        bindUser.setCreateTime(System.currentTimeMillis());
        bindUser.setBindUser(new DBRef("user",user.getUserId()));
        bindUserDAO.saveOrUpdateBindUser(bindUser);
    }
}
