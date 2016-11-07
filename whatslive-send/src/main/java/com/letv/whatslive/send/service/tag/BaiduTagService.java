package com.letv.whatslive.send.service.tag;

import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.baidu.yun.push.model.*;
import com.google.common.collect.Lists;
import com.letv.whatslive.model.PushDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by wangjian7 on 2015/8/24.
 */
public class BaiduTagService {
    private static final Logger logger = LoggerFactory.getLogger(BaiduTagService.class);

    private BaiduPushClient baiduPushClient;
    /**
     *设备类型，3：Android，4：IOS
     */
    private int deviceType;

    public BaiduTagService(BaiduPushClient baiduPushClient, int deviceType){
        this.baiduPushClient = baiduPushClient;
        this.deviceType = deviceType;
    }


    public PushDetail createTag(Long pushId, String tagName){
        PushDetail result = PushDetail.getPushDetailByPlantform(pushId, deviceType);
        try {
            // 4. specify request arguments
            CreateTagRequest request = new CreateTagRequest().addTagName(
                    tagName).addDeviceType(deviceType);
            // 5. http request
            CreateTagResponse response = baiduPushClient.createTag(request);
            logger.info(String.format("tagName: %s, result: %d",
                    response.getTagName(), response.getResult()));
        }catch (PushClientException e) {
            logger.error("PushClient error!", e);
            result.setResult(false);
            result.setErrorMsg(e.getMessage());
            result.setExceptionClassName(e.getClass().getName());
        } catch (PushServerException e) {
            logger.error(String.format(
                    "PushServer error!requestId: %d, errorCode: %d, errorMessage: %s",
                    e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            result.setResult(false);
            result.setRequestId(e.getRequestId());
            result.setErrorCode(e.getErrorCode());
            result.setErrorMsg(e.getErrorMsg());
            result.setExceptionClassName(e.getClass().getName());
        } catch (Exception e){
            logger.error("PushOther error!", e);
            result.setResult(false);
            result.setErrorMsg(e.getMessage());
            result.setExceptionClassName(e.getClass().getName());
        }
        result.setEndTime(System.currentTimeMillis());
        return result;
    }
    public PushDetail deleteTag(Long pushId, String tagName){
        PushDetail result = PushDetail.getPushDetailByPlantform(pushId, deviceType);
        try {
            // 4. specify request arguments
            DeleteTagRequest request = new DeleteTagRequest().addTagName(
                    tagName).addDeviceType(deviceType);
            // 5. http request
            DeleteTagResponse response = baiduPushClient.deleteTag(request);
            // Http请求结果解析打印
            System.out.println(String.format("tagName: %s, result: %d",
                    response.getTagName(), response.getResult()));
        }catch (PushClientException e) {
            logger.error("PushClient error!", e);
            result.setResult(false);
            result.setErrorMsg(e.getMessage());
            result.setExceptionClassName(e.getClass().getName());
        } catch (PushServerException e) {
            logger.error(String.format(
                    "PushServer error!requestId: %d, errorCode: %d, errorMessage: %s",
                    e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            result.setResult(false);
            result.setRequestId(e.getRequestId());
            result.setErrorCode(e.getErrorCode());
            result.setErrorMsg(e.getErrorMsg());
            result.setExceptionClassName(e.getClass().getName());
        } catch (Exception e){
            logger.error("PushOther error!", e);
            result.setResult(false);
            result.setErrorMsg(e.getMessage());
            result.setExceptionClassName(e.getClass().getName());
        }
        result.setEndTime(System.currentTimeMillis());
        return result;
    }

    public List<PushDetail> addDevicesToTag(Long pushId, String tagName, String[] channelIds){
        //TODO 异常处理
        List<PushDetail> resultList = Lists.newArrayList();
        PushDetail sucessResult = PushDetail.getPushDetailByPlantform(pushId, deviceType);
        sucessResult.setResult(true);
        PushDetail failResult = PushDetail.getPushDetailByPlantform(pushId, deviceType);
        failResult.setResult(false);
        List<String> sucessChannels = Lists.newArrayList();
        List<String> failChannels = Lists.newArrayList();
        try {
            // 4. specify request arguments
            AddDevicesToTagRequest request = new AddDevicesToTagRequest()
                    .addTagName(tagName).addChannelIds(channelIds)
                    .addDeviceType(3);
            // 5. http request
            AddDevicesToTagResponse response = baiduPushClient
                    .addDevicesToTag(request);
            // Http请求结果解析打印
            if (null != response) {
                StringBuilder strBuilder = new StringBuilder();
                strBuilder.append("devicesInTag：{");
                List<?> devicesInfo = response.getDevicesInfoAfterAdded();
                for (int i = 0; i < devicesInfo.size(); i++) {
                    Object object = devicesInfo.get(i);
                    if (i != 0) {
                        strBuilder.append(",");
                    }
                    if (object instanceof DeviceInfo) {
                        DeviceInfo deviceInfo = (DeviceInfo) object;
                        strBuilder.append("{channelId:"
                                + deviceInfo.getChannelId() + ",result:"
                                + deviceInfo.getResult() + "}");
                    }
                }
                strBuilder.append("}");
                System.out.println(strBuilder.toString());
            }
        }catch (PushClientException e) {
            logger.error(String.format("PushClient error! tagName: %s", tagName), e);
//            result.setResult(false);
//            result.setErrorMsg(e.getMessage());
//            result.setExceptionClassName(e.getClass().getName());
        } catch (PushServerException e) {
            logger.error(String.format(
                    "PushServer error! tagName: %s, requestId: %d, errorCode: %d, errorMessage: %s",
                    tagName, e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
//            result.setResult(false);
//            result.setRequestId(e.getRequestId());
//            result.setErrorCode(e.getErrorCode());
//            result.setErrorMsg(e.getErrorMsg());
//            result.setExceptionClassName(e.getClass().getName());
        } catch (Exception e){
            logger.error("PushOther error!", e);
//            result.setResult(false);
//            result.setErrorMsg(e.getMessage());
//            result.setExceptionClassName(e.getClass().getName());
        }
//        result.setEndTime(System.currentTimeMillis());
        return resultList;
    }

    public List<PushDetail> deleteDevicesToTag(Long pushId, String tagName, String[] channelIds){
        //TODO 异常处理
        List<PushDetail> resultList = Lists.newArrayList();
        PushDetail sucessResult = PushDetail.getPushDetailByPlantform(pushId, deviceType);
        sucessResult.setResult(true);
        PushDetail failResult = PushDetail.getPushDetailByPlantform(pushId, deviceType);
        failResult.setResult(false);
        List<String> sucessChannels = Lists.newArrayList();
        List<String> failChannels = Lists.newArrayList();
        try {
            // 4. specify request arguments
            DeleteDevicesFromTagRequest request = new DeleteDevicesFromTagRequest()
                    .addTagName("xxxxx").addChannelIds(channelIds)
                    .addDeviceType(3);
            // 5. http request
            DeleteDevicesFromTagResponse response = baiduPushClient
                    .deleteDevicesFromTag(request);
            // Http请求结果解析打印
            if (null != response) {
                StringBuilder strBuilder = new StringBuilder();
                strBuilder.append("devicesInfoAfterDel:{");
                List<?> list = response.getDevicesInfoAfterDel();
                for (int i = 0; i < list.size(); i++) {
                    if (i != 0) {
                        strBuilder.append(",");
                    }
                    Object object = list.get(i);
                    if (object instanceof DeviceInfo) {
                        DeviceInfo deviceInfo = (DeviceInfo) object;
                        strBuilder.append("{channelId: "
                                + deviceInfo.getChannelId() + ", result: "
                                + deviceInfo.getResult() + "}");
                    }
                }
                strBuilder.append("}");
                System.out.println(strBuilder.toString());
            }
        }catch (PushClientException e) {
            logger.error(String.format("PushClient error! tagName: %s", tagName), e);
//            result.setResult(false);
//            result.setErrorMsg(e.getMessage());
//            result.setExceptionClassName(e.getClass().getName());
        } catch (PushServerException e) {
            logger.error(String.format(
                    "PushServer error! tagName: %s, requestId: %d, errorCode: %d, errorMessage: %s",
                    tagName, e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
//            result.setResult(false);
//            result.setRequestId(e.getRequestId());
//            result.setErrorCode(e.getErrorCode());
//            result.setErrorMsg(e.getErrorMsg());
//            result.setExceptionClassName(e.getClass().getName());
        } catch (Exception e){
            logger.error("PushOther error!", e);
//            result.setResult(false);
//            result.setErrorMsg(e.getMessage());
//            result.setExceptionClassName(e.getClass().getName());
        }
        return resultList;

    }

}
