package com.letv.whatslive.mongo.dao;

import com.letv.whatslive.mongo.BaseDAO;
import org.springframework.stereotype.Repository;

/**
 * Created by gaoshan on 15-10-23.
 */
@Repository
public class StartConstantDAO extends BaseDAO {

    protected String collectionName = "start.constant";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }
}
