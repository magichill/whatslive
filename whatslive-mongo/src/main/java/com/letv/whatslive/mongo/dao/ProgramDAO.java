package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.openapi.sdk.util.LetvApiUtil;
import com.letv.openapi.sdk.util.OrderRetainingMap;
import com.letv.whatslive.common.httpclient.HttpClientUtil;
import com.letv.whatslive.common.httpclient.HttpFetchResult;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.DateUtils;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.model.convert.ProgramConvert;
import com.letv.whatslive.model.utils.ObjUtils;
import com.letv.whatslive.mongo.BaseDAO;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.redis.template.JedisTemplate;
import com.mongodb.*;
import org.apache.commons.lang3.*;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Iterator;

/**
 * 直播Collection操作DAO
 * Created by gaoshan on 15-7-13.
 */
@Repository
public class ProgramDAO extends BaseDAO {

    private final static Logger logger = LoggerFactory.getLogger(ProgramDAO.class);

    protected String collectionName = "program";

    @Autowired
    protected JedisDAO jedisDAO;

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    public long insertProgram(Program program){
        DBObject obj = ProgramConvert.castProgramToDBObject(program);
        DBRef userRef = new DBRef("user",program.getUserId());
        obj.put("user",userRef);
        return this.insert(obj);
    }

    public List<Program> getAllProgram(Integer start,Integer limit,Map<String,Object> params){
        params.put("status",1);
        DBObject order = new BasicDBObject();
        order.put("pType",1);
        order.put("priority",-1);
        order.put("createTime",-1);
        List<DBObject> programList = selectAll(start,limit,params,order);
        List<Program> result = Lists.newArrayList();
        for(DBObject obj : programList) {
            Program program = ProgramConvert.castDBObjectToProgram(obj);
            if(program.getLikeNum() == 0){
                program.setLikeNum(ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_LIKE_KEY+program.getId()),0l));
            }
            if(program.getCommentNum() == 0){
                program.setCommentNum(ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_COMMENT_KEY+program.getId()),0l));
            }
            if(program.getWatchNum() == 0){
                program.setWatchNum(jedisDAO.getJedisReadTemplate().llen(Constants.LIVE_ONLINE_USER_LIST_KEY+program.getId()));
            }
            result.add(program);
        }
        return result;
    }

    public List<DBObject> getUpdateProgramListByIds(BasicDBList userIds,Long timestamp,Integer start,Integer limit){
        List<DBObject> result = Lists.newArrayList();
        try{
            DBObject queryCondition = new BasicDBObject();
            queryCondition.put("userId", new BasicDBObject("$in", userIds));
            queryCondition.put("status",1);
            queryCondition.put("createTime",new BasicDBObject("$gt",timestamp));
            DBObject order = new BasicDBObject();
            order.put("pType",1);
            DBCursor cur = dbCollection.find(queryCondition).sort(order).skip((start - 1) * limit).limit(limit);
            while (cur.hasNext()) {
                result.add(cur.next());

            }
        }catch (Exception e){
            logger.error("", e);
        }
        return result;
    }

    public List<Program> getProgramListByIds(BasicDBList userIds,Integer start,Integer limit){
        List<Program> result = Lists.newArrayList();
        try{
            DBObject queryCondition = new BasicDBObject();
            queryCondition.put("userId", new BasicDBObject("$in", userIds));
            queryCondition.put("status",1);
            DBObject order = new BasicDBObject();
            order.put("pType",1);
            order.put("startTime",-1);
            DBCursor cur = dbCollection.find(queryCondition).sort(order).skip((start - 1) * limit).limit(limit);
            while (cur.hasNext()) {
                Program program = ProgramConvert.castDBObjectToProgram(cur.next());
                if(program.getPType() == 1) {
                    if (program.getLikeNum() == 0) {
                        program.setLikeNum(ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_LIKE_KEY + program.getId()), 0l));
                    }
                    if (program.getCommentNum() == 0) {
                        program.setCommentNum(ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_COMMENT_KEY + program.getId()), 0l));
                    }
                    if (program.getWatchNum() == 0) {
                        program.setWatchNum(jedisDAO.getJedisReadTemplate().llen(Constants.LIVE_ONLINE_USER_LIST_KEY + program.getId()));
                    }
                }
                result.add(program);

            }
        }catch (Exception e){
            logger.error("", e);
        }
        return result;
    }

    /**
     * 查询视频列表
     * @param orders 查询的排序条件
     * @param params 模糊查询的查询条件
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @return
     */
    public List<Program> getProgramListByParams(Map<String, Object> orders, Map<String, Object> params, Integer start, Integer limit) {

        List<Program> result = Lists.newArrayList();
        if (limit == null) {
            limit = DEFAULT_NUM;
        }
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject query = getBasicDBObjectByParams(params);
            BasicDBObject order = getOrderParams(orders);
            Integer skip = start == null ? 0 : start;
            DBCursor cur = this.dbCollection.find(query).skip(skip)
                    .sort(order).limit(limit);//按照视频类型和创建时间倒序
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                Program program = ProgramConvert.castDBObjectToProgram(dbObject);
                setValueToProgram(program);
                result.add(program);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return result;
    }

    public List<Long> getProgramIdsByParams(Map<String, Object> params, Map<String, Object> orders) {

        List<Long> result = Lists.newArrayList();
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject query = getBasicDBObjectByParams(params);
            BasicDBObject order = getOrderParams(orders);
            DBCursor cur = this.dbCollection.find(query).sort(order);
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                result.add(ObjUtils.toLong(dbObject.get("_id")));
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return result;
    }

    /**
     * 根据多个Id的字符串查询直播信息
     * @param ids 多个以英文逗号分隔
     * @return
     */
    public List<Program> getProgramListByIds(String ids){
        List<Program> result = Lists.newArrayList();
        try{
            BasicDBObject query = new BasicDBObject();// 新建查询基类对象 dbo
            String[] idList = ids.split(",");
            BasicDBList values = new BasicDBList();
            for(String id : idList){
                values.add(ObjectUtils.toLong(id));
            }
            query.put("_id", new BasicDBObject("$in", values));
            DBCursor cur = dbCollection.find(query);
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                Program program = ProgramConvert.castDBObjectToProgram(dbObject);
                setValueToProgram(program);
                result.add(program);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return result;
    }


    /**
     * 查询直播的记录数
     * @param params 查询条件参数
     * @return 满足条件的文档记录数
     */
    public Long countProgramByParams(Map params) {
        //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
        BasicDBObject query = getBasicDBObjectByParams(params);
        return this.dbCollection.count(query);
    }

    /**
     * 根据直播ID查询直播信息
     * @param id
     * @return
     */
    public Program getProgramById(Long id){
        DBObject dbObject = new BasicDBObject();
        dbObject.put("_id", id);
        DBObject result = dbCollection.findOne(dbObject);
        if (result == null) {
            return null;
        }
        Program program = ProgramConvert.castDBObjectToProgram(result);
        setValueToProgram(program);
        return program;
    }

    /**
     * 更新直播信息
     * @param program
     */
    public void updateProgram(Program program) {
        DBObject queryCondition = new BasicDBObject();
        Long pid = program.getId();
        queryCondition.put("_id", pid); // 用来确定要修改的文档
        BasicDBObject setValue = (BasicDBObject)ProgramConvert.castProgramToDBObject(program);
        //剔除主键修改
        if (setValue.containsField("_id")) {
            setValue.removeField("_id");
        }
        DBRef userRef = new DBRef(this.dbCollection.getDB(),"user",program.getUserId());
        setValue.put("user",userRef);
        DBObject updateValue = new BasicDBObject("$set", setValue);
        this.dbCollection.update(queryCondition, updateValue, true, false);
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
            if (params.get("userId") != null) {
                query.put("userId", ObjectUtils.toLong(params.get("userId")));
            }
            if (params.get("pType") != null) {
                String pType = ObjectUtils.toString(params.get("pType"));
                if (pType.length()>2){
                    BasicDBList values = new BasicDBList();
                    String[] pTypeArray = pType.split(",");
                    for(String type : pTypeArray){
                        values.add(ObjectUtils.toInteger(type));
                    }
                    query.put("pType", new BasicDBObject("$in", values));
                }else{
                    query.put("pType", ObjectUtils.toInteger(pType));
                }
            }
            if (params.get("pName") != null) {
                BasicDBList values = new BasicDBList();
                Pattern pattern = Pattern.compile("^.*" + params.get("pName") + ".*$", Pattern.CASE_INSENSITIVE);
                values.add(new BasicDBObject("_id", ObjectUtils.toLong(params.get("pName"))));
                values.add(new BasicDBObject("pName", pattern));
                query.put("$or", values);
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
            if (params.get("reportNum") != null){
                query.put("reportNum", new BasicDBObject("$gt", 0L));
            }
            if(params.get("startTime_start") != null && params.get("startTime_end") != null){
                query.put("startTime", new BasicDBObject("$gt",
                        ObjectUtils.toLong(params.get("startTime_start"))).append("$lte", ObjectUtils.toLong(params.get("startTime_end"))));

            }
            if(params.get("isCarousel") != null){
                query.put("isCarousel", new BasicDBObject("$exists", params.get("isCarousel")));
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
            order.append("createTime", -1);
        }
        return order;
    }

    /**
     * 设置直播信息中的特殊值
     * @param program
     */
    private void setValueToProgram(Program program) {
        program.setStartTimeStr(program.getStartTime() != null ?
                DateUtils.long2YMDHMS(program.getStartTime()) : "");
        program.setCreateTimeStr(program.getCreateTime() != null ?
                DateUtils.long2YMDHMS(program.getCreateTime()) : "");
    }

    /**
     * 关闭直播需要更新数据并调用关闭直播流的接口
     * @param id program id
     * @param delayTime 关闭延迟时间
     */
    public void updateData(Long id, long delayTime){
        try {
            DBObject query = new BasicDBObject();
            query.put("_id",id);
            DBObject program = find(query);
            if(program != null) {
                //只有直播时常大于25秒的才会转录播
                if((System.currentTimeMillis()-ObjUtils.toLong(program.get("startTime"))-delayTime)>=25*1000){
                    endProgram(id, -1);
                    //判断是否是直播，并不是轮播台
                    if(program.get("isCarousel") == null) {
                        String activityId = ObjectUtils.toString(program.get("activityId"));
                        if (StringUtils.isNotBlank(activityId)) {
                            logger.info("end live start! programId:"+id);
                            try {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("activityId", activityId);
                                Map<String, String> map = new OrderRetainingMap();
                                map.put("method", "letv.cloudlive.activity.stop");
                                map.put("ver", VER);
                                map.put("userid", ProgramConstants.LIVE_CLOUD_USER_ID);
                                map.put("timestamp", String.valueOf(System.currentTimeMillis()));
                                map.putAll(params);
                                String sign = LetvApiUtil.digest(map, ProgramConstants.LIVE_CLOUD_USER_SECRETE);
                                map.put("sign", sign);

                                List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
                                for (Map.Entry<String, String> entry : map.entrySet()) {
                                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                                }

                                Map<String, String> headers = Maps.newHashMap();
                                headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                                logger.info("call letv cloud api start! programId:"+id);
                                HttpFetchResult result = HttpClientUtil.requestPost(ProgramConstants.LIVE_CLOUD_URL, nvps, headers);
                                logger.info("call letv cloud api end programId:"+id+" result:"+result.getStatus().getStatusCode());
                            } catch (Exception e) {
                                logger.error("call letv cloud api error! " + e.getMessage() + " programId:" + id, e);
                            }
                        }else{
                            logger.info("program activityId is null, programId:" + id + " activityId:"+activityId);
                        }
                    }else{
                        logger.info("program is carousel, programId:" + id);
                    }
                }else{
                    endProgram(id, -2);
                    logger.info("program play time is less than 25 secends, don't call live cloud end api! programId:"+id);
                }
            }else{
                logger.info("program is not exist, programId:" + id);
            }

        }catch (Exception e){
            logger.error(e.getMessage() + " programId:" + id, e);
        }

    }

    private void endProgram(Long id, int status){
        DBObject update = new BasicDBObject();
        update.put("status", status);
        update.put("pType", 3);
        update.put("endTime", System.currentTimeMillis());
        update.put("watchNum", ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().smembers(Constants.LIVE_ONLINE_TOTALUSER_KEY + id).size(),0l));
        update.put("likeNum", ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_LIKE_KEY + id),0l));
        update.put("commentNum", ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_COMMENT_KEY + id),0l));

        update.put("realWatchNum", ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().smembers(Constants.LIVE_ONLINE_REAL_TOTALUSER_KEY + id).size(),0l));
        update.put("realLikeNum", ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_REAL_LIKE_KEY + id),0l));
        update.put("realCommentNum", ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_REAL_COMMENT_KEY + id),0l));

        update.put("updateTime", System.currentTimeMillis());
        DBObject query = new BasicDBObject();
        query.put("_id",id);
        this.update(query, new BasicDBObject("$set", update));
        logger.info("end program success! programId:" + id);
    }

    /**
     * 根据指定查询条件，分组条件获得指定项目的合计值。
     * @param queryParams 查询条件参数
     * @param groupParams 分组条件参数
     * @param sumColumnName 计算合计值得列名称
     * @return 以组为单位的合计值
     */
    public Map<String, Long> getTotalNumByParams(Map queryParams, List<String> groupParams, String sumColumnName) {

        // 合计值
        Map<String, Long> totalNumMap = new HashMap<String, Long>();
        List<DBObject> pipeline = new ArrayList<DBObject>();

        // 检索条件
        BasicDBObject matchDBObj = new BasicDBObject();
        BasicDBObject matchCondition = new BasicDBObject();
        if (queryParams.get("pType") != null) {

            String pType = ObjectUtils.toString(queryParams.get("pType"));
            if (pType.length() > 1) {

                BasicDBList values = new BasicDBList();
                String[] pTypeArray = pType.split(",");
                for (String type : pTypeArray) {
                    values.add(new BasicDBObject("pType", ObjectUtils.toInteger(type)));
                }
                matchCondition.put("$or", values);
            } else {

                matchCondition.put("pType", ObjectUtils.toInteger(pType));
            }
        }
        if (queryParams.get("status") != null) {
            matchCondition.put("status", ObjectUtils.toInteger(queryParams.get("status")));
        }
        if(queryParams.get("isCarousel") != null){
            matchCondition.put("isCarousel", new BasicDBObject("$exists", queryParams.get("isCarousel")));
        }
        matchDBObj.put("$match", matchCondition);
        pipeline.add(matchDBObj);

        // 分组
        BasicDBObject groupDBObj = new BasicDBObject();
        BasicDBObject groupCondition = new BasicDBObject();
        BasicDBObject groupParamDBObj = new BasicDBObject();
        for (String groupParam : groupParams) {

            groupParamDBObj.put(groupParam, "$" + groupParam);
        }
        groupCondition.put("_id", groupParamDBObj);
        groupCondition.put(sumColumnName, new BasicDBObject("$sum", "$" + sumColumnName));
        groupDBObj.put("$group", groupCondition);
        pipeline.add(groupDBObj);

        // 获取合计值
        AggregationOutput output = this.dbCollection.aggregate(pipeline);
        Iterator<DBObject> result = output.results().iterator();

        while (result.hasNext()) {

            DBObject dbObject = result.next();
            DBObject keys = (DBObject)dbObject.get("_id");
            StringBuilder sb = new StringBuilder();

            // 用分组列的值作为key，各分组列的值以_分隔
            for (String groupParam : groupParams) {

                sb.append(ObjUtils.toString(keys.get(groupParam)));
                sb.append("_");
            }
            sb.deleteCharAt(sb.lastIndexOf("_"));
            String key = sb.toString();
            Long totalNum = ObjUtils.toLong(dbObject.get(sumColumnName), 0L);
            totalNumMap.put(key, totalNum);
        }

        return totalNumMap;
    }
}
