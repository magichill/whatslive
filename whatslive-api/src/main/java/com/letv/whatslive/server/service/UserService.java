package com.letv.whatslive.server.service;

import com.letv.whatslive.model.BindUser;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.convert.BindUserConvert;
import com.letv.whatslive.model.convert.UserConvert;
import com.letv.whatslive.mongo.dao.BindUserDAO;
import com.letv.whatslive.mongo.dao.UserDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by gaoshan on 15-7-9.
 */
@Service
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserDAO userDAO;

    @Resource
    private BindUserDAO bindUserDAO;


    public User getUserById(long userId){
        DBObject query = new BasicDBObject();
        query.put("_id",userId);
        query.put("userStatus",1);
        DBObject obj = userDAO.find(query);
        User user = UserConvert.castDBObjectToUser(obj);
        return user;
    }

    public DBObject getUserBySinaId(String sinaId,String type){
        DBObject query = new BasicDBObject();
        query.put("sinaId",sinaId);
        query.put(type,1);
        DBObject obj = userDAO.find(query);
        return obj;
    }

    public DBObject getUserByThirdId(String thirdId,String type){
        DBObject query = new BasicDBObject();
        query.put("thirdId",thirdId);
//        query.put("userType",type);
        query.put(type,1);
        DBObject obj = userDAO.find(query);
        return obj;
    }


    /**
     * 判断第三方用户是否已经注册app
     * @param thirdId
     * @param type
     * @return
     */
    public DBObject checkRegister(String thirdId,int type){
        DBObject query = new BasicDBObject();
        query.put("thirdId",thirdId);
        query.put("userType",type);
        DBObject user = userDAO.find(query);
        if(user == null){
            DBObject bindUser = bindUserDAO.find(query);
            if(bindUser != null){
                BindUser bind = BindUserConvert.castDBObjectToUser(bindUser);
                user = bind.getBindUser().fetch();
            }
        }
        return user;
    }

    /**
     * 判断微博用户是否已经注册app
     * @param thirdId
     * @return
     */
    public DBObject checkWeiboRegister(String thirdId){
        DBObject query = new BasicDBObject();
        query.put("sinaId",thirdId);
        query.put("sinaBind",1);
        DBObject user = userDAO.find(query);
        if(user == null){
            DBObject bindUser = bindUserDAO.find(query);
            if(bindUser != null){
                BindUser bind = BindUserConvert.castDBObjectToUser(bindUser);
                user = bind.getBindUser().fetch();
            }
        }
        return user;
    }

    public int saveOrUpdateUser(User user){
        return userDAO.saveOrUpdateUser(user);
    }

    public Long insertUser(User user){
        return userDAO.insert(UserConvert.castUserToDBObject(user));
    }

    public void updateUserAfterClose(Long userId,Long programId,String videoId){
        userDAO.updateDataAfterClose(userId,programId,videoId);
    }

    public User getAdminUser(){
        return userDAO.getAdminUser();
    }
}
