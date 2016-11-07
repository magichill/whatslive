package com.letv.whatslive.server.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.model.ThirdFriend;
import com.letv.whatslive.mongo.dao.FacebookFriendDAO;
import com.letv.whatslive.mongo.dao.ThirdFriendDAO;
import com.letv.whatslive.mongo.dao.TwitterFriendDAO;
import com.letv.whatslive.mongo.dao.WeiboFriendDAO;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-8-4.
 */
@Service
public class ThirdFriendService {

    @Resource
    private ThirdFriendDAO thirdFriendDAO;

    @Resource
    private FacebookFriendDAO facebookFriendDAO;

    @Resource
    private TwitterFriendDAO twitterFriendDAO;

    @Resource
    private WeiboFriendDAO weiboFriendDAO;

    public List<DBObject> getFriendListByUserId(Integer start,Integer limit,Long userId,Integer type){
        Map query = Maps.newHashMap();
        query.put("userId",userId);
        query.put("type",type);
        List<DBObject> friends = Lists.newArrayList();
        switch (type){
            case 1: //facebook
                friends =  facebookFriendDAO.selectAll(start,limit,query);
                break;
            case 2: //twitter
                friends =  twitterFriendDAO.selectAll(start,limit,query);
                break;
            case 4:  //weibo
                friends = weiboFriendDAO.selectAll(start,limit,query);
                break;
            case 5:  //no way to get friends' list
                break;
            default:
                break;
        }
        return friends;
    }

    public void insertFriend(List<DBObject> objectList,Integer type){
        switch (type){
            case 1:
                for(DBObject object : objectList){
                    facebookFriendDAO.save(object);
                }
                break;
            case 2:
                for(DBObject object : objectList){
                    twitterFriendDAO.save(object);
                }
                break;
            case 4:
                for(DBObject object : objectList){
                    weiboFriendDAO.save(object);
                }
                break;
            case 5:
                break;
            default:
                break;
        }

    }
}
