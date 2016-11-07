package com.letv.whatslive.server.controller;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Comment;
import com.letv.whatslive.server.service.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by gaoshan on 15-7-3.
 */
@Service
public class CommentController extends BaseController {

    @Resource
    private CommentService commentService;
    /**
     * 发表评论接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody submitComment(Map<String, Object> params, String sid, RequestHeader header) {
        long userId = ObjectUtils.toLong(header.getUserId());
        String content = ObjectUtils.toString(params.get("content"));
        long pId = ObjectUtils.toLong(params.get("vid"));
        Map<String,Object> paramMap = Maps.newHashMap();
        paramMap.put("userId",userId);
        paramMap.put("content",content);
        String checkParams = validateParams(paramMap);
        if(!(checkParams.indexOf("ok")>-1)){
            return getErrorResponse(sid,checkParams);
        }
        if(validateUser(userId) == null){
            return getErrorResponse(sid, "this user is not exist!");
        }
        Comment comment = new Comment();
        comment.setPostId(userId);
        comment.setPId(pId);
        comment.setStatus(1);
        comment.setContent(content);
        commentService.insertComment(comment);
        return getResponseBody(sid,"success");
    }
}
