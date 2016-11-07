package com.letv.whatslive.web.controller.activity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.common.utils.PriorityUtils;
import com.letv.whatslive.model.Activity;
import com.letv.whatslive.model.ActivityContent;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.model.constant.PublicConstants;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.constant.WebConstants;
import com.letv.whatslive.web.controller.PubController;
import com.letv.whatslive.web.service.activity.ActivityService;
import com.letv.whatslive.web.service.user.UserService;
import com.letv.whatslive.web.service.video.VideoService;
import com.letv.whatslive.web.util.String.StringUtils;
import com.letv.whatslive.web.util.util.DateUtils;
import com.letv.whatslive.web.util.util.MD5Util;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 活动维护Controller
 * Created by wangjian7 on 2015/10/10.
 */
@Controller
@RequestMapping("/activity")
public class ActivityController extends PubController{
    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService;

    /**
     * 显示所有活动列表页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/activity/activity");
        return modelAndView;
    }

    /**
     * 查询活动列表
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
        Map params = Maps.newHashMap();
        Map orders = Maps.newLinkedHashMap();
        orders.put("priority", -1);
        orders.put("createTime", -1);
        Long activityNum = activityService.countActivityByParams(params);
        List<Activity> activityList = activityService.getActivityListByParams(orders, params, start, limit);
        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
        result.put("iTotalRecords", activityNum); //total
        result.put("iTotalDisplayRecords", activityNum); //totalAfterFilter
        result.put("aaData", activityList.toArray());
        setResContent2Json(response);
        return result;
    }

    /**
     * 查询活动的直播列表
     *
     * @param valueMap
     * @param response
     */
    @RequestMapping("/programList")
    @ResponseBody
    public Map<String, Object> programList(@RequestBody MultiValueMap valueMap, HttpServletResponse response) {
        Map<String, Object> param = valueMap.toSingleValueMap();
        Integer start = ObjectUtils.toInteger(param.get("iDisplayStart"));
        Integer limit = ObjectUtils.toInteger(param.get("iDisplayLength"));
        String sEcho = ObjectUtils.toString(param.get("sEcho"));
        //查询过滤条件
        String search_videoTitle = ObjectUtils.toString(param.get("search_videoTitle"));
        Long search_actId = ObjectUtils.toLong(param.get("search_actId"));
        Long programNum = 0L;
        List<Long> activityContentIdList = Lists.newLinkedList();
        List<Program> programList = Lists.newArrayList();
        if(StringUtils.isBlank(search_videoTitle)){
            programNum = activityService.countProgramIdsByActivityId(search_actId);
            activityContentIdList = activityService.getProgramIdsByActivityId(search_actId, start, limit);
            for(Long id : activityContentIdList){
                Program program = videoService.getProgramById(id);
                programList.add(program);
            }
        }else{
            List<Program> programListTmp = Lists.newArrayList();
            activityContentIdList = activityService.getProgramIdsByActivityId(search_actId, 0, Integer.MAX_VALUE);
            for(Long programId : activityContentIdList){
                Program program = videoService.getProgramById(programId);
                if(program.getPName().contains(search_videoTitle)){
                    programListTmp.add(program);
                }
            }
            programNum = (long)programListTmp.size();
            if(programListTmp.size()>start){
                int maxNum = (start+limit)>programListTmp.size()?programListTmp.size():start+limit;
                programList = programListTmp.subList(start, maxNum);
            }
        }

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
                ActivityContent activityContent = activityService.getActivityContentByActIdAndPId(search_actId, program.getId());
                if(null != activityContent){
                    program.setPriority(PriorityUtils.isFirst(program.getPType(), null==program.getStartTime()?0L:program.getStartTime(), activityContent.getPriority())?1L:0L);
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
     * 设置活动上下线
     * @param id
     * @param op
     * @return
     */
    @RequestMapping("/effect")
    @ResponseBody
    public Map<String, Object> effect(String id, String op) {
        if (id == null) {
            logger.error("非法参数，id为空");
            return getFailMap("参数不能为空!");
        }
        Activity activity = activityService.getActivityById(ObjectUtils.toLong(id));
        if(null!=op && 0==ObjectUtils.toInteger(op) && activity.getIsRecommend()==1){
            activity.setIsRecommend(PublicConstants.RECOMMEND_OFF);
        }
        activity.setStatus(ObjectUtils.toInteger(op));
        ResultBean resultBean = activityService.updateActivity(activity, null, null);
        if(resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }

    }
    /**
     * 设置活动的优先级
     * @param id
     * @param op
     * @return
     */
    @RequestMapping("/priority")
    @ResponseBody
    public Map<String, Object> priority(String id, String op) {
        if (id==null || op==null) {
            logger.error("非法参数，id或op为空");
            return getFailMap("参数不能为空!");
        }
        Activity activity = activityService.getActivityById(ObjectUtils.toLong(id));
        int opIntValue = ObjectUtils.toInteger(op);
        if(1==opIntValue){
            activity.setPriority(System.currentTimeMillis());
        }else{
            activity.setPriority(0L);
        }
        ResultBean resultBean = activityService.updateActivity(activity, null, null);
        if(resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }
    }

    /**
     * 修改活动是否推荐
     * @param id
     * @param op
     * @return
     */
    @RequestMapping("/recommend")
    @ResponseBody
    public Map<String, Object> recommend(String id, String op) {
        ResultBean resultBean = ResultBean.getTrueInstance();
        if (id == null) {
            logger.error("非法参数，id为空");
            return getFailMap("参数不能为空!");
        }
        Activity activity = activityService.getActivityById(ObjectUtils.toLong(id));
        if(PublicConstants.RECOMMEND_ON.equals(ObjectUtils.toInteger(op))){
            if(StringUtils.isBlank(activity.getPicture())){
                return getFailMap("推荐的活动必须先上传封面!");
            }
            if(0==activity.getStatus()){
                return getFailMap("推荐的活动必须先上线!");
            }
            Map params = Maps.newHashMap();
            params.put("isRecommend", PublicConstants.RECOMMEND_ON);
            List<Activity> oldRecommendActivitys = activityService.getActivityListByParams(null, params, 0 ,100);
            for(Activity act : oldRecommendActivitys){
                act.setIsRecommend(PublicConstants.RECOMMEND_OFF);
                resultBean = activityService.updateActivity(act, null, null);
                if(!resultBean.isFlag()){
                    break;
                }
            }
            activity.setIsRecommend(PublicConstants.RECOMMEND_ON);
        }else{
            activity.setIsRecommend(PublicConstants.RECOMMEND_OFF);
        }
        resultBean = activityService.updateActivity(activity, null, null);
        if(resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }
    }

    /**
     * 新增或者修改活动信息页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/editActivity")
    public ModelAndView editActivity(WebRequest webRequest) {
        ModelAndView modelAndView = new ModelAndView();
        Long aid = ObjectUtils.toLong(webRequest.getParameter("aid"));
        Activity activity = new Activity();
        activity.setStatus(0);
        if(null != aid){
            activity = activityService.getActivityById(aid);
        }
        modelAndView.setViewName("activity/activity_edit");
        modelAndView.addObject("activity", activity);
        return modelAndView;
    }

    /**
     * 上传封面图片
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
     * 保存修改的活动信息
     * @param activity
     * @param vPic
     * @param sPic
     * @param response
     * @return
     */
    @RequestMapping("/saveActivity")
    @ResponseBody
    public Map<String, Object> saveActivity(Activity activity, String vPic, String sPic, HttpServletResponse response) {
        ResultBean resultBean = ResultBean.getTrueInstance();
        if (activity.getId() != null) {  // 修改
            String title = activity.getTitle();
            String tag = activity.getTag();
            String url = activity.getUrl();
            String aDesc = activity.getADesc();
            activity = activityService.getActivityById(activity.getId());
            activity.setTitle(title);
            activity.setTag(tag);
            activity.setUrl(url);
            activity.setType(1);
            activity.setADesc(aDesc);
            resultBean = activityService.updateActivity(activity, vPic, sPic);
        }else{
            if(StringUtils.isBlank(sPic) && StringUtils.isBlank(activity.getPicture())){
                return getFailMap("必须上传活动的封面图片！");
            }
            activity.setStatus(0);
            activity.setType(1);
            activity.setCreateTime(System.currentTimeMillis());
            resultBean = activityService.saveActivity(activity, vPic, sPic);
        }
        // 返回结果
        setResContent2Json(response);
        if(!resultBean.isFlag()){
            return getFailMap(resultBean.getMsg());
        }else{
            return getSuccessMap();
        }
    }
}
