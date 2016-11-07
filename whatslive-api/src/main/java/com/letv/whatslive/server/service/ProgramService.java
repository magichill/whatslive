package com.letv.whatslive.server.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.ProgramReplay;
import com.letv.whatslive.model.Subscribe;
import com.letv.whatslive.model.convert.ProgramConvert;
import com.letv.whatslive.model.dto.ProgramDTO;
import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
import com.letv.whatslive.model.utils.ObjUtils;
import com.letv.whatslive.mongo.dao.FriendsDAO;
import com.letv.whatslive.mongo.dao.ProgramDAO;
import com.letv.whatslive.mongo.dao.ProgramReplayDAO;
import com.letv.whatslive.mongo.dao.SubscribeDAO;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.PageBean;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by gaoshan on 15-7-13.
 */
@Service
public class ProgramService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Resource
    private ProgramDAO programDAO;

    @Resource
    private FriendsDAO friendsDAO;

    @Resource
    private SubscribeDAO subscribeDAO;

    @Resource
    private ProgramReplayDAO programReplayDAO;

    @Resource
    private JedisDAO jedisDAO;

    /**
     * 查看当前用户是否正在直播
     * @param userId
     * @return
     */
    public DBObject checkBroadProgram(Long userId){
        DBObject query = new BasicDBObject();
        query.put("status",1);
        query.put("pType",1);
        query.put("userId",userId);
        DBObject program = programDAO.find(query);
        return program;
    }

    /**
     * 查看是否有同一时间的预约
     * @param userId
     * @param startTime
     * @return
     */
    public DBObject checkPlanProgram(Long userId,Long startTime){
        DBObject query = new BasicDBObject();
        query.put("status",1);
        query.put("pType",2);
        query.put("userId",userId);
        query.put("startTime",startTime);
        DBObject planProgram = programDAO.find(query);
        return planProgram;
    }

    public Long createProgram(Program program){
        Long userId = program.getUserId();
        DBObject obj = checkBroadProgram(userId);
        if(obj == null){
            return programDAO.insertProgram(program);
        }else{
            DBObject query = new BasicDBObject();
            query.put("_id",ObjectUtils.toLong(obj.get("_id")));
            DBObject update = new BasicDBObject();
            update.put("pType",3);
            update.put("status",0);
            programDAO.update(query,new BasicDBObject("$set", update));
            jedisDAO.getJedisWriteTemplate().publish("chatroom", ChatEvent.createChatEvent(ObjectUtils.toString(userId), ObjectUtils.toString(obj.get("_id")), 7).toString());
            return null;
        }
    }

    /**
     * 预生成programId作为分享id
     * @param program
     * @return
     */
    public Long createEmptyProgram(Program program){
        return programDAO.insertProgram(program);
    }

    /**
     * 通过Id获取预分享的节目信息
     * @param id
     * @return
     */
    public Program getEmtpyProgramById(long id){
        DBObject query = new BasicDBObject();
        query.put("_id",id);
        query.put("status",0);
        query.put("pType",-1);
        DBObject obj = programDAO.find(query);
        if(obj != null){
            return ProgramConvert.castDBObjectToProgram(obj);
        }else{
            return null;
        }
    }

    /**
     * 开启分享至第三方的直播
     * @param program
     */
    public boolean startEmptyProgram(Program program){
        DBObject obj = checkBroadProgram(program.getUserId());
        if(obj == null) {
            DBObject query = new BasicDBObject();
            query.put("_id", program.getId());
            query.put("pType", -1);
            query.put("userId", program.getUserId());
            DBObject update = new BasicDBObject();
            update.put("pType", 1);
            update.put("status", program.getStatus());
            update.put("pName", program.getPName());
            update.put("pDesc", program.getPDesc());
            update.put("activityId", program.getActivityId());
            update.put("startTime", System.currentTimeMillis());
            update.put("endTime", program.getEndTime());
            programDAO.update(query, new BasicDBObject("$set", update));
            return true;
        }else{
            DBObject query = new BasicDBObject();
            query.put("_id",ObjectUtils.toLong(obj.get("_id")));
            DBObject update = new BasicDBObject();
            update.put("pType",3);
            update.put("status",0);
            programDAO.update(query,new BasicDBObject("$set", update));
            return false;
        }
    }

    /**
     * 创建分享至第三方的预约直播
     * TODO
     * @param program
     */
    public boolean createEmptyPlanProgram(Program program){
        DBObject obj = checkPlanProgram(program.getUserId(),program.getStartTime());
        if(obj == null) {
            DBObject query = new BasicDBObject();
            query.put("_id", program.getId());
            query.put("pType", -1);
            query.put("userId", program.getUserId());
            DBObject update = new BasicDBObject();
            update.put("pType", 2);
            update.put("status", program.getStatus());
            update.put("pName", program.getPName());
            update.put("pDesc", program.getPDesc());
            update.put("province",program.getProvince());
            update.put("city",program.getCity());
            update.put("startTime",program.getStartTime());
            update.put("endTime", program.getEndTime());
            programDAO.update(query, new BasicDBObject("$set", update));
            return true;
        }else{
            return false;
        }
    }

    public Long createPlanProgram(Program program){
        Long userId = program.getUserId();
        DBObject query = new BasicDBObject();
        query.put("status",1);
        query.put("pType",1);
        query.put("userId",userId);
        DBObject obj = programDAO.find(query);
        if(obj == null){
            //排除相同开始时间的预约直播
            DBObject planProgram = checkPlanProgram(userId,program.getStartTime());
            if(planProgram == null) {
                return programDAO.insertProgram(program);
            }else {
                return -1l;
            }
        }else{
            return 0l;
        }
    }

    public void startPlanProgram(Long programId,Long userId,String activityId){
        DBObject query = new BasicDBObject();
        query.put("_id",programId);
        query.put("pType",2);
        query.put("userId",userId);
        DBObject update = new BasicDBObject();
        update.put("pType",1);
        update.put("activityId",activityId);
        update.put("isShow",1);
        update.put("startTime",System.currentTimeMillis());
        programDAO.update(query,new BasicDBObject("$set",update));
    }

    public void closePlanProgram(Long programId,Long userId){
        DBObject query = new BasicDBObject();
        query.put("_id",programId);
        query.put("pType",2);
        query.put("userId",userId);
        DBObject update = new BasicDBObject();
        update.put("status",0);
        update.put("endTime",System.currentTimeMillis());
        programDAO.update(query,new BasicDBObject("$set",update));
    }

    public boolean updateProgramPic(Program program) {
        DBObject query = new BasicDBObject();
        query.put("_id",program.getId());
        DBObject update = new BasicDBObject();
        update.put("picture",program.getPicture());
        return programDAO.update(query,new BasicDBObject("$set",update));
    }

    public Map<String,Object> updateCount(Map<String,Object> params){
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
        Long userId = ObjectUtils.toLong(params.get("userId"));
        Long timestamp = ObjectUtils.toLong(params.get("timestamp"));

        Map<String,Object> paramMap = Maps.newHashMap();
        paramMap.put("startTime", new BasicDBObject("$lt", timestamp));

        List<DBObject> focusResult = Lists.newArrayList();
        List<Program> subResult = Lists.newArrayList();

        BasicDBList userIds = friendsDAO.getAllFocusId(userId);
        focusResult = programDAO.getUpdateProgramListByIds(userIds,timestamp,start,limit);

        paramMap.put("userId",userId);
        paramMap.put("status",1);
        List<Subscribe> subscribes = subscribeDAO.getAllSubscribe(start,limit,paramMap);
        for(Subscribe s : subscribes){
            DBObject program = s.getProgram().fetch();
            if(program != null && ObjectUtils.toInteger(program.get("status"))==1){
                subResult.add(ProgramConvert.castDBObjectToProgram(program));
            }
        }
        Map<String,Object> result = Maps.newHashMap();
        result.put("focus",focusResult!=null?focusResult.size():0);
        result.put("subscribe",subResult!=null?subResult.size():0);
        return result;
    }

    public List<Program> recommendList(Integer start,Integer limit,Integer type,Long timestamp){
        List<Program> result = Lists.newArrayList();
        Map<String,Object> paramMap = Maps.newHashMap();
        paramMap.put("isShow",1);
        if(start != 1) {
            if (timestamp != null && timestamp > 0) {
                paramMap.put("createTime", new BasicDBObject("$lt", timestamp));
            }
        }
        result = programDAO.getAllProgram(start,limit,paramMap);
        return result;
    }
    /**
     * 根据类型获取节目列表接口
     * @param params
     * @return
     */
    public List<Program> list(Map<String,Object> params){
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
        Integer type = ObjectUtils.toInteger(params.get("type"));
        Long userId = ObjectUtils.toLong(params.get("userId"));
        Long timestamp = ObjectUtils.toLong(params.get("timestamp"));
        Map<String,Object> paramMap = Maps.newHashMap();
        paramMap.put("isShow",1);
        if(start != 1) {
            if (timestamp != null && timestamp > 0) {
                paramMap.put("createTime", new BasicDBObject("$lt", timestamp));
            }
        }
        List<Program> result = Lists.newArrayList();
        switch (type){
            case Constant.INDEX_RECOMMEND:
                result = programDAO.getAllProgram(start,limit,paramMap);
                break;
            case Constant.INDEX_FOLLOW:
                BasicDBList userIds = friendsDAO.getAllFocusId(userId);
                result = programDAO.getProgramListByIds(userIds,start,limit);
                break;
            case Constant.INDEX_SUBSCRIBE:
//                paramMap.put("userId",userId);
//                paramMap.put("status",1);
//                List<Subscribe> subscribes = subscribeDAO.getAllSubscribe(start,limit,paramMap);
//                for(Subscribe s : subscribes){
//                    DBObject program = s.getProgram().fetch();
//                    if(program != null){
//                        result.add(ProgramConvert.castDBObjectToProgram(program));
//                    }
//                }
                DBObject query = new BasicDBObject();
                query.put("userId",userId);
                query.put("status",1);
                List<Subscribe> subscribes = subscribeDAO.getAllSubscribeObj(query);
                for(Subscribe s : subscribes){
                    DBObject program = s.getProgram().fetch();
                    if(program != null){
                        if(ObjectUtils.toInteger(program.get("status")).intValue() == 1) {
                            result.add(ProgramConvert.castDBObjectToProgram(program));
                        }
                    }
                }

                Collections.sort(result,new Comparator<Program>() {
                    @Override
                    public int compare(Program o1, Program o2) {
                            if(o1 instanceof Program && o2 instanceof Program){
                                Program p1 = (Program)o1;
                                Program p2 = (Program)o2;
                                if(p1.getPType() > p2.getPType()){
                                    return 1;
                                }else if(p1.getPType() == p2.getPType()){
                                    return 0;
                                }else{
                                    return -1;
                                }
                            }else {
                                return 0;
                            }
                        }
                });
                PageBean<Program> pageBean = new PageBean<Program>();
                result = pageBean.page(start,limit,result);

                break;
            default:
                break;
        }
        return result;
    }

    public void getUpdateInfoByUserId(Long userId){

    }
    /**
     * 获取用户发布的直播列表
     * @param start
     * @param limit
     * @param userId
     * @return
     */
    public List<ProgramDTO> getProgramByUserId(Integer start,Integer limit,Long userId,Integer... type){
        Map query = Maps.newHashMap();
        query.put("userId",userId);
        if(type.length>0){
            query.put("pType",type[0]);
        }
        query.put("status",1);
        DBObject order = new BasicDBObject();
        order.put("createTime",-1);
        List<DBObject> programs = programDAO.selectAll(start,limit,query,order);
        List<ProgramDTO> result = Lists.newArrayList();
        for(DBObject p : programs) {
            Program program =  ProgramConvert.castDBObjectToProgram(p);
            ProgramDTO dto = new ProgramDTO(program);
//            dto.setShareUrl(ProgramConstants.SHARE_URL+program.getId());
            result.add(dto);
        }
        return result;
    }

    public List<ProgramDTO> getReplayByUserId(Integer start,Integer limit,Long userId){
        Map query = Maps.newHashMap();
        query.put("userId",userId);
        query.put("pType",3);
        BasicDBList values = new BasicDBList();
        values.add(-1);
        values.add(1);
        query.put("status",new BasicDBObject("$in",values));
        DBObject order = new BasicDBObject();
        order.put("createTime",-1);
        List<DBObject> programs = programDAO.selectAll(start,limit,query,order);
        List<ProgramDTO> result = Lists.newArrayList();
        for(DBObject p : programs) {
            Program program =  ProgramConvert.castDBObjectToProgram(p);
            ProgramDTO dto = new ProgramDTO(program);
            result.add(dto);
        }
        return result;
    }

    public boolean checkStartProgram(Long userId){
        DBObject query = new BasicDBObject();
        query.put("userId",userId);
        query.put("status",1);
        query.put("pType",1);
        DBObject obj = programDAO.find(query);
        if(obj == null){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获取用户发布的预约直播列表
     * @param userId
     * @return
     */
    public List<ProgramDTO> getPlanProgramByUserId(Long userId){
        DBObject query = new BasicDBObject();
        query.put("userId",userId);
        BasicDBList values = new BasicDBList();
        values.add(2);
        values.add(0);
        query.put("pType", new BasicDBObject("$in", values));
        query.put("status",1);
        DBObject order = new BasicDBObject();
        List<DBObject> programs = programDAO.findAll(query,order);
        List<ProgramDTO> result = Lists.newArrayList();
        for(DBObject p : programs) {
            Program program =  ProgramConvert.castDBObjectToProgram(p);
            ProgramDTO dto = new ProgramDTO(program);
//            dto.setShareUrl(ProgramConstants.SHARE_URL+program.getId());
            result.add(dto);
        }
        return result;
    }
    /**
     * 获取用户发布的直播数量
     * @param userId
     * @return
     */
    public Long countProgramByUserId(Long userId){
        Map query = Maps.newHashMap();
        query.put("userId",userId);
        query.put("status",1);
        Long count = programDAO.countList(query);
        return count;
    }

    /**
     * 获取用户发布的录播数量
     * @param userId
     * @param newVersion 新版本显示转码中的录播
     * @return
     */
    public Long countReplayProgramByUserId(Long userId,int newVersion){
        Map query = Maps.newHashMap();
        query.put("userId",userId);
        query.put("pType",3);
        if(newVersion == 1){
            BasicDBList values = new BasicDBList();
            values.add(-1);
            values.add(1);
            query.put("status",new BasicDBObject("$in",values));
        }else {
            query.put("status",1);
        }
        Long count = programDAO.countList(query);
        return count;
    }
    /**
     * 获取用户发布的直播数量
     * @param userId
     * @return
     */
    public Long countProgramByUserId(Long userId,Map<String,Object> params){
        Map query = Maps.newHashMap();
        query.put("userId",userId);
        Iterator iter = params.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            query.put(ObjectUtils.toString(entry.getKey()),entry.getValue());
        }
        Long count = programDAO.countList(query);
        return count;
    }
    /**
     * 通过Id获取节目信息
     * @param id
     * @return
     */
    public Program getProgramById(long id){
        DBObject query = new BasicDBObject();
        query.put("_id",id);
        query.put("status",1);
        DBObject obj = programDAO.find(query);
        if(obj != null){
            return ProgramConvert.castDBObjectToProgram(obj);
        }else{
            return null;
        }
    }

    /**
     * 通过Id获取节目信息,忽略status
     * @param id
     * @return
     */
    public Program getProgramIngoreStatusById(long id){
        DBObject query = new BasicDBObject();
        query.put("_id",id);
        DBObject obj = programDAO.find(query);
        if(obj != null){
            return ProgramConvert.castDBObjectToProgram(obj);
        }else{
            return null;
        }
    }

    /**
     * 通过Id获取节目信息,linkcard使用
     * @param id
     * @return
     */
    public DBObject getLinkCardProgramById(long id){
        DBObject query = new BasicDBObject();
        query.put("_id",id);
        DBObject obj = programDAO.find(query);
        if(obj != null){
            return obj;
        }else{
            return null;
        }
    }
    /**
     * 获取结束的直播
     * @param id
     * @return
     */
    public Program getEndProgramById(Long id){
        DBObject query = new BasicDBObject();
        query.put("_id",id);
//        query.put("status",0);
        DBObject obj = programDAO.find(query);
        if(obj != null){
            return ProgramConvert.castDBObjectToProgram(obj);
        }else{
            return null;
        }
    }

    public Program getPlanProgramByUserId(Long id,Long userId){
        DBObject query = new BasicDBObject();
        query.put("_id",id);
        query.put("status",1);
        query.put("pType",2);
        query.put("userId",userId);
        DBObject obj = programDAO.find(query);
        if(obj != null){
            return ProgramConvert.castDBObjectToProgram(obj);
        }else{
            return null;
        }

    }

    public Program getReplayProgramByUserId(Long id,Long userId){
        DBObject query = new BasicDBObject();
        query.put("_id",id);
//        query.put("status",1);
        query.put("pType",3);
        query.put("userId",userId);
        DBObject obj = programDAO.find(query);
        if(obj != null){
            return ProgramConvert.castDBObjectToProgram(obj);
        }else{
            return null;
        }

    }
    /**
     * 点赞数自增或自减
     * @param vid
     * @param status
     */
    public void incLikeNum(long vid,int status){
        DBObject incObj = new BasicDBObject();
        incObj.put("likeNum",status);
        DBObject query = new BasicDBObject();
        query.put("_id",vid);
        programDAO.update(query,new BasicDBObject("$inc",incObj));
    }

    /**
     * 更改直播状态为结束
     * @param id
     */
    public void closeProgram(long id,int status){
        DBObject query = new BasicDBObject();
        query.put("_id",id);
        DBObject update = new BasicDBObject();
//        update.put("status",-1);
//        update.put("pType",3);
//        update.put("endTime",System.currentTimeMillis());
        update.put("status", status);
        update.put("pType", 3);
        update.put("endTime", System.currentTimeMillis());
        update.put("watchNum", ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().smembers(Constants.LIVE_ONLINE_TOTALUSER_KEY + id).size(),0l));
        update.put("likeNum", ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_LIKE_KEY + id),0l));
        update.put("commentNum", ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_COMMENT_KEY + id),0l));

        update.put("realWatchNum", ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().smembers(Constants.LIVE_ONLINE_REAL_TOTALUSER_KEY + id).size(),0l));
        update.put("realLikeNum", ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_REAL_LIKE_KEY + id),0l));
        update.put("realCommentNum", ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_REAL_COMMENT_KEY + id),0l));

        update.put("updateTime", System.currentTimeMillis());
        programDAO.update(query,new BasicDBObject("$set",update));
    }


    public void cancelProgram(Long programId){
        DBObject query = new BasicDBObject();
        query.put("_id",programId);
        DBObject update = new BasicDBObject();
        update.put("status",0);
        programDAO.update(query,new BasicDBObject("$set",update));
    }

    /**
     * 获取随机直播频道
     * @return
     */
    public Program getRandomProgram(){
        DBObject query = new BasicDBObject();
        query.put("pType",1);
        query.put("status",1);
        DBObject order = new BasicDBObject();
        order.put("createTime",-1);
        DBObject program = new BasicDBObject();
        List<DBObject> programList = programDAO.selectAll(query,order);
        if(programList != null && programList.size()>0){
            int index = (int)(Math.random()*programList.size());
            program = programList.get(index);
        }else{
            return null;
        }
        Program programDTO = ProgramConvert.castDBObjectToProgram(program);
        long likeNum = ObjUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_LIKE_KEY+programDTO.getId()), 0l);
        programDTO.setLikeNum(likeNum);
        return programDTO;
    }

    public List<DBObject> getSubscribeProgram(BasicDBList programIds){
        DBObject query = new BasicDBObject();
        query.put("_id",new BasicDBObject("$in",programIds));
        DBObject order = new BasicDBObject();
        order.put("startTime",1);
        return programDAO.findAll(query,order);
    }

    /**
     * 通过活动id获取直播
     * @param activityId
     * @return
     */
    public DBObject getProgramByActivityId(String activityId){
        DBObject query = new BasicDBObject();
        query.put("activityId",activityId);
        DBObject program = programDAO.find(query);
        return program;
    }
    /**
     * 转码回调更新状态
     * @param activityId
     * @param videoId
     */
    public void updateProgramByVideoId(String activityId,String videoId,String vuid,long duration){
        DBObject query = new BasicDBObject();
        query.put("activityId",activityId);
        query.put("pType",3);
        DBObject program = programDAO.find(query);
        if(program!=null) {
            DBObject update = new BasicDBObject();
            update.put("videoId", videoId);
            update.put("isShow",0); //默认录播不显示在推荐列表
            if(duration > 0l){
                update.put("endTime",ObjectUtils.toLong(program.get("startTime"))+duration*1000);
            }
            if(ObjectUtils.toInteger(program.get("status")).intValue() != 0) {
                update.put("status", 1);
            }
            update.put("vuid", vuid);
            programDAO.update(query, new BasicDBObject("$set", update));
        }
    }

    /**
     * 获取回放文件生成地址
     * @param programId
     * @return
     */
    public String replayFileUrl(Long programId){
        ProgramReplay replay = programReplayDAO.querySuccessReplayByPid(programId);
        if(replay == null){
            return "";
        }else{
            return replay.getLogURL();
        }
    }

    public List<ProgramDTO> getProgramListByCity(Integer start,Integer limit,String city,String province){
        Map query = Maps.newHashMap();
        query.put("city",city);
        query.put("province",province);
        DBObject order = new BasicDBObject();
        order.put("pType",1);
        order.put("priority",-1);
        List<DBObject> list = programDAO.selectAll(start,limit,query);
        List<ProgramDTO> result = Lists.newArrayList();
        for(DBObject dbo : list){
            result.add(new ProgramDTO(ProgramConvert.castDBObjectToProgram(dbo)));
        }
        return result;
    }

    public List<ProgramDTO> searchProgramList(Integer start,Integer limit,String keyword,Long timestamp){
        Map query = Maps.newHashMap();
        Pattern pattern = Pattern.compile("^.*" + keyword+ ".*$", Pattern.CASE_INSENSITIVE);
        query.put("pName",pattern);
        if(start != 1) {
            if (timestamp != null && timestamp > 0) {
                query.put("createTime", new BasicDBObject("$lt", timestamp));
            }
        }
        DBObject order = new BasicDBObject();
        order.put("pType",1);
        order.put("priority",-1);
        List<DBObject> list = programDAO.selectAll(start,limit,query,order);
        List<ProgramDTO> result = Lists.newArrayList();
        for(DBObject dbo : list){
            result.add(new ProgramDTO(ProgramConvert.castDBObjectToProgram(dbo)));
        }
        return result;
    }
}
