package com.letv.whatslive.web.service.tag;

import com.letv.whatslive.model.Tag;
import com.letv.whatslive.mongo.dao.TagDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/8/19.
 */
@Component
public class TagService {
    @Autowired
    TagDAO tagDAO;

    public Tag getTagById(Long id){
        return tagDAO.getTagById(id);
    }

    public List<Tag> getTagListByParams(Map params){
        return tagDAO.getTagListByParams(params);
    }
}
