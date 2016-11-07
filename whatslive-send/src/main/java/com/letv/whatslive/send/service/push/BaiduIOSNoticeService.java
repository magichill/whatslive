package com.letv.whatslive.send.service.push;

import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.baidu.yun.push.model.*;
import com.google.common.collect.Lists;
import com.letv.whatslive.model.PushDetail;
import com.letv.whatslive.model.push.message.notice.IOSNotificationMessge;
import com.letv.whatslive.send.constants.PushCosntants;
import com.letv.whatslive.send.service.BaiduBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 百度推送IOS通知服务
 * Created by wangjian7 on 2015/8/14.
 */
@Component
public class BaiduIOSNoticeService extends BaiduBaseService {

    private static final Logger logger = LoggerFactory.getLogger(BaiduIOSNoticeService.class);

    /**
     *消息超时时间，目前设置为7天
     */
    private int msgExpires = 86400*7;

    /**
     *设备类型，3：Android，4：IOS
     */
    private int deviceType = 4;

    /**
     *消息类型，1：通知消息
     */
    private int MessageType = 1;

    private int deployStatus = PushCosntants.BAIDU_PUSH_IOS_DEPLOY_STATUS;

    @Autowired
    BaiduPushClient baiduPushIOSClient;

    /**
     * 推送消息到单个设备
     * @param channelId 设备的channelId
     * @param messge 消息对象
     */
    public PushDetail pushMessageToSingleDevice(Long pushId, String channelId, IOSNotificationMessge messge) {
        PushDetail result = PushDetail.getIOSPushDetail(pushId);
        try {
            PushMsgToSingleDeviceRequest request = new PushMsgToSingleDeviceRequest()
                    .addChannelId(channelId)
                    .addMsgExpires(msgExpires) // 设置message的有效时间
                    .addMessageType(MessageType)// 1：通知,0:透传消息.默认为0 注：IOS只有通知.
                    .addMessage(messge.toJsonString())
                    .addDeployStatus(deployStatus)
                    .addDeviceType(deviceType);// deviceType => 3:android, 4:ios
            // 5. http request
            PushMsgToSingleDeviceResponse response = baiduPushIOSClient
                    .pushMsgToSingleDevice(request);
            // Http请求结果解析打印
            logger.info(String.format(
                    "Push success! pushId:%s, channelId:%s, msgId: %s, sendTime: %s",
                    pushId, channelId, response.getMsgId(), response.getSendTime()));
            result.setMsgId(response.getMsgId());
            result.setSendTime(response.getSendTime());
        }catch (PushClientException e) {
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
    public List<PushDetail> pushMessageToBatchDevice(Long pushId, String[] channelIds, IOSNotificationMessge messge){
        List<PushDetail> resultList = Lists.newArrayList();
        PushDetail sucessResult = PushDetail.getIOSPushDetail(pushId);
        sucessResult.setResult(true);
        PushDetail failResult = PushDetail.getIOSPushDetail(pushId);
        failResult.setResult(false);
        List<String> sucessChannels = Lists.newArrayList();
        List<String> failChannels = Lists.newArrayList();
        //循环发送
        for(String channelId:channelIds){
            try {
                PushMsgToSingleDeviceRequest request = new PushMsgToSingleDeviceRequest()
                        .addChannelId(channelId)
                        .addMsgExpires(msgExpires) // 设置message的有效时间
                        .addMessageType(MessageType)// 1：通知,0:透传消息.默认为0 注：IOS只有通知.
                        .addMessage(messge.toJsonString())
                        .addDeployStatus(deployStatus)
                        .addDeviceType(deviceType);// deviceType => 3:android, 4:ios
                // 5. http request
                PushMsgToSingleDeviceResponse response = baiduPushIOSClient
                        .pushMsgToSingleDevice(request);
                // Http请求结果解析打印
                logger.info(String.format(
                        "Push success! pushId:%s, channelId:%s, msgId: %s, sendTime: %s",
                        pushId, channelId, response.getMsgId(), response.getSendTime()));
                sucessChannels.add(channelId);

            }catch (PushClientException e) {
                logger.error(String.format("PushClient error! pushId:%s , channelId: %s",pushId,channelId), e);
                failChannels.add(channelId);
                if(failResult.getErrorMsg()!=null){
                    failResult.setErrorMsg(e.getMessage());
                    failResult.setExceptionClassName(e.getClass().getName());
                }

            } catch (PushServerException e) {
                logger.error(String.format(
                        "PushServer error! pushId:%s, channelId: %s, requestId: %d, errorCode: %d, errorMessage: %s",
                        pushId, channelId, e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
                failChannels.add(channelId);
                if(failResult.getErrorCode()!=null){
                    failResult.setRequestId(e.getRequestId());
                    failResult.setErrorCode(e.getErrorCode());
                    failResult.setErrorMsg(e.getErrorMsg());
                    failResult.setExceptionClassName(e.getClass().getName());
                }

            } catch (Exception e){
                logger.error(String.format("PushOther error! pushId:%s, channelId: %s",pushId, channelId), e);
                failChannels.add(channelId);
            }
        }
        if(sucessChannels.size()>0){
            sucessResult.setEndTime(System.currentTimeMillis());
            sucessResult.setChannels(sucessChannels);
            resultList.add(sucessResult);
        }
        if(failChannels.size()>0){
            failResult.setEndTime(sucessResult.getEndTime());
            failResult.setChannels(failChannels);
            resultList.add(failResult);
        }
        return resultList;
    }

    /**
     * 推送消息到全部设备
     * @param messge 消息对象
     */
    public PushDetail pushMessageToAllDevices(Long pushId, long sendTime, IOSNotificationMessge messge) {
        PushDetail result = PushDetail.getIOSPushDetail(pushId);
        try {
            PushMsgToAllRequest request = new PushMsgToAllRequest()
                    .addMsgExpires(msgExpires) // 设置message的有效时间
                    .addMessageType(MessageType)// 1：通知,0:透传消息.默认为0 注：IOS只有通知.
                    .addMessage(messge.toJsonString())
                    .addDepolyStatus(deployStatus)
                    .addDeviceType(deviceType);// deviceType => 3:android, 4:ios
            Long time = getSendTIme(sendTime);
            if(null != getSendTIme(sendTime)){
                request.addSendTime(time);
            }
            // 5. http request
            PushMsgToAllResponse response = baiduPushIOSClient
                    .pushMsgToAll(request);
            // Http请求结果解析打印
            logger.info(String.format(
                    "Push success! pushId:%s, msgId: %s, sendTime: %s, timerId: %s",
                    pushId ,response.getMsgId(),response.getSendTime(), response.getTimerId()));
            result.setMsgId(response.getMsgId());
            result.setSendTime(response.getSendTime());
            if(null != time){
                result.setTimerId(response.getTimerId());
            }
        }catch (PushClientException e) {
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
     * 推送消息到标签中的设备
     * @param pushId
     * @param tagName
     * @param sendTime
     * @param messge
     * @return
     */
    public PushDetail pushMessageToTagDevices(Long pushId, String tagName, long sendTime, IOSNotificationMessge messge) {
        PushDetail result = PushDetail.getIOSPushDetail(pushId);
        try {
            PushMsgToTagRequest request = new PushMsgToTagRequest()
                    .addTagName(tagName)
                    .addMsgExpires(msgExpires) // 设置message的有效时间
                    .addMessageType(MessageType)// 1：通知,0:透传消息.默认为0 注：IOS只有通知.
                    .addMessage(messge.toJsonString())
                    .addDeployStatus(deployStatus)
                    .addDeviceType(deviceType);// deviceType => 3:android, 4:ios
            Long time = getSendTIme(sendTime);
            if(null != getSendTIme(sendTime)){
                request.addSendTime(time);
            }
            // 5. http request
            PushMsgToTagResponse response = baiduPushIOSClient.pushMsgToTag(request);
            // Http请求结果解析打印
            logger.info(String.format(
                    "Push success! pushId:%s, msgId: %s, sendTime: %s, timerId: %s",
                    pushId ,response.getMsgId(),response.getSendTime(), response.getTimerId()));
            result.setMsgId(response.getMsgId());
            result.setSendTime(response.getSendTime());
            if(null != time){
                result.setTimerId(response.getTimerId());
            }
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

}
