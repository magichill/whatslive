package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.ProgramReplay;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 直播回放转换器
 * Created by wangjian7 on 2015/10/20.
 */
public class ProgramReplayConvert {


    public static DBObject castProgramReplayToDBObject(ProgramReplay programReplay) {
        DBObject dbo = new BasicDBObject();
        if (programReplay.getId() != null) {
            dbo.put("_id", programReplay.getId());
        }
        dbo.put("pid", ObjUtils.toLong(programReplay.getPid()));
        dbo.put("status", ObjUtils.ifNull(programReplay.getStatus(), 1));
        dbo.put("logURL",ObjUtils.toString(programReplay.getLogURL(), ""));
        dbo.put("createTime", ObjUtils.toLong(programReplay.getCreateTime(), System.currentTimeMillis()));
        dbo.put("updateTime", ObjUtils.toLong(programReplay.getUpdateTime()));
        dbo.put("retryNum", ObjUtils.toInteger(programReplay.getUpdateTime(), 0));
        return dbo;
    }

    public static ProgramReplay castDBObjectToProgramReplay(DBObject dbObject){
        ProgramReplay programReplay = new ProgramReplay();
        if(dbObject != null) {
            programReplay.setId(ObjUtils.toLong(dbObject.get("_id")));
            programReplay.setPid(ObjUtils.toLong(dbObject.get("pid")));
            programReplay.setLogURL(ObjUtils.toString(dbObject.get("logURL")));
            programReplay.setStatus(ObjUtils.toInteger(dbObject.get("status"), 0));
            programReplay.setCreateTime(ObjUtils.toLong(dbObject.get("createTime")));
            programReplay.setUpdateTime(ObjUtils.toLong(dbObject.get("updateTime")));
            programReplay.setRetryNum(ObjUtils.toInteger(dbObject.get("retryNum"),0));
        }else{
            return null;
        }
        return programReplay;
    }
}
