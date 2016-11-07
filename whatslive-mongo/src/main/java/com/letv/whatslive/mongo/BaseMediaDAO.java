package com.letv.whatslive.mongo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by gaoshan on 14-10-24.
 */
public abstract class BaseMediaDAO {

    @Autowired
    protected MediaMongoTemplate mongoTemplate;

    /**
     * 自增ID生成器
     */
    protected MongoIDGenerator idGenerate;

    /**
     * 集合操作器
     */
    protected DBCollection dbCollection;


    /**
     * 默认返回列表数量
     */
    public static final Integer DEFAULT_NUM = 5;

    @PostConstruct
    protected abstract void init();

    public void init(String collectionName){
        this.dbCollection = mongoTemplate.getDB().getCollection(collectionName);
        idGenerate = new MongoIDGenerator(mongoTemplate.getDB(), 1);
        try {
            if (!this.idGenerate.exists(collectionName)) {
                this.idGenerate.setInitialValue(collectionName, 0L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    protected abstract long getAutoIncrementId();

    public List selectAll(DBObject query,DBObject order){
        DBCursor cur = null;
        List result = Lists.newArrayList();
        try{
            cur = this.dbCollection.find(query);
            cur.sort(order);
            while(cur.hasNext()){
                DBObject obj = cur.next();
                result.add(obj);
            }
        }catch(Exception e){

        }finally{
            if(cur!= null){
                cur.close();
            }
        }
        return result;
    }

    private BasicDBObject getLikeStr(String findStr,String fieldName) {
        BasicDBObject dbo=new BasicDBObject();// 新建查询基类对象 dbo
        String filterStr = ObjectUtils.StringFilter(findStr);
        if(null == filterStr || filterStr.equals("")){
            return null;
        }
        Pattern pattern = Pattern.compile("^.*" + filterStr+ ".*$", Pattern.CASE_INSENSITIVE);
        dbo.put(fieldName, pattern);
        return dbo;
    }

    public DBObject find(DBObject obj){
        return this.dbCollection.findOne(obj);
    }

    public List<DBObject> findAll(DBObject query,DBObject order){
        DBCursor cur = null;
        List result = Lists.newArrayList();
        try{
            cur = this.dbCollection.find(query).sort(order);
            while(cur.hasNext()){
                DBObject obj = cur.next();
                result.add(obj);
            }
        }finally{
            if(cur != null){
                cur.close();
            }
        }
        return result;
    }

    public Map<Long,DBObject> findAll(DBObject query){
        Map result = Maps.newHashMap();
        DBCursor cur = null;
        try{
            cur = this.dbCollection.find(query);
            while(cur.hasNext()){
                DBObject obj = cur.next();
                result.put(ObjectUtils.toLong(obj.get("_id")),obj);
            }
        }finally{
            if(cur != null){
                cur.close();
            }
        }
        return result;
    }
    public void save(DBObject obj){
        this.dbCollection.save(obj);
    }


    /**
     * 按条件查询列表
     * @param start
     * @param limit
     * @param params
     * @return
     */
    public List<DBObject> selectAll(Integer start,Integer limit,Map<String,Object> params,DBObject... orderBy){
        List result = Lists.newArrayList();
        DBCursor cur = null;
        try{
            if(params != null){
                Iterator iter = params.entrySet().iterator();
                DBObject query = new BasicDBObject();
                while(iter.hasNext()){
                    Map.Entry entry = (Map.Entry)iter.next();
                    query.put(ObjectUtils.toString(entry.getKey()),entry.getValue());
                }
                cur = this.dbCollection.find(query).skip((start - 1) * limit).limit(limit);
            }else{
                cur = this.dbCollection.find().skip((start - 1) * limit).limit(limit);
            }
            if(orderBy.length >0){
                cur.sort(orderBy[0]);
            }
            while(cur.hasNext()){
                DBObject obj = cur.next();
                result.add(obj);
            }
        }finally{
            if(cur != null){
                cur.close();
            }
        }
        return result;
    }

    /**
     * 按需取相应的字段
     * @param start
     * @param limit
     * @param fields
     * @return
     */
    public List selectQuery(Integer start,Integer limit,String... fields){
        List result = Lists.newArrayList();
        DBCursor cur = null;
        try{
            if(fields==null){
                cur = this.dbCollection.find().skip((start - 1) * limit).limit(limit);
            }else{
                BasicDBObject keys = new BasicDBObject();
                for(String field : fields){
                    keys.put(field,1);
                }
                cur = this.dbCollection.find(new BasicDBObject(), keys).skip((start - 1) * limit).limit(limit);
            }
            while(cur.hasNext()){
                DBObject obj = cur.next();
                result.add(obj);
            }
        }finally{
            if(cur != null){
                cur.close();
            }
        }
        return result;
    }

    /**
     * 按关键字模糊查询指定字段
     * @param keyword
     * @param limit
     * @param fieldName
     * @return
     */
    public List select(String keyword,Integer start,Integer limit,String fieldName) {
        List result = Lists.newArrayList();
        DBCursor cur = null;
        if(limit == null){
            limit = DEFAULT_NUM;
        }
        try {
            BasicDBObject dbObject = getLikeStr(keyword,fieldName);
            if(null == dbObject) {
                return result;
            }
            cur = this.dbCollection.find(dbObject).skip((start - 1) * limit).limit(limit);
            while (cur.hasNext()) {
                result.add(cur.next());
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(cur != null){
                cur.close();
            }
        }
        return result;
    }

    /**
     * 根据字段查询单一记录
     * @param field
     * @param query
     * @return
     */
    public DBObject selectOneByQuery(String field,String query){
        BasicDBObject dbo=new BasicDBObject();
        dbo.put(field,query);
        try {
            return this.dbCollection.findOne(dbo);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据字段查询列表
     * @param field
     * @param query
     * @return
     */
    public <T> List<DBObject>  selectListByQuery(String field,T query){
        DBCursor cur = null;
        List<DBObject> result = Lists.newArrayList();
        BasicDBObject dbo=new BasicDBObject();
        dbo.put(field,query);
        try {
            cur = this.dbCollection.find(dbo);
            while(cur.hasNext()){
                result.add(cur.next());
            }
            return result;
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(cur != null){
                cur.close();
            }
        }
        return null;
    }

    public Long insert(DBObject dbObject){
        Long id = this.getAutoIncrementId();
        dbObject.put("_id",id);
        this.dbCollection.insert(dbObject);
        return id;
    }

    public void multiInsert(List<DBObject> dbList){
        List<DBObject> insertDataList=new ArrayList<DBObject>();
        for(DBObject obj : dbList){
            long id = this.getAutoIncrementId();
            obj.put("_id",id);
            insertDataList.add(obj);
        }
        if(insertDataList != null && insertDataList.size()>0){
            this.dbCollection.insert(insertDataList);
        }

    }

    public boolean update(DBObject dbObject,DBObject targetObject){
        WriteResult result = this.dbCollection.update(dbObject,targetObject,false,true);
        return true;
    }

    public Long countList(Map<String,Object> params) {
        DBObject query = new BasicDBObject();
        if(params == null){
            return 0l;
        }
        Iterator iter = params.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            query.put(ObjectUtils.toString(entry.getKey()),entry.getValue());
        }
        return this.dbCollection.count(query);
    }

    public DBCollection getDbCollection() {
        return dbCollection;
    }

    public void delete(DBObject query){
        this.dbCollection.findAndRemove(query);
    }
}
