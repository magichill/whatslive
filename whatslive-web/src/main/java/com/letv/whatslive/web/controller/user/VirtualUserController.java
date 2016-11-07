package com.letv.whatslive.web.controller.user;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.constant.UserConstants;
import com.letv.whatslive.web.service.user.UserService;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.constant.WebConstants;
import com.letv.whatslive.web.controller.PubController;
import com.letv.whatslive.web.util.String.StringUtils;
import com.letv.whatslive.web.util.util.DateUtils;
import com.letv.whatslive.web.util.util.MD5Util;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by wangjian7 on 2015/7/7.
 */
@Controller
@RequestMapping("/user/virtualUser")
public class VirtualUserController extends PubController {

    private static final Logger logger = LoggerFactory.getLogger(VirtualUserController.class);

    @Autowired
    private UserService userService;


    /**
     * 显示用户列表页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/user/virtualUser");
        return modelAndView;
    }

    /**
     *  统计用户相关的数量信息用于页面显示
     * @param response
     * @return
     */
    @RequestMapping("/countUser")
    @ResponseBody
    public String getAllUserNum(HttpServletResponse response){
        Map<String, Object> result = getSuccessMap();
        Map params = Maps.newHashMap();
        params.put("userType", UserConstants.userType_system);
        long alluser = userService.countUserByParams(params);
        result.put("allUser", alluser);
        setResContent2Text(response);
        return map2JsonString(result);
    }

    /**
     * 查询用户列表
     *
     * @param valueMap
     * @param response
     * @return
     */
    @RequestMapping("/list.json")
    @ResponseBody
    public Map<String, Object> list(@RequestBody MultiValueMap valueMap, HttpServletResponse response) {
        Map<String, Object> param = valueMap.toSingleValueMap();
        Integer start = ObjectUtils.toInteger(param.get("iDisplayStart"));
        Integer limit = ObjectUtils.toInteger(param.get("iDisplayLength"));
        String sEcho = ObjectUtils.toString(param.get("sEcho"));
        String search_nickName = ObjectUtils.toString(param.get("search_nickName"));

        Map params = Maps.newHashMap();
        if (!StringUtils.isBlank(search_nickName)) {
            params.put("nickName", search_nickName);
        }
        params.put("userType", UserConstants.userType_system);
        Long userNum = userService.countUserByParams(params);
        Map orders = Maps.newHashMap();
        orders.put("role", -1);
        List<User> userList = userService.getUserListByParams(params, orders, start, limit);

        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
        result.put("iTotalRecords", userNum); //total
        result.put("iTotalDisplayRecords", userNum); //totalAfterFilter
        result.put("aaData", userList.toArray());
        result.put("userNum", userNum);
        setResContent2Json(response);
        return result;
    }

    /**
     * 跳转到修改用户页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/userNew")
    public ModelAndView userNew(WebRequest webRequest) {
        ModelAndView modelAndView = new ModelAndView();
        Long uid = ObjectUtils.toLong(webRequest.getParameter("uid"));
        User user = new User();
        if (uid != null) {
            user = userService.getUserById(uid);
            modelAndView.setViewName("user/virtualUser_edit");
        } else {
            modelAndView.setViewName("user/virtualUser_edit");
        }
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @RequestMapping("/userSave")
    @ResponseBody
    public Map<String, Object> userSave(User user, String vPic, String sPic, HttpServletResponse response) {
        ResultBean resultBean = ResultBean.getTrueInstance();
        if(StringUtils.isBlank(sPic) && StringUtils.isBlank(user.getPicture())){
            return getFailMap("必须上传头像");
        }
        if (user.getUserId() != null) {  // 修改
            userService.updateUser(user, vPic, sPic);
        }else {
            resultBean = userService.insertUser(user, vPic, sPic);
        }
        // 返回结果
        setResContent2Json(response);
        if(!resultBean.isFlag()){
            return getFailMap(resultBean.getMsg());
        }else{
            return getSuccessMap();
        }

    }

    /**
     * 上传头像图片
     *
     * @return
     */
    @RequestMapping("/uploadImg")
    @ResponseBody
    public String uploadImg(@RequestParam("uploadFile") MultipartFile uploadFile, HttpServletResponse response) {
        try {
            if (uploadFile == null || uploadFile.isEmpty()) {
                setResContent2Text(response);
                return map2JsonString(getFailMap("参数不能为空！"));
            }
            // 处理上传文件
            StringBuffer savePath = new StringBuffer(WebConstants.UPLOAD_PATH_IMGS);
            savePath.append(DateUtils.getToday()).append("/");
            savePath.append(MD5Util.inputStreamMd5(uploadFile.getInputStream()));
            // 获取后缀名
            String extName = getFileExtName(uploadFile.getOriginalFilename());
            savePath.append(extName);

            String localUrl = WebConstants.UPLOAD_PATH_ROOT + savePath.toString();
            stream2File(uploadFile.getInputStream(), localUrl);
            // 返回结果
            Map<String, Object> result = getSuccessMap();
            result.put("filePath", savePath.toString());
            result.put("fileUrl", WebConstants.UPLOAD_SERVER_HOST + savePath.toString());
            result.put("localUrl", localUrl);
            setResContent2Text(response);
            return map2JsonString(result);
        } catch (IOException e) {
            setResContent2Text(response);
            return map2JsonString(getFailMap("上传图片异常！"));
        }
    }
    /**
     * 删除用户
     *
     * @param id 用户id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String, Object> delete(String id) {
        if (id == null) {
            logger.error("非法参数，id为空");
            return getFailMap("参数不能为空!");
        }

        ResultBean resultBean = userService.deleteUserById(ObjectUtils.toLong(id));
        if(resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }
    }

    @RequestMapping("/saveBatchUser")
    @ResponseBody
    public Map<String, Object> batchSaveUser(){
        File keywordFileTmp = new File(WebConstants.UPLOAD_PATH_ROOT+"user.txt");
        if(keywordFileTmp.exists()){
            try {
                List<String> userNameList = FileUtils.readLines(keywordFileTmp,"UTF-8");
                File filePath = new File(WebConstants.UPLOAD_PATH_ROOT+"userPic/");
                File[] userPicList = filePath.listFiles();
                if(userPicList.length == userNameList.size()){

                    List<String> resultString = new ArrayList<String>(userPicList.length);
                    for(int i = 0;i<userNameList.size();i++){

                        User user = new User();
                        user.setNickName(userNameList.get(i));
                        user.setCreateTime(System.currentTimeMillis());
                        user.setLastLoginTime(System.currentTimeMillis());
                        user.setLevel(0);
                        user.setUserStatus(1);
                        user.setUserType(3);
                        String vPic = userPicList[i].getAbsolutePath();
                        String sPic = WebConstants.UPLOAD_SERVER_HOST+"userPic/"+userPicList[i].getName();
                        ResultBean resultBean = userService.insertUser(user, vPic, sPic);
                        resultString.add(userNameList.get(i)+":"+userPicList[i].getName()+" result:"+resultBean.isFlag());
                    }
                    File result = new File(WebConstants.UPLOAD_PATH_ROOT+"user_result_"+new Date()+".txt");
                    FileUtils.writeLines(result, resultString);
                    FileUtils.deleteQuietly(filePath);
                    FileUtils.deleteQuietly(keywordFileTmp);
                }

            } catch (IOException e) {
                logger.error("", e);
            }
        }
        return getSuccessMap();
    }

    @RequestMapping("/batchUpdateUser")
    @ResponseBody
    public Map<String, Object> batchUpdateUser(){
        File keywordFileTmp = new File(WebConstants.UPLOAD_PATH_ROOT+"user_update.txt");
        if(keywordFileTmp.exists()){
            try {
                List<String> userList = FileUtils.readLines(keywordFileTmp,"UTF-8");
                File filePath = new File(WebConstants.UPLOAD_PATH_ROOT+"userPicUpdate/");
                File[] userPicList = filePath.listFiles();
                if(userPicList.length == userList.size()){

                    List<String> resultString = new ArrayList<String>(userPicList.length);
                    for(int i = 0;i<userList.size();i++){

                        User user = new User();
                        user.setUserId(Long.valueOf(userList.get(i).split(",")[0]));
                        user.setNickName(userList.get(i).split(",")[1]);
                        user.setCreateTime(System.currentTimeMillis());
                        user.setLastLoginTime(System.currentTimeMillis());
                        user.setLevel(0);
                        user.setUserStatus(1);
                        user.setUserType(3);
                        String vPic = userPicList[i].getAbsolutePath();
                        String sPic = WebConstants.UPLOAD_SERVER_HOST+"userPicUpdate/"+userPicList[i].getName();
                        ResultBean resultBean = userService.updateUser(user,vPic,sPic);
                        resultString.add(userList.get(i)+":"+userPicList[i].getName()+" result:");
                    }
                    File result = new File(WebConstants.UPLOAD_PATH_ROOT+"user_update_result_"+System.currentTimeMillis()+".txt");
                    FileUtils.writeLines(result, resultString);
                    FileUtils.deleteQuietly(filePath);
                    FileUtils.deleteQuietly(keywordFileTmp);
                }

            } catch (IOException e) {
                logger.error("",e);
            }
        }
        return getSuccessMap();
    }

    /**
     * 机器人启停
     * @param op 机器人op
     * @return
     */
    @RequestMapping("/opRobot")
    @ResponseBody
    public Map<String, Object> opRobot(String op) {
        if (op == null) {
            logger.error("非法参数，op为空");
            return getFailMap("参数不能为空!");
        }
        ResultBean resultBean = userService.opRobot(op);
        if (resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }
    }
    /**
     * 机器人评论启停
     * @param op 机器人评论op
     * @return
     */
    @RequestMapping("/opRobotComment")
    @ResponseBody
    public Map<String, Object> opRobotComment(String op) {
        if (op == null) {
            logger.error("非法参数，op为空");
            return getFailMap("参数不能为空!");
        }
        ResultBean resultBean = userService.opRobotComment(op);
        if (resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }
    }

}
