package com.letv.whatslive.server.service;

import com.letv.whatslive.model.Comment;
import com.letv.whatslive.mongo.dao.CommentDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by gaoshan on 15-7-9.
 */

@Service
public class CommentService {

    private final static Logger logger = LoggerFactory.getLogger(CommentService.class);


    @Resource
    private CommentDAO commentDAO;

    public boolean insertComment(Comment comment){
        int result = commentDAO.insertComment(comment);
        if(result > 0){
            return true;
        }else{
            return false;
        }

    }
}
