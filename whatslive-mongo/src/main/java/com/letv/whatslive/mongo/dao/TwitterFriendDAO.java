package com.letv.whatslive.mongo.dao;

import com.letv.whatslive.mongo.BaseDAO;
import org.springframework.stereotype.Repository;

/**
 * Created by gaoshan on 15-8-5.
 */
@Repository
public class TwitterFriendDAO extends BaseDAO {


    protected String collectionName = "thirdFriend.twitter";


    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }
}
