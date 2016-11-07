package com.letv.whatslive.server.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.MessageStatus;
import com.letv.whatslive.model.convert.MessageConvert;
import com.letv.whatslive.model.dto.MessageDTO;
import com.letv.whatslive.server.service.MessageService;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-8-6.
 */
@Service
public class MessageController extends BaseController {

    @Resource
    private MessageService messageService;

    /**
     * 个人中心消息数量接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody messageCount(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        if(userId == null){
            return getErrorResponse(sid, Constant.PARAMS_ERROR_CODE);
        }
        try {
            Integer count = messageService.newMessageCount(userId);
            Map<String, Object> result = Maps.newHashMap();
            result.put("messageCount", count);
            return getResponseBody(sid, result);
        }catch (Exception e){
            LogUtils.logError("fail to get message count,[exception] ",e);
            return getErrorResponse(sid,Constant.MESSAGE_ERROR_CODE);
        }
    }

    public ResponseBody messageList(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
        List<MessageDTO> result = Lists.newArrayList();
        try {
            List<MessageStatus> list = messageService.messageList(userId, start, limit);
            for(MessageStatus ms : list){
                MessageDTO dto = new MessageDTO(ms);
                result.add(dto);
            }
        }catch (Exception e){
            LogUtils.logError("fail to get message list,[exception] ",e);
            return getErrorResponse(sid,Constant.MESSAGE_ERROR_CODE);
        }

        return getResponseBody(sid,result);
    }

}
