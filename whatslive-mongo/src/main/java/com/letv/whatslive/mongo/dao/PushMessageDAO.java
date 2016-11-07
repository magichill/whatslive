package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.DateUtils;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.PushMessage;
import com.letv.whatslive.model.convert.PushMessageConvert;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 推送消息Collection操作DAO
 * Created by wangjian7 on 2015/7/28.
 */
@Repository
public class PushMessageDAO extends BaseDAO{
    protected String collectionName = "pushMessage";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    /**
     * 查询推送消息列表
     *
     * @param params 模糊查询的查询条件，
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @return
     */
    public List<PushMessage> getPushMessageListByParams(Map<String, Object> params, Integer start, Integer limit) {

        List<PushMessage> result = Lists.newArrayList();
        if (limit == null) {
            limit = DEFAULT_NUM;
        }
        try {
            BasicDBObject query = getBasicDBObjectByParams(params);
            Integer skip = start == null ? 0 : start;
            DBCursor cur = this.dbCollection.find(query).skip(skip).sort(new BasicDBObject("createTime", -1)).limit(limit);//按createTime的值倒叙排列
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                PushMessage pushMessage = PushMessageConvert.castDBObjectToPushMessage(dbObject);
                setValueToPushMessage(pushMessage);
                result.add(pushMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询推送消息的数量
     *
     * @param params 查询条件参数
     * @return 满足条件的文档记录数
     */
    public Long countPushMessageByParams(Map params) {
        BasicDBObject query = getBasicDBObjectByParams(params);
        return this.dbCollection.count(query);
    }

    public PushMessage getPushMessageById(Long id){
        BasicDBObject query = new BasicDBObject();
        PushMessage pushMessage = new PushMessage();
        query.put("_id", id);
        DBObject dbObject = this.dbCollection.findOne(query);
        if (dbObject == null) return null;
        try {
            pushMessage = PushMessageConvert.castDBObjectToPushMessage(dbObject);
            setValueToPushMessage(pushMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pushMessage;
    }

    /**
     * 保存推送消息
     * @param pushMessage
     */
    public String savePushMessage(PushMessage pushMessage) {
        try {
            pushMessage.setId(getAutoIncrementId());
            DBObject dbo = PushMessageConvert.castPushMessageToDBObject(pushMessage);
            WriteResult wr = this.dbCollection.insert(dbo);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    /**
     * 更新推送消息
     * @param pushMessage
     */
    public void updatePushMessage(PushMessage pushMessage) {

        DBObject queryCondition = new BasicDBObject();
        queryCondition.put("_id", pushMessage.getId()); // 用来确定要修改的文档
        DBObject setValue = PushMessageConvert.castPushMessageToDBObject(pushMessage);
        //剔除主键修改
        if (setValue.containsKey("_id")) {
            setValue.removeField("_id");
        }
        DBObject values = new BasicDBObject("$set", setValue); // 修改器
        // 第一个为true表示如果不存在则创建，
        this.dbCollection.update(queryCondition, values, false, false);
    }

    /**
     * 根据消息id删除推送消息
     * @param id
     */
    public void deletePushMessage(Long id){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", id);
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
            if (params.get("content") != null) {
                Pattern pattern = Pattern.compile("^.*" + params.get("content") + ".*$", Pattern.CASE_INSENSITIVE);
                query.put("content", pattern);
            }
            if (params.get("status") != null) {
                String status = ObjectUtils.toString(params.get("status"));
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

        }
        return query;
    }

    /**
     * 设置推送消息中的特殊值
     * @param pushMessage
     */
    private void setValueToPushMessage(PushMessage pushMessage) {
        pushMessage.setCreateTimeStr(pushMessage.getCreateTime() != null ?
                DateUtils.long2YMDHMS(pushMessage.getCreateTime()):"");
        pushMessage.setSendTimeStr(pushMessage.getSendTime() != null ?
                DateUtils.long2YMDHMS(pushMessage.getSendTime()):"");

    }


}
