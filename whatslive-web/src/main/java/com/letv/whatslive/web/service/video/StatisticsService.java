package com.letv.whatslive.web.service.video;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.DateUtils;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Statistics;
import com.letv.whatslive.mongo.dao.ProgramDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/10/15.
 */
@Component
public class StatisticsService {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);

    @Autowired
    private ProgramDAO programDAO;

    /**
     * 根据直播查询参数获取直播列表
     * @param params 查询参数
     * @return
     */
    public Statistics getStatisticsByParams(Map params) {
        if(ObjectUtils.toLong(params.get("startTime_start"))>System.currentTimeMillis())
            return null;
        long startTime_start = ObjectUtils.toLong(params.get("startTime_start"));
        long startTime_end = ObjectUtils.toLong(params.get("startTime_end"));
        Statistics statistics = new Statistics();
        statistics.setStartTime(startTime_start);
        statistics.setEndTime(startTime_end);
        Map query = Maps.newHashMap();
        query.put("startTime_start", startTime_start);
        query.put("startTime_end", startTime_end);
        query.put("pType", "1,3");
        long liveCount = programDAO.countProgramByParams(query);
        query.put("pType", "3");
        query.put("status", "1");
        long transedCount_normal = programDAO.countProgramByParams(query);
        query.put("status", "0");
        long offlineCount = programDAO.countProgramByParams(query);
        query.put("status", "-1");
        long notTransCount = programDAO.countProgramByParams(query);
        query.put("status", "-2");
        long liveTooShortCount = programDAO.countProgramByParams(query);
        statistics.setLiveCount(liveCount);
        statistics.setReplayTransedCount(transedCount_normal);
        statistics.setReplayNotTransCount(notTransCount);
        statistics.setOfflineCount(offlineCount);
        statistics.setLiveTooShortCount(liveTooShortCount);
        statistics.setDateStr(DateUtils.long2YMD(statistics.getStartTime()));
        return statistics;
    }
}
