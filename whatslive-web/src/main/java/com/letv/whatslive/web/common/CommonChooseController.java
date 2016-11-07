package com.letv.whatslive.web.common;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.model.constant.PublicConstants;
import com.letv.whatslive.web.controller.PubController;
import com.letv.whatslive.web.service.activity.ActivityService;
import com.letv.whatslive.web.service.user.UserService;
import com.letv.whatslive.web.service.video.VideoService;
import com.letv.whatslive.web.util.WebUtils;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/10/27.
 */
@Controller
@RequestMapping("/commonChoose")
public class CommonChooseController extends PubController {

    private static final Logger logger = LoggerFactory.getLogger(CommonChooseController.class);

    private static final int TYPE_ACTIVITY = 1;


    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    /**
     * 视频选择列表页
     * @return
     */
    @RequestMapping("/page/videoList")
    public ModelAndView maintain(@RequestParam(required = false) Integer phoneType) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("common/video_list_choose");
        return modelAndView;
    }

    /**
     * 查询直播列表
     *
     * @param valueMap
     * @param response
     * @return Map<String, Object>
     */
    @RequestMapping("/list.json")
    @ResponseBody
    public Map<String, Object> list(@RequestBody MultiValueMap valueMap, HttpServletResponse response) {
        Map<String, Object> param = valueMap.toSingleValueMap();
        Integer start = ObjectUtils.toInteger(param.get("iDisplayStart"));
        Integer limit = ObjectUtils.toInteger(param.get("iDisplayLength"));
        String sEcho = ObjectUtils.toString(param.get("sEcho"));
        //查询过滤条件
        String search_videoTitle = ObjectUtils.toString(param.get("search_videoTitle"));
        String search_videoStatus = ObjectUtils.toString(param.get("search_videoStatus"));
        //search_type:1.表示是活动查询
        Integer search_type = ObjectUtils.toInteger(param.get("search_type"));
        Long search_id = ObjectUtils.toLong(param.get("search_id"));
        //排序条件
        Map params = Maps.newHashMap();
        if (StringUtils.isNotBlank(search_videoTitle)) {
            params.put("pName", search_videoTitle);
        }

        if (StringUtils.isNotBlank(search_videoStatus)) {
            params.put("status", search_videoStatus);
        }

        params.put("pType", "1,3");

        Map orders = Maps.newLinkedHashMap();

        //过滤轮播台
        params.put("isCarousel", false);
        Long programNum = videoService.countProgramByParams(params);
        List<Program> programList = videoService.getProgramListByParams(orders, params, start, limit);

        //设置用户Video对象中的nickName和level信息
        if(programList.size()>0){
            List<Long> userIdList = new ArrayList<Long>();
            for(Program program : programList){
                userIdList.add(program.getUserId());
            }
            Map<Long, User> userMap = userService.getUserByIds(userIdList);
            for(Program program : programList){
                program.setNickName(userMap.get(program.getUserId()).getNickName());
                program.setUserLevel(userMap.get(program.getUserId()).getLevel());
                //需要将program中的UserRef设置为空，否则会导致返回到页面转json出错
                program.setUserRef(null);
                if(null!=search_id){
                    if(TYPE_ACTIVITY==search_type){
                        //1表示是活动接口进来的查询
                        program.setStatus(queryIsActivity(search_id, program.getId()));
                    }
                }
            }
        }
        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
        result.put("iTotalRecords", programNum); //total
        result.put("iTotalDisplayRecords", programNum); //totalAfterFilter
        result.put("aaData", programList.toArray());
        setResContent2Json(response);
        return result;
    }

    /**
     * 修改直播状态
     *
     * @param id 视频id
     * @return
     */
    @RequestMapping("/recommend")
    @ResponseBody
    public Map<String, Object> recommend(String type, String id, String programId, String op, HttpServletRequest request) {
        if (type == null || id == null || programId == null) {
            logger.error("非法参数，参数type、id、programId为空");
            return getFailMap("参数不能为空!");
        }
        ResultBean resultBean = ResultBean.getTrueInstance();

        if(PublicConstants.COMMEND_ADD.equals(ObjectUtils.toInteger(op))){
            if(TYPE_ACTIVITY==Integer.valueOf(type)){
                Program program = videoService.getProgramById(ObjectUtils.toLong(programId));
                activityService.saveActivityContent(Long.valueOf(id), false, program);
            }
        }else if(PublicConstants.COMMEND_DEL.equals(ObjectUtils.toInteger(op))){
            if(TYPE_ACTIVITY==Integer.valueOf(type)){
                activityService.deleteActivityContent(ObjectUtils.toLong(id), ObjectUtils.toLong(programId));
            }
        }else if(PublicConstants.COMMEND_FIRST.equals(ObjectUtils.toInteger(op))){
            if(TYPE_ACTIVITY==Integer.valueOf(type)){
                activityService.deleteActivityContent(ObjectUtils.toLong(id), ObjectUtils.toLong(programId));
                Program program = videoService.getProgramById(ObjectUtils.toLong(programId));
                activityService.saveActivityContent(Long.valueOf(id), true, program);
            }
        }else if(PublicConstants.COMMEND_CANCEL_FIRST.equals(ObjectUtils.toInteger(op))){
            if(TYPE_ACTIVITY==Integer.valueOf(type)){
                activityService.deleteActivityContent(ObjectUtils.toLong(id), ObjectUtils.toLong(programId));
                Program program = videoService.getProgramById(ObjectUtils.toLong(programId));
                activityService.saveActivityContent(Long.valueOf(id), false, program);
            }
        }

        if(resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }

    }

    /**
     * 查询直播是否属于某个活动
     * @param actId
     * @param programId
     * @return 1表示是，0表示否
     */
    private int queryIsActivity(Long actId, Long programId){
        if(null!=activityService.getActivityContentByActIdAndPId(actId, programId)){
            return 1;
        }else{
            return 0;
        }
    }

}
