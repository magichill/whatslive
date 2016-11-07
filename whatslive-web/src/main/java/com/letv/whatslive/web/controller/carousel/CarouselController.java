package com.letv.whatslive.web.controller.carousel;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.constant.WebConstants;
import com.letv.whatslive.web.controller.PubController;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/8/11.
 */
@Controller
@RequestMapping("/carousel")
public class CarouselController extends PubController{

    private static final Logger logger = LoggerFactory.getLogger(CarouselController.class);

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService;

    /**
     * 显示所有轮播列表页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/video/carousel");
        return modelAndView;
    }

    /**
     * 查询轮播列表
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
        params.put("isCarousel", true);
        Map orders = Maps.newHashMap();
        orders.put("priority", -1);
        Long carouselNum = videoService.countProgramByParams(params);
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
        result.put("iTotalRecords", carouselNum); //total
        result.put("iTotalDisplayRecords", carouselNum); //totalAfterFilter
        result.put("aaData", programList.toArray());
        setResContent2Json(response);
        return result;
    }
    /**
     * 修改轮播状态
     *
     * @param id 视频id
     * @return
     */
    @RequestMapping("/effect")
    @ResponseBody
    public Map<String, Object> effect(String id, String op) {
        if (id == null) {
            logger.error("非法参数，id为空");
            return getFailMap("参数不能为空!");
        }
        ResultBean resultBean = ResultBean.getTrueInstance();
        Program program = videoService.getProgramById(ObjectUtils.toLong(id));
        program.setStatus(ObjectUtils.toInteger(op));
        if(ProgramConstants.pStatus_offLine.equals(ObjectUtils.toInteger(op))){
            //需要调用直播结束接口对直播进行关闭
            videoService.endLive(program.getUserId(), program.getId());
        }else{
            //需要调用直播开始接口发起直播的聊天室
            resultBean = videoService.startChartRoom(program, program.getUserId());
        }

        if(resultBean.isFlag()){
            return getSuccessMap();
        }else{
            return getFailMap(resultBean.getMsg());
        }

    }


    /**
     * 跳转到修改轮播信息页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/editCarousel")
    public ModelAndView editCarousel(WebRequest webRequest) {
        ModelAndView modelAndView = new ModelAndView();
        Long vid = ObjectUtils.toLong(webRequest.getParameter("vid"));
        Program program = new Program();
        Map<String, Object> params = new HashMap<String, Object>();
        if(null != vid){
            program = videoService.getProgramById(vid);
            User user = userService.getUserById(program.getUserId());
            program.setNickName(user.getNickName());
            params.put("userIdNotEqual", program.getUserId());
        }
        List<User> userList = userService.getAllVirtualUser(params);
        modelAndView.setViewName("video/carousel_edit");
        modelAndView.addObject("userList", userList);
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
     * 保存修改的轮播信息
     * @param program
     * @param vPic
     * @param sPic
     * @param response
     * @return
     */
    @RequestMapping("/saveCarousel")
    @ResponseBody
    public Map<String, Object> saveCarousel(Program program, String vPic, String sPic, HttpServletResponse response) {
        ResultBean resultBean = ResultBean.getTrueInstance();
        if((StringUtils.isBlank(program.getActivityId())&&StringUtils.isBlank(program.getLiveId()))
                || (!StringUtils.isBlank(program.getActivityId())&&!StringUtils.isBlank(program.getLiveId()))){
            return getFailMap("activityId和liveId必须并且只能录入一个！");
        }
        if (program.getId() != null) {  // 修改
            String liveId = program.getLiveId();
            String activityId = program.getActivityId();
            Long userId = program.getUserId();
            String pname = program.getPName();
            int from = program.getFrom();
            program = videoService.getProgramById(program.getId());
            program.setUserId(userId);
            program.setPName(pname);
            program.setLiveId(liveId);
            program.setActivityId(activityId);
            program.setFrom(from);
            resultBean = videoService.updateProgram(program, vPic, sPic);
        }else{
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userId", program.getUserId());
            params.put("isCarousel", true);
            Long carouselNum = videoService.countProgramByParams(params);

            if(null != carouselNum && carouselNum >0){
                return getFailMap("此用户已经维护过一条轮播视频！");
            }
            if(StringUtils.isBlank(sPic) && StringUtils.isBlank(program.getPicture())){
                return getFailMap("必须上传轮播的封面图片！");
            }
            program.setPType(1);
            program.setIsCarousel(1);
            program.setStartTime(System.currentTimeMillis());
            resultBean = videoService.saveProgram(program, vPic, sPic);
            if(resultBean.isFlag()){
                //启动聊天室
                videoService.startChartRoom(program, program.getUserId());
            }
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
