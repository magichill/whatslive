package com.letv.whatslive.web.controller.video;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.ProgramReplay;
import com.letv.whatslive.model.Tag;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.constant.WebConstants;
import com.letv.whatslive.web.controller.PubController;
import com.letv.whatslive.web.service.actionLog.ActionLogService;
import com.letv.whatslive.web.service.scheduler.ReplayGenerateService;
import com.letv.whatslive.web.service.tag.TagService;
import com.letv.whatslive.web.service.user.UserService;
import com.letv.whatslive.web.service.video.VideoService;
import com.letv.whatslive.web.util.WebUtils;
import com.letv.whatslive.web.util.util.DateUtils;
import com.letv.whatslive.web.util.util.MD5Util;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直播Controller
 * Created by wangjian7 on 2015/7/10.
 */
@Controller
@RequestMapping("/video")
public class VideoController extends PubController{

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    private ReplayGenerateService replayGenerateService;

    /**
     * 显示直播列表页面
     *
     * @return ModelAndView
     */
    @RequestMapping("/showLive")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/video/live");
        modelAndView.addObject("videoType", ProgramConstants.pType_live);
        modelAndView.addObject("videoStatus", ProgramConstants.pStatus_onLine);
        return modelAndView;
    }

    /**
     * 显示已经下线列表页面
     *
     * @return ModelAndView
     */
    @RequestMapping("/showOffLine")
    public ModelAndView showOffLine() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/video/live");
        modelAndView.addObject("videoType", ProgramConstants.pType_end);
        modelAndView.addObject("videoStatus", ProgramConstants.pStatus_offLine);
        return modelAndView;
    }

    /**
     * 显示已结束列表页面
     *
     * @return ModelAndView
     */
    @RequestMapping("/showEnd")
    public ModelAndView showEnd() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/video/live");
        modelAndView.addObject("videoType", ProgramConstants.pType_end);
        modelAndView.addObject("videoStatus", ProgramConstants.pStatus_onLine);
        return modelAndView;
    }

    /**
     * 显示预约列表页面
     *
     * @return ModelAndView
     */
    @RequestMapping("/showOrder")
    public ModelAndView showOrder() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/video/order");
        modelAndView.addObject("videoType", ProgramConstants.pType_order+","+ProgramConstants.pType_order_recommend);
        modelAndView.addObject("videoStatus", ProgramConstants.pStatus_onLine);
        return modelAndView;
    }

    /**
     * 显示可视化审核页面
     * @return ModelAndView
     */
    @RequestMapping("/review")
    public ModelAndView review() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/video/review");
        modelAndView.addObject("videoStatus", ProgramConstants.pStatus_onLine);
        return modelAndView;
    }

    /**
     * 获取所有的直播/录播/预约/可视化审核页面显示统计信息
     * @param valueMap
     * @param response
     * @return Map<String, Object>
     */
    @RequestMapping("/countProgram")
    @ResponseBody
    public Map<String, Object> getAllProgramNum(@RequestBody MultiValueMap valueMap, HttpServletResponse response){
        Map<String, Object> result = getSuccessMap();
        Map<String, Object> param = valueMap.toSingleValueMap();

        Integer search_videoType = ObjectUtils.toInteger(param.get("search_videoType"));
        Integer search_videoStatus = ObjectUtils.toInteger(param.get("search_videoStatus"));
        //用于标识是否是可视化审核页面的统计数据
        Integer search_isReview = ObjectUtils.toInteger(param.get("search_isReview"));
        String message = getCountMessage(search_videoType, search_videoStatus, search_isReview);
        result.put("type", search_videoType);
        result.put("status", search_videoStatus);
        result.put("message", message);
        setResContent2Json(response);
        return result;
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
        String search_videoType = ObjectUtils.toString(param.get("search_videoType"));
        String search_videoStatus = ObjectUtils.toString(param.get("search_videoStatus"));
        String search_reportNum = ObjectUtils.toString(param.get("search_reportNum"));
        //排序条件
        String order_report = ObjectUtils.toString(param.get("order_report"));
        String order_priority = ObjectUtils.toString(param.get("order_priority"));
        String order_pType = ObjectUtils.toString(param.get("order_pType"));
        Map params = Maps.newHashMap();
        if (StringUtils.isNotBlank(search_videoTitle)) {
            params.put("pName", search_videoTitle);
        }
        if (StringUtils.isNotBlank(search_videoType)) {
            params.put("pType", search_videoType);
        }
        if (StringUtils.isNotBlank(search_videoStatus)) {
            params.put("status", search_videoStatus);
        }
        if (StringUtils.isNotBlank(search_reportNum)) {
            params.put("reportNum", search_reportNum);
        }
        Map orders = Maps.newLinkedHashMap();
        if(StringUtils.isNotBlank(order_report)){
            orders.put("reportNum", order_report);
        }
        if(StringUtils.isNotBlank(order_priority)){
            //如果是按照优先级的，需要再按照创建时间排序
            orders.put("priority", order_priority);
            orders.put("startTime", -1);
        }
        if(StringUtils.isNotBlank(order_pType)){
            orders.put("pType", order_pType);
        }
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
    @RequestMapping("/effect")
    @ResponseBody
    public Map<String, Object> effect(String id, String op, HttpServletRequest request) {
        if (id == null) {
            logger.error("非法参数，id为空");
            return getFailMap("参数不能为空!");
        }
        ResultBean resultBean = ResultBean.getTrueInstance();
        Program program = videoService.getProgramById(ObjectUtils.toLong(id));

        if(ProgramConstants.pStatus_offLine.equals(ObjectUtils.toInteger(op))){
            //需要调用直播结束接口对直播进行关闭
            if(ProgramConstants.pType_live.equals(program.getPType())){
                resultBean = videoService.endLive(program.getUserId(), program.getId());
                if(resultBean.isFlag()){
                    logger.info("program have bean offline ! program id:" + program.getId() + " operate user:" + WebUtils.getLoginUserNameNotNull(request));
                    //String message = String.format(DocumentConstants.LIVE_OFF_LINE_MESSAGE, program.getStartTimeStr(), program.getPName());
                    //resultBean = videoService.sendOffLineMessage(program.getId(),program.getUserId(),message);
                }

            }else if(ProgramConstants.pType_end.equals(program.getPType())){
                program.setStatus(ObjectUtils.toInteger(op));
                program.setPriority(0L);
                resultBean = videoService.updateProgram(program, null, null);
                if(resultBean.isFlag()){
                    logger.info("program have bean offline ! program id:" + program.getId() + " operate user:" + WebUtils.getLoginUserNameNotNull(request));
//                    String message = String.format(DocumentConstants.REPLAY_OFF_LINE_MESSAGE, program.getStartTimeStr(), program.getPName());
//                    resultBean = videoService.sendOffLineMessage(program.getId(),program.getUserId(),message);
                }

            }
        }else{
            program.setStatus(ObjectUtils.toInteger(op));
            program.setPriority(0L);
            resultBean = videoService.updateProgram(program, null, null);
        }

        if(resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }

    }

    /**
     * 机器人启停
     * @param id 直播id
     * @param op 机器人op
     * @return
     */
    @RequestMapping("/opLiveRobot")
    @ResponseBody
    public Map<String, Object> opLiveRobot(String id, String op) {
        if (op == null) {
            logger.error("非法参数，op为空");
            return getFailMap("参数不能为空!");
        }
        ResultBean resultBean = userService.opOneLiveRobot(id, op);
        if (resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }
    }

    /**
     * 修改直播的优先级
     * @param id
     * @param op
     * @return
     */
    @RequestMapping("/priority")
    @ResponseBody
    public Map<String, Object> priority(String id, String op) {
        if (id == null) {
            logger.error("非法参数，id为空");
            return getFailMap("参数不能为空!");
        }
        Program program = videoService.getProgramById(ObjectUtils.toLong(id));
        if(ProgramConstants.PRIORITY_ON.equals(ObjectUtils.toInteger(op))){
            program.setPriority(System.currentTimeMillis());
        }else{
            program.setPriority(0L);
        }
        ResultBean resultBean = videoService.updateProgram(program, null, null);
        if(resultBean.isFlag()){
                return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }
    }
    /**
     * 修改直播是否推荐
     * @param id
     * @param op
     * @return
     */
    @RequestMapping("/recommend")
    @ResponseBody
    public Map<String, Object> recommend(String id, String op) {
        if (id == null) {
            logger.error("非法参数，id为空");
            return getFailMap("参数不能为空!");
        }
        Program program = videoService.getProgramById(ObjectUtils.toLong(id));
        if(ProgramConstants.RECOMMEND_ON.equals(ObjectUtils.toInteger(op))){
            program.setIsShow(ProgramConstants.RECOMMEND_ON);
        }else{
            program.setIsShow(ProgramConstants.RECOMMEND_OFF);
        }
        ResultBean resultBean = videoService.updateProgram(program, null, null);
        if(resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }
    }

    /**
     * 预约置顶
     * @param id
     * @param op
     * @return
     */
    @RequestMapping("/orderTop")
    @ResponseBody
    public Map<String, Object> orderTop(String id, String op) {
        ResultBean resultBean = ResultBean.getTrueInstance();
        if (id == null) {
            return getFailMap("参数不能为空!");
        }
        Program program = videoService.getProgramById(ObjectUtils.toLong(id));
        if(ProgramConstants.TOP_ON.equals(ObjectUtils.toInteger(op))){
            if(StringUtils.isBlank(program.getPicture())){
                return getFailMap("推荐的预约视频必须先上传封面!");
            }
            Map params = Maps.newHashMap();
            params.put("pType", ProgramConstants.pType_order_recommend);
            List<Program> oldRecommendPrograms = videoService.getProgramListByParams(null, params, 0 ,100);
            for(Program p : oldRecommendPrograms){
                p.setPType(ProgramConstants.pType_order);
                resultBean = videoService.updateProgram(p, null, null);
                if(!resultBean.isFlag()){
                    break;
                }
            }
            program.setPType(ProgramConstants.pType_order_recommend);
        }else{
            program.setPType(ProgramConstants.pType_order);
        }
        if(resultBean.isFlag()){
            resultBean = videoService.updateProgram(program, null, null);
        }

        if(resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }
    }

     /**
     * 跳转到修改封面页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/converChange")
    public ModelAndView converChange(WebRequest webRequest) {
        ModelAndView modelAndView = new ModelAndView();
        Long vid = ObjectUtils.toLong(webRequest.getParameter("vid"));
        Program program = videoService.getProgramById(vid);
        if (vid != null) {
            modelAndView.setViewName("video/cover_edit");
        } else {
        }
        modelAndView.addObject("program", program);
        return modelAndView;
    }

    /**
     * 跳转到修改预约信息页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/orderChange")
    public ModelAndView orderChange(WebRequest webRequest) {
        ModelAndView modelAndView = new ModelAndView();
        Long vid = ObjectUtils.toLong(webRequest.getParameter("vid"));
        Program program = videoService.getProgramById(vid);
        Map params = Maps.newHashMap();
        List<Tag> tagList = Lists.newArrayList();
        tagList.add(new Tag());
        if(program != null && program.getTagList() != null && program.getTagList().size() >0){
            Tag tag = tagService.getTagById(program.getTagList().get(0));
            tag.setSelected("selected");
            program.setTagId(tag.getId());
            program.setTagValue(tag.getValue());
            params.put("tagIdNotEqual", program.getTagList().get(0));
            tagList.add(tag);
        }
        tagList.addAll(tagService.getTagListByParams(params));
        if (vid != null) {
            modelAndView.setViewName("video/order_edit");
        } else {
        }
        modelAndView.addObject("tagList", tagList);
        modelAndView.addObject("program", program);
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
     * 保存封面信息
     * @param program
     * @param vPic
     * @param sPic
     * @param response
     * @return
     */
    @RequestMapping("/coverChange")
    @ResponseBody
    public Map<String, Object> coverChange(Program program, String vPic, String sPic, HttpServletResponse response) {
        ResultBean resultBean = ResultBean.getTrueInstance();
        String pName = program.getPName();
        long watchNum = program.getWatchNum()==0?0L:program.getWatchNum();
        long likeNum = program.getWatchNum()==0?0L:program.getLikeNum();
        long commentNum = program.getWatchNum()==0?0L:program.getCommentNum();
//        if(StringUtils.isBlank(sPic)){
//            return getFailMap("必须上传封面");
//        }
        if (program.getId() != null) {  // 修改
            program = videoService.getProgramById(program.getId());
            program.setPName(pName);
            program.setWatchNum(watchNum);
            program.setLikeNum(likeNum);
            program.setCommentNum(commentNum);
            resultBean = videoService.updateProgram(program, vPic, sPic);
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
     * 保存预约信息
     * @param program
     * @param vPic
     * @param sPic
     * @param response
     * @return
     */
    @RequestMapping("/orderChange")
    @ResponseBody
    public Map<String, Object> orderChange(Program program, String vPic, String sPic, HttpServletResponse response) {
        ResultBean resultBean = ResultBean.getTrueInstance();

        if (program.getId() != null) {  // 修改
            Long tagId = program.getTagId();
            List<Long> tagList = Lists.newArrayList();
            if(null != tagId){
                tagList.add(tagId);
            }
            program = videoService.getProgramById(program.getId());
            program.setTagList(tagList);
            resultBean = videoService.updateProgram(program, vPic, sPic);
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
     * 视频预览
     *
     * @param id
     * @param response
     * @return
     */
    @RequestMapping("/view")
    @ResponseBody
    public Map<String, Object> view(@RequestParam("id") Long id, HttpServletResponse response) {
        if (id == null) {
            logger.info("非法参数");
            return getFailMap("参数不能为空!");
        }
        Program program = videoService.getProgramById(id);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("rsCode", "A00000");
        result.put("rsMsg", "成功");
        result.put("url", WebConstants.LETV_WEB_URL+id);
        return result;
    }

    /**
     * 下线视频预览
     *
     * @param id
     * @param response
     * @return
     */
    @RequestMapping("/viewOffLine")
    @ResponseBody
    public Map<String, Object> viewOffLine(@RequestParam("id") Long id, HttpServletResponse response) {
        if (id == null) {
            logger.info("非法参数");
            return getFailMap("参数不能为空!");
        }
        Program program = videoService.getProgramById(id);
        Map<String, Object> result = new HashMap<String, Object>();
        if (program.getPType()==ProgramConstants.pType_end) {
            result.put("rsCode", "A00000");
            result.put("rsMsg", "成功");
            result.put("vuid", program.getVuid());
            result.put("uuid", WebConstants.LETV_LIVE_UUID);
            return result;
        }
        return getFailMap("视频不能观看");
    }

    /**
     * 视频在线播放
     *
     * @param vuid
     * @param uuid
     * @return
     */
    @RequestMapping("/page/toView")
    @ResponseBody
    public ModelAndView toView(@RequestParam("vuid") String vuid, @RequestParam("uuid") String uuid) {
        if (StringUtils.isBlank(vuid) && StringUtils.isBlank(uuid)) {
            logger.info("非法参数");
            getFailMap("参数不能为空!");
        }
        ModelAndView modelAndView = new ModelAndView();
        Program program = new Program();
        program.setActivityId(null);
        program.setVuid(vuid);
        //将录播用到的UUID放到videoId字段中
        program.setVideoId(uuid);
        modelAndView.addObject("program", program);
        modelAndView.setViewName("video/video_replay");
        return modelAndView;
    }

    /**
     * 视频监控
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/monitor")
    public ModelAndView monitor(WebRequest webRequest) {

        // 视频类型
        Integer videoType = ObjectUtils.toInteger(webRequest.getParameter("videoType"));
        // 开始行号
        Integer startNo = ObjectUtils.toInteger(webRequest.getParameter("startNo"));
        // 当前页号
        Integer pageNo = ObjectUtils.toInteger(webRequest.getParameter("pageNo"));

        // 排序条件(视频种别升序，优先级降序，创建时间降序)
        Map orders = Maps.newLinkedHashMap();
        orders.put("priority", -1);
        orders.put("createTime", -1);

        // 检索条件
        Map params = Maps.newHashMap();
        // 视频种别(1:直播,3:录播)
        params.put("pType", videoType);
        // 状态(1:正常)
        params.put("status", 1);
        // 轮播台的视频不显示
        params.put("isCarousel", false);

        // 获取视频列表信息
        List<Program> programList = videoService.getProgramListByParams(
                orders, params, startNo, 8);

        long total = 0L;
        // 获取视频记录总数
        total = videoService.countProgramByParams(params);

        // 获取被举报视频总数
        long reportedVideoCount = 0L;
        initParams(params);
        // 视频种别(1:直播,3:录播)
        params.put("pType", videoType);
        // 状态(1:正常)
        params.put("status", 1);
        // 被举报次数大于0
        params.put("reportNum", 1);
        reportedVideoCount = videoService.countProgramByParams(params);

        // 获取观看总人数
        long totalWatchNum = 0L;
        // 查询条件
        Map queryParams = Maps.newHashMap();
        // 视频种别(1:直播,3:录播)
        queryParams.put("pType", videoType);
        // 状态(1:正常)
        queryParams.put("status", 1);
        // 非轮播台的视频
        queryParams.put("isCarousel", false);

        // 分组列条件
        List<String> groupParams = new ArrayList<String>();
        // 用视频种别分组
        groupParams.add("pType");
        // 对观看人数做合计
        String sumColumn = "watchNum";

        Map<String, Long> totalWatchNumMap = videoService.getTotalNumByParams(queryParams, groupParams, sumColumn);
        totalWatchNum = ObjectUtils.toLong(totalWatchNumMap.get(ObjectUtils.toString(videoType)), 0L);;

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("videoInfoList", programList);
        modelAndView.addObject("videoType", videoType);
        modelAndView.addObject("startNo", startNo);
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.addObject("total", total);
        modelAndView.addObject("reportedVideoCount", reportedVideoCount);
        modelAndView.addObject("totalWatchNum", totalWatchNum);

        // 当为直播视频时
        if (ProgramConstants.pType_live.equals(videoType)) {
            modelAndView.setViewName("video/monitor_live");
        } else {

            modelAndView.addObject("uuid", WebConstants.LETV_LIVE_UUID);
            modelAndView.setViewName("video/monitor_replay");
        }
        return modelAndView;
    }

    /**
     * 生成录播的回放日志文件
     * @param id
     * @return
     */
    @RequestMapping("/createReplay")
    @ResponseBody
    public Map<String, Object> createReplay(String id) {
        if (id == null) {
            logger.error("非法参数，id为空");
            return getFailMap("参数不能为空!");
        }
        Program program = videoService.getProgramById(ObjectUtils.toLong(id));
        ResultBean resultBean = ResultBean.getTrueInstance();
        ProgramReplay programReplay = videoService.getProgramReplay(Long.valueOf(id));
        if(program.getPType()==3&&(program.getStatus()==1 || program.getStatus()==-1)){
            if(null!=programReplay){
                if(programReplay.getStatus()==3){
                    return getFailMap("录播已经生成回放历史!");
                }else if(programReplay.getStatus()==5){
                    return getFailMap("录播没有回放历史，无法生成录播!");
                }
            }
            resultBean =  actionLogService.createProgramActionLogFile(program.getId(),
                    program.getStartTime()==null?0L:program.getStartTime());
        }else{
            return getFailMap("此录播不能生成回放历史!");
        }

//            replayGenerateService.scanAllRecord();
        if(resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }
    }



    /**
     * 根据参数获取页面显示的统计消息
     * @param search_videoType
     * @param search_videoStatus
     * @param search_isReview 判断是否是可视化页面发过来的统计请求
     * @return
     */
    private String getCountMessage(Integer search_videoType, Integer search_videoStatus, Integer search_isReview) {
        StringBuilder message = new StringBuilder();
        long currentProgram = 0L;
        long allProgram = 0L;
        long reportProgram = 0L;
        long offLineProgram = 0L;
        Map params = Maps.newHashMap();
        initParams(params);
        if(null != search_isReview){
            params.put("status", ProgramConstants.pStatus_onLine);
            if(ProgramConstants.pType_live.equals(search_videoType)){
                params.put("pType", search_videoType);
                currentProgram = videoService.countProgramByParams(params);
                message.append("当前共").append(currentProgram).append("个直播视频，");
            }else if(ProgramConstants.pType_end.equals(search_videoType)){
                params.put("pType", search_videoType);
                currentProgram = videoService.countProgramByParams(params);
                message.append("当前共").append(currentProgram).append("个录播视频，");
            }
            initParams(params);
            params.put("reportNum", 0L);
            reportProgram = videoService.countProgramByParams(params);
            initParams(params);
            params.put("pType", ProgramConstants.pType_live+","+ProgramConstants.pType_end);
            params.put("status", ProgramConstants.pStatus_offLine);
            offLineProgram = videoService.countProgramByParams(params);
            message.append(reportProgram).append("个被举报视频，");
            message.append(offLineProgram).append("个已经下架");
        }else{
            if(ProgramConstants.pStatus_onLine.equals(search_videoStatus)){
                if(ProgramConstants.pType_live.equals(search_videoType)){
                    params.put("pType", search_videoType);
                    currentProgram = videoService.countProgramByParams(params);
                    initParams(params);
                    params.put("pType", ProgramConstants.pType_live+","+ProgramConstants.pType_end);
                    allProgram = videoService.countProgramByParams(params);
                    message.append("累计直播次数").append(allProgram).append("，当前共")
                            .append(currentProgram).append("个直播进行中");
                }else if(ProgramConstants.pType_end.equals(search_videoType)){
                    params.put("status", ProgramConstants.pStatus_onLine);
                    params.put("pType", search_videoType);
                    currentProgram = videoService.countProgramByParams(params);
                    initParams(params);
                    params.put("pType", ProgramConstants.pType_live+","+ProgramConstants.pType_end);
                    allProgram = videoService.countProgramByParams(params);
                    message.append("累计直播次数").append(allProgram).append("，当前共")
                            .append(currentProgram).append("个历史直播");
                }else if(ProgramConstants.pType_order.equals(search_videoType)){
                    params.put("status", ProgramConstants.pStatus_onLine);
                    params.put("pType", ProgramConstants.pType_order);
                    currentProgram = videoService.countProgramByParams(params);
                    message.append("当前共").append(currentProgram).append("个预约");
                }
            }else {
                params.put("pType", ProgramConstants.pType_live+","+ProgramConstants.pType_end);
                allProgram = videoService.countProgramByParams(params);
                params.put("status", ProgramConstants.pStatus_offLine);
                offLineProgram = videoService.countProgramByParams(params);
                message.append("累计直播次数").append(allProgram).append("，当前共")
                        .append(offLineProgram).append("个视频被下线");
            }
        }
        return message.toString();
    }
    private void initParams(Map params){
        params.clear();
        params.put("isCarousel", false);
    }


}
