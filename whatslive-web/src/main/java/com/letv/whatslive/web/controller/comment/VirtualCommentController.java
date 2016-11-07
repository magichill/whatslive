package com.letv.whatslive.web.controller.comment;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.VirtualComment;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.constant.WebConstants;
import com.letv.whatslive.web.controller.PubController;
import com.letv.whatslive.web.service.comment.VirtualCommentService;
import com.letv.whatslive.web.util.WebUtils;
import org.apache.commons.io.FileUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/7/28.
 */
@Controller
@RequestMapping("/virtualComment")
public class VirtualCommentController extends PubController {
    private static final Logger logger = LoggerFactory.getLogger(VirtualCommentController.class);

    @Autowired
    VirtualCommentService commentService;

    /**
     * 显示虚拟评论页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/comment/virtualComment");
        return modelAndView;
    }

    /**
     * 查询虚拟评论列表
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
        String search_content = ObjectUtils.toString(param.get("search_content"));
        String search_status = ObjectUtils.toString(param.get("search_status"));

        Map params = Maps.newHashMap();
        if (StringUtils.isNotBlank(search_content)) {
            params.put("content", search_content);
        }
        if (StringUtils.isNotBlank(search_status)) {
            params.put("status", search_status);
        }
        Long commentNum = commentService.countVirtualCommentByParams(params);
        List<VirtualComment> commentNumList = commentService.getVirtualCommentListByParams(params, start, limit);
        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
        result.put("iTotalRecords", commentNum); //total
        result.put("iTotalDisplayRecords", commentNum); //totalAfterFilter
        result.put("aaData", commentNumList.toArray());
        setResContent2Json(response);
        return result;
    }
    /**
     * 跳转到修改虚拟评论页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/commentNew")
    public ModelAndView userNew(WebRequest webRequest) {
        ModelAndView modelAndView = new ModelAndView();
        Long kid = ObjectUtils.toLong(webRequest.getParameter("kid"));
        VirtualComment comment = new VirtualComment();
        if (kid != null) {
            comment = commentService.getVirtualCommentById(kid);
            modelAndView.setViewName("comment/virtualComment_edit");
        } else {
            modelAndView.setViewName("comment/virtualComment_edit");
        }
        modelAndView.addObject("virtualComment", comment);
        return modelAndView;
    }

    @RequestMapping("/commentSave")
    @ResponseBody
    public Map<String, Object> userSave(VirtualComment comment, HttpServletRequest request, HttpServletResponse response) {
        ResultBean resultBean = ResultBean.getTrueInstance();

        if (comment.getId() != null) {  // 修改

            commentService.updateVirtualComment(comment);
        }else {
            comment.setCreateUser(WebUtils.getLoginUserNameNotNull(request));
            comment.setCreateTime(System.currentTimeMillis());
            resultBean = commentService.insertVirtualComment(comment);
        }
        // 返回结果
        setResContent2Json(response);
        if(!resultBean.isFlag()){
            return getFailMap(resultBean.getMsg());
        }else{
            return getSuccessMap();
        }

    }

    @RequestMapping("/saveBatchComment")
    @ResponseBody
    public Map<String, Object> saveBatchComment(){
        File keywordFileTmp = new File(WebConstants.UPLOAD_PATH_ROOT+"comment.txt");
        if(keywordFileTmp.exists()){
            try {
                List<String> commentList = FileUtils.readLines(keywordFileTmp, "UTF-8");
                for(int i = 0;i<commentList.size();i++){
                    VirtualComment comment = new VirtualComment();
                    comment.setContent(commentList.get(i));
                    comment.setCreateUser("system");
                    comment.setCreateTime(System.currentTimeMillis());
                    commentService.insertVirtualComment(comment);
                }
                FileUtils.deleteQuietly(keywordFileTmp);
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        return getSuccessMap();
    }
    /**
     * 删除虚拟评论
     *
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String, Object> delete(String id) {
        if (id == null) {
            logger.error("非法参数，id为空");
            return getFailMap("参数不能为空!");
        }
        commentService.deleteVirtualComment(ObjectUtils.toLong(id));
        return getSuccessMap();
    }

    /**
     * 修改虚拟评论状态
     *
     * @param id
     * @param op
     * @return
     */
    @RequestMapping("/effect")
    @ResponseBody
    public Map<String, Object> effect(String id, String op) {
        if (id == null) {
            logger.error("非法参数，id为空");
            return getFailMap("参数不能为空!");
        }
        VirtualComment comment = commentService.getVirtualCommentById(ObjectUtils.toLong(id));
        comment.setStatus(ObjectUtils.toInteger(op));
        commentService.updateVirtualComment(comment);
        return getSuccessMap();
    }
}
