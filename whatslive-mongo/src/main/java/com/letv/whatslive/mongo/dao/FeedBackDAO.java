package com.letv.whatslive.mongo.dao;

import com.letv.whatslive.mongo.BaseDAO;
import org.springframework.stereotype.Repository;

/**
 * Created by gaoshan on 15-8-31.
 */
@Repository
public class FeedBackDAO extends BaseDAO {

    protected String collectionName = "feedback";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }
}
