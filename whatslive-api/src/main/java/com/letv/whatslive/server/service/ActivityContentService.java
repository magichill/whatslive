package com.letv.whatslive.server.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.ActivityContent;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.convert.ActivityContentConvert;
import com.letv.whatslive.model.convert.ProgramConvert;
import com.letv.whatslive.mongo.dao.ActivityContentDAO;
import com.letv.whatslive.server.util.PageBean;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-10-20.
 */

@Service
public class ActivityContentService {

    @Resource
    private ActivityContentDAO activityContentDAO;

    public void insertActivityContent(ActivityContent activityContent){
        if(activityContent != null) {
            activityContentDAO.insert(ActivityContentConvert.castActivityContentToDBObject(activityContent));
        }
    }


    public List<Program> getProgramListByActId(Long actId,Integer start,Integer limit){
//        Map<String,Object> params = Maps.newHashMap();
//        params.put("actId",actId);
//        List<DBObject> list = activityContentDAO.selectAll(1,10000,params);
        DBObject query = new BasicDBObject();
        query.put("actId",actId);
        DBObject order = new BasicDBObject();
        order.put("priority",-1);
        List<DBObject> list = activityContentDAO.selectAll(query,order);
        List<Program> result = Lists.newArrayList();
        for(DBObject dbo : list){
            DBRef programRef = (DBRef)dbo.get("program");
            DBObject programDBO = programRef.fetch();
            Program program = ProgramConvert.castDBObjectToProgram(programDBO);
            if(program.getStatus().intValue() == 1) {
                result.add(program);
            }
//            if(result.size()==(start*limit)){
//                break;
//            }
        }
        if(result != null && result.size()>0){
            Collections.sort(result, new Comparator<Program>() {
                @Override
                public int compare(Program o1, Program o2) {
                    int id = ObjectUtils.toInteger(o2.getId() - o1.getId());
                    int type = ObjectUtils.toInteger(o1.getPType() - o2.getPType());

                    if (type != 0) {
                        return type;
                    } else {
                        return id;
                    }
                }
            });
        }

        PageBean<Program> pageBean = new PageBean<Program>();
        return pageBean.page(start,limit,result);
//        if(result.size()<(start-1)*limit){
//            result.clear();
//            return result;
//        }else if(result.size()<start*limit){
//            return result.subList((start-1)*limit, result.size());
//        }else{
//            return result.subList((start-1)*limit, start*limit);
//        }

    }

}
