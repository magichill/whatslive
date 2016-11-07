package com.letv.whatslive.web.controller.system;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.web.service.system.SysLogService;
import com.letv.whatslive.model.mysql.system.SysLog;
import com.letv.whatslive.web.controller.PubController;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by wangruifeng on 14-5-22.
 */
@Controller
@RequestMapping("/system/log")
public class LogController extends PubController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);
    @Autowired
    private SysLogService sysLogService;

    /**
     * 显示日志列表页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("/system/log");
        return modelAndView;
    }

    /**
     * 查询日志列表
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

        String opUserName = ObjectUtils.toString(param.get("opUserName"));
        String clientId = ObjectUtils.toString(param.get("clientId"));
        String opTime = ObjectUtils.toString(param.get("opTime"));
        String opType = ObjectUtils.toString(param.get("opType"));
        Map params = Maps.newHashMap();
        params.put("sEchp", sEcho);

        if (StringUtils.isNotBlank(opUserName)) {
            params.put("opUserName", opUserName);
        }
        if (StringUtils.isNotBlank(clientId)) {
            params.put("clientId", clientId);
        }
        if (StringUtils.isNotBlank(opTime)) {
            params.put("opTime", opTime);
        }
        if (StringUtils.isNotBlank(opType)) {
            params.put("opType", opType);
        }
        Integer logNum = sysLogService.countSysLog(params);
        List<SysLog> logList = sysLogService.getSysLogList(params, start, limit);
        for (SysLog sysLog : logList) {
            sysLog.setOpTimes(DateUtils.getFormatDate(sysLog.getOpTime(), "yyyy-MM-dd HH:mm:ss"));
        }

        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
        result.put("iTotalRecords", logNum); //total
        result.put("iTotalDisplayRecords", logNum); //totalAfterFilter
        result.put("aaData", logList.toArray());
        setResContent2Json(response);
        return result;
    }

    /**
     * @param webRequest
     * @param response
     * @return
     */
    @RequestMapping("/opContent")
    @ResponseBody
    public ModelAndView opContent(WebRequest webRequest, HttpServletResponse response) {

        Integer id = ObjectUtils.toInteger(webRequest.getParameter("id"));

        SysLog sysLog = sysLogService.getSysLogById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("sysLog", sysLog);
        modelAndView.setViewName("/system/logDetail");
        return modelAndView;

    }


}
