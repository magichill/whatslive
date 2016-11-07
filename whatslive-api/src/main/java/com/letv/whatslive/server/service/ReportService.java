package com.letv.whatslive.server.service;

import com.letv.whatslive.mongo.dao.ReportDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by gaoshan on 15-8-7.
 */
@Service
public class ReportService  {

    @Resource
    private ReportDAO reportDAO;

    public void addReport(Long userId,Long pId){
        DBObject report = new BasicDBObject();
        report.put("postId",userId);
        report.put("programId",pId);
        DBObject obj = reportDAO.find(report);
        if(obj == null){
            report.put("type",1);
            report.put("content","");
            report.put("status",1);
            report.put("createTime",System.currentTimeMillis());
            report.put("updateTime",System.currentTimeMillis());
            reportDAO.insert(report);
        }
    }

    /**
     * 查询用户是否举报过该直播，1为已举报，0为未举报
     * @param userId
     * @param pId
     * @return
     */
    public Integer checkReport(Long userId,Long pId){
        DBObject report = new BasicDBObject();
        report.put("postId",userId);
        report.put("programId",pId);
        DBObject obj = reportDAO.find(report);
        if(obj == null){
            return 0;
        }else{
            return 1;
        }
    }
}
