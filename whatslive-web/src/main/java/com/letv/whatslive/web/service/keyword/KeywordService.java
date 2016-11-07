package com.letv.whatslive.web.service.keyword;

import com.letv.whatslive.model.Keyword;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.mongo.dao.KeywordDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/7/22.
 */
@Component
public class KeywordService {
    @Autowired
    private KeywordDAO keywordDAO;

    /**
     * 根据条件查询所有关键词
     *
     * @param params 模糊查询的查询条件
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @return 满足条件的对象的集合
     */
    public List<Keyword> getKeywordListByParams(Map params, Integer start, Integer limit) {
        return keywordDAO.getKeywordListByParams(params, start, limit);
    }

    /**
     * 根据指定条件查询关键词的数量
     *
     * @param params 模糊查询的查询条件
     * @return 满足条件的结果记录数
     */
    public Long countKeywordByParams(Map params) {
        return keywordDAO.countKeywordByParams(params);
    }


    public Keyword getKeywordById(Long kid) {
        return keywordDAO.getKeywordById(kid);
    }

    public ResultBean insertKeyword(Keyword keyword){
        ResultBean res = ResultBean.getTrueInstance();
        String message = keywordDAO.saveKeyword(keyword);
        if(null!=message){
            res.setFalseAndMsg(message);
        }
        return res;
    }

    public void updateKeyword(Keyword keyword) {
        keywordDAO.updateKeyword(keyword);
    }

    public void deleteKeyword(Long kid){
        keywordDAO.deleteKeyword(kid);
    }
    public List<String> queryAllKeyword(){
        return keywordDAO.queryAllKeyword();
    }

}
