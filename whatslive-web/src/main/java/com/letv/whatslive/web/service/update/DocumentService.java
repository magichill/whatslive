package com.letv.whatslive.web.service.update;

import com.letv.whatslive.model.Document;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.mongo.dao.DocumentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/7/27.
 */
@Component
public class DocumentService {
    @Autowired
    private DocumentDAO documentDAO;

    /**
     * 根据条件查询所有文案
     *
     * @param params 模糊查询的查询条件
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @return 满足条件的对象的集合
     */
    public List<Document> getDocumentListByParams(Map params, Integer start, Integer limit) {
        return documentDAO.getDocumentListByParams(params, start, limit);
    }

    /**
     * 根据指定条件查询文案的数量
     *
     * @param params 模糊查询的查询条件
     * @return 满足条件的结果记录数
     */
    public Long countDocumentByParams(Map params) {
        return documentDAO.countDocumentByParams(params);
    }


    public Document getDocumentById(Long did) {
        return documentDAO.getDocumentById(did);
    }

    public List<Document> getDocumentsByVersion(String version) {
        return documentDAO.getDocumentsByName(version);
    }

    public ResultBean insertDocument(Document document){
        ResultBean res = ResultBean.getTrueInstance();
        String message = documentDAO.saveDocument(document);
        if(null!=message){
            res.setFalseAndMsg(message);
        }
        return res;
    }

    public void blockAllDocument(){
        documentDAO.blockAllDocument();
    }

    public void updateDocument(Document document) {
        documentDAO.updateDocument(document);
    }

    public void deleteDocument(Long did){
        documentDAO.deleteDocument(did);
    }

}
