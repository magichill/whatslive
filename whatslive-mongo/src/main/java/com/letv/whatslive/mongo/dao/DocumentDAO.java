package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.DateUtils;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Document;
import com.letv.whatslive.model.convert.DocumentConvert;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 升级文案Collection操作DAO
 * Created by wangjian7 on 2015/7/27.
 */
@Repository
public class DocumentDAO extends BaseDAO{
    protected String collectionName = "document";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    /**
     * 查询文案列表
     *
     * @param params 模糊查询的查询条件，
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @return
     */
    public List<Document> getDocumentListByParams(Map<String, Object> params, Integer start, Integer limit) {

        List<Document> result = Lists.newArrayList();
        if (limit == null) {
            limit = DEFAULT_NUM;
        }
        try {
            BasicDBObject query = getBasicDBObjectByParams(params);
            Integer skip = start == null ? 0 : start;
            DBCursor cur = this.dbCollection.find(query).skip(skip).sort(new BasicDBObject("createTime", -1)).limit(limit);//按createTime的值倒叙排列
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                Document document = DocumentConvert.castDBObjectToDocument(dbObject);
                setValueToDocument(document);
                result.add(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询文案数量
     * @param params 查询条件参数
     * @return 满足条件的文档记录数
     */
    public Long countDocumentByParams(Map params) {
        BasicDBObject query = getBasicDBObjectByParams(params);
        return this.dbCollection.count(query);
    }

    /**
     * 根据文案ID获取文案对象
     * @param id
     * @return
     */
    public Document getDocumentById(Long id){
        BasicDBObject query = new BasicDBObject();
        Document document = new Document();
        query.put("_id", id);
        DBObject dbObject = this.dbCollection.findOne(query);
        if (dbObject == null) return null;
        try {
            document = DocumentConvert.castDBObjectToDocument(dbObject);
            setValueToDocument(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 根据文案的版本号，获取所有的的相同版本的文案对象
     * @param version 文案版本号
     * @return
     */
    public List<Document> getDocumentsByName(String version){
        List<Document> result = Lists.newArrayList();
        BasicDBObject query = new BasicDBObject();
        query.put("version", version);
        DBCursor cur = this.dbCollection.find(query);
        while (cur.hasNext()) {
            DBObject dbObject = cur.next();
            Document document = DocumentConvert.castDBObjectToDocument(dbObject);
            setValueToDocument(document);
            result.add(document);
        }
        return result;
    }

    /**
     * 保存文案信息
     * @param document
     */
    public String saveDocument(Document document) {
        try {
            document.setId(getAutoIncrementId());
            DBObject dbo = DocumentConvert.castDocumentToDBObject(document);
            WriteResult wr = this.dbCollection.insert(dbo);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    /**
     * 更新文案信息
     * @param document
     */
    public void updateDocument(Document document) {

        DBObject queryCondition = new BasicDBObject();
        queryCondition.put("_id", document.getId()); // 用来确定要修改的文档
        DBObject setValue = DocumentConvert.castDocumentToDBObject(document);
        //剔除主键修改
        if (setValue.containsKey("_id")) {
            setValue.removeField("_id");
        }
        DBObject values = new BasicDBObject("$set", setValue); // 修改器
        this.dbCollection.update(queryCondition, values, false, false);
    }

    /**
     * 将所有有效的文案置为作废问题
     */
    public void blockAllDocument(){
        DBObject queryCondition = new BasicDBObject();
        queryCondition.put("status", 1);
        DBObject setValue = new BasicDBObject();
        setValue.put("status", 0);
        DBObject values = new BasicDBObject("$set", setValue); // 修改器
        this.dbCollection.updateMulti(queryCondition, values);
    }

    /**
     * 根据文案ID删除文案
     * @param id
     */
    public void deleteDocument(Long id){
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
            if (params.get("version") != null) {
                Pattern pattern = Pattern.compile("^.*" + params.get("version") + ".*$", Pattern.CASE_INSENSITIVE);
                query.put("version", pattern);
            }
            if (params.get("status") != null) {
                query.put("status", ObjectUtils.toInteger(params.get("status")));
            }

        }
        return query;
    }

    /**
     * 设置文案中的特殊值
     * @param document
     */
    private void setValueToDocument(Document document) {
        document.setCreateTimeStr(document.getCreateTime() != null ?
                DateUtils.long2YMDHMS(document.getCreateTime()):"");

    }


}
