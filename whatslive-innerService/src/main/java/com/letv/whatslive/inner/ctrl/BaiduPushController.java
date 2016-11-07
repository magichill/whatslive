package com.letv.whatslive.inner.ctrl;

import com.letv.psp.swift.core.service.MessageContext;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.inner.Service.BaiduPushService;
import com.letv.whatslive.inner.push.schedule.OrderPushService;
import com.letv.whatslive.inner.utils.String.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wangjian7 on 2015/8/24.
 */
@Service("BaiduPushController")
public class BaiduPushController extends BaseController{

    @Autowired
    BaiduPushService baiduPushService;

    @Autowired
    OrderPushService orderPushService;

    public String pushToAll(MessageContext msgCtx) {
        String result = null;
        String bussikey = msgCtx.getParameter("bussikey");
        String message = msgCtx.getParameter("message");
        long sendtime = ObjectUtils.toLong(msgCtx.getParameter("message"),0L);
        result = checkParams(bussikey, message);
        if(result != null){
            logger.error(result+msgCtx.getParameterMap());
        }else{
            baiduPushService.pushNoticeMessageToAllDevices(bussikey, 0, message, sendtime);
            result = "sucess";
        }
        return result;
    }

    public String pushToTag(MessageContext msgCtx) {
        String result = null;
        String bussikey = msgCtx.getParameter("bussikey");
        String tagName = msgCtx.getParameter("tagName");
        String message = msgCtx.getParameter("message");
        long sendtime = ObjectUtils.toLong(msgCtx.getParameter("message"),0L);
        result = checkParams(bussikey, tagName, message);
        if(result != null){
            logger.error(result+msgCtx.getParameterMap());
        }else{
            baiduPushService.pushNoticeMessageToTagDevices(bussikey, 0, tagName, message, sendtime);
            result = "sucess";
        }
        return result;
    }

    public String createTag(MessageContext msgCtx){
        String result = null;
        String bussikey = msgCtx.getParameter("bussikey");
        String tagName = msgCtx.getParameter("tagName");
        result = checkParams(bussikey, tagName);
        if(result != null){
            logger.error(result+msgCtx.getParameterMap());
        }else{
            baiduPushService.createTag(bussikey, tagName);
            result = "sucess";
        }
        return result;
    }

    public String addDevicesToTag(MessageContext msgCtx){
        String result = null;
        String bussikey = msgCtx.getParameter("bussikey");
        String channelIds = msgCtx.getParameter("channelIds");
        String plantform = msgCtx.getParameter("plantform");
        String tagName = msgCtx.getParameter("tagName");
        result = checkParams(bussikey, channelIds, tagName, plantform);
        if(result != null){
            logger.error(result+msgCtx.getParameterMap());
        }else{
            baiduPushService.addDevicesToTag(bussikey, channelIds.split(","), tagName, Integer.valueOf(plantform));
            logger.info("Add devices to tag! tagName=" + tagName);
            result = "sucess";
        }
        return result;
    }

    public String queryMsgStatus(MessageContext msgCtx){
        String result = null;
        String msgIds = msgCtx.getParameter("msgIds");
        String plantform = msgCtx.getParameter("plantform");
        result = checkParams(msgIds, plantform);
        if(result != null){
            logger.error(result+msgCtx.getParameterMap());
        }else{
            baiduPushService.queryMsgStatus(msgIds.split(","),Integer.valueOf(plantform));
            result = "sucess";
        }
        return result;
    }

    public String pushOrder(MessageContext msgCtx){
        String result = null;
        String pid = msgCtx.getParameter("pid");
        result = checkParams(pid);
        if(result != null){
            logger.error(result+msgCtx.getParameterMap());
        }else{
            orderPushService.pushOrderByProgramId(Long.valueOf(pid));
            logger.info("Push order sucess! pid=" + pid);
            result = "sucess";
        }
        return result;
    }

    private String checkParams(String... params){
        for(String p : params){
            if(StringUtils.isBlank(p)){
                return "CheckParams error! some params is null!";
            }
        }
        return null;
    }



}
