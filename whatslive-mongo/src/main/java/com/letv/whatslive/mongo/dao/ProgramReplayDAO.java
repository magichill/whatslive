package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.ProgramReplay;
import com.letv.whatslive.model.convert.ProgramReplayConvert;
import com.letv.whatslive.mongo.BaseDAO;
import com.letv.whatslive.redis.JedisDAO;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 直播回放Collection操作DAO
 * Created by wangjian7 on 15-10-20.
 */
@Repository
public class ProgramReplayDAO extends BaseDAO {

    private final static Logger logger = LoggerFactory.getLogger(ProgramReplayDAO.class);

    protected String collectionName = "programReplay";

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

    /**
     * 插入直播回放信息
     * @param programReplay
     * @return
     */
    public long insertReplay(ProgramReplay programReplay){
        programReplay.setCreateTime(System.currentTimeMillis());
        DBObject obj = ProgramReplayConvert.castProgramReplayToDBObject(programReplay);
        return this.insert(obj);
    }
    /**
     * 更新直播回放信息
     * @param programReplay
     */
    public void updateReplay(ProgramReplay programReplay) {
        programReplay.setUpdateTime(System.currentTimeMillis());
        DBObject queryCondition = new BasicDBObject();
        Long pid = programReplay.getId();
        queryCondition.put("_id", pid); // 用来确定要修改的文档
        BasicDBObject setValue = (BasicDBObject)ProgramReplayConvert.castProgramReplayToDBObject(programReplay);
        //剔除主键修改
        if (setValue.containsField("_id")) {
            setValue.removeField("_id");
        }
        DBObject updateValue = new BasicDBObject("$set", setValue);
        this.dbCollection.update(queryCondition, updateValue, true, false);
    }

    /**
     * 查询回放历史日志生成成功的数据
     * @param pid
     * @return
     */
    public ProgramReplay querySuccessReplayByPid(Long pid) {
        Map<String, Object> queryParams = Maps.newHashMap();
        queryParams.put("pid",pid);
        queryParams.put("status",3);
        return queryReplayByParams(queryParams);
    }

    public ProgramReplay queryReplayByPid(Long pid){
        Map<String, Object> queryParams = Maps.newHashMap();
        queryParams.put("pid",pid);
        return queryReplayByParams(queryParams);
    }

    private ProgramReplay queryReplayByParams(Map params){
        DBObject dbObject = getBasicDBObjectByParams(params);
        DBObject result = dbCollection.findOne(dbObject);
        if (result == null) {
            return null;
        }
        ProgramReplay programReplay = ProgramReplayConvert.castDBObjectToProgramReplay(result);
        return programReplay;
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
            if (params.get("pid") != null) {
                query.put("pid", ObjectUtils.toLong(params.get("pid")));
            }
            if (params.get("status") != null) {
                query.put("status", ObjectUtils.toInteger(params.get("status")));
            }
        }
        return query;
    }
}
