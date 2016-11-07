package com.letv.whatslive.website.controller.web;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.website.common.VideoInfoBean;
import com.letv.whatslive.website.constant.WebsiteConstants;
import com.letv.whatslive.website.controller.PubController;
import com.letv.whatslive.website.service.user.UserService;
import com.letv.whatslive.website.service.video.VideoService;
import com.letv.whatslive.website.util.String.StringUtils;
import com.letv.whatslive.website.util.configuration.PropertyGetter;
import com.letv.whatslive.website.util.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by haojiayao on 2015/8/11.
 */
@Controller
@RequestMapping("/web")
public class webController extends PubController {

    private static final Logger logger = LoggerFactory.getLogger(webController.class);

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService;

    /**
     * 显示web播放页面
     *
     * @param webRequest
     *
     * @return
     */
    @RequestMapping("/webLive")
    public ModelAndView webLiveInit(WebRequest webRequest) throws Exception {

        // 视频ID(异常时输出日志用)
        String idError = webRequest.getParameter("id");

        ModelAndView modelAndView = new ModelAndView();

        try {
            // 选定视频ID
            Long id;
            // 选定id是否合法(true:合法，false:不合法)
            boolean idValidatedFlg = true;
            // 选定id是否存在(true:存在，false:不存在)
            boolean idExistFlg = true;
            // 视频及用户信息
            VideoInfoBean videoInfo = new VideoInfoBean();

            try {

                id = Long.valueOf(webRequest.getParameter("id"));
                // 根据视频ID获取视频信息
                Program program = videoService.getProgramById(id);

                if (program != null) {

                    // 用户ID
                    Long userId = program.getUserId();
                    // 根据视频ID获取用户信息
                    User user = userService.getUsersById(userId);
                    // 合并视频和用户信息
                    convert(videoInfo, program, user);
                    // 预约视频时，根据视频ID获取预约人数
                    if (ProgramConstants.pType_order.equals(program.getPType())
                            || ProgramConstants.pType_order_recommend.equals(program.getPType())) {

                        long orderNum = videoService.getOrderNum(id);
                        videoInfo.setOrderNum(orderNum);
                    }
                    // 分享URL
                    videoInfo.setShareUrl(PropertyGetter.getString("shareUrl") + ObjectUtils.toString(id));

                    // 轮播台LiveID
                    String liveId = videoInfo.getLiveId();

                    if (liveId != null && !"".equals(liveId)) {

                        // 选定的视频为轮播台视频时, 显示首页
                        modelAndView = new ModelAndView("redirect:/");
                        return modelAndView;
                    }
                } else {

                    idExistFlg = false;
                }
            } catch (NumberFormatException e) {

                idValidatedFlg = false;
            }

            // id不合法或者id不存在
            if (!idValidatedFlg || !idExistFlg) {

                // 视频种别设定为录播
                videoInfo.setProgramType(ProgramConstants.pType_end);
                // 状态设定为结束
                videoInfo.setStatus(WebsiteConstants.VIDEO_STATUS_END);
            }

            // 排序条件(视频种别升序，优先级降序，创建时间降序)
            Map orders = Maps.newLinkedHashMap();
            orders.put("pType", WebsiteConstants.ORDER_ASC);
            orders.put("priority", WebsiteConstants.ORDER_DESC);
            orders.put("createTime", WebsiteConstants.ORDER_DESC);

            // 检索条件
            Map params = Maps.newHashMap();
            // 视频种别(1:直播,3:录播)
            params.put("pType", "1,3");
            // 状态(1:正常)
            params.put("status", WebsiteConstants.VIDEO_STATUS_NORMAL);
            // 轮播台的视频不显示
            params.put("isCarousel", false);

            // 获取视频列表信息
            List<Program> programList = videoService.getProgramListByParamsForWeb(
                    orders, params, WebsiteConstants.SEARCH_LIMIT);

            List<Long> userIdList = new ArrayList<Long>();
            for (Program p : programList) {

                userIdList.add(p.getUserId());
            }
            // 获取用户列表信息
            Map<Long ,User> userMap = userService.getUsersByIds(userIdList);

            // 合并视频和用户信息列表
            List<VideoInfoBean> videoInfoList = convertList(programList, userMap);

            // 选定的视频为直播视频或录播视频，并且状态为正常时将选中视频放在视频列表头条
            if((ProgramConstants.pType_live.equals(videoInfo.getProgramType())
                    || ProgramConstants.pType_end.equals(videoInfo.getProgramType()))
                    && WebsiteConstants.VIDEO_STATUS_NORMAL.equals(videoInfo.getStatus())) {

                // 选定的视频ID在视频列表中是否存在
                for (VideoInfoBean v : videoInfoList) {

                    // 如果存在则删除
                    if (videoInfo.getId().equals(v.getId())) {

                        videoInfoList.remove(v);
                        break;
                    }
                }

                // 当列表数量等于5时，删除最后一条
                if (videoInfoList.size() == WebsiteConstants.SEARCH_LIMIT) {

                    videoInfoList.remove(videoInfoList.size() - 1);
                }

                // 将选定视频信息加入视频列表
                videoInfoList.add(0, videoInfo);
            }

            modelAndView.addObject("videoInfo", videoInfo);
            modelAndView.addObject("videoInfoList", videoInfoList);

            // 选定的视频为直播视频或直播已结束录播未形成时，显示直播播放页面
            if((ProgramConstants.pType_live.equals(videoInfo.getProgramType()))
                    || (ProgramConstants.pType_end.equals(videoInfo.getProgramType())
                            && WebsiteConstants.VIDEO_STATUS_REPLAY_MAKING.equals(videoInfo.getStatus()))) {

                modelAndView.setViewName("/web/webLive");
            } else if (ProgramConstants.pType_end.equals(videoInfo.getProgramType())) {

                // 用户唯一ID
                videoInfo.setUuid(PropertyGetter.getString("uuid"));

                // 选定的视频为录播视频时, 显示录播播放页面
                modelAndView.setViewName("/web/webReplay");
            } else if (ProgramConstants.pType_order.equals(videoInfo.getProgramType())
                            || ProgramConstants.pType_order_recommend.equals(videoInfo.getProgramType())) {

                // 系统时间
                modelAndView.addObject("currentTime", System.currentTimeMillis());

                // 选定的视频为预约视频或推荐预约视频时，显示预约视频播放页面
                modelAndView.setViewName("/web/webOrder");
            }
        } catch (Exception e) {

            StringBuilder buffer = new StringBuilder();
            buffer.append("videoid: ").append(idError);
            logger.error(buffer.toString(), e);
            throw e;
        }

        return modelAndView;
    }

    /**
     * 合并视频和用户信息列表
     *
     * @param programList 视频列表
     * @param userMap 用户列表
     *
     * @return
     */
    private List<VideoInfoBean> convertList(List<Program> programList, Map<Long ,User> userMap) {

        List<VideoInfoBean> videoInfoList = new ArrayList<VideoInfoBean>();

        for (Program program : programList) {

            User user = userMap.get(program.getUserId());

            VideoInfoBean videoInfo = new VideoInfoBean();
            convert(videoInfo, program, user);
            videoInfoList.add(videoInfo);
        }

        return videoInfoList;
    }

    /**
     * 合并视频和用户信息
     * @param videoInfo 视频及用户信息
     * @param program 视频信息
     * @param user 用户信息
     *
     * @return
     */
    private VideoInfoBean convert(VideoInfoBean videoInfo, Program program, User user) {

        // 视频ID
        videoInfo.setId(program.getId());
        // 标题
        videoInfo.setTitle(program.getPName());
        // 节目类型
        videoInfo.setProgramType(program.getPType());
        // 状态
        videoInfo.setStatus(program.getStatus());
        // 地理位置
        String location = program.getLocation();
        if (!StringUtils.isBlank(location)) {

            if (location.contains("澳门")) {

                location = "澳门特别行政区";
            } else if (location.contains("香港")){

                location = "香港特别行政区";
            } else {

                location = location.substring(0, location.indexOf("市") + 1);
            }
        }
        videoInfo.setLocation(location);
        // 封面
        videoInfo.setCoverPicture(program.getPicture());
        // 点赞数
        videoInfo.setLikeNum(program.getLikeNum());
        // 观众总数
        videoInfo.setWatchNum(program.getWatchNum());
        // 在线观看人数
        videoInfo.setWatchOnlineUserNum(program.getWatchOnlineUserNum());
        // 直播开始时间
        videoInfo.setStartTime(program.getStartTime());
        // 直播结束时间
        videoInfo.setEndTime(program.getEndTime());
        // 优先级
        videoInfo.setPriority(program.getPriority());
        // 发布人ID
        videoInfo.setUserId(program.getUserId());
        // 昵称
        videoInfo.setNickName(user.getNickName());
        // 头像
        String picture = user.getPicture();
        if (picture != null && !"".equals(picture)) {

            String[] pictures = picture.split(",");
            videoInfo.setHeadPortrait(pictures[0]);
        }
        // 预约人数
        videoInfo.setOrderNum(program.getOrderNum());
        // 直播活动ID
        videoInfo.setActivityId(program.getActivityId());
        // 轮播台LiveID
        videoInfo.setLiveId(program.getLiveId());
        // 录播视频唯一标识
        videoInfo.setVuid(program.getVuid());

        // 视频为录播时，计算视频时长
        if (ProgramConstants.pType_end.equals(program.getPType())) {

            Long startTime = program.getStartTime();
            Long endTime = program.getEndTime();
            String liveTimeLength = DateUtils.getTimeLength(startTime, endTime);
            videoInfo.setLiveTimeLength(liveTimeLength);
        }

        return videoInfo;
    }

    /**
     * 获取观看视频总人数
     *
     * @param valueMap 参数
     * @param response
     *
     * @return
     */
    @RequestMapping("/webGetWatchedNum")
    @ResponseBody
    public String getWatchedNum(@RequestBody MultiValueMap valueMap, HttpServletResponse response){

        Map<String, Object> param = valueMap.toSingleValueMap();
        // 视频ID
        Long id = Long.valueOf(ObjectUtils.toString(param.get("id")));

        // 根据视频ID获取视频信息
        Program program = videoService.getProgramById(id);

        Map<String, Object> result = getSuccessMap();
        result.put("watchedNum", program.getWatchNum());
        setResContent2Text(response);

        return map2JsonString(result);
    }
}
