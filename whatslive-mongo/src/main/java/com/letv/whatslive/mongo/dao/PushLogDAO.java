package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.PushLog;
import com.letv.whatslive.model.convert.PushLogConvert;
import com.letv.whatslive.model.utils.ObjUtils;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 直播Collection操作DAO
 * Created by gaoshan on 15-7-13.
 */
@Repository
public class PushLogDAO extends BaseDAO {

    protected String collectionName = "pushLog";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    public Long insertPushLog(PushLog orderPush){
        orderPush.setId(getAutoIncrementId());
        DBObject obj = PushLogConvert.castPushLogToDBObject(orderPush);
        this.dbCollection.insert(obj);
        return orderPush.getId();
    }

    public void updatePushLog(PushLog orderPush){
        if(orderPush.getId()!=null){
            DBObject query = new BasicDBObject();
            query.put("_id", orderPush.getId());
            DBObject obj = PushLogConvert.castPushLogToDBObject(orderPush);
            this.dbCollection.update(query, obj);
        }
    }

    public List<Long> getProgramIdsByParams(Map<String, Object> params) {

        List<Long> result = Lists.newArrayList();
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject query = getBasicDBObjectByParams(params);
            DBCursor cur = this.dbCollection.find(query);
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                String bussikey = ObjUtils.toString(dbObject.get("bussikey"));
                result.add(Long.valueOf(bussikey.split("_")[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private PushLog getPushLogById(Long pid){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", pid);
        DBObject dbObject = this.dbCollection.findOne(query);
        PushLog pushLog = null;
        if (dbObject != null) {
            try {
                pushLog = PushLogConvert.castDBObjectToPushLog(dbObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pushLog;

    }

    public void deletePushLogById(Long pid){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", pid);
        this.dbCollection.remove(query);
    }

    /**
     * 根据查询参数Map获取查询条件对象
     * @param params 查询参数Map
     * @return
     */
    private BasicDBObject getBasicDBObjectByParams(Map params){
        //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
        BasicDBObject query = new BasicDBObject();// 新建查询基类对象 dbo
        if (params != null && params.size() > 0) {
            if (params.get("id") != null) {
                query.put("_id", ObjectUtils.toLong(params.get("id")));
            }
            if (params.get("status") != null) {
                query.put("status", ObjectUtils.toInteger(params.get("status")));
            }
            if (params.get("type") != null) {
                query.put("type", ObjectUtils.toInteger(params.get("type")));
            }
            if (params.get("bussiType") != null) {
                query.put("bussiType", ObjectUtils.toInteger(params.get("bussiType")));
            }
            if (params.get("statusIn") != null) {
                String status = ObjectUtils.toString(params.get("statusIn"));
                if (status.length()>2){
                    BasicDBList values = new BasicDBList();
                    String[] pTypeArray = status.split(",");
                    for(String type : pTypeArray){
                        values.add(ObjectUtils.toInteger(type));
                    }
                    query.put("status", new BasicDBObject("$in", values));
                }else{
                    query.put("status", ObjectUtils.toInteger(status));
                }
            }
            if (params.get("statusNotEqual") != null) {
                query.put("statusNotEqual", new BasicDBObject("$ne", ObjectUtils.toLong(params.get("statusNotEqual"))));
            }
            if(params.get("startTime_start") != null){
                query.put("startTime", new BasicDBObject("$gt", ObjectUtils.toLong(params.get("startTime_start"))));
            }
            if(params.get("startTime_end") != null){
                query.put("startTime", new BasicDBObject("$lt", ObjectUtils.toLong(params.get("startTime_end"))));
            }
            if(params.get("startTime_start") != null && params.get("startTime_end") != null){
                BasicDBList values = new BasicDBList();
                query.put("startTime", new BasicDBObject("$gt",
                        ObjectUtils.toLong(params.get("startTime_start"))).append("$lte", ObjectUtils.toLong(params.get("startTime_end"))));

            }
        }
        return query;
    }



    /**
     * 获取排序的BasicDBObject对象
     * @param orders
     * @return
     */
    private BasicDBObject getOrderParams(Map<String, Object> orders) {
        BasicDBObject order = new BasicDBObject();
        if(null != orders &&orders.size()>0){
            for(String key : orders.keySet()){
                order.append(key, Integer.valueOf(String.valueOf(orders.get(key))));
            }
        }else{
            //默认按照创建时间排序
//            order.append("createTime", -1);
        }
        return order;
    }

}
