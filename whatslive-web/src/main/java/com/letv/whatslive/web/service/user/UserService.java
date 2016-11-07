package com.letv.whatslive.web.service.user;

import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.BindUser;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.constant.UserConstants;
import com.letv.whatslive.mongo.dao.BindUserDAO;
import com.letv.whatslive.mongo.dao.UserDAO;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.constant.ServiceConstants;
import com.letv.whatslive.web.service.common.ApiInnerService;
import com.letv.whatslive.web.util.String.StringUtils;
import com.letv.whatslive.web.util.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 用户操作service
 * Created by wangjian7 on 2015/7/7.
 */
@Component
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private BindUserDAO bindUserDAO;

    @Autowired
    private ApiInnerService apiInnerService;

    @Autowired
    private JedisDAO jedisDAO;

    /**
     * 根据条件查询所有用户
     * @param params 模糊查询的查询条件，(uid or userName or nickName) and userStatus =
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @return 满足条件的对象的集合
     */
    public List<User> getUserListByParams(Map params, Map orders, Integer start, Integer limit) {
        List<User> userList = userDAO.getUserListByParams(params, orders, start, limit);
        //如果是查询所有非虚拟用户，则需要根据用户的id查询绑定的设备信息
        if(!(params.containsKey("userType")
                && ObjectUtils.toInteger(params.get("userType"))==UserConstants.userType_system)){
            for(User user : userList){
                //TODO 查询设备类型
            }
        }
        return userList;
    }

    public List<User> getAllVirtualUser(Map<String, Object> params){
        params.put("userType", UserConstants.userType_system);
        return userDAO.getAllUserByParams(params);
    }

    /**
     * 根据用户id查询一条数据
     * @param uid 用户id,在库中是_id
     * @return User对象
     */
    public User getUserById(Long uid) {
        return userDAO.getUserById(uid);
    }

    /**
     * 根据userId的List查询一组数据
     * @param uids
     * @return
     */
    public Map<Long, User> getUserByIds(List<Long> uids){
        return userDAO.getUsersByIds(uids);
    }

    /**
     * 根据指定条件查询用户的数量
     * @param params 模糊查询的查询条件，(uid or userName or nickName) and userStatus =
     * @return 满足条件的结果记录数
     */
    public Long countUserByParams(Map params) {
        return userDAO.countUserByParams(params);
    }

    /**
     * 插入新增用户信息，目前主要用户新增虚拟用户
     * @param user
     * @param vPic
     * @param sPic
     * @return
     */
    public ResultBean insertUser(User user, String vPic, String sPic){
        ResultBean res = ResultBean.getTrueInstance();
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());
        user = userDAO.saveUser(user);
        String message = null;
        if(null == user){
            res.setFalseAndMsg("用户信息保存失败");
        }else{
            message = saveImg(user, vPic, sPic);
        }
        if(null != message){
            res.setFalseAndMsg(message);
        }
        return res;
    }

    /**
     * 更新用户信息，包括真实用户和虚拟用户
     * @param user
     * @param vPic
     * @param sPic
     * @return
     */
    public ResultBean updateUser(User user, String vPic, String sPic) {
        ResultBean res = ResultBean.getTrueInstance();
        user.setUpdateTime(System.currentTimeMillis());
        userDAO.updateUser(user);
        String message = saveImg(user, vPic, sPic);
        if(null != message){
            res.setFalseAndMsg(message);
        }
        return res;
    }

    /**
     * 根据用户ID删除用户信息
     * @param uid
     * @return
     */
    public ResultBean deleteUserById(Long uid){
        ResultBean res = ResultBean.getTrueInstance();
        userDAO.deleteUser(uid);
        return res;

    }

    /**
     * 根据用户id拼接用户绑定所有第三方的字符串，多个以英文逗号分隔，例如facebook,twitter
     * @param userId
     * @return
     */
    private String getThirdBindString(Long userId) {
        StringBuilder thirdBindUser = new StringBuilder();
        List<BindUser> bindUserList = bindUserDAO.getAllBindUserByUserId(userId);
        for(BindUser bindUser : bindUserList){
            thirdBindUser.append(UserConstants.userTypeMap.get(bindUser.getBindType())).append(",");
        }
        if(thirdBindUser.length()>0){
            thirdBindUser.deleteCharAt(thirdBindUser.length()-1);
        }
        return thirdBindUser.toString();
    }

    /**
     * 保存用户头像信息，上传到相应的存储服务器
     * @param user
     * @param vPic
     * @param sPic
     * @return
     */
    private String saveImg(User user, String vPic, String sPic){
        String message = null;
        if (!StringUtils.isBlank(vPic) && !StringUtils.isBlank(sPic)) {
            File file = new File(vPic);
            if (file.exists()) {
                //生成上传图片的key值，前缀+文件MD5值
                String md5= MD5Util.fileMd5(file);
                String key = apiInnerService.getAbstractUploadService()
                        .getKey(ServiceConstants.UPLOAD_CODE_USER_PIC, md5, ObjectUtils.toString(user.getUserId()));

                if(!apiInnerService.getAbstractUploadService().uploadFile(md5, file.length(), vPic, sPic, key)){
                    message = "用户头像上传失败！请重新录入!";
                }else{
                    //如果使用的是AWS S3服务则需要将用户头像设置为上传AWS S3后返回的URL
                    if(ServiceConstants.FILE_UPLOAD_TYPE_AWS_S3.equals(ServiceConstants.FILE_UPLOAD_TYPE)){
                        user.setPicture(ServiceConstants.AWS_S3_URL_PREX+key);
                    }
                }
            } else {
                message = "用户头像文件不存在！";
            }
        }
        return message;
    }

    /**
     * 根据操作类型控制是否开启机器人
     * @param op
     * @return
     */
    public ResultBean opRobot(String op){
        ResultBean res = ResultBean.getTrueInstance();
        try{
            if(op.equals("1")){
                jedisDAO.getJedisWriteTemplate().del("robot_live_switch");
            }else{
                jedisDAO.getJedisWriteTemplate().setex("robot_live_switch","0",60*60*24*365);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            res.setFalseAndMsg(e.getMessage());
        }
        return res;
    }
    /**
     * 根据操作类型控制是否开启机器人评论
     * @param op
     * @return
     */
    public ResultBean opRobotComment(String op){
        ResultBean res = ResultBean.getTrueInstance();
        try{
            if(op.equals("1")){
                jedisDAO.getJedisWriteTemplate().del("robot_live_user_comment_switch");
            }else{
                jedisDAO.getJedisWriteTemplate().setex("robot_live_user_comment_switch","0",60*60*24*365);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            res.setFalseAndMsg(e.getMessage());
        }
        return res;
    }

    /**
     * 操作一个直播中的机器人
     * @param id
     * @param op
     * @return
     */
    public ResultBean opOneLiveRobot(String id, String op){
        ResultBean res = ResultBean.getTrueInstance();
        try{
            if(op.equals("1")){
//                jedisDAO.getJedisWriteTemplate().del("robot_live_switch");
            }else{
                if(jedisDAO.getJedisWriteTemplate().get("robot_live_chat_"+id)==null){
                    res.setFalseAndMsg("机器人尚未启动！");
                }else if(jedisDAO.getJedisWriteTemplate().get("robot_live_chat_"+id).equals("1")){
                    jedisDAO.getJedisWriteTemplate().setex("robot_live_chat_"+id,"3",60*60*24);
                }else if(jedisDAO.getJedisWriteTemplate().get("robot_live_chat_"+id).equals("2")){
                    res.setFalseAndMsg("机器人已经关闭！");
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            res.setFalseAndMsg(e.getMessage());
        }
        return res;

    }


}
