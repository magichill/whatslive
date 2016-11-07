package com.letv.whatslive.inner.Service;

import com.baidu.yun.push.model.MsgSendInfo;
import com.letv.whatslive.model.PushDetail;
import com.letv.whatslive.model.PushLog;
import com.letv.whatslive.model.push.message.notice.AndroidNotificationMessge;
import com.letv.whatslive.model.push.message.notice.IOSNotificationMessge;
import com.letv.whatslive.mongo.dao.PushDetailDAO;
import com.letv.whatslive.mongo.dao.PushLogDAO;
import com.letv.whatslive.send.service.push.BaiduAndroidNoticeService;
import com.letv.whatslive.send.service.push.BaiduAndroidPassThroughService;
import com.letv.whatslive.send.service.push.BaiduIOSNoticeService;
import com.letv.whatslive.send.service.query.BaiduQueryService;
import com.letv.whatslive.send.service.tag.BaiduTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wangjian7 on 2015/8/24.
 */
@Component
public class BaiduPushService {

    private static final Logger logger = LoggerFactory.getLogger(BaiduPushService.class);

    @Autowired
    BaiduTagService baiduAndroidTagService;
    @Autowired
    BaiduTagService baiduIOSTagService;

    @Autowired
    BaiduQueryService baiduAndroidQueryService;
    @Autowired
    BaiduQueryService baiduIOSQueryService;

    @Autowired
    BaiduAndroidPassThroughService baiduAndroidPassThroughService;
    @Autowired
    BaiduIOSNoticeService baiduIOSNoticeService;
    @Autowired
    BaiduAndroidNoticeService baiduAndroidNoticeService;

    @Autowired
    PushLogDAO pushLogDAO;

    @Autowired
    PushDetailDAO pushDetailDAO;

    public void pushNoticeMessageToAllDevices(String bussikey, int bussiType, String message, long sendTime){
        int status = 4;
        PushLog pushLog = PushLog.getAllPushLog(bussikey, bussiType, message, sendTime);
        pushLog.setPushStartTime(System.currentTimeMillis());
        pushLog.setId(pushLogDAO.insertPushLog(pushLog));
        try {
            AndroidNotificationMessge androidMessge = new AndroidNotificationMessge(message, message, null);
            PushDetail androidResult = baiduAndroidNoticeService.pushMessageToAllDevice(pushLog.getId(), androidMessge, sendTime);
            pushDetailDAO.saveOrUpdatePushDetail(androidResult);
            if(!androidResult.isResult()){
                status = 5;
            }
            IOSNotificationMessge IOSMessage = new IOSNotificationMessge(message, null, 1, null);
            PushDetail IOSResult = baiduIOSNoticeService.pushMessageToAllDevices(pushLog.getId(), sendTime, IOSMessage);
            pushDetailDAO.saveOrUpdatePushDetail(IOSResult);
            if(!IOSResult.isResult()){
                status = 5;
            }
        }catch (Exception e){
            logger.error("push to all error! bussikey:"+bussikey, e);
            status = 3;
        }
        pushLog.setStatus(status);
        pushLog.setPushEndTime(System.currentTimeMillis());
        pushLogDAO.updatePushLog(pushLog);

    }

    public void pushNoticeMessageToTagDevices(String bussikey, int bussiType, String tagName, String message, long sendTime){
        int status = 0;
        PushLog pushLog = PushLog.getAllPushLog(bussikey, bussiType, message, sendTime);
        pushLog.setPushStartTime(System.currentTimeMillis());
        pushLog.setId(pushLogDAO.insertPushLog(pushLog));
        try {
            AndroidNotificationMessge androidMessge = new AndroidNotificationMessge(message, message, null);
            PushDetail androidResult = baiduAndroidNoticeService.pushMessageToTag(pushLog.getId(), tagName, sendTime, androidMessge);
            pushDetailDAO.saveOrUpdatePushDetail(androidResult);
            if(!androidResult.isResult()){
                status = 5;
            }
            IOSNotificationMessge IOSMessage = new IOSNotificationMessge(message, null, 1, null);
            PushDetail IOSResult = baiduIOSNoticeService.pushMessageToTagDevices(pushLog.getId(), tagName, sendTime, IOSMessage);
            pushDetailDAO.saveOrUpdatePushDetail(IOSResult);
            if(!IOSResult.isResult()){
                status = 5;
            }
        }catch (Exception e){
            logger.error("push to tag error! bussikey:"+bussikey, e);
            status = 3;

        }
        pushLog.setStatus(status);
        pushLog.setPushEndTime(System.currentTimeMillis());
        pushLogDAO.updatePushLog(pushLog);
    }

    public void createTag(String bussikey, String tagName){
        int status = 0;
        PushLog pushLog = PushLog.getAddTagLog(bussikey, tagName);
        pushLog.setPushStartTime(System.currentTimeMillis());
        pushLog.setId(pushLogDAO.insertPushLog(pushLog));
        try {
            //TODO 处理返回结果
            baiduAndroidTagService.createTag(pushLog.getId(), tagName);
            baiduIOSTagService.createTag(pushLog.getId(), tagName);
        }catch (Exception e){
            logger.error("create tag error! bussikey:"+bussikey, e);
            status = 3;
        }
        pushLog.setStatus(status);
        pushLog.setPushEndTime(System.currentTimeMillis());
        pushLogDAO.updatePushLog(pushLog);
    }

    public void addDevicesToTag(String bussikey, String[] channels, String tagName, int plantform){
        int status = 1;
        PushLog pushLog = PushLog.getAddTagLog(bussikey, tagName);
        pushLog.setPushStartTime(System.currentTimeMillis());
        pushLog.setId(pushLogDAO.insertPushLog(pushLog));
        try {
            //TODO 处理返回结果
            if (plantform == 3) {
                baiduAndroidTagService.addDevicesToTag(pushLog.getId(), tagName, channels);
            } else if (plantform == 4) {
                baiduIOSTagService.addDevicesToTag(pushLog.getId(), tagName, channels);
            }
        }catch (Exception e){
            logger.error("add devices to tag! bussikey:"+bussikey, e);
            status = 3;
        }
        pushLog.setStatus(status);
        pushLog.setPushEndTime(System.currentTimeMillis());
        pushLogDAO.updatePushLog(pushLog);
    }

    public void queryMsgStatus(String[] msgIds, int plantform){
        List<MsgSendInfo> msgSendInfoList = null;
        if(plantform==3){
            msgSendInfoList = baiduAndroidQueryService.queryMessageStatus(msgIds);
        }else if(plantform==4){
            msgSendInfoList = baiduIOSQueryService.queryMessageStatus(msgIds);
        }
        if(msgSendInfoList!=null && msgSendInfoList.size()>0){
            for(MsgSendInfo msgSendInfo : msgSendInfoList){
                PushDetail pushDetail = pushDetailDAO.getpushDetailByMsgId(msgSendInfo.getMsgId());
                pushDetail.setStatus(msgSendInfo.getMsgStatus());
                pushDetail.setSendTime(msgSendInfo.getSendTime());
                pushDetail.setUpdateTime(System.currentTimeMillis());
                pushDetailDAO.saveOrUpdatePushDetail(pushDetail);
            }
        }
    }

}
