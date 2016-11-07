package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.utils.Constant;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaoshan on 15-7-13.
 */
public class ProgramConvert {


    public static DBObject castProgramToDBObject(Program program) {
        DBObject dbo = new BasicDBObject();
        if (program.getId() != null) {
            dbo.put("_id", program.getId());
        }
        dbo.put("pName", ObjUtils.ifNull(program.getPName(), ""));
        dbo.put("pDesc",ObjUtils.ifNull(program.getPDesc(),""));
        dbo.put("pType", ObjUtils.toInteger(program.getPType()));
        dbo.put("activityId",ObjUtils.toString(program.getActivityId(),""));
        dbo.put("videoId",ObjUtils.toString(program.getVideoId(),""));
        dbo.put("userId", ObjUtils.toLong(program.getUserId()));
        dbo.put("status", ObjUtils.ifNull(program.getStatus(), 1));
        dbo.put("picture", ObjUtils.ifNull(program.getPicture(), Constant.DEFAULT_PICTURE));
        dbo.put("likeNum", ObjUtils.toLong(program.getLikeNum(),0l));
        dbo.put("watchNum", ObjUtils.toLong(program.getWatchNum(),0l));
        dbo.put("commentNum", ObjUtils.toLong(program.getCommentNum(),0l));
        dbo.put("realLikeNum", program.getRealLikeNum());
        dbo.put("realWatchNum", program.getRealLikeNum());
        dbo.put("realCommentNum", program.getRealLikeNum());
        dbo.put("startTime", ObjUtils.toLong(program.getStartTime(), System.currentTimeMillis()));
        dbo.put("endTime", ObjUtils.toLong(program.getEndTime(), System.currentTimeMillis()));
        dbo.put("createTime", ObjUtils.toLong(program.getCreateTime(), System.currentTimeMillis()));
        dbo.put("updateTime", ObjUtils.toLong(program.getUpdateTime(), System.currentTimeMillis()));
        dbo.put("priority", ObjUtils.toLong(program.getPriority(), 0L));
        dbo.put("isShow",ObjUtils.toInteger(program.getIsShow(),1));
        dbo.put("from",ObjUtils.toInteger(program.getFrom(),1));
        dbo.put("location",ObjUtils.toString(program.getLocation(),""));
        dbo.put("vuid",ObjUtils.toString(program.getVuid(),""));
        dbo.put("province",ObjUtils.toString(program.getProvince()));
        dbo.put("city",ObjUtils.toString(program.getCity()));
        dbo.put("primaryPName",ObjUtils.toString(program.getPrimaryPName(),program.getPName()));
        if(program.getLiveId()!=null && program.getLiveId().length()>0){
            dbo.put("liveId", program.getLiveId());
        }
        if(program.getIsCarousel() != null){
            dbo.put("isCarousel", program.getIsCarousel());
        }
        if(program.getIsContinue() != null){
            dbo.put("isContinue",program.getIsContinue());
        }
        if(program.getOrderNum() != null){
            dbo.put("orderNum", program.getOrderNum());
        }
        if(program.getTagList()!=null && program.getTagList().size()>0){
            dbo.put("tagList",program.getTagList());
        }else{
            dbo.put("tagList",new ArrayList<Long>());
        }
        return dbo;
    }

    public static Program castDBObjectToProgram(DBObject dbObject){
        Program program = new Program();
        if(dbObject != null) {
            program.setId(ObjUtils.toLong(dbObject.get("_id")));
            program.setPName(ObjUtils.toString(dbObject.get("pName")));
            program.setPDesc(ObjUtils.toString(dbObject.get("pDesc")));
            program.setPType(ObjUtils.toInteger(dbObject.get("pType")));
            program.setUserId(ObjUtils.toLong(dbObject.get("userId")));
            program.setUserRef((DBRef) dbObject.get("user"));
            program.setStatus(ObjUtils.toInteger(dbObject.get("status")));
            program.setPicture(ObjUtils.toString(dbObject.get("picture"),Constant.DEFAULT_PICTURE));
            program.setCommentNum(ObjUtils.toLong(dbObject.get("commentNum"), 0l));
            program.setLikeNum(ObjUtils.toLong(dbObject.get("likeNum"), 0l));
            program.setWatchNum(ObjUtils.toLong(dbObject.get("watchNum"), 0l));

            program.setRealCommentNum(ObjUtils.toLong(dbObject.get("realCommentNum"), 0l));
            program.setRealLikeNum(ObjUtils.toLong(dbObject.get("realLikeNum"), 0l));
            program.setRealWatchNum(ObjUtils.toLong(dbObject.get("realWatchNum"), 0l));

//            program.setReportNum(ObjUtils.toLong(dbObject.get("reportNum"), 0l));
            program.setStartTime(ObjUtils.toLong(dbObject.get("startTime")));
            program.setEndTime(ObjUtils.toLong(dbObject.get("endTime")));
            program.setCreateTime(ObjUtils.toLong(dbObject.get("createTime")));
            program.setUpdateTime(ObjUtils.toLong(dbObject.get("updateTime")));
            program.setPriority(ObjUtils.toLong(dbObject.get("priority"), 0l));
            program.setIsShow(ObjUtils.toInteger(dbObject.get("isShow"), 1));
            program.setOrderNum(ObjUtils.toLong(dbObject.get("orderNum"), 0l));
            program.setFrom(ObjUtils.toInteger(dbObject.get("from"), 1));
            program.setLiveId(ObjUtils.toString(dbObject.get("liveId")));
            program.setIsCarousel(ObjUtils.toInteger(dbObject.get("isCarousel")));
            program.setActivityId(ObjUtils.toString(dbObject.get("activityId")));
            program.setVideoId(ObjUtils.toString(dbObject.get("videoId")));
            program.setVuid(ObjUtils.toString(dbObject.get("vuid"),""));
            program.setLocation(ObjUtils.toString(dbObject.get("location"),""));
            program.setPrimaryPName(ObjUtils.toString(dbObject.get("primaryPName"),program.getPName()));
            program.setIsContinue(ObjUtils.toInteger(dbObject.get("isContinue")));
            program.setProvince(ObjUtils.toString(dbObject.get("province")));
            program.setCity(ObjUtils.toString(dbObject.get("city")));
            if(dbObject.get("tagList")!=null){
                BasicDBList dbList = (BasicDBList)(dbObject.get("tagList"));
                if(dbList.size()>0){
                    List<Long> tagList = new ArrayList<Long>();
                    for(int i=0; i<dbList.size(); i++){
                        tagList.add(ObjUtils.toLong(dbList.get(i)));
                    }
                    program.setTagList(tagList);
                }
            }
        }else{
            return null;
        }
        return program;
    }
}
