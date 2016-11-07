package com.letv.whatslive.server.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.dto.MenuDTO;
import com.letv.whatslive.mongo.dao.StartConstantDAO;
import com.letv.whatslive.mongo.dao.StartVariableDAO;
import com.letv.whatslive.mongo.dao.TagDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-10-23.
 */
@Service
public class StartService {

    @Resource
    private StartConstantDAO constantDAO;

    @Resource
    private StartVariableDAO variableDAO;

    @Resource
    private TagDAO tagDAO;


    public Map getAllStart(RequestHeader header,Map<String, Object> params){
        String model = ObjectUtils.toString(params.get("model")); //机型参数
        String sysVer = ObjectUtils.toString(params.get("sysVer")); //系统版本
        String appVer = ObjectUtils.toString(params.get("appVer")); //app版本
        String pcode = ObjectUtils.toString(params.get("pcode")); //渠道号
        String resolution = ObjectUtils.toString(params.get("resolution")); //分辨率
        Integer from = ObjectUtils.toInteger(header.getFrom(),2);
        Map<String,Object> result = Maps.newHashMap();
        if(StringUtils.isNotBlank(model) && StringUtils.isNotBlank(sysVer) && StringUtils.isNotBlank(appVer) && StringUtils.isNotBlank(pcode)) {
            if (model.equalsIgnoreCase("x600") && sysVer.equals("5.0.2") && appVer.equals("1.1.3") && pcode.equals("testlehi")) {
                result.put("maxStream", 1000);
            } else {
                result.put("maxStream", 0);
            }
        }else{
            result.put("maxStream", 0);
        }
        //TODO 后续由cms配置生成
//        DBObject query = new BasicDBObject();
//        query.put("isOpen",1);
//        query.put("phoneType",from);
//        DBObject constant = constantDAO.find(query);
//        DBObject variable = variableDAO.find(query);


//        result.put("constant",new StartConsDTO(true));
//        result.put("variable",new StartVarDTO(true));

        //TODO tag标签type
//        DBObject tagQuery = new BasicDBObject();
//        tagQuery.put("type",1);
//        DBObject tagOrder = new BasicDBObject();
//        List<DBObject> tagList = tagDAO.selectAll(tagQuery,tagOrder);
        List<MenuDTO> menus = Lists.newArrayList();
//        for(DBObject obj : tagList){
            MenuDTO dto = new MenuDTO();
            dto.setMenuId(1l);
            dto.setMenuName("推荐");
            dto.setCommand("recommend");
            menus.add(dto);
//        }
        result.put("menus",menus);
        return result;
    }
}
