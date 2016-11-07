package com.letv.whatslive.inner.push.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.inner.constants.InnerConstants;
import com.letv.whatslive.inner.utils.String.StringUtils;
import com.letv.whatslive.model.*;
import com.letv.whatslive.model.push.message.notice.AndroidNotificationMessge;
import com.letv.whatslive.model.push.message.notice.IOSNotificationMessge;
import com.letv.whatslive.model.push.message.pass.PassThroughMessage;
import com.letv.whatslive.mongo.dao.*;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.send.service.push.BaiduAndroidNoticeService;
import com.letv.whatslive.send.service.push.BaiduAndroidPassThroughService;
import com.letv.whatslive.send.service.push.BaiduIOSNoticeService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/8/31.
 */
@Component
public class PushService {

    private final Logger logger = LoggerFactory.getLogger(PushService.class);

    private int MaxChannelSize = 10000;

    @Autowired
    UserDAO userDAO;

    @Autowired
    ProgramDAO programDAO;

    @Autowired
    SubscribeDAO subscribeDAO;

    @Autowired
    DeviceDAO deviceDAO;

    @Autowired
    JedisDAO jedisDAO;

    @Autowired
    PushLogDAO pushLogDAO;

    @Autowired
    PushDetailDAO pushDetailDAO;

    @Autowired
    BaiduAndroidNoticeService baiduAndroidNoticeService;

    @Autowired
    BaiduAndroidPassThroughService baiduAndroidPassThroughService;

    @Autowired
    BaiduIOSNoticeService baiduIOSNoticeService;

    /**
     * 推送预约的通知
     * @param uuid
     * @param pid
     */
    public void pushOrderMessage(String uuid, Long pid){
        String puhLogStringPrefix = "Order push to host";
        logger.info(puhLogStringPrefix+" Program:"+pid+" start push orderMessage! "+uuid);
        Program program = programDAO.getProgramById(pid);
        int status = 0;
        PushLog pushLog = PushLog.getSinglePushLog(pid.toString(), 1, program.getPName());
        pushLog.setPushStartTime(System.currentTimeMillis());
        pushLog.setStartTime(program.getStartTime());
        pushLogDAO.insertPushLog(pushLog);
        if(null != program){
            List<String> androidChannelIdList = Lists.newArrayList();
            List<String> IOSChannelIdList = Lists.newArrayList();
            PassThroughMessage passThroughMessage = new PassThroughMessage("<"+program.getPName()+">即将开始!");
            Map<String, Object> values = Maps.newHashMap();
            values.put("tag","1");
            IOSNotificationMessge iosMessage = new IOSNotificationMessge("<"+program.getPName()+">即将开始!", null, 1, values);
            status = getPushChannelIds(true, pid, pushLog.getId(), androidChannelIdList, IOSChannelIdList, program.getUserId(), passThroughMessage,uuid, puhLogStringPrefix);
            //获取所有推送的ChannelId成功才进行下一步的推送任务
            if(status==0){
                status = pushMessage(pushLog.getId(), androidChannelIdList, IOSChannelIdList, passThroughMessage, iosMessage, uuid, puhLogStringPrefix);
            }
        }
        pushLog.setStatus(status);
        pushLog.setPushEndTime(System.currentTimeMillis());
        pushLogDAO.updatePushLog(pushLog);
        logger.info(puhLogStringPrefix + " program:" + pid + " end push orderMessgae! "+uuid);
    }

    /**
     * 推送直播的通知
     * @param uuid
     * @param pid
     */
    public void pushLiveMessage(String uuid, Long pid){
        String puhLogStringPrefix = "live push to view";
        logger.info(puhLogStringPrefix+" Program:"+pid+" start push orderMessage! "+uuid);
        Program program = programDAO.getProgramById(pid);
        int status = 0;

        if(null != program){
            PushLog pushLog = PushLog.getBatchPushLog(pid.toString(), 2, program.getPName());
            pushLog.setPushStartTime(System.currentTimeMillis());
            pushLog.setStartTime(program.getStartTime());
            pushLogDAO.insertPushLog(pushLog);
            List<String> androidChannelIdList = Lists.newArrayList();
            List<String> IOSChannelIdList = Lists.newArrayList();
            PassThroughMessage passThroughMessage = new PassThroughMessage("<"+program.getPName()+">直播已经开始!");
            Map<String, Object> values = Maps.newHashMap();
            values.put("tag",2);
            IOSNotificationMessge iosMessage = new IOSNotificationMessge("<"+program.getPName()+">直播已经开始!", null, 1, values);
            status = getPushChannelIds(false, pid, pushLog.getId(), androidChannelIdList, IOSChannelIdList, program.getUserId(), passThroughMessage,uuid, puhLogStringPrefix);
            //获取所有推送的ChannelId成功才进行下一步的推送任务
            if(status==0){
                status = pushMessage(pushLog.getId(), androidChannelIdList, IOSChannelIdList, passThroughMessage, iosMessage, uuid, puhLogStringPrefix);
            }
            pushLog.setStatus(status);
            pushLog.setPushEndTime(System.currentTimeMillis());
            pushLogDAO.updatePushLog(pushLog);
            logger.info(puhLogStringPrefix + " program:" + pid + " end push orderMessgae! "+uuid);
        }else{
            logger.error(puhLogStringPrefix + " program:" + pid + " error program is not exist! "+uuid);
        }

    }


    private int getPushChannelIds(boolean pushToHost,Long pid, Long pushId, List<String> androidChannelIdList, List<String> IOSChannelIdList,
                                     Long userId, PassThroughMessage passThroughMessage,  String uuid, String puhLogStringPrefix){
        int result = 0;
        try {
            //查询所有订阅的用户
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("programId", pid);
            params.put("status", 1);
            List<Subscribe> subscribeList = subscribeDAO.getAllSubscribe(1, Integer.MAX_VALUE, params);
            Map<Long, List<Device>> userDev = Maps.newHashMap();

            if(pushToHost){
                //查询预约发起用户的设备信息
                User user = userDAO.getUserById(userId);
                params.clear();
                if (null != user.getDevIdList() && user.getDevIdList().size() > 0) {
                    params.put("devIdList", user.getDevIdList());
                    userDev.put(user.getUserId(), deviceDAO.getDeviceListByParams(params, 0, Integer.MAX_VALUE));
                }
            }else{
                //查询预约订阅用户的设备信息
                for (Subscribe subscribe : subscribeList) {
                    User subUser = userDAO.getUserById(subscribe.getUserId());
                    params.clear();
                    if (null != subUser.getDevIdList() && subUser.getDevIdList().size() > 0) {
                        params.put("devIdList", subUser.getDevIdList());
                        userDev.put(subUser.getUserId(), deviceDAO.getDeviceListByParams(params, 0, Integer.MAX_VALUE));
                    }
                }
            }

            //根据设备是Android还是IOS进行不同的处理
            for (Long key : userDev.keySet()) {
                for (Device device : userDev.get(key)) {
                    if (null != device.getPlatformId() && !StringUtils.isBlank(device.getChannelId())) {
                        if (device.getPlatformId() == 2) {
                            if (checkUserIsOnline(key)) {
                                //查询用户是否在线，在线的话直接推送消息
                                sendMessageToUser(device.getChannelId(), passThroughMessage.toJsonString());
                            } else {
                                androidChannelIdList.add(device.getChannelId());
                            }
                        } else {
                            IOSChannelIdList.add(device.getChannelId());
                        }
                    }
                }
            }
        }catch(Exception e){
            logger.error(puhLogStringPrefix+" process data error! pushId:"+pushId+" "+uuid, e);
            result = 3;
        }
        return result;
    }

    private int pushMessage(Long pushId, List<String> androidChannelIdList, List<String> IOSChannelIdList,
                            PassThroughMessage passThroughMessage, IOSNotificationMessge iosMessage, String uuid, String puhLogStringPrefix){
        int result = 4;
        List<PushDetail> pushDetailList = Lists.newArrayList();
        if (androidChannelIdList.size() > 0) {
            for (int i = 0; i < androidChannelIdList.size() / MaxChannelSize + 1; i++) {
                int startNum = i * MaxChannelSize;
                int endNum = (i + 1) * MaxChannelSize > androidChannelIdList.size() ? androidChannelIdList.size() : (i + 1) * MaxChannelSize;
                String[] subAndroidChannelIds = new String[]{};
                subAndroidChannelIds = androidChannelIdList.subList(startNum, endNum).toArray(subAndroidChannelIds);
//                AndroidNotificationMessge messge = new AndroidNotificationMessge(passThroughMessage.getTitle(),"测试内容",null);
                pushDetailList.add(baiduAndroidPassThroughService.pushMessageToBatchDevice(pushId, subAndroidChannelIds, passThroughMessage));
            }
        }
        if (IOSChannelIdList.size() > 0) {
            String[] IOSChannelIds = new String[]{};
            IOSChannelIds = IOSChannelIdList.toArray(IOSChannelIds);
            pushDetailList.addAll(baiduIOSNoticeService.pushMessageToBatchDevice(pushId, IOSChannelIds, iosMessage));
        }
        for(int i=0;i<pushDetailList.size(); i++ ){
            if(null != pushDetailList.get(i).getChannels()
                    && pushDetailList.get(i).getChannels().size()>0){
                try {
                    String fileName = InnerConstants.PUSH_CHANNEL_FILE_PATH+pushDetailList.get(i).getPushId()+"_"
                            +pushDetailList.get(i).getRetryNum()+"_"+pushDetailList.get(i).getPlantform()+"_"
                            +i+".txt";
                    File file = new File(fileName);
                    FileUtils.writeLines(file, pushDetailList.get(i).getChannels());
                    pushDetailList.get(i).setFileName(fileName);
                } catch (IOException e) {
                    logger.error("Order push to host WriteFile error! pushId:"+pushId+" "+uuid, e);
                }
            }
            pushDetailDAO.saveOrUpdatePushDetail(pushDetailList.get(i));
            if(!pushDetailList.get(i).isResult()){
                result = 5;
            }
        }
        return result;
    }

    private boolean checkUserIsOnline(Long userId){
        Long value = ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get("live_push_online_user_" + userId));
        if(value!=null&&value>0){
            return true;
        }else{
            return false;
        }
    }

    private void sendMessageToUser(String token, String message){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", token);
        jsonObject.put("message",message);
        jedisDAO.getJedisWriteTemplate().publish("push",jsonObject.toString());
        logger.info("Order push to host push data to channel:<"+token+">success! ");
    }
}
