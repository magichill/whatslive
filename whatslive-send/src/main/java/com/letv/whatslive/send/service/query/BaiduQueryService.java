package com.letv.whatslive.send.service.query;

import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.constants.BaiduPushConstants;
import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.baidu.yun.push.model.MsgSendInfo;
import com.baidu.yun.push.model.QueryMsgStatusRequest;
import com.baidu.yun.push.model.QueryMsgStatusResponse;
import com.letv.whatslive.send.service.BaiduBaseService;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by wangjian7 on 2015/8/26.
 */
public class BaiduQueryService extends BaiduBaseService{
    private static final Logger logger = getLogger(BaiduQueryService.class);

    private BaiduPushClient baiduPushClient;
    /**
     *设备类型，3：Android，4：IOS
     */
    private int deviceType;

    public BaiduQueryService(BaiduPushClient baiduPushClient, int deviceType){
        this.baiduPushClient = baiduPushClient;
        this.deviceType = deviceType;
    }

    public List<MsgSendInfo> queryMessageStatus(String[] msgIds){
        List<MsgSendInfo> msgSendInfoList = null;
        try {
            // 4. specify request arguments
            QueryMsgStatusRequest request = new QueryMsgStatusRequest()
                    .addMsgIds(msgIds)
                    .addDeviceType(3);
            // 5. http request
            QueryMsgStatusResponse response = baiduPushClient
                    .queryMsgStatus(request);
            // Http请求返回值解析
            System.out.println("totalNum: " + response.getTotalNum() + "\n"
                    + "result:");
            if (null != response) {
                msgSendInfoList = response.getMsgSendInfos();
            }
        } catch (PushClientException e) {
            //TODO 异常处理
            if (BaiduPushConstants.ERROROPTTYPE) {
//                throw e;
            } else {
                e.printStackTrace();
            }
        } catch (PushServerException e) {
            if (BaiduPushConstants.ERROROPTTYPE) {
//                throw e;
            } else {
                System.out.println(String.format(
                        "requestId: %d, errorCode: %d, errorMsg: %s",
                        e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            }
        }
        return msgSendInfoList;
    }
}
