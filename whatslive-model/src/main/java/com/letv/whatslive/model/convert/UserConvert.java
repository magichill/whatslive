package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.User;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaoshan on 15-7-7.
 */
public class UserConvert {

    public static DBObject castUserToDBObject(User user) {
        DBObject dbo = new BasicDBObject();
        if (user.getUserId() != null) {
            dbo.put("_id", user.getUserId());
        }
        dbo.put("userName", ObjUtils.ifNull(user.getUserName(), ""));
        dbo.put("nickName", ObjUtils.ifNull(user.getNickName(), ""));
        dbo.put("userType",ObjUtils.ifNull(user.getUserType(),0));
        dbo.put("sex", ObjUtils.ifNull(user.getSex(), 0));
        dbo.put("mobile", ObjUtils.ifNull(user.getMobile(), ""));
        dbo.put("phone", ObjUtils.ifNull(user.getPhone(), ""));
        dbo.put("email", ObjUtils.ifNull(user.getEmail(), ""));
        dbo.put("address", ObjUtils.ifNull(user.getAddress(), ""));
        dbo.put("picture", ObjUtils.ifNull(user.getPicture(), ""));
        dbo.put("userStatus", ObjUtils.ifNull(user.getUserStatus(), 1));
        dbo.put("thirdId", ObjUtils.ifNull(user.getThirdId(),""));
        dbo.put("createTime", ObjUtils.toLong(user.getCreateTime(), System.currentTimeMillis()));
        dbo.put("updateTime", ObjUtils.toLong(user.getUpdateTime(), System.currentTimeMillis()));
        dbo.put("lastLoginTime", ObjUtils.toLong(user.getLastLoginTime(), System.currentTimeMillis()));
        dbo.put("accessToken", ObjUtils.toString(user.getAccessToken()));
        dbo.put("role", ObjUtils.toInteger(user.getRole(),0));
        dbo.put("level", ObjUtils.toInteger(user.getLevel(),0));
        dbo.put("introduce", ObjUtils.toString(user.getIntroduce(),""));
        dbo.put("likeNum", ObjUtils.toLong(user.getLikeNum(),0l));
        dbo.put("sinaBind",ObjUtils.toInteger(user.getSinaBind(),0));
        dbo.put("qqBind",ObjUtils.toInteger(user.getQqBind(),0));
        dbo.put("weixinBind",ObjUtils.toInteger(user.getWeixinBind(),0));
        dbo.put("sinaId",ObjUtils.toString(user.getSinaId(),""));
        dbo.put("qqId",ObjUtils.toString(user.getQqId(),""));
        dbo.put("weixinId",ObjUtils.toString(user.getWeixinId(),""));

        if(user.getDevIdList()!=null && user.getDevIdList().size()>0){
            dbo.put("devIdList",user.getDevIdList());
        }else{
            dbo.put("devIdList",new ArrayList<Long>());
        }
        dbo.put("sso_tk",ObjUtils.toString(user.getSsoTk()));
        return dbo;
    }

    public static User castDBObjectToUser(DBObject dbObject){
        if(dbObject != null) {
            User user = new User();
            user.setUserId(ObjUtils.toLong(dbObject.get("_id")));
            user.setUserName(ObjUtils.toString(dbObject.get("userName"),""));
            user.setNickName(ObjUtils.toString(dbObject.get("nickName")));
            user.setSex(ObjUtils.toInteger(dbObject.get("sex")));
            user.setPicture(ObjUtils.toString(dbObject.get("picture")));
            user.setUserType(ObjUtils.toInteger(dbObject.get("userType")));


            user.setRole(ObjUtils.toInteger(dbObject.get("role")));
            user.setLevel(ObjUtils.toInteger(dbObject.get("level")));
            user.setLikeNum(ObjUtils.toLong(dbObject.get("likeNum"),0l));

            user.setIntroduce(ObjUtils.toString(dbObject.get("introduce")));
            user.setCreateTime(ObjUtils.toLong(dbObject.get("createTime")));
            user.setLastLoginTime(ObjUtils.toLong(dbObject.get("lastLoginTime")));
            user.setUserStatus(ObjUtils.toInteger(dbObject.get("userStatus"), 1));

            user.setBroadCastNum(ObjUtils.toLong(dbObject.get("broadCastNum"),0L));
            user.setSinaBind(ObjUtils.toInteger(dbObject.get("sinaBind"),0));
            user.setQqBind(ObjUtils.toInteger(dbObject.get("qqBind"),0));
            user.setWeixinBind(ObjUtils.toInteger(dbObject.get("weixinBind"),0));
            user.setSinaId(ObjUtils.toString(dbObject.get("sinaId"),""));
            user.setQqId(ObjUtils.toString(dbObject.get("qqId"),""));
            user.setWeixinId(ObjUtils.toString(dbObject.get("weixinId"),""));
            if(dbObject.get("devIdList")!=null){
                BasicDBList dbList = (BasicDBList)(dbObject.get("devIdList"));
                if(dbList.size()>0){
                    List<String> devList = new ArrayList<String>();
                    for(int i=0; i<dbList.size(); i++){
                        devList.add(String.valueOf(dbList.get(i)));
                    }
                    user.setDevIdList(devList);
                }
            }

            user.setAccessToken(ObjUtils.toString(dbObject.get("accessToken"),""));
            user.setSsoTk(ObjUtils.toString(dbObject.get("sso_tk"), ""));

            return user;
        }else{
            return null;
        }

    }
}
