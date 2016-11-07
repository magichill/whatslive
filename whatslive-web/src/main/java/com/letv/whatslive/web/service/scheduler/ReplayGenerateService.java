package com.letv.whatslive.web.service.scheduler;

import com.google.common.collect.Maps;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.ProgramReplay;
import com.letv.whatslive.mongo.dao.ProgramDAO;
import com.letv.whatslive.mongo.dao.ProgramReplayDAO;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.web.service.actionLog.ActionLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 生成回放定时任务
 * Created by wangjian7 on 2015/10/22.
 */
@Component
public class ReplayGenerateService {

    private static final Logger logger = LoggerFactory.getLogger(ReplayGenerateService.class);

    private static final String openCreateReplay = "openCreateReplay";

    @Autowired
    ProgramDAO programDAO;

    @Autowired
    ProgramReplayDAO programReplayDAO;

    @Autowired
    JedisDAO jedisDAO;

    @Autowired
    ActionLogService actionLogService;

    /**
     * 定时生成回放历史记录
     */
    public void scanAllRecord(){
        String queryFlag = jedisDAO.getJedisReadTemplate().get(openCreateReplay);
        if(queryFlag!=null && queryFlag.toLowerCase().equals("n")){
            logger.info("create replay is off!");
            return;
        }
        String uuid = String.valueOf(UUID.randomUUID());
        logger.info("create replay begin to scan last 0.5 day program to generate replay! "+uuid);
        Map query = Maps.newHashMap();
        query.put("startTime_start",System.currentTimeMillis()-12*60*60*1000);
        query.put("startTime_end",System.currentTimeMillis());
        query.put("pType", 3);
        query.put("status", "1,-1");
        Map order = Maps.newHashMap();
        order.put("createTime", 1);
        List<Long> programIds = programDAO.getProgramIdsByParams(query, order);
        if(null==programIds || programIds.size()==0) {
            logger.error("create replay no program need generate replay! " + uuid);
        }else if(programIds.size()>1000000){
            logger.error("create replay error too many programNum! "+programIds.size()+" "+uuid);
        }else {
            logger.info("create replay some program begin to generate replay! program num:" + programIds.size() + " " + uuid);
            ExecutorService executor = Executors.newFixedThreadPool(5);
            for(Long pid : programIds){
                ProgramReplay programReplay = programReplayDAO.queryReplayByPid(pid);
                if(null==programReplay || (programReplay.getStatus()==4&&programReplay.getRetryNum()<3)){
                    Program program = programDAO.getProgramById(pid);
                    ReplayGenerateThread replayGenerateThread = new ReplayGenerateThread(pid, program.getStartTime());
                    executor.execute(replayGenerateThread);
                }else{
                    logger.info("create replay program don't need to create program replay! programId:"+pid+" "+uuid);
                }
            }
        }

    }

    class ReplayGenerateThread implements Runnable {
        private Long pid;
        private Long startTime;

        ReplayGenerateThread(Long pid, Long startTime) {
            this.pid = pid;
            this.startTime = startTime;
        }

        @Override
        public void run() {
            actionLogService.createProgramActionLogFile(pid, startTime);
        }
    }

}
