package com.letv.whatslive.server.controller;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.model.convert.UserConvert;
import com.letv.whatslive.model.dto.ProgramDTO;
import com.letv.whatslive.model.dto.UserDTO;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.server.service.ProgramService;
import com.letv.whatslive.server.service.ReportService;
import com.letv.whatslive.server.service.StartService;
import com.letv.whatslive.server.service.UserService;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by gaoshan on 15-7-10.
 */
@Service
public class IndexController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private ProgramService programService;

    @Resource
    private ReportService reportService;

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Resource(name = "redisTemplate")
//    private ValueOperations<String, Object> valOps;

    @Autowired
    private JedisDAO jedisDAO;

    @Resource
    private StartService startService;
    /**
     * 开机启动接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody startup(Map<String, Object> params, String sid, RequestHeader header){
        try {
            Map data = startService.getAllStart(header,params);
            return getResponseBody(sid, data);
        }catch(Exception e){
            LogUtils.logError("IndexController startup Failure : ", e);
            return getErrorResponse(sid," get start up failure");
        }
    }

    /**
     * web首页宣传接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody index(Map<String, Object> params, String sid, RequestHeader header){
//        User user = userService.getUserById(100l);
//        UserDTO dto = null;
//        if(user != null){
//            dto = new UserDTO(user);
//        }
        Long userId = ObjectUtils.toLong(header.getUserId());
        Program program = programService.getProgramById(0l);
        if(program != null) {
            program.setLikeNum(ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_LIKE_KEY + 0), 0l));
            program.setWatchNum(ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().llen(Constants.LIVE_ONLINE_USER_LIST_KEY + 0)));
            program.setCommentNum(ObjectUtils.toLong(jedisDAO.getJedisReadTemplate().get(Constants.LIVE_ONLINE_COMMENT_KEY + 0), 0l));
            program.setPType(1);
            program.setPicture("http://i3.letvimg.com/lc02_iscms/201507/29/12/42/806c5d5d4a864980bd17df2b4ce21763.jpg");
            program.setStartTime(System.currentTimeMillis());
            program.setStatus(1);
            DBObject user = program.getUserRef().fetch();
            UserDTO  dto = null;
            if(user != null){
                dto = new UserDTO(UserConvert.castDBObjectToUser(user));
            }
            ProgramDTO programDTO = new ProgramDTO(program, dto);
//            programDTO.setShareUrl(Constant.INNER_SHARE_URL+program.getId());
            if(userId != null) {
                Integer report = reportService.checkReport(userId, program.getId());
                programDTO.setIsReport(report);
            }else{
                programDTO.setIsReport(0);
            }
            return getResponseBody(sid, programDTO);
        }else{
            return getErrorResponse(sid, Constant.LIVE_NULL_ERROR_CODE);
        }
    }
}
