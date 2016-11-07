package com.letv.whatslive.server.controller;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.server.service.ReportService;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by gaoshan on 15-8-7.
 */

@Service
public class ReportController extends BaseController {

    @Resource
    private ReportService reportService;

    /**
     * 用户举报接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody addReport(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        Long programId = ObjectUtils.toLong(params.get("programId"));
        Map<String,Object> validateParams = Maps.newHashMap();
        validateParams.put("userId",userId);
        validateParams.put("programId",programId);
        String checkValue = validateParams(validateParams);
        if(checkValue != null){
            LogUtils.logError(checkValue);
            return getErrorResponse(sid, Constant.PARAMS_ERROR_CODE);
        }
        try {
            reportService.addReport(userId, programId);
            return getResponseBody(sid, "ok");
        }catch (Exception e){
            LogUtils.logError("fail to report,[exception] ",e);
            return getErrorResponse(sid,Constant.REPORT_ERROR_CODE);
        }
    }

    /**
     * 用户举报状态
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody checkReport(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        Long programId = ObjectUtils.toLong(params.get("programId"));
        Integer isReport = reportService.checkReport(userId,programId);
        Map<String,Object> result = Maps.newHashMap();
        result.put("isReport",isReport);
        return getResponseBody(sid,result);
    }
}
