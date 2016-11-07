package com.letv.whatslive.inner.push.schedule;

import com.alibaba.fastjson.JSON;
import com.letv.whatslive.common.httpclient.HttpClientUtil;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.mongo.dao.UserDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 定时更新用户第三方头像
 * Created by gaoshan on 15-9-21.
 */
@Component
public class UserPicService {

    private static final Logger logger = LoggerFactory.getLogger(UserPicService.class);

    @Autowired
    private UserDAO userDAO;

    public static final String USER_INFO_URL = "http://api.sso.letv.com/api/getUserByID/uid/";

    public void updateUserPic(){
        List<DBObject> users = userDAO.likeFindAll("tx298.png", "picture");
        for(DBObject user : users) {
            if (user != null) {
                String uid = ObjectUtils.toString(user.get("_id"));
                String url = USER_INFO_URL + uid + "/dlevel/total";
                String result = "";
                String picture = "";
                try {
                    result = HttpClientUtil.requestGet(url).getContent();
                    Map data = JSON.parseObject(result, Map.class);
                    if (ObjectUtils.toInteger(data.get("status")).intValue() == 1) {
                        String strBean = ObjectUtils.toString(data.get("bean"));
                        if (StringUtils.isNotBlank(strBean)) {
                            Map bean = JSON.parseObject(strBean, Map.class);
                            picture = ObjectUtils.toString(bean.get("picture"));
                        }
                        if (picture.indexOf("tx298.png") > -1) {
                            logger.info("no update in uc,uid=" + uid);
                        } else {
                            DBObject query = new BasicDBObject();
                            query.put("_id", ObjectUtils.toLong(uid));
                            DBObject update = new BasicDBObject();
                            update.put("picture", picture);
                            userDAO.update(query, new BasicDBObject("$set", update));
                            logger.info("success to update picture,uid=" + uid);
                        }
                    } else {
                        logger.error("fail to get user info from uc,uid=" + uid);
                    }
                } catch (Exception e) {
                    logger.error("fail to update user pic", e);
                }
            } else {
                logger.info("no user needs to update picture");
            }
        }
    }


}
