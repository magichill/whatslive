package com.letv.whatslive.web.controller.video;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Statistics;
import com.letv.whatslive.web.controller.PubController;
import com.letv.whatslive.web.service.video.StatisticsService;
import com.letv.whatslive.web.util.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/10/15.
 */
@Controller
@RequestMapping("/statistics")
public class StatisticsController extends PubController {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 显示直播统计列表页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/video/statistics");
        return modelAndView;
    }

    /**
     * 查询直播统计列表
     *
     * @param valueMap
     * @param response
     * @return
     */
    @RequestMapping("/list.json")
    @ResponseBody
    public Map<String, Object> list(@RequestBody MultiValueMap valueMap, HttpServletResponse response) {
        Map<String, Object> param = valueMap.toSingleValueMap();
        Integer start = ObjectUtils.toInteger(param.get("iDisplayStart"));
        Integer limit = ObjectUtils.toInteger(param.get("iDisplayLength"));
        String sEcho = ObjectUtils.toString(param.get("sEcho"));
        //查询过滤条件
        String search_startTime_start = ObjectUtils.toString(param.get("search_startTime_start"));
        String search_startTime_end = ObjectUtils.toString(param.get("search_startTime_end"));
        Long statisticsNum = 0L;
        List<Statistics> statisticsList = Lists.newArrayList();
        if (StringUtils.isNotBlank(search_startTime_start) || StringUtils.isNotBlank(search_startTime_end)) {
            if(StringUtils.isBlank(search_startTime_start))
                search_startTime_start = search_startTime_end;
            else if(StringUtils.isBlank(search_startTime_end))
                search_startTime_end = search_startTime_start;
            try {
                SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");
                Date startDate=sdf.parse(search_startTime_start);
                Date endDate=sdf.parse(search_startTime_end);
                //结束日期+1
                Calendar cld= Calendar.getInstance();
                cld.setTime(endDate);
                cld.add(Calendar.DATE, 1);
                endDate = cld.getTime();
                int dateDiffDay = new Long((endDate.getTime() - startDate.getTime())/(24*60*60*1000)).intValue();
                if(dateDiffDay>0){
                    statisticsNum = (long)dateDiffDay;
                    limit = (start + limit) > dateDiffDay?dateDiffDay-start:limit;
                    Map query = Maps.newHashMap();
                    for(int i=start; i<start+limit; i++){
                        query.put("startTime_start", startDate.getTime()+Long.valueOf(i)*24*60*60*1000);
                        query.put("startTime_end", startDate.getTime()+Long.valueOf(i+1)*24*60*60*1000);
                        Statistics statistics = statisticsService.getStatisticsByParams(query);
                        if(null != statistics)
                            statisticsList.add(statistics);
                    }
                }
            } catch (ParseException e) {
                logger.error("date convert error!", e);
            }
        }

        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
        result.put("iTotalRecords", statisticsNum); //total
        result.put("iTotalDisplayRecords", statisticsNum); //totalAfterFilter
        result.put("aaData", statisticsList.toArray());
        setResContent2Json(response);
        return result;
    }

}
