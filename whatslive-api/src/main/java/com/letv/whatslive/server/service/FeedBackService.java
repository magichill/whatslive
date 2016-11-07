package com.letv.whatslive.server.service;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.mongo.dao.FeedBackDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by gaoshan on 15-8-31.
 */
@Service
public class FeedBackService  {

    @Resource
    private FeedBackDAO feedBackDAO;

    public void submitFeedBack(DBObject feedback){
        feedBackDAO.insert(feedback);
    }
}
