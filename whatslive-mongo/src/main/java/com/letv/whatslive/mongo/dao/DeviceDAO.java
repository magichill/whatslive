package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Device;
import com.letv.whatslive.model.convert.DeviceConvert;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 设备Collection操作DAO
 * Created by wangjian7 on 2015/7/8.
 */
@Repository
public class DeviceDAO extends BaseDAO {
    protected String collectionName = "device";

    private static final Logger logger = LoggerFactory.getLogger(DeviceDAO.class);

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    public String saveOrUpdate(Device device) {
        DBObject query = new BasicDBObject("_id", device.getId());
        DBObject dbo = this.dbCollection.findOne(query);
        if (dbo == null) {
            dbo = DeviceConvert.castDeviceToDBObject(device);
            if (dbo != null) {
                this.dbCollection.save(dbo);
                return (String)dbo.get("_id");
            }
        } else {
            DBObject update = new BasicDBObject();
            if (StringUtils.isNotBlank(device.getDevToken())) {
                update.put("devToken", device.getDevToken());
            }
            if (device.getLastActiveTime() > 0) {
                update.put("lastActiveTime", device.getLastActiveTime());
            }

            if (device.getLastActiveTime() > 0) {
                update.put("lastActiveTime", device.getLastActiveTime());
            }

            if (device.getPlatformId() > 0) {
                update.put("platformId", device.getPlatformId());
            }

            if (device.getEditionId() > 0) {
                update.put("editionId", device.getEditionId());
            }
            update.put("updateTime", System.currentTimeMillis());
            this.dbCollection.update(query, new BasicDBObject("$set", update), false, false);
            return device.getId();
        }

        return "";
    }

    /**
     * 根据查询条件查询查询所有的设备列表
     * @param params
     * @param start
     * @param limit
     * @return
     */
    public List<Device> getDeviceListByParams(Map<String, Object> params, Integer start, Integer limit) {
        List<Device> result = Lists.newArrayList();
        if (limit == null) {
            limit = DEFAULT_NUM;
        }
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject query = getBasicDBObjectByParams(params);
            Integer skip = start == null ? 0 : start;
            DBCursor cur = this.dbCollection.find(query).skip(skip).limit(limit);//按照视频类型和创建时间倒序
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                Device device = DeviceConvert.castDBObjectToDevice(dbObject);
                result.add(device);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据查询条件Map获取查询条件对象
     * @param params
     * @return
     */
    private BasicDBObject getBasicDBObjectByParams(Map params){
        //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
        BasicDBObject query = new BasicDBObject();// 新建查询基类对象 dbo
        if (params != null && params.size() > 0) {
            if (params.get("devIdList") != null) {
                List<String> devIdList = (List<String>)params.get("devIdList");
                BasicDBList values = new BasicDBList();
                for (String id : devIdList) {
                    values.add(id);
                }
                BasicDBObject queryCondition = new BasicDBObject();
                query.put("_id", new BasicDBObject("$in", values));
            }
        }

        return query;
    }

}
