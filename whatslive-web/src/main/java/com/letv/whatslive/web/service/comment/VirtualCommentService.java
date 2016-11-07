package com.letv.whatslive.web.service.comment;

import com.letv.whatslive.model.VirtualComment;
import com.letv.whatslive.web.common.ResultBean;
import com.letv.whatslive.mongo.dao.VirtualCommentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/7/22.
 */
@Component
public class VirtualCommentService {
    @Autowired
    private VirtualCommentDAO virtualCommentDAO;

    /**
     * 根据条件查询所有虚拟评论
     *
     * @param params 模糊查询的查询条件
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @return 满足条件的对象的集合
     */
    public List<VirtualComment> getVirtualCommentListByParams(Map params, Integer start, Integer limit) {
        return virtualCommentDAO.getVirtualCommentListByParams(params, start, limit);
    }

    /**
     * 根据指定条件查询虚拟评论的数量
     *
     * @param params 模糊查询的查询条件
     * @return 满足条件的结果记录数
     */
    public Long countVirtualCommentByParams(Map params) {
        return virtualCommentDAO.countVirtualCommentByParams(params);
    }


    public VirtualComment getVirtualCommentById(Long kid) {
        return virtualCommentDAO.getVirtualCommentById(kid);
    }

    public ResultBean insertVirtualComment(VirtualComment comment){
        ResultBean res = ResultBean.getTrueInstance();
        String message = virtualCommentDAO.saveVirtualComment(comment);
        if(null!=message){
            res.setFalseAndMsg(message);
        }
        return res;
    }

    public void updateVirtualComment(VirtualComment comment) {
        virtualCommentDAO.updateVirtualComment(comment);
    }

    public void deleteVirtualComment(Long cid){
        virtualCommentDAO.deleteVirtualComment(cid);
    }

}
