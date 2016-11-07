package com.letv.whatslive.mongo.dao;

import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.DBObject;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by gaoshan on 15-8-3.
 */
@Repository
public class ThirdFriendDAO extends BaseDAO {

    protected String collectionName = "thirdFriend";


    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

}
