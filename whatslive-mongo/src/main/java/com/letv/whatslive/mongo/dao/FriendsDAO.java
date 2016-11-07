package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-7-9.
 */
@Repository
public class FriendsDAO extends BaseDAO {


    protected String collectionName = "friend";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    public void saveRelation(long followerId,long userId,Integer status){
        DBObject query = new BasicDBObject();
        query.put("follower.$id",followerId);
        query.put("user.$id",userId);
        DBObject relation = this.find(query);
        if(relation == null){
            //存入两条关联数据，建立粉丝关系
            DBObject friend = new BasicDBObject();
            DBObject friend1 = new BasicDBObject();
            DBRef followerRef = new DBRef("user",followerId);
            DBRef userRef = new DBRef("user",userId);
            friend.put("follower",followerRef);
            friend.put("user",userRef);
            friend.put("status",1);
            friend.put("createTime",System.currentTimeMillis());
            friend.put("updateTime",System.currentTimeMillis());
            friend1.put("follower",userRef);
            friend1.put("user",followerRef);
            friend1.put("status",0);
            friend1.put("createTime",System.currentTimeMillis());
            friend1.put("updateTime",System.currentTimeMillis());
//            this.insert(friend);
            List<DBObject> dbList = Lists.newArrayList();
            dbList.add(friend);
            dbList.add(friend1);
            this.multiInsert(dbList);
        }else{
            DBObject update = new BasicDBObject();
            update.put("status",status);
            this.update(relation,new BasicDBObject("$set",update));
        }

    }

    public void multiInsert(List<DBObject> dbList){
        List<DBObject> insertDataList=new ArrayList<DBObject>();
        for(DBObject obj : dbList){
            long id = this.getAutoIncrementId();
            obj.put("_id",id);
            insertDataList.add(obj);
        }
        if(insertDataList != null && insertDataList.size()==2){
            DBObject related1 = insertDataList.get(0);
            DBObject related2 = insertDataList.get(1);
            DBRef relatedRef1 = new DBRef("friend",ObjectUtils.toLong(related2.get("_id")));
            DBRef relatedRef2 = new DBRef("friend",ObjectUtils.toLong(related1.get("_id")));
            related1.put("relatedFriend",relatedRef1);
            related2.put("relatedFriend",relatedRef2);
            this.dbCollection.insert(insertDataList);
        }

    }

    /**
     * 获取粉丝列表
     * @param userId
     * @param start
     * @param limit
     * @return
     */
    public List<DBObject> getFollowers(long userId,Integer start,Integer limit){
        Map params = Maps.newHashMap();
        params.put("user.$id",userId);
        params.put("status",1);
        DBObject order = new BasicDBObject();
        order.put("updateTime",-1);
        return this.selectAll(start,limit,params,order);
    }

    /**
     * 获取关注列表
     * @param userId
     * @param start
     * @param limit
     * @return
     */
    public List<DBObject> getFocus(long userId,Integer start,Integer limit){
        Map params = Maps.newHashMap();
        params.put("follower.$id",userId);
        params.put("status",1);
        DBObject order = new BasicDBObject();
        order.put("updateTime",-1);
        return this.selectAll(start,limit,params,order);
    }

    public BasicDBList getAllFocusId(Long userId) {
        BasicDBList result = new BasicDBList();
        DBObject params = new BasicDBObject();
        params.put("follower.$id",userId);
        params.put("status",1);
        DBObject order = new BasicDBObject();
        order.put("createTime",-1);
        List<DBObject> friends = this.selectAll(params,order);
        for(DBObject f : friends){
            DBRef focus = (DBRef)f.get("user");
            DBObject user = focus.fetch();
            if(user != null) {
                result.add(ObjectUtils.toLong(user.get("_id")));
            }
        }
        return result;
    }
}
