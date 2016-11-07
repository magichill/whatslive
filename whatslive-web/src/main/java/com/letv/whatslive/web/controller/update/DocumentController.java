package com.letv.whatslive.web.controller.update;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Document;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.web.constant.ModelConstants;
import com.letv.whatslive.web.controller.PubController;
import com.letv.whatslive.web.service.update.DocumentService;
import com.letv.whatslive.web.util.WebUtils;
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
import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/7/27.
 */
@Controller
@RequestMapping("/document")
public class DocumentController extends PubController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    DocumentService documentService;

    /**
     * 显示文案列表页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/update/document");
        return modelAndView;
    }

    /**
     * 查询文案列表
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
        String search_version = ObjectUtils.toString(param.get("search_version"));
        String search_status = ObjectUtils.toString(param.get("search_status"));

        Map params = Maps.newHashMap();
        if (StringUtils.isNotBlank(search_version)) {
            params.put("version", search_version);
        }
        if (StringUtils.isNotBlank(search_status)) {
            params.put("status", search_status);
        }
        Long documentNum = documentService.countDocumentByParams(params);
        List<Document> documentNumList = documentService.getDocumentListByParams(params, start, limit);
        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
        result.put("iTotalRecords", documentNum); //total
        result.put("iTotalDisplayRecords", documentNum); //totalAfterFilter
        result.put("aaData", documentNumList.toArray());
        setResContent2Json(response);
        return result;
    }
    /**
     * 跳转到修改文案编辑页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/documentNew")
    public ModelAndView userNew(WebRequest webRequest) {
        ModelAndView modelAndView = new ModelAndView();
        Long did = ObjectUtils.toLong(webRequest.getParameter("did"));
        Document document = new Document();
        if (did != null) {
            document = documentService.getDocumentById(did);
            document.setComment(document.getComment().replaceAll("<br/>","\n"));
            modelAndView.setViewName("update/document_edit");
        } else {
            modelAndView.setViewName("update/document_edit");
        }
        modelAndView.addObject("document", document);
        return modelAndView;
    }

    @RequestMapping("/documentSave")
    @ResponseBody
    public Map<String, Object> documentSave(Document document, HttpServletRequest request, HttpServletResponse response) {
        ResultBean resultBean = ResultBean.getTrueInstance();


        document.setComment(document.getComment().replaceAll("\n", "<br/>"));
        if (document.getId() != null) {  // 修改
            documentService.updateDocument(document);
        }else {
            List<Document> otherSameVerisonDocs = documentService.getDocumentsByVersion(document.getVersion());
            if(null != otherSameVerisonDocs && otherSameVerisonDocs.size() >0 ){
                resultBean.setFalseAndMsg("此版本号的文案已经存在，不能重复录入！");
            }else{
                document.setCreateUser(WebUtils.getLoginUserNameNotNull(request));
                document.setCreateTime(System.currentTimeMillis());
                resultBean = documentService.insertDocument(document);
            }

        }
        // 返回结果
        setResContent2Json(response);
        if(!resultBean.isFlag()){
            return getFailMap(resultBean.getMsg());
        }else{
            return getSuccessMap();
        }

    }
    /**
     * 删除文案
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
        documentService.deleteDocument(ObjectUtils.toLong(id));
        return getSuccessMap();
    }

    /**
     * 修改文案状态
     *
     * @param id
     * @param op
     * @return
     */
    @RequestMapping("/effect")
    @ResponseBody
    public Map<String, Object> effect(String id, String op) {
        if (id == null || op== null) {
            logger.error("非法参数，id或者op为空");
            return getFailMap("参数不能为空!");
        }
        //如果将文案置为启用状态，则其他已经启用的文案需要修改为停用状态
        if(ModelConstants.EFFECTIVE_YES == ObjectUtils.toInteger(op)){
            documentService.blockAllDocument();
        }
        Document document = documentService.getDocumentById(ObjectUtils.toLong(id));
        document.setStatus(ObjectUtils.toInteger(op));
        documentService.updateDocument(document);
        return getSuccessMap();
    }
}
