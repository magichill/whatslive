package com.letv.whatslive.web.controller.push;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.PushMessage;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.controller.PubController;
import com.letv.whatslive.web.push.PushService;
import com.letv.whatslive.web.service.push.PushMessageService;
import com.letv.whatslive.web.util.WebUtils;
import org.apache.commons.lang3.StringUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/7/28.
 */
@Controller
@RequestMapping("/pushMessage")
public class PushMessageController extends PubController {
    private static final Logger logger = LoggerFactory.getLogger(PushMessageController.class);

    @Autowired
    PushMessageService pushMessageService;

    @Autowired
    PushService pushService;

    /**
     * 显示推送消息页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/push/pushMessage");
        return modelAndView;
    }

    /**
     * 查询推送消息列表
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
        String search_content = ObjectUtils.toString(param.get("search_content"));
        String search_status = ObjectUtils.toString(param.get("search_status"));

        Map params = Maps.newHashMap();
        if (StringUtils.isNotBlank(search_content)) {
            params.put("content", search_content);
        }
        if (StringUtils.isNotBlank(search_status)) {
            params.put("status", search_status);
        }
        Long pushMessageNum = pushMessageService.countPushMessageByParams(params);
        List<PushMessage> pushMessageNumList = pushMessageService.getPushMessageListByParams(params, start, limit);
        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
        result.put("iTotalRecords", pushMessageNum); //total
        result.put("iTotalDisplayRecords", pushMessageNum); //totalAfterFilter
        result.put("aaData", pushMessageNumList.toArray());
        setResContent2Json(response);
        return result;
    }
    /**
     * 跳转到修改推送消息页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/pushMessageNew")
    public ModelAndView userNew(WebRequest webRequest) {
        ModelAndView modelAndView = new ModelAndView();
        Long kid = ObjectUtils.toLong(webRequest.getParameter("kid"));
        PushMessage pushMessage = new PushMessage();
        if (kid != null) {
            pushMessage = pushMessageService.getPushMessageById(kid);
            modelAndView.setViewName("push/pushMessage_edit");
        } else {
            modelAndView.setViewName("push/pushMessage_edit");
        }
        modelAndView.addObject("pushMessage", pushMessage);
        return modelAndView;
    }

    @RequestMapping("/pushMessageSave")
    @ResponseBody
    public Map<String, Object> userSave(PushMessage pushMessage, HttpServletRequest request, HttpServletResponse response) {
        ResultBean resultBean = ResultBean.getTrueInstance();

        if (pushMessage.getId() != null) {  // 修改

            pushMessageService.updatePushMessage(pushMessage);
        }else {
            pushMessage.setCreateUser(WebUtils.getLoginUserNameNotNull(request));
            pushMessage.setCreateTime(System.currentTimeMillis());
            resultBean = pushMessageService.insertPushMessage(pushMessage);
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
     * 删除推送消息
     *
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String, Object> delete(String id) {
        if (id == null) {
            logger.error("非法参数，id为空");
            return getFailMap("参数不能为空!");
        }
        pushMessageService.deletePushMessage(ObjectUtils.toLong(id));
        return getSuccessMap();
    }

    /**
     * 推送消息
     *
     * @param id
     * @param op
     * @return
     */
    @RequestMapping("/sendMessge")
    @ResponseBody
    public Map<String, Object> sendMessage(String id, String op) {
        if (id == null) {
            logger.error("非法参数，id为空");
            return getFailMap("参数不能为空!");
        }
        PushMessage pushMessage = pushMessageService.getPushMessageById(ObjectUtils.toLong(id));
        if(pushMessage.getSendType()==-1){
//            pushService.pushMessageToAll(pushMessage.getId().toString(),pushMessage.getContent(),0L);
            pushMessageService.pushMessage(pushMessage.getContent());

        }else if(pushMessage.getSendType()==0){
//            pushService.pushMessageToTag(pushMessage.getId().toString(),"标签A", pushMessage.getContent(),0L);
        }
        pushMessage.setStatus(1);
        pushMessageService.updatePushMessage(pushMessage);
        return getSuccessMap();
    }


}
