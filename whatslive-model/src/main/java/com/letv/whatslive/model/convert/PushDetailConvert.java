package com.letv.whatslive.model.convert;

import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.PushDetail;
import com.letv.whatslive.model.PushLog;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by gaoshan on 15-7-13.
 */
public class PushDetailConvert {


    public static DBObject castPushLogToDBObject(PushDetail pushDetail) {
        DBObject dbo = new BasicDBObject();
        if (pushDetail.getId() != null) {
            dbo.put("_id", pushDetail.getId());
        }
        dbo.put("pushId", pushDetail.getPushId());
        dbo.put("result", pushDetail.isResult()?1:0);
        dbo.put("plantform", pushDetail.getPlantform());
        if(null != pushDetail.getMsgId()){
            dbo.put("msgId", pushDetail.getMsgId());
        }
        if(null != pushDetail.getSendTime()){
            dbo.put("sendTime", pushDetail.getSendTime());
        }
        if(null != pushDetail.getStatus()){
            dbo.put("status", pushDetail.getStatus());
        }

        if(null != pushDetail.getRequestId()){
            dbo.put("requestId", pushDetail.getRequestId());
        }
        if(null != pushDetail.getErrorCode()){
            dbo.put("errorCode", pushDetail.getErrorCode());
        }
        if(null != pushDetail.getErrorMsg()){
            dbo.put("errorMsg", pushDetail.getErrorMsg());
        }
        if(null != pushDetail.getExceptionClassName()){
            dbo.put("exceptionClassName", pushDetail.getExceptionClassName());
        }
        if(null != pushDetail.getTimerId()){
            dbo.put("timerId", pushDetail.getTimerId());
        }
        dbo.put("startTime", pushDetail.getStartTime());
        dbo.put("endTime", pushDetail.getEndTime());
        dbo.put("retryNum", pushDetail.getRetryNum());
        if(null != pushDetail.getFileName()){
            dbo.put("fileName", pushDetail.getFileName());
        }
        dbo.put("createTime", pushDetail.getCreateTime());
        if(null != pushDetail.getUpdateTime()){
            dbo.put("updateTime", pushDetail.getUpdateTime());
        }
        return dbo;
    }

    public static PushDetail castDBObjectToPushDetail(DBObject dbObject){
        PushDetail pushDetail = new PushDetail();
        if(dbObject != null) {
            pushDetail.setId(ObjectUtils.toLong(dbObject.get("_id")));
            pushDetail.setPushId(ObjectUtils.toLong(dbObject.get("pushId")));
            pushDetail.setResult(ObjectUtils.toInteger(dbObject.get("result"))==1?true:false);
            pushDetail.setPlantform(ObjectUtils.toInteger(dbObject.get("plantform")));
            pushDetail.setMsgId(ObjectUtils.toString(dbObject.get("msgId")));
            pushDetail.setSendTime(ObjectUtils.toLong(dbObject.get("sendTime")));
            pushDetail.setStatus(ObjectUtils.toInteger(dbObject.get("status")));
            pushDetail.setRequestId(ObjectUtils.toLong(dbObject.get("requestId")));
            pushDetail.setErrorCode(ObjectUtils.toInteger(dbObject.get("errorCode")));
            pushDetail.setErrorMsg(ObjectUtils.toString(dbObject.get("errorMsg")));
            pushDetail.setExceptionClassName(ObjectUtils.toString(dbObject.get("exceptionClassName")));
            pushDetail.setTimerId(ObjectUtils.toString(dbObject.get("exceptionClassName")));
            pushDetail.setStartTime(ObjectUtils.toLong(dbObject.get("startTime"), 0L));
            pushDetail.setEndTime(ObjectUtils.toLong(dbObject.get("endTime"), 0L));
            pushDetail.setRetryNum(ObjectUtils.toInteger(dbObject.get("endTime"), 0));
            pushDetail.setFileName(ObjectUtils.toString("fileName"));
            pushDetail.setCreateTime(ObjectUtils.toLong(dbObject.get("createTime")));
            pushDetail.setUpdateTime(ObjectUtils.toLong(dbObject.get("createTime")));

        }else{
            return null;
        }
        return pushDetail;
    }
}
