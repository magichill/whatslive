package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.model.BindUser;
import com.letv.whatslive.model.convert.BindUserConvert;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-7-24.
 */
@Repository
public class BindUserDAO extends BaseDAO {

    protected String collectionName = "bindUser";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    public int saveOrUpdateBindUser(BindUser user) {
        WriteResult result = null;
        if(user!=null){
            DBObject query = new BasicDBObject("bindThirdId", user.getBindThirdId());
            query.put("bindType",user.getBindType());
            DBObject dbo = this.dbCollection.findOne(query);
            if (dbo == null) {//insert
                dbo = BindUserConvert.castUserToDBObject(user);

                if (dbo != null) {
                    result = this.dbCollection.save(dbo);
                }
            } else {//update
                DBObject update = new BasicDBObject();

                update.put("token",user.getBindToken());
                update.put("tokenSecret",user.getBindTokenSecret());

                result = this.dbCollection.update(query, new BasicDBObject("$set", update), false, false);
            }
        }else{
            this.insert(BindUserConvert.castUserToDBObject(user));
            return 0;
        }
        return result.getN();

    }

    public Long insert(DBObject dbObject){
        long id = this.getAutoIncrementId();
        dbObject.put("_id",id);
        this.dbCollection.insert(dbObject);
        return id;
    }

    /**
     * 根据userId获取所有的BindUser的List
     * @param userId
     * @return
     */
    public List<BindUser> getAllBindUserByUserId(Long userId){
        List<BindUser> bindUserList =Lists.newArrayList();
        DBObject params = new BasicDBObject();
        params.put("bindUser.$id",userId);
        List<DBObject> result = this.selectAll(params, null);
        for(DBObject dbObject : result){
            bindUserList.add(BindUserConvert.castDBObjectToUser(dbObject));
        }
        return bindUserList;
    }
}
