package com.letv.whatslive.web.controller.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Device;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.constant.UserConstants;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.controller.PubController;
import com.letv.whatslive.web.service.user.DeviceService;
import com.letv.whatslive.web.service.user.UserService;
import com.letv.whatslive.web.service.video.VideoService;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/7/7.
 */
@Controller
@RequestMapping("/user/realUser")
public class RealUserController extends PubController {

    private static final Logger logger = LoggerFactory.getLogger(RealUserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private VideoService videoService;

    /**
     * 显示用户列表页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/user/realUser");
        return modelAndView;
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
        String search_userRole = ObjectUtils.toString(param.get("search_userRole"));
        String search_userLevel = ObjectUtils.toString(param.get("search_userLevel"));
        String search_broadCastNum = ObjectUtils.toString(param.get("search_broadCastNum"));
        Map params = Maps.newHashMap();
        if (StringUtils.isNotBlank(search_nickName)) {
            params.put("nickName", search_nickName);
        }
        if (StringUtils.isNotBlank(search_userRole)) {
            params.put("userRole", search_userRole);
        }
        if (StringUtils.isNotBlank(search_userLevel)) {
            params.put("userLevel", search_userLevel);
        }
        if (StringUtils.isNotBlank(search_broadCastNum)) {
            params.put("broadCastNum", search_broadCastNum);
        }
        params.put("userTypeNotEqual", UserConstants.userType_system);
        Long userNum = userService.countUserByParams(params);
        List<User> userList = userService.getUserListByParams(params, null, start, limit);

        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
        result.put("iTotalRecords", userNum); //total
        result.put("test", userNum); //total
        result.put("iTotalDisplayRecords", userNum); //totalAfterFilter
        result.put("aaData", userList.toArray());
        setResContent2Json(response);
        return result;
    }

    @RequestMapping("/countUser")
    @ResponseBody
    public String getAllUserNum(HttpServletResponse response){
        Map<String, Object> result = getSuccessMap();
        Map params = Maps.newHashMap();
        params.put("userTypeNotEqual", UserConstants.userType_system);
        long alluser = userService.countUserByParams(params);
        params.put("broadCastNum", 0L);
        long allBroadcastedUser = userService.countUserByParams(params);
        result.put("allUser", alluser);
        result.put("allBroadcastedUser", allBroadcastedUser);
        setResContent2Text(response);
        return map2JsonString(result);
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
            modelAndView.setViewName("user/realUser_edit");
        } else {
            //CMS端暂时没有新增真实用户功能
            //modelAndView.setViewName("user/user_new");
        }
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    /**
     * 查询用户的设备列表
     *
     * @param valueMap
     * @param response
     * @return
     */
    @RequestMapping("/deviceList")
    @ResponseBody
    public Map<String, Object> deviceList(@RequestBody MultiValueMap valueMap, @RequestParam("uid") Long uid, HttpServletResponse response) {
        Map<String, Object> param = valueMap.toSingleValueMap();
        Integer start = ObjectUtils.toInteger(param.get("iDisplayStart"));
        Integer limit = ObjectUtils.toInteger(param.get("iDisplayLength"));

        Map queryMap = new HashMap();
        queryMap.put("userId", uid);
        User user = userService.getUserById(uid);
        Long deviceNum = user.getDevIdList()==null?0L:user.getDevIdList().size();
        List<Device> deviceList = Lists.newArrayList();
        if(deviceNum>0){
            Map params = Maps.newHashMap();
            params.put("devIdList",user.getDevIdList());
            deviceList = deviceService.getDeviceListByIds(params, start, limit);
        }
        Map<String, Object> result = getSuccessMap();
        result.put("iTotalRecords", deviceNum); //total
        result.put("iTotalDisplayRecords", deviceNum); //totalAfterFilter
        result.put("aaData", deviceList.toArray());
        setResContent2Json(response);
        return result;
    }

    @RequestMapping("/videoList")
    @ResponseBody
    public Map<String, Object> videoList(@RequestBody MultiValueMap valueMap, @RequestParam("userid") Long userId, HttpServletResponse response) {
        Map<String, Object> param = valueMap.toSingleValueMap();
        Integer start = ObjectUtils.toInteger(param.get("iDisplayStart"));
        Integer limit = ObjectUtils.toInteger(param.get("iDisplayLength"));
        Map queryMap = new HashMap();
        queryMap.put("userId", userId);
        Map orderMap = new HashMap();
        orderMap.put("createTime", -1);
        Long videoNum = videoService.countProgramByParams(queryMap);
        List<Program> videoListList = videoService.getProgramListByParams(orderMap, queryMap, start, limit);
        for(Program program : videoListList){
            //需要将program中的UserRef设置为空，否则会导致返回到页面转json出错
            program.setUserRef(null);
        }
        Map<String, Object> result = getSuccessMap();
        result.put("iTotalRecords", videoNum); //total
        result.put("iTotalDisplayRecords", videoNum); //totalAfterFilter
        result.put("aaData", videoListList.toArray());
        setResContent2Json(response);
        return result;
    }

    @RequestMapping("/userSave")
    @ResponseBody
    public Map<String, Object> userSave(User user, HttpServletResponse response) {
        if (user.getUserId() != null) {  // 修改
            User primaryUser = userService.getUserById(user.getUserId());
            primaryUser.setLevel(user.getLevel());
            primaryUser.setRole(user.getRole());
            primaryUser.setIntroduce(user.getIntroduce());
            primaryUser.setUpdateTime(new Date().getTime());
            userService.updateUser(primaryUser, null, null);
        }
        // 返回结果
        setResContent2Json(response);
        return getSuccessMap();
    }

    /**
     * 修改用户状态
     * @param id 用户id
     * @return
     */
    @RequestMapping("/effect")
    @ResponseBody
    public Map<String, Object> effect(String id, String op, HttpServletRequest request) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(op)) {
            logger.error("非法参数，id和op为空");
            return getFailMap("参数不能为空!");
        }
        User user = userService.getUserById(ObjectUtils.toLong(id));
        user.setUserStatus(ObjectUtils.toInteger(op));
        ResultBean resultBean = userService.updateUser(user, null, null);
        if(resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }

    }

}
