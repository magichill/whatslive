package com.letv.whatslive.web.controller.keyword;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Keyword;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.constant.WebConstants;
import com.letv.whatslive.web.controller.PubController;
import com.letv.whatslive.web.service.keyword.KeywordService;
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
import java.util.List;
import java.util.Map;

/**
 * 关键词Controller
 * Created by wangjian7 on 2015/7/22.
 */
@Controller
@RequestMapping("/keyword")
public class KeywordController extends PubController {
    private static final Logger logger = LoggerFactory.getLogger(KeywordController.class);

    @Autowired
    KeywordService keywordService;

    /**
     * 显示关键词屏蔽页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/keyword/keyword");
        return modelAndView;
    }

    /**
     * 查询关键词列表
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
        String search_key = ObjectUtils.toString(param.get("search_key"));
        String search_status = ObjectUtils.toString(param.get("search_status"));
        Map params = Maps.newHashMap();
        if (StringUtils.isNotBlank(search_key)) {
            params.put("key", search_key);
        }
        if (StringUtils.isNotBlank(search_status)) {
            params.put("status", search_status);
        }
        Long keywordNum = keywordService.countKeywordByParams(params);
        List<Keyword> keywordNumList = keywordService.getKeywordListByParams(params, start, limit);
        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
        result.put("iTotalRecords", keywordNum); //total
        result.put("iTotalDisplayRecords", keywordNum); //totalAfterFilter
        result.put("aaData", keywordNumList.toArray());
        setResContent2Json(response);
        return result;
    }
    /**
     * 跳转到修改关键词页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/keywordNew")
    public ModelAndView keywordNew(WebRequest webRequest) {
        ModelAndView modelAndView = new ModelAndView();
        Long kid = ObjectUtils.toLong(webRequest.getParameter("kid"));
        Keyword keyword = new Keyword();
        if (kid != null) {
            keyword = keywordService.getKeywordById(kid);
            modelAndView.setViewName("keyword/keyword_edit");
        } else {
            modelAndView.setViewName("keyword/keyword_edit");
        }
        modelAndView.addObject("keyword", keyword);
        return modelAndView;
    }

    @RequestMapping("/keywordSave")
    @ResponseBody
    public Map<String, Object> keywordSave(Keyword keyword, HttpServletRequest request, HttpServletResponse response) {
        ResultBean resultBean = ResultBean.getTrueInstance();

        if (keyword.getId() != null) {  // 修改

            keywordService.updateKeyword(keyword);
        }else {
            keyword.setCreateUser(WebUtils.getLoginUserNameNotNull(request));
            keyword.setCreateTime(System.currentTimeMillis());
            resultBean = keywordService.insertKeyword(keyword);
        }
        //需要所有有效的keyword查询出来保存到文件中
        saveKeyToFile();
        // 返回结果
        setResContent2Json(response);
        if(!resultBean.isFlag()){
            return getFailMap(resultBean.getMsg());
        }else{
            return getSuccessMap();
        }

    }
    /**
     * 删除关键词
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
        keywordService.deleteKeyword(ObjectUtils.toLong(id));
        //需要所有有效的keyword查询出来保存到文件中
        saveKeyToFile();
        return getSuccessMap();
    }

    /**
     * 修改关键词状态
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
        Keyword keyword = keywordService.getKeywordById(ObjectUtils.toLong(id));
        keyword.setStatus(ObjectUtils.toInteger(op));
        keywordService.updateKeyword(keyword);
        //需要所有有效的keyword查询出来保存到文件中
        saveKeyToFile();
        return getSuccessMap();
    }

    /**
     * 将所有有效的keyword查询出来保存到文件中
     * @return
     */
    private String saveKeyToFile(){
        String message = null;
        List<String> keywordList = keywordService.queryAllKeyword();
        String fileNameTmp = WebConstants.UPLOAD_PATH_ROOT + WebConstants.UPLOAD_KEYWORD_TMP;
        String fileName = WebConstants.UPLOAD_PATH_ROOT + WebConstants.UPLOAD_KEYWORD;
        try {
            File keywordFileTmp = new File(fileNameTmp);
            FileUtils.writeLines(keywordFileTmp, keywordList);
            File keywordFile = new File(fileName);
            FileUtils.copyFile(keywordFileTmp, keywordFile);
        } catch (IOException e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        return message;
    }

}
