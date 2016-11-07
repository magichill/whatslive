package com.letv.whatslive.inner.push.schedule;

import com.letv.whatslive.inner.push.service.PushService;
import com.letv.whatslive.inner.utils.String.StringUtils;
import com.letv.whatslive.model.redis.push.PushEvent;
import com.letv.whatslive.mongo.dao.*;
import com.letv.whatslive.redis.JedisDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 预约推送定时任务
 * Created by wangjian7 on 2015/8/17.
 */
@Component
public class OrderPushService {

    private static final Logger logger = LoggerFactory.getLogger(OrderPushService.class);

    private static final String openOrderPush = "openOrderPush";

    private static final String orderPushStartTime = "orderPushStartTime";

    private static final String orderPushEndTime = "orderPushEndTime";

    @Autowired
    ProgramDAO programDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    PushLogDAO pushLogDAO;

    @Autowired
    JedisDAO jedisDAO;

    @Autowired
    PushService pushService;


    /**
     * 根据pid推送消息
     * @param pid
     */
    public void pushOrderByProgramId(Long pid){
        String uuid = UUID.randomUUID()+"_"+0;
//        PushThread pushThread = new PushThread(uuid.toString(),pid);
//        pushThread.run();
        PushEvent p = new PushEvent();
        p.setAction(1);
        p.setProgramId(3292L);
        jedisDAO.getJedisWriteTemplate().publish("pushMessage", p.toString());
    }

    /**
     * 定时扫描预约信息推送消息
     */
    public void scanAllOrderTimer(){
        String queryFlag = jedisDAO.getJedisReadTemplate().get(openOrderPush);
        if(queryFlag!=null && queryFlag.toLowerCase().equals("n")){
            logger.info("Order push is off!");
            return;
        }
        String uuid = String.valueOf(UUID.randomUUID());
        logger.info("Order push begin to scan all order for push "+uuid);
        Map<String, Object> orders = new HashMap<String, Object>();
        orders.put("startTime",1);
        String startTime = jedisDAO.getJedisReadTemplate().get(orderPushStartTime);
        String endTime = jedisDAO.getJedisReadTemplate().get(orderPushEndTime);
        Long currentTime = System.currentTimeMillis();
        Long queryStartTime = StringUtils.isBlank(startTime)?currentTime:Long.valueOf(startTime)*1000+currentTime;
        Long queryEndTime = StringUtils.isBlank(endTime)?currentTime+10*60*1000:Long.valueOf(endTime)*1000+currentTime;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pType", 2);
        params.put("status", 1);
        params.put("startTime_start", queryStartTime);
        params.put("startTime_end", queryEndTime);
        Long programNum = programDAO.countProgramByParams(params);

        if(programNum>0){
            List<Long> programIds= programDAO.getProgramIdsByParams(params, orders);
            params.clear();
            params.put("startTime_start", queryStartTime);
            params.put("startTime_end", queryEndTime);
            params.put("statusIn","0,1,4,5");
            params.put("bussiType","1");
            List<Long> pushedProgramIds = pushLogDAO.getProgramIdsByParams(params);
            programIds.removeAll(pushedProgramIds);

            //TODO 暂时不处理推送失败的数据
//                params.remove("statusNotEqual");
//                params.put("status",2);
//                List<Long> failedPushedProgramIds = pushLogDAO.getPushLogIdsByParams(params);
//                programIds.addAll(failedPushedProgramIds);
            if(programIds.size()>1000000){
                logger.error("Order push error too many programNum! "+programIds.size()+" "+uuid);
            }else{
                logger.info("Order push Count "+programIds.size()+" programs to push "+uuid);
            }
            ExecutorService executor = Executors.newFixedThreadPool(5);
            for(Long pid : programIds){
                PushThread pushThread = new PushThread(uuid,pid);
                executor.execute(pushThread);
            }
        }else{
            logger.info("Order push there is no program to push! "+uuid);
        }
    }

    class PushThread implements Runnable {
        private final Logger logger = LoggerFactory.getLogger(PushThread.class);
        private Long pid;
        private String uuid;

        PushThread(String uuid, Long pid) {
            this.uuid = uuid + "_" + pid;
            this.pid = pid;
        }

        @Override
        public void run() {
            pushService.pushOrderMessage(uuid, pid);
        }
    }

}
