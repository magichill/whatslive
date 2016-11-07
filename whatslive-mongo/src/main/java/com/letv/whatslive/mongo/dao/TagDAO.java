package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Tag;
import com.letv.whatslive.model.convert.TagConvert;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/8/19.
 */
@Repository
public class TagDAO extends BaseDAO{

    protected String collectionName = "tag";

    private static final Logger logger = LoggerFactory.getLogger(TagDAO.class);

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    public Tag getTagById(Long id){
        Tag tag = null;
        BasicDBObject query = new BasicDBObject();
        query.put("_id",id);
        DBCursor cur = this.dbCollection.find(query);
        while (cur.hasNext()) {
            DBObject dbObject = cur.next();
            tag = TagConvert.castDBObjectToTag(dbObject);
        }
        return tag;
    }

    public List<Tag> getTagListByParams(Map params){
        List<Tag> result = Lists.newArrayList();
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject query = getBasicDBObjectByParams(params);
            DBCursor cur = this.dbCollection.find(query);
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                Tag tag = TagConvert.castDBObjectToTag(dbObject);
                result.add(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    private BasicDBObject getBasicDBObjectByParams(Map params) {
        //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
        BasicDBObject query = new BasicDBObject();// 新建查询基类对象 dbo
        if (params != null && params.size() > 0) {
            if (params.get("tagIdNotEqual") != null) {
                query.put("_id", new BasicDBObject("$ne", ObjectUtils.toLong(params.get("tagIdNotEqual"))));
            }
            if (params.get("tagId") != null) {
                query.put("_id", ObjectUtils.toLong(params.get("tagId")));
            }
        }
        return query;
    }
}
