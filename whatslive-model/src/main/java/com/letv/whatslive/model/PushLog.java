package com.letv.whatslive.model;

import com.letv.whatslive.model.constant.PushConstants;
import com.mongodb.DBRef;
import lombok.Getter;
import lombok.Setter;

/**
 * 推送日志
 * Created by wangjian on 15-8-20.
 */

@Getter
@Setter
public class PushLog {

    private Long id;
    private String bussikey; //业务主键
    private String message;
    private String tagName;
    private Long sendTime;
    private Integer bussiType;//1.预约主播推送，2.预约转直播推送到已预约客户端
    private Integer type; //1.推送到单个设备，2.推送到批量设备，3.推送到全设备，4.推送到tag设备，5.增加标签，6.删除标签，7.增加标签下的用户，8.删除标签下的用户
    private int status = 0; //状态 0:推送中，1.推送成功，2.推送失败，3.内部处理过程失败，4.发送请求到百度云推送成功，5.发送请求到百度云推送失败
    private Long startTime; //预约开始时间
    private Long pushStartTime; //推送开始时间
    private Long pushEndTime; //推送结束时间
    private Long createTime; //创建时间

    public PushLog(){

    }

    public PushLog(String bussikey, Integer bussiType, int type, String tagName, String message, Long sendTime){
        this.bussikey = bussikey;
        this.bussiType = bussiType;
        this.type = type;
        this.tagName = tagName;
        this.message = message;
        if(null!=sendTime && sendTime >0){
            this.sendTime = sendTime;
        }
        this.setCreateTime(System.currentTimeMillis());
        this.setPushStartTime(System.currentTimeMillis());
    }
    public static PushLog getSinglePushLog(String bussikey, int bussiType, String message){
        return new PushLog(PushConstants.PUSH_TYPE_PUSH_SINGLE+"_"+bussikey, bussiType, 1, null, message, null);
    }
    public static PushLog getBatchPushLog(String bussikey, int bussiType, String message){
        return new PushLog(PushConstants.PUSH_TYPE_PUSH_BATCH+"_"+bussikey, bussiType, 2, null, message, null);
    }
    public static PushLog getAllPushLog(String bussikey, int bussiType, String message, Long sendTime){
        return new PushLog(PushConstants.PUSH_TYPE_PUSH_ALL+"_"+bussikey, bussiType, 3, null, message, sendTime);
    }
    public static PushLog getTagPushLog(String bussikey, int bussiType, String tagName, String message, Long sendTime){
        return new PushLog(PushConstants.PUSH_TYPE_PUSH_TAG+"_"+bussikey, bussiType, 4, tagName, message, sendTime);
    }
    public static PushLog getAddTagLog(String bussikey, String tagName){
        return new PushLog(PushConstants.PUSH_TYPE_ADD_TAG+"_"+bussikey, null, 5, tagName, null, null);
    }
    public static PushLog getDelTagLog(String bussikey, String tagName){
        return new PushLog(PushConstants.PUSH_TYPE_DEL_TAG+"_"+bussikey, null, 6, tagName, null, null);
    }
    public static PushLog getAddDevicesToTagLog(String bussikey, int bussiType, String tagName){
        return new PushLog(PushConstants.PUSH_TYPE_ADD_DEVICES_TO_TAG+"_"+bussikey, null, 7, tagName, null, null);
    }
    public static PushLog getDelDevicesFromTagLog(String bussikey, String tagName){
        return new PushLog(PushConstants.PUSH_TYPE_DEL_DEVICES_FROM_TAG+"_"+bussikey, null, 8, tagName, null, null);
    }

}
