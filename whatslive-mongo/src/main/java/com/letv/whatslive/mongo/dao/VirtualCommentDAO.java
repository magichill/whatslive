package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.DateUtils;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.VirtualComment;
import com.letv.whatslive.model.convert.VirtualCommentConvert;
import com.letv.whatslive.model.utils.ObjUtils;
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
 * 虚拟评论Collection操作DAO
 * Created by wangjian7 on 2015/7/28.
 */
@Repository
public class VirtualCommentDAO extends BaseDAO{
    protected String collectionName = "virtualComment";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    /**
     * 查询虚拟评论列表
     *
     * @param params 模糊查询的查询条件，
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @return
     */
    public List<VirtualComment> getVirtualCommentListByParams(Map<String, Object> params, Integer start, Integer limit) {

        List<VirtualComment> result = Lists.newArrayList();
        if (limit == null) {
            limit = DEFAULT_NUM;
        }
        try {
            BasicDBObject query = getBasicDBObjectByParams(params);
            Integer skip = start == null ? 0 : start;
            DBCursor cur = this.dbCollection.find(query).skip(skip).sort(new BasicDBObject("createTime", -1)).limit(limit);//按createTime的值倒叙排列
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                VirtualComment comment = VirtualCommentConvert.castDBObjectToVirtualComment(dbObject);
                setValueToVirtualComment(comment);
                result.add(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String> getAllVirtualComment(){
        List<String> result = Lists.newArrayList();
        try {
            Map<String, String> params = Maps.newHashMap();
            params.put("status","1");
            BasicDBObject query = getBasicDBObjectByParams(params);
            DBCursor cur = this.dbCollection.find(query);
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                result.add(ObjUtils.toString(dbObject.get("content")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 查询虚拟评论的数量
     *
     * @param params 查询条件参数
     * @return 满足条件的文档记录数
     */
    public Long countVirtualCommentByParams(Map params) {
        BasicDBObject query = getBasicDBObjectByParams(params);
        return this.dbCollection.count(query);
    }

    public VirtualComment getVirtualCommentById(Long kid){
        BasicDBObject query = new BasicDBObject();
        VirtualComment comment = new VirtualComment();
        query.put("_id", kid);
        DBObject dbObject = this.dbCollection.findOne(query);
        if (dbObject == null) return null;
        try {
            comment = VirtualCommentConvert.castDBObjectToVirtualComment(dbObject);
            setValueToVirtualComment(comment);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comment;
    }

    /**
     * 保存虚拟评论
     * @param comment
     */
    public String saveVirtualComment(VirtualComment comment) {
        try {
            comment.setId(getAutoIncrementId());
            DBObject dbo = VirtualCommentConvert.castVirtualCommentToDBObject(comment);
            WriteResult wr = this.dbCollection.insert(dbo);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    /**
     * 更新虚拟评论
     * @param comment
     */
    public void updateVirtualComment(VirtualComment comment) {

        DBObject queryCondition = new BasicDBObject();
        queryCondition.put("_id", comment.getId()); // 用来确定要修改的文档
        DBObject setValue = VirtualCommentConvert.castVirtualCommentToDBObject(comment);
        //剔除主键修改
        if (setValue.containsKey("_id")) {
            setValue.removeField("_id");
        }
        DBObject values = new BasicDBObject("$set", setValue); // 修改器
        this.dbCollection.update(queryCondition, values, false, false);
    }

    /**
     * 根据id删除虚拟评论
     * @param id
     */
    public void deleteVirtualComment(Long id){
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
                query.put("status", ObjectUtils.toInteger(params.get("status")));
            }

        }
        return query;
    }

    /**
     * 设置虚拟评论信息中的特殊值
     * @param comment
     */
    private void setValueToVirtualComment(VirtualComment comment) {
        comment.setCreateTimeStr(comment.getCreateTime() != null ?
                DateUtils.long2YMDHMS(comment.getCreateTime()):"");

    }


}
