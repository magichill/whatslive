package com.letv.whatslive.mongo.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.ActionLog;
import com.letv.whatslive.model.convert.ActionLogConvert;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-9-24.
 */
@Repository
public class ActionLogDAO extends BaseDAO {

    private final static Logger logger = LoggerFactory.getLogger(ActionLogDAO.class);

    protected String collectionName = "actionLog";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    /**
     * 插入行为日志
     * @param actionLog
     */
    public long insertActionLog(ActionLog actionLog){
        try {
            DBObject obj = ActionLogConvert.castActionLogToDBObject(actionLog);
            return this.insert(obj);
        } catch (Exception e) {
            logger.error("mongodb insert data error! "+collectionName, e);
            return -1L;
        }
    }

    /**
     * 查询行为日志的记录数
     * @param params 查询条件参数
     * @return 满足条件的文档记录数
     */
    public long countActionLogByParams(Map params) {
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject query = getBasicDBObjectByParams(params);
            return this.dbCollection.count(query);
        } catch (Exception e) {
            logger.error("mongodb count data error! "+collectionName, e);
            return -1L;
        }
    }

    /**
     * 查询行为日志字符串列表
     * @param orders 查询的排序条件
     * @param params 模糊查询的查询条件
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @param programStartTime  直播开始时间，用于计算行为日志的相对时间
     * @return
     */
    public List<String> getActionLogJsonStrListByParams(Map<String, Object> orders, Map<String, Object> params
            , Integer start, Integer limit, long programStartTime) {

        List<String> result = Lists.newArrayList();
        if (limit == null) {
            limit = DEFAULT_NUM;
        }
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject query = getBasicDBObjectByParams(params);
            BasicDBObject order = getOrderParams(orders);
            Integer skip = start == null ? 0 : start;
            DBCursor cur = this.dbCollection.find(query).skip(skip).sort(order).limit(limit);
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                Map<String, Object> value = ActionLogConvert.castDBObjectToMap(dbObject, programStartTime);
                result.add(JSON.toJSONString(value));
            }
        } catch (Exception e) {
            logger.error("mongodb query data list error! "+collectionName, e);
        }
        return result;
    }

    /**
     * 根据直播id删除所有直播的回放日志
     * @param pid
     * @return
     */
    public int deleteActionLogById(String pid){
        Map<String, Object> queryParams = Maps.newHashMap();
        queryParams.put("roomId",pid);
        DBObject dbObject = getBasicDBObjectByParams(queryParams);
        WriteResult writeResult = dbCollection.remove(dbObject);
        return writeResult.getN();
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
            if (params.get("roomId") != null) {
                query.put("roomId", ObjectUtils.toString(params.get("roomId")));
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
        }
        return order;
    }

}
