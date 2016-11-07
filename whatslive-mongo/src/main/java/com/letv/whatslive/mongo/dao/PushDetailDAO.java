package com.letv.whatslive.mongo.dao;

import com.letv.whatslive.model.PushDetail;
import com.letv.whatslive.model.convert.PushDetailConvert;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangjian7 on 2015/8/20.
 */
@Repository
public class PushDetailDAO extends BaseDAO {

    private static final Logger logger = LoggerFactory.getLogger(PushDetailDAO.class);

    protected String collectionName = "pushDetail";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    public void saveOrUpdatePushDetail(PushDetail pushDetail){
        if(null==pushDetail.getId()){
            pushDetail.setId(getAutoIncrementId());
            DBObject db = PushDetailConvert.castPushLogToDBObject(pushDetail);
            this.getDbCollection().insert(db);
        }else{
            DBObject query = new BasicDBObject();
            query.put("_id", pushDetail.getId());
            DBObject db = PushDetailConvert.castPushLogToDBObject(pushDetail);
            this.getDbCollection().update(query, db);

        }
        //TODO 异常处理
    }

    public PushDetail getpushDetailByMsgId(String msgId){
        PushDetail pushDetail = null;
        if(null != msgId){
            BasicDBObject query = new BasicDBObject();
            query.append("msgId", msgId);
            DBObject dbObject = this.getDbCollection().findOne(query);
            pushDetail = PushDetailConvert.castDBObjectToPushDetail(dbObject);
        }
        return pushDetail;
    }

    public void saveOrUpdatePushDetailList(List<PushDetail> pushDetailList){
        for(PushDetail pushDetail : pushDetailList){
            saveOrUpdatePushDetail(pushDetail);
        }
    }
}
