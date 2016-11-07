package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.Subscribe;
import com.letv.whatslive.model.convert.SubscribeConvert;
import com.letv.whatslive.model.utils.ObjUtils;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-7-23.
 */
@Repository
public class SubscribeDAO extends BaseDAO {

    protected String collectionName = "subscribe";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    public long insertSubscribe(Subscribe subscribe){
        DBObject obj = SubscribeConvert.castSubscribeToDBObject(subscribe);
        DBRef programRef = new DBRef("program",subscribe.getProgramId());
        obj.put("program",programRef);
        return this.insert(obj);
    }

    public List<Subscribe> getAllSubscribe(Integer start,Integer limit,Map<String,Object> params){
        DBObject order = new BasicDBObject();
        List<DBObject> programList = selectAll(start,limit,params,order);
        List<Subscribe> result = Lists.newArrayList();
        for(DBObject obj : programList) {
            Subscribe subscribe = SubscribeConvert.castDBObjectToSubscribe(obj);
            result.add(subscribe);
        }
        return result;
    }

    public List<Long> getAllSubscribe(DBObject query){
        DBObject order = new BasicDBObject();
        order.put("createTime",-1);
        List<DBObject> programList = selectAll(query,order);
        List<Long> result = Lists.newArrayList();
        for(DBObject obj : programList) {
            Subscribe subscribe = SubscribeConvert.castDBObjectToSubscribe(obj);
            result.add(subscribe.getProgramId());
        }
        return result;
    }

    public List<Subscribe> getAllSubscribeObj(DBObject query){
        DBObject order = new BasicDBObject();
        order.put("createTime",-1);
        List<DBObject> programList = selectAll(query,order);
        List<Subscribe> result = Lists.newArrayList();
        for(DBObject obj : programList) {
            Subscribe subscribe = SubscribeConvert.castDBObjectToSubscribe(obj);
            result.add(subscribe);
        }
        return result;
    }
    public long countSubscribeByProgramId(Long pid){
        DBObject query = new BasicDBObject();
        query.put("programId", pid);
        return this.getDbCollection().count(query);
    }

    public List<DBObject> getAllSubScribeStartByUser(Long userId){
        DBObject query = new BasicDBObject();
        query.put("userId",userId);
        DBObject order = new BasicDBObject();
        order.put("createTime",-1);
        List<DBObject> subList = selectAll(query,order);

        return subList;
    }

    public Map<Long,DBObject> getAllSubscribeUser(Long programId){
        DBObject query = new BasicDBObject();
        query.put("programId",programId);
        DBObject order = new BasicDBObject();
        List<DBObject> subs = selectAll(query,order);
        Map result = Maps.newHashMap();
        for(DBObject obj : subs) {
            result.put(ObjectUtils.toLong(obj.get("userId")),obj);
        }
        return result;
    }
}
