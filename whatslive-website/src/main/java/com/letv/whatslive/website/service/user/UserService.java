package com.letv.whatslive.website.service.user;

import com.letv.whatslive.model.User;
import com.letv.whatslive.mongo.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直播用户信息取得服务
 * Created by haojiayao on 2015/8/12.
 */
@Component
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    /**
     * 根据直播用户ID列表获取用户信息
     * @param userIdList 用户ID列表
     * @return
     */
    public Map<Long ,User> getUsersByIds(List<Long> userIdList) {

        Map<Long ,User> users = new HashMap<Long, User>();

        users = userDAO.getUsersByIds(userIdList);

        return users;
    }

    /**
     * 根据直播用户ID获取用户信息
     * @param  userId 用户ID
     * @return
     */
    public User getUsersById(Long userId) {

        User user = userDAO.getUserById(userId);

        return user;
    }
}
