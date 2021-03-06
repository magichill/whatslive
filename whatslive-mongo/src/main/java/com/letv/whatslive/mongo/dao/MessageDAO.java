package com.letv.whatslive.mongo.dao;

import com.letv.whatslive.mongo.BaseDAO;
import org.springframework.stereotype.Repository;

/**
 * Created by gaoshan on 15-8-7.
 */
@Repository
public class MessageDAO extends BaseDAO {

    protected String collectionName = "message";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

}
