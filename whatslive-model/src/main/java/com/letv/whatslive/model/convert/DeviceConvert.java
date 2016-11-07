package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.Device;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by wangjian7 on 2015/7/23.
 */
public class DeviceConvert {
    public static DBObject castDeviceToDBObject(Device device){
        DBObject dbo = new BasicDBObject();
        if(StringUtils.isNotBlank(device.getId())){
            dbo.put("_id",device.getId());
        }else{
            return null;
        }

        if(device.getPlatformId()>0){
            dbo.put("platformId",device.getPlatformId());
        }else{
            dbo.put("platformId",0);
        }

        if(StringUtils.isNotBlank(device.getDevModel())){
            dbo.put("devModel",device.getDevModel());
        }else{
            dbo.put("devModel","");
        }

        if(device.getEditionId()>0){
            dbo.put("editionId",device.getEditionId());
        }else{
            dbo.put("editionId",0);
        }

        if(StringUtils.isNotBlank(device.getCorporationId())){
            dbo.put("corporationId",device.getCorporationId());

        }else{
            dbo.put("corporationId","");
        }

        if(StringUtils.isNotBlank(device.getSubCoopId())){
            dbo.put("subCoopId",device.getSubCoopId());
        }else{
            dbo.put("subCoopId","");
        }

        if(StringUtils.isNotBlank(device.getImei())){
            dbo.put("imei",device.getImei());
        }else{
            dbo.put("imei","");
        }

        if(StringUtils.isNotBlank(device.getImsi())){
            dbo.put("imsi",device.getImsi());
        }else{
            dbo.put("imsi","");
        }

        if(StringUtils.isNotBlank(device.getDevToken())){
            dbo.put("devToken",device.getDevToken());
        }else{
            dbo.put("devToken","");
        }

        if(StringUtils.isNotBlank(device.getDevStatus())){
            dbo.put("devStatus",device.getDevStatus());
        }else{
            dbo.put("devStatus","");
        }

        if(device.getLastActiveTime()>0){
            dbo.put("lastActiveTime",device.getLastActiveTime());
        }

        if(StringUtils.isNotBlank(device.getGpuModel())){
            dbo.put("gpuModel",device.getGpuModel());
        }else{
            dbo.put("gpuModel","");
        }

        if(StringUtils.isNotBlank(device.getCore())){
            dbo.put("core",device.getCore());
        }else{
            dbo.put("core","");
        }

        if(StringUtils.isNotBlank(device.getMemory())){
            dbo.put("memory",device.getMemory());
        }else{
            dbo.put("memory","");
        }

        if(StringUtils.isNotBlank(device.getGcard())){
            dbo.put("gcard",device.getGcard());
        }else{
            dbo.put("gcard","");
        }
        if(StringUtils.isNotBlank(device.getGmem())){
            dbo.put("gmem",device.getGmem());
        }else{
            dbo.put("gmem","");
        }
        dbo.put("channelId", device.getChannelId());
        dbo.put("type",device.getType());
        dbo.put("createTime",System.currentTimeMillis());
        dbo.put("updateTime",System.currentTimeMillis());

        return dbo;


//        DBObject dbo = new BasicDBObject();
//        if (device.getId() != null) {
//            dbo.put("_id", device.getId());
//        }
//        dbo.put("platformId",device.getPlatformId());
//        dbo.put("osVersion", device.getOsVersion());
//        dbo.put("devModel", device.getDevModel());
//        dbo.put("editionId", device.getEditionId());
//        dbo.put("corporationId", device.getCorporationId());
//        dbo.put("channelId", device.getChannelId());
//        return dbo;
    }

    public static Device castDBObjectToDevice(DBObject dbObject){
        Device device = new Device();
        if(dbObject != null) {
            device.setId(ObjUtils.toString(dbObject.get("_id")));
            device.setPlatformId(ObjUtils.toInteger(dbObject.get("platformId")));
            device.setOsVersion(ObjUtils.toString(dbObject.get("osVersion")));
            device.setDevModel(ObjUtils.toString(dbObject.get("devModel")));
            device.setEditionId(ObjUtils.toInteger(dbObject.get("editionId")));
            device.setCorporationId(ObjUtils.toString(dbObject.get("corporationId")));
            device.setChannelId(ObjUtils.toString(dbObject.get("channelId")));
        }

        return device;
    }
}
