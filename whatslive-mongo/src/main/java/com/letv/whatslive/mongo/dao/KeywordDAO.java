package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.DateUtils;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Keyword;
import com.letv.whatslive.model.convert.KeywordConvert;
import com.letv.whatslive.model.utils.ObjUtils;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 关键词Collection操作DAO
 * Created by wangjian7 on 2015/7/22.
 */
@Repository
public class KeywordDAO extends BaseDAO{
    protected String collectionName = "keyword";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    /**
     * 查询关键词屏蔽列表
     *
     * @param params 模糊查询的查询条件，
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @return
     */
    public List<Keyword> getKeywordListByParams(Map<String, Object> params, Integer start, Integer limit) {

        List<Keyword> result = Lists.newArrayList();
        if (limit == null) {
            limit = DEFAULT_NUM;
        }
        try {
            BasicDBObject query = getBasicDBObjectByParams(params);
            Integer skip = start == null ? 0 : start;
            DBCursor cur = this.dbCollection.find(query).skip(skip).sort(new BasicDBObject("createTime", -1)).limit(limit);//按createTime的值倒叙排列
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                Keyword keyword = KeywordConvert.castDBObjectToKeyword(dbObject);
                setValueToKeyword(keyword);
                result.add(keyword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询关键词的数量
     *
     * @param params 查询条件参数
     * @return 满足条件的文档记录数
     */
    public Long countKeywordByParams(Map params) {
        BasicDBObject query = getBasicDBObjectByParams(params);
        return this.dbCollection.count(query);
    }

    /**
     * 根据关键词ID获取关键词对象
     * @param kid
     * @return
     */
    public Keyword getKeywordById(Long kid){
        BasicDBObject query = new BasicDBObject();
        Keyword keyword = new Keyword();
        query.put("_id", kid);
        DBObject dbObject = this.dbCollection.findOne(query);
        if (dbObject == null) return null;
        try {
            keyword = KeywordConvert.castDBObjectToKeyword(dbObject);
            setValueToKeyword(keyword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyword;
    }

    /**
     * 保存关键词信息
     * @param keyword
     */
    public String saveKeyword(Keyword keyword) {
        try {
            keyword.setId(getAutoIncrementId());
            DBObject dbo = KeywordConvert.castKeywordToDBObject(keyword);
            WriteResult wr = this.dbCollection.insert(dbo);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    /**
     * 更新关键词信息
     * @param keyword
     */
    public void updateKeyword(Keyword keyword) {

        DBObject queryCondition = new BasicDBObject();
        queryCondition.put("_id", keyword.getId()); // 用来确定要修改的文档
        DBObject setValue = KeywordConvert.castKeywordToDBObject(keyword);
        //剔除主键修改
        if (setValue.containsKey("_id")) {
            setValue.removeField("_id");
        }
        DBObject values = new BasicDBObject("$set", setValue); // 修改器
        // 第一个为true表示如果不存在则创建，
        this.dbCollection.update(queryCondition, values, false, false);
    }

    /**
     * 根据关键词ID删除关键词
     * @param kid
     */
    public void deleteKeyword(Long kid){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", kid);
        this.dbCollection.remove(query);
    }

    /**
     * 查询所有关键词信息
     * @return
     */
    public List<String> queryAllKeyword(){
        List<String> keywordList = new ArrayList<String>();
        BasicDBObject query = new BasicDBObject();
        query.put("status", 1);
        DBCursor cur = this.dbCollection.find(query);
        while (cur.hasNext()) {
            DBObject dbObject = cur.next();
            String key = ObjUtils.toString(dbObject.get("key"));
            keywordList.add(key);
        }
        return keywordList;
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
            if (params.get("key") != null) {
                Pattern pattern = Pattern.compile("^.*" + params.get("key") + ".*$", Pattern.CASE_INSENSITIVE);
                query.put("key", pattern);
            }
            if (params.get("status") != null) {
                query.put("status", ObjectUtils.toInteger(params.get("status")));
            }

        }
        return query;
    }

    /**
     * 设置关键词信息中的特殊值
     * @param keyword
     */
    private void setValueToKeyword(Keyword keyword) {
        keyword.setCreateTimeStr(keyword.getCreateTime() != null ?
                DateUtils.long2YMDHMS(keyword.getCreateTime()):"");

    }


}
