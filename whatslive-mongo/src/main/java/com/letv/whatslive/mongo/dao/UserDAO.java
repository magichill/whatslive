package com.letv.whatslive.mongo.dao;


import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.DateUtils;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.constant.UserConstants;
import com.letv.whatslive.model.convert.UserConvert;
import com.letv.whatslive.mongo.BaseDAO;
import com.letv.whatslive.redis.JedisDAO;
import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 用户collection操作DAO
 * Created by gaoshan on 15-07-07.
 */
@Repository
public class UserDAO extends BaseDAO {

    private final static Logger logger = LoggerFactory.getLogger(UserDAO.class);

    protected String collectionName = "user";

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

    public int saveOrUpdateUser(User user,Integer... saveType) {
        WriteResult result = null;
        if(user!=null && user.getUserId()!=null){
            DBObject query = new BasicDBObject("_id", user.getUserId());
            DBObject dbo = this.dbCollection.findOne(query);
            if (dbo == null) {//insert
                dbo = UserConvert.castUserToDBObject(user);
                if (dbo != null) {
                    result = this.dbCollection.save(dbo);
                }
            } else {//update
                DBObject update = new BasicDBObject();

                if (user.getSex() >= -1) {
                    update.put("sex", user.getSex());
                }
                if(user.getBroadCastNum() > 0){
                    update.put("broadCastNum",user.getBroadCastNum());
                }
                if(user.getLikeNum() != null && user.getLikeNum() > 0){
                    update.put("likeNum",user.getLikeNum());
                }
                if (StringUtils.isNotBlank(user.getUserName()) && saveType.length==0) {
                    update.put("userName", user.getUserName());
                }

                if (StringUtils.isNotBlank(user.getPicture()) && saveType.length==0) {
                    update.put("picture", user.getPicture());
                }

                if (StringUtils.isNotBlank(user.getNickName()) && saveType.length==0) {
                    update.put("nickName", user.getNickName());
                }

                if (saveType.length==0) {
                    update.put("introduce", user.getIntroduce());
                }
                if(ObjectUtils.toInteger(user.getQqBind(),0)>0) {
                    update.put("qqBind", user.getQqBind());
                }
                if(ObjectUtils.toInteger(user.getSinaBind(),0)>0){
                    update.put("sinaBind",user.getSinaBind());
                }

                if(ObjectUtils.toInteger(user.getWeixinBind(),0) > 0){
                    update.put("weixinBind",user.getWeixinBind());
                }
                if(StringUtils.isNotBlank(user.getQqId())){
                    update.put("qqId",user.getQqId());
                }
                if(StringUtils.isNotBlank(user.getSinaId())){
                    update.put("sinaId",user.getSinaId());
                }
                if(StringUtils.isNotBlank(user.getWeixinId())){
                    update.put("weixinId",user.getWeixinId());
                }
                update.put("updateTime", System.currentTimeMillis());
                update.put("lastLoginTime", System.currentTimeMillis());
                update.put("accessToken",user.getAccessToken());
                update.put("devIdList",user.getDevIdList());
                update.put("sso_tk",user.getSsoTk());
                result = this.dbCollection.update(query, new BasicDBObject("$set", update), true, false);
            }
        }else{
            return ObjectUtils.toInteger(this.insert(UserConvert.castUserToDBObject(user)));
        }
        return result.getN();

    }

    /**
     * 查询用户列表
     *
     * @param params 模糊查询的查询条件
     * @param orders 排序的条件
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @return
     */
    public List<User> getUserListByParams(Map<String, Object> params, Map<String, Object> orders, Integer start, Integer limit) {

        List<User> result = Lists.newArrayList();
        if (limit == null) {
            limit = DEFAULT_NUM;
        }
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject order = getOrderParams(orders);
            BasicDBObject query = getBasicDBObjectByParams(params);
            Integer skip = start == null ? 0 : start;
            DBCursor cur = this.dbCollection.find(query).skip(skip).sort(order).limit(limit);//按createTime的值倒叙排列
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                User user = UserConvert.castDBObjectToUser(dbObject);
                setValueToUser(user);
                result.add(user);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return result;
    }

    public List<User> getAllUserByParams(Map<String, Object> params){
        List<User> result = Lists.newArrayList();
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject query = getBasicDBObjectByParams(params);
            DBCursor cur = this.dbCollection.find(query);
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                User user = UserConvert.castDBObjectToUser(dbObject);
                setValueToUser(user);
                result.add(user);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 根据主键查询一个用户
     * @param uid 用户主键
     * @return 一个User对象
     */
    public User getUserById(Long uid) {
        BasicDBObject query = new BasicDBObject();
        User user = new User();
        query.put("_id", uid);
        DBObject dbObject = this.dbCollection.findOne(query);
        if (dbObject == null) return null;
        try {
            user = UserConvert.castDBObjectToUser(dbObject);
            setValueToUser(user);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return user;
    }

    public User getAdminUser() {
        BasicDBObject query = new BasicDBObject();
        User user = new User();
        query.put("role", 2);
        DBObject dbObject = this.dbCollection.findOne(query);
        if (dbObject == null) return null;
        try {
            user = UserConvert.castDBObjectToUser(dbObject);
            setValueToUser(user);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return user;
    }

    /**
     * 根据用户ID的集合查询一组用户
     * @param idList
     * @return
     */
    public Map<Long ,User> getUsersByIds(List<Long> idList){
        Map<Long ,User> users = new HashMap<Long, User>();
        BasicDBList values = new BasicDBList();
        for (Long id : idList) {
            values.add(id);
        }
        BasicDBObject queryCondition = new BasicDBObject();
        queryCondition.put("_id", new BasicDBObject("$in", values));
        DBCursor cur = this.dbCollection.find(queryCondition);
        while (cur.hasNext()) {
            DBObject dbObject = cur.next();
            User user = UserConvert.castDBObjectToUser(dbObject);
            setValueToUser(user);
            users.put(user.getUserId(), user);
        }
        return users;
    }

    /**
     * 查询用户的数量
     * @param params 查询条件参数
     * @return 满足条件的文档记录数
     */
    public Long countUserByParams(Map params) {
        //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
        BasicDBObject query = getBasicDBObjectByParams(params);
        return this.dbCollection.count(query);
    }

    /**
     * 保存用户信息
     * @param user
     * @return
     */
    public User saveUser(User user) {
        try {
            user.setUserId(getAutoIncrementId());
            user.setRole(UserConstants.role_normal);
            user.setUserType(UserConstants.userType_system);
            DBObject dbo = UserConvert.castUserToDBObject(user);
            if(user.getPicture()==null || user.getPicture().length()==0){
                dbo.removeField("picture");
            }
            WriteResult wr = this.dbCollection.insert(dbo);
            return user;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 更新用户基本信息
     * @param user
     */
    public void updateUser(User user) {
        DBObject queryCondition = new BasicDBObject();
        Long userId = user.getUserId();
        queryCondition.put("_id", userId); // 用来确定要修改的文档
        DBObject setValue = UserConvert.castUserToDBObject(user);
        //剔除主键修改
        if (setValue.containsKey("_id")) {
            setValue.removeField("_id");
        }
        if(user.getPicture()==null || user.getPicture().length()==0){
            setValue.removeField("picture");
        }
        DBObject values = new BasicDBObject("$set", setValue);
        this.dbCollection.update(queryCondition, values, false, false);
    }

    /**
     * 根据用户ID删除用户
     * @param uid
     */
    public void deleteUser(Long uid){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", uid);
        this.dbCollection.remove(query);
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
     * 根据查询参数Map获取查询条件对象
     * @param params 查询参数Map
     * @return
     */
    private BasicDBObject getBasicDBObjectByParams(Map params){
        //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
        BasicDBObject query = new BasicDBObject();// 新建查询基类对象 dbo
        if (params != null && params.size() > 0) {
            if (params.get("userName") != null) {
                Pattern pattern = Pattern.compile("^.*" + params.get("userName") + ".*$", Pattern.CASE_INSENSITIVE);
                query.put("userName", pattern);
            }
            if (params.get("nickName") != null) {
                Pattern pattern = Pattern.compile("^.*" + params.get("nickName") + ".*$", Pattern.CASE_INSENSITIVE);
                query.put("nickName", pattern);
            }
            if (params.get("userRole") != null) {
                query.put("role", ObjectUtils.toInteger(params.get("userRole")));
            }
            if (params.get("userLevel") != null) {
                query.put("level", ObjectUtils.toInteger(params.get("userLevel")));
            }
            if (params.get("userType") != null) {
                query.put("userType", ObjectUtils.toInteger(params.get("userType")));
            }
            if(params.get("userTypeNotEqual") != null){
                query.put("userType", new BasicDBObject("$ne", ObjectUtils.toLong(params.get("userTypeNotEqual"))));
            }
            if (params.get("broadCastNum") != null) {
                query.put("broadCastNum", new BasicDBObject("$gt", ObjectUtils.toLong(params.get("broadCastNum"))));
            }
            if (params.get("userIdNotEqual") != null) {
                query.put("_id", new BasicDBObject("$ne", ObjectUtils.toLong(params.get("userIdNotEqual"))));
            }
        }
        return query;
    }

    /**
     * 设置用户信息中的特殊值
     * @param user
     */
    private void setValueToUser(User user) {
        user.setCreateTimeStr(user.getCreateTime() != null ?
                DateUtils.long2YMDHMS(user.getCreateTime()):"");
        user.setLastLoginTimeStr(user.getLastLoginTime() != null ?
                DateUtils.long2YMDHMS(user.getLastLoginTime()):"");
    }

    public void updateDataAfterClose(Long id,Long programId,String... videoId){
        DBObject query = new BasicDBObject();
        query.put("_id",id);
        DBObject incObj = new BasicDBObject();
        incObj.put("watchNum", jedisDAO.getJedisReadTemplate().smembers(Constants.LIVE_ONLINE_TOTALUSER_KEY + programId).size());
        incObj.put("likeNum",ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_LIKE_KEY + programId), 0l));
        incObj.put("commentNum",ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_COMMENT_KEY+programId),0l));
        this.update(query, new BasicDBObject("$inc", incObj));
        if(null != videoId && videoId.length >0 && StringUtils.isNotBlank(videoId[0])){
            this.update(query,new BasicDBObject("videoId",videoId[0]));
        }
    }
}
