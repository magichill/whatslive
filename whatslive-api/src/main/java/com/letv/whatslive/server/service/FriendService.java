package com.letv.whatslive.server.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.convert.UserConvert;
import com.letv.whatslive.model.dto.FollowerDTO;
import com.letv.whatslive.model.dto.UserDTO;
import com.letv.whatslive.mongo.dao.FriendsDAO;
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
 * Created by gaoshan on 15-7-9.
 */
@Service
public class FriendService {

    private final static Logger logger = LoggerFactory.getLogger(FriendService.class);

    @Resource
    private FriendsDAO friendsDAO;

    public Long getFollowerCount(Long userId){
        Map query = Maps.newHashMap();
        query.put("user.$id",userId);
        query.put("status",1);
        Long count = friendsDAO.countList(query);
        return count;
    }

    public Long getFollowingCount(Long userId){
        Map query = Maps.newHashMap();
        query.put("follower.$id",userId);
        query.put("status",1);
        Long count = friendsDAO.countList(query);
        return count;
    }

    public List<FollowerDTO> getFollowers(Long userId,Integer start,Integer limit){
        List<DBObject> friends = friendsDAO.getFollowers(userId,start,limit);
        List<FollowerDTO> result = Lists.newArrayList();
        for(DBObject obj : friends){
            DBRef userRef = (DBRef)obj.get("follower");
            if(userRef.fetch() != null) {
                FollowerDTO dto = new FollowerDTO(UserConvert.castDBObjectToUser(userRef.fetch()));
                Integer status = ObjectUtils.toInteger(obj.get("status"));
                DBRef relatedFriend = (DBRef) obj.get("relatedFriend");
                Integer relatedStatus = ObjectUtils.toInteger(relatedFriend.fetch().get("status"));
                if (status == 1 && relatedStatus == 1) {
                    dto.setStatus(2);
                } else {
                    dto.setStatus(1);
                }
                result.add(dto);
            }
        }
        return result;
    }

    public List<FollowerDTO> getOtherFollowers(Long otherId,Long userId,Integer start,Integer limit){
        List<DBObject> friends = friendsDAO.getFollowers(otherId,start,limit);
        List<FollowerDTO> result = Lists.newArrayList();
        for(DBObject obj : friends){
            DBRef userRef = (DBRef)obj.get("follower");
            if(userRef.fetch() != null) {
                FollowerDTO dto = new FollowerDTO(UserConvert.castDBObjectToUser(userRef.fetch()));
                Integer status = getFriendShipByUserId(userId, dto.getUserId());
                dto.setStatus(status);
                result.add(dto);
            }
        }
        return result;
    }

    public List<FollowerDTO> getFocus(Long userId,Integer start,Integer limit){
        List<DBObject> friends = friendsDAO.getFocus(userId,start,limit);
        List<FollowerDTO> result = Lists.newArrayList();
        for(DBObject obj : friends){
            DBRef userRef = (DBRef)obj.get("user");
            User user = UserConvert.castDBObjectToUser(userRef.fetch());
            if(user != null) {
                FollowerDTO dto = new FollowerDTO(user);
                Integer status = ObjectUtils.toInteger(obj.get("status"));
                DBRef relatedFriend = (DBRef) obj.get("relatedFriend");
                Integer relatedStatus = ObjectUtils.toInteger(relatedFriend.fetch().get("status"));
                if (status == 1 && relatedStatus == 1) {
                    dto.setStatus(2);
                } else {
                    dto.setStatus(1);
                }
                result.add(dto);
            }
        }
        return result;
    }

    public List<FollowerDTO> getOtherFocus(Long otherId,Long userId,Integer start,Integer limit){
        List<DBObject> friends = friendsDAO.getFocus(otherId,start,limit);
        List<FollowerDTO> result = Lists.newArrayList();
        for(DBObject obj : friends){
            DBRef userRef = (DBRef)obj.get("user");
            if(userRef.fetch() != null) {
                User user = UserConvert.castDBObjectToUser(userRef.fetch());
                if (user != null) {
                    FollowerDTO dto = new FollowerDTO(user);
                    Integer status = getFriendShipByUserId(userId, dto.getUserId());
                    dto.setStatus(status);
                    result.add(dto);
                }
            }
        }
        return result;
    }

    public void focus(Long followerId,Long userId,Integer status){
       friendsDAO.saveRelation(followerId,userId,status);
    }

    public Integer getFriendShipByUserId(Long userId,Long friendId){
        DBObject query = new BasicDBObject();
        Integer result = 0;
        query.put("follower.$id",userId);
        query.put("user.$id",friendId);
        DBObject friend = friendsDAO.find(query);
        if(friend == null){
            result = 0;
        }else{
            Integer status = ObjectUtils.toInteger(friend.get("status"));
            DBRef relatedFriend = (DBRef)friend.get("relatedFriend");
            Integer relatedStatus = ObjectUtils.toInteger(relatedFriend.fetch().get("status"));
            if(status == 1){
                if(relatedStatus == 1){
                    result = 2;
                }else{
                    result = 1;
                }
            }else{
//                if(relatedStatus == 1){
//                    result = 2;
//                }else{
//                    result = 0;
//                }
                result = 0;
            }
        }
        return result;
    }
}
