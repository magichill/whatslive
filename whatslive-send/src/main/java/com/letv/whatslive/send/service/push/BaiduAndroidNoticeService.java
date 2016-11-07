package com.letv.whatslive.send.service.push;

import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.constants.BaiduPushConstants;
import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.baidu.yun.push.model.*;
import com.letv.whatslive.model.PushDetail;
import com.letv.whatslive.model.push.message.notice.AndroidNotificationMessge;
import com.letv.whatslive.send.service.BaiduBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 百度推送android通知消息服务
 * Created by wangjian7 on 2015/8/14.
 */
@Component
public class BaiduAndroidNoticeService extends BaiduBaseService {


    private static final Logger logger = LoggerFactory.getLogger(BaiduAndroidNoticeService.class);

    /**
     *消息超时时间，目前设置为7天
     */
    private int msgExpires = 86400*7;

    /**
     *设备类型，3：Android，4：IOS
     */
    private int deviceType = 3;

    /**
     *消息类型，1：通知消息
     */
    private int MessageType = 1;

    @Autowired
    BaiduPushClient baiduPushAndroidClient;



    /**
     * 推送消息到单个设备
     * @param channelId 设备的channelId
     * @param messge 消息对象
     */
    public PushDetail pushMessageToSingleDevice(Long pushId, String channelId, AndroidNotificationMessge messge){
        PushDetail result = PushDetail.getAndroidPushDetail(pushId);
        try {
            PushMsgToSingleDeviceRequest request = new PushMsgToSingleDeviceRequest();
            request.addChannelId(channelId)
                    .addMessageType(MessageType)
                    .addMsgExpires(msgExpires)
                    .addMessage(messge.toJsonString())
                    .addDeviceType(deviceType);
            // 5. http request
            PushMsgToSingleDeviceResponse response = baiduPushAndroidClient
                    .pushMsgToSingleDevice(request);
            // Http请求结果解析打印
            logger.info(String.format(
                    "Push success!pushId:%s, msgId: %s, sendTime: %s",
                    pushId ,response.getMsgId(),response.getSendTime()));
        }catch (PushClientException e) {
            logger.error("PushClient error! pushId:"+pushId, e);
            result.setResult(false);
            result.setErrorMsg(e.getMessage());
            result.setExceptionClassName(e.getClass().getName());
        } catch (PushServerException e) {
            logger.error(String.format(
                    "PushServer error! pushId:%s, requestId: %d, errorCode: %d, errorMessage: %s",
                    pushId, e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            result.setResult(false);
            result.setRequestId(e.getRequestId());
            result.setErrorCode(e.getErrorCode());
            result.setErrorMsg(e.getErrorMsg());
            result.setExceptionClassName(e.getClass().getName());
        } catch (Exception e){
            logger.error("PushOther error! pushId:"+pushId, e);
            result.setResult(false);
            result.setErrorMsg(e.getMessage());
            result.setExceptionClassName(e.getClass().getName());
        }
        result.setEndTime(System.currentTimeMillis());
        return result;
    }

    /**
     * 推送消息到批量用户
     * @param channelIds 推送的channelId数组
     * @param messge 消息对象
     */
    public PushDetail pushMessageToBatchDevice(Long pushId, String[] channelIds, AndroidNotificationMessge messge){
        PushDetail result = PushDetail.getAndroidPushDetail(pushId);
        result.setChannels(Arrays.asList(channelIds));
        try {
            // 4. specify request arguments
            // pushTagTpye = 1 for common tag pushing
            PushBatchUniMsgRequest request = new PushBatchUniMsgRequest()
                    .addChannelIds(channelIds)
                    .addMsgExpires(msgExpires/7)//批量推送消息过期时间只支持1天
                    .addMessageType(MessageType)
                    .addMessage(messge.toJsonString())
                    .addDeviceType(deviceType);
            // 5. http request
            PushBatchUniMsgResponse response = baiduPushAndroidClient.pushBatchUniMsg(request);
            // Http请求结果解析打印
            logger.info(String.format("Push success! pushId: %s, msgId: %s, sendTime: %d",
                    pushId, response.getMsgId(), response.getSendTime()));
            result.setMsgId(response.getMsgId());
            result.setSendTime(response.getSendTime());
        }catch (PushClientException e) {
            logger.error(String.format("PushClient error! pushId: %s", pushId), e);
            result.setResult(false);
            result.setErrorMsg(e.getMessage());
            result.setExceptionClassName(e.getClass().getName());
        } catch (PushServerException e) {
            logger.error(String.format(
                    "PushServer error! pushId: %s, requestId: %d, errorCode: %d, errorMessage: %s",
                    pushId, e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            result.setResult(false);
            result.setRequestId(e.getRequestId());
            result.setErrorCode(e.getErrorCode());
            result.setErrorMsg(e.getErrorMsg());
            result.setExceptionClassName(e.getClass().getName());
        } catch (Exception e){
            logger.error("PushOther error! pushId: "+pushId, e);
            result.setResult(false);
            result.setErrorMsg(e.getMessage());
            result.setExceptionClassName(e.getClass().getName());
        }
        result.setEndTime(System.currentTimeMillis());
        return result;
    }

    /**
     * 推送消息到所有设备
     * @param sendTime 定时发送的时间，必须大于当前时间后一分钟
     * @param messge 消息对象
     */
    public PushDetail pushMessageToAllDevice(Long pushId, AndroidNotificationMessge messge,long sendTime){
        PushDetail result = PushDetail.getAndroidPushDetail(pushId);
        try {
            // 4. specify request arguments
            PushMsgToAllRequest request = new PushMsgToAllRequest()
                    .addMsgExpires(msgExpires)
                    .addMessageType(MessageType)
                    .addMessage(messge.toJsonString())
                    .addDeviceType(deviceType);
            Long time = getSendTIme(sendTime);
            if(null != getSendTIme(sendTime)){
                request.addSendTime(time);
            }
            // 5. http request
            PushMsgToAllResponse response = baiduPushAndroidClient.pushMsgToAll(request);
            // Http请求结果解析打印
            logger.info(String.format(
                    "Push success! PushId %s, msgId: %s, sendTime: %s, timerId: %s",
                    pushId, response.getMsgId(),response.getSendTime(), response.getTimerId()));
            result.setMsgId(response.getMsgId());
            result.setSendTime(response.getSendTime());
            if(null != time){
                result.setTimerId(response.getTimerId());
            }
        } catch (PushClientException e) {
            logger.error("PushClient error!pushId:"+pushId, e);
            result.setResult(false);
            result.setErrorMsg(e.getMessage());
            result.setExceptionClassName(e.getClass().getName());
        } catch (PushServerException e) {
            logger.error(String.format(
                    "PushServer error! pushId:%s, requestId: %d, errorCode: %d, errorMessage: %s",
                    pushId, e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            result.setResult(false);
            result.setRequestId(e.getRequestId());
            result.setErrorCode(e.getErrorCode());
            result.setErrorMsg(e.getErrorMsg());
            result.setExceptionClassName(e.getClass().getName());
        } catch (Exception e){
            logger.error("PushOther error! pushId:"+pushId, e);
            result.setResult(false);
            result.setErrorMsg(e.getMessage());
            result.setExceptionClassName(e.getClass().getName());
        }
        result.setEndTime(System.currentTimeMillis());
        return result;
    }

    /**
     * 推送消息到tag中的所有用户
     * @param tag 推送的目标tag
     * @param sendTime 推送的定时时间
     * @param messge 消息对象
     */
    public PushDetail pushMessageToTag(Long pushId, String tag, long sendTime, AndroidNotificationMessge messge){
        PushDetail result = PushDetail.getAndroidPushDetail(pushId);
        try {
            // 4. specify request arguments
            // pushTagTpye = 1 for common tag pushing
            PushMsgToTagRequest request = new PushMsgToTagRequest()
                    .addTagName(tag)
                    .addMsgExpires(msgExpires)
                    .addMessageType(MessageType)
                    .addMessage(messge.toJsonString())
                    .addDeviceType(deviceType);
            Long time = getSendTIme(sendTime);
            if(null != getSendTIme(sendTime)){
                request.addSendTime(time);
            }
            // 5. http request
            PushMsgToTagResponse response = baiduPushAndroidClient.pushMsgToTag(request);
            // Http请求结果解析打印
            logger.info(String.format(
                    "Push success!pushId:%s, msgId: %s, sendTime: %s, timerId: %s",
                    pushId ,response.getMsgId(),response.getSendTime(), response.getTimerId()));
            result.setMsgId(response.getMsgId());
            result.setSendTime(response.getSendTime());
            if(null != time){
                result.setTimerId(response.getTimerId());
            }
        } catch (PushClientException e) {
            logger.error("PushClient error!pushId:"+pushId, e);
            result.setResult(false);
            result.setErrorMsg(e.getMessage());
            result.setExceptionClassName(e.getClass().getName());
        } catch (PushServerException e) {
            logger.error(String.format(
                    "PushServer error! pushId:%s, requestId: %d, errorCode: %d, errorMessage: %s",
                    pushId, e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            result.setResult(false);
            result.setRequestId(e.getRequestId());
            result.setErrorCode(e.getErrorCode());
            result.setErrorMsg(e.getErrorMsg());
            result.setExceptionClassName(e.getClass().getName());
        } catch (Exception e){
            logger.error("PushOther error! pushId:"+pushId, e);
            result.setResult(false);
            result.setErrorMsg(e.getMessage());
            result.setExceptionClassName(e.getClass().getName());
        }
        result.setEndTime(System.currentTimeMillis());
        return result;
    }

}
