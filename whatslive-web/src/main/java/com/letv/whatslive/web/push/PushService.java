package com.letv.whatslive.web.push;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.httpclient.HttpClientUtil;
import com.letv.whatslive.common.httpclient.HttpFetchResult;
import com.letv.whatslive.web.constant.ServiceConstants;
import com.letv.whatslive.web.util.http.HttpClientUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by wangjian7 on 2015/8/25.
 */
@Component
public class PushService {
    private static final Logger logger = LoggerFactory.getLogger(PushService.class);

    private final int MaxChannelSize = 10;

    public void pushMessageToAll(String bussikey, String message, long sendTime){
        try {
            message = URLEncoder.encode(message,"UTF-8");
            StringBuilder request = new StringBuilder(ServiceConstants.PUSH_SEND_MESSAGE_TO_ALL);
            request.append("?bussikey=").append(bussikey).append("&");
            request.append("message=").append(message).append("&");
            request.append("sendTime=").append(sendTime);
            Long startTime = System.currentTimeMillis();
            String result =  HttpClientUtils.get(request.toString(), 2000, 2000, 1);
            Long endTime = System.currentTimeMillis();
            logger.info("request push to all result：" + result + " spend Time:"+ (endTime-sendTime));
        } catch (Exception e) {
            logger.error("request push to all error!",e);
        }
    }
    public void pushMessageToTag(String bussikey, String tagName, String message, long sendTime){
        try {
            message = URLEncoder.encode(message,"UTF-8");
            tagName = URLEncoder.encode(tagName,"UTF-8");
            StringBuilder request = new StringBuilder(ServiceConstants.PUSH_SEND_MESSAGE_TO_TAG);
            request.append("?bussikey=").append(bussikey).append("&");
            request.append("tagName=").append(tagName).append("&");
            request.append("message=").append(message).append("&");
            request.append("sendTime=").append(sendTime);
            Long startTime = System.currentTimeMillis();
            String result =  HttpClientUtils.get(request.toString(), 2000, 2000, 1);
            Long endTime = System.currentTimeMillis();
            logger.info("request push to tag result：" + result + " spend Time:"+ (endTime-sendTime));
        } catch (Exception e) {
            logger.error("request push to tag error!",e);
        }
    }
    public void addDevicesToTag(List<String> channelIds, String tagName, int plantform){
        StringBuilder channelIdString = new StringBuilder();
        for (int i = 0; i < channelIds.size(); i++) {
            if(i>0 && (i)%MaxChannelSize==0){
                try {
                    tagName = URLEncoder.encode(tagName,"UTF-8");
                    StringBuilder request = new StringBuilder(ServiceConstants.PUSH_ADD_DEVICES_TO_TAG);
                    request.append("plantform=").append(String.valueOf(plantform)).append("&");
                    request.append("channelIds=")
                            .append(channelIdString.deleteCharAt(channelIdString.length()-1).toString());
                    Long startTime = System.currentTimeMillis();
                    String result =  HttpClientUtils.get(request.toString(), 2000, 2000, 1);
                    Long endTime = System.currentTimeMillis();
                    logger.info("request push to tag result：" + result + " spend Time:"+ (endTime-startTime));
                } catch (Exception e) {
                    logger.error("request add devices to tag error!",e);
                }
                channelIdString.delete(0, channelIdString.length());
            }
            channelIdString.append(channelIds.get(i)).append(",");
        }
    }

    public void delDevicesFromTag(List<String> channelIds, String tagName, int plantform){
        StringBuilder channelIdString = new StringBuilder();
        for (int i = 0; i < channelIds.size(); i++) {
            if(i>0 && (i)%MaxChannelSize==0){
                try {
                    tagName = URLEncoder.encode(tagName,"UTF-8");
                    StringBuilder request = new StringBuilder(ServiceConstants.PUSH_DEL_DEVICES_FROM_TAG);
                    request.append("plantform=").append(String.valueOf(plantform)).append("&");
                    request.append("channelIds=")
                            .append(channelIdString.deleteCharAt(channelIdString.length() - 1).toString());
                    Long startTime = System.currentTimeMillis();
                    String result =  HttpClientUtils.get(request.toString(), 2000, 2000, 1);
                    Long endTime = System.currentTimeMillis();
                    logger.info("request push to tag result：" + result + " spend Time:"+ (endTime-startTime));
                } catch (Exception e) {
                    logger.error("request add devices to tag error!",e);
                }
                channelIdString.delete(0, channelIdString.length());
            }
            channelIdString.append(channelIds.get(i)).append(",");
        }
    }
}
