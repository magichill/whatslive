package com.letv.whatslive.website.service.video;

import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.mongo.dao.ProgramDAO;
import com.letv.whatslive.mongo.dao.SubscribeDAO;
import com.letv.whatslive.redis.JedisDAO;

import com.letv.whatslive.website.util.String.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 直播信息取得服务
 * Created by haojiayao on 2015/8/12.
 */
@Component
public class VideoService {

    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private JedisDAO jedisDAO;

    @Autowired
    private SubscribeDAO subscribeDAO;



    /**
     * 根据查询参数获取视频列表信息
     * @param order 排序参数
     * @param params 检索条件
     * @param limit 本次要查询的记录数
     * @return
     */
    public List<Program> getProgramListByParams(Map order, Map params, Integer limit) {

        // 获取视频列表
        List<Program> programsList = programDAO.getProgramListByParams(order, params, null, limit);

//        List<Program> programsList = new ArrayList<Program>();
//        Program p1 = programDAO.getProgramById(10002181L);
//        Program p2 = programDAO.getProgramById(10002153L);
//        Program p3 = programDAO.getProgramById(10002131L);
//        Program p4 = programDAO.getProgramById(10002186L);
//        Program p5 = programDAO.getProgramById(10002290L);
//        programsList.add(p1);
//        programsList.add(p2);
//        programsList.add(p3);
//        programsList.add(p4);
//        programsList.add(p5);

        for(Program program : programsList) {

            // 如果是查询结果中有直播视频需要从redis中获取观众总数、在线观看人数和点赞数
            if(ProgramConstants.pType_live.equals(program.getPType())) {

                // 观众总数
                long watchNum = jedisDAO.getJedisReadTemplate().smembers(Constants.LIVE_ONLINE_TOTALUSER_KEY + program.getId()).size();
                program.setWatchNum(watchNum);

                // 在线观看人数
                Long watchOnlineUserNum = jedisDAO.getJedisReadTemplate().llen(Constants.LIVE_ONLINE_USER_LIST_KEY + program.getId());
                program.setWatchOnlineUserNum(ObjectUtils.toLong(watchOnlineUserNum, 0L));

                // 点赞数
                Long likeNum = jedisDAO.getJedisReadTemplate().getAsLong(Constants.LIVE_ONLINE_LIKE_KEY + program.getId());
                program.setLikeNum(ObjectUtils.toLong(likeNum, 0L));
            }
        }

        return programsList;
    }

    /**
     * 根据查询参数获取视频列表信息
     * @param order 排序参数
     * @param params 检索条件
     * @param limit 本次要查询的记录数
     * @return
     */
    public List<Program> getProgramListByParamsForWeb(Map order, Map params, Integer limit) {

        // 获取视频列表
        List<Program> programsList = null;

        String ids = jedisDAO.getJedisReadTemplate().get("website_videolist_for_activity");
        if (!StringUtils.isBlank(ids)) {

            String[] idList = ids.split(",");
            programsList = new ArrayList<Program>();
            for(String id : idList) {

                programsList.add(programDAO.getProgramById(ObjectUtils.toLong(id)));
            }
        } else {

            programsList = programDAO.getProgramListByParams(order, params, null, limit);
        }

        for(Program program : programsList) {

            // 如果是查询结果中有直播视频需要从redis中获取观众总数、在线观看人数和点赞数
            if(ProgramConstants.pType_live.equals(program.getPType())) {

                // 观众总数
                long watchNum = jedisDAO.getJedisReadTemplate().smembers(Constants.LIVE_ONLINE_TOTALUSER_KEY + program.getId()).size();
                program.setWatchNum(watchNum);

                // 在线观看人数
                Long watchOnlineUserNum = jedisDAO.getJedisReadTemplate().llen(Constants.LIVE_ONLINE_USER_LIST_KEY + program.getId());
                program.setWatchOnlineUserNum(ObjectUtils.toLong(watchOnlineUserNum, 0L));

                // 点赞数
                Long likeNum = jedisDAO.getJedisReadTemplate().getAsLong(Constants.LIVE_ONLINE_LIKE_KEY + program.getId());
                program.setLikeNum(ObjectUtils.toLong(likeNum, 0L));
            }
        }

        return programsList;
    }

    /**
     * 根据直播ID获取视频信息
     * @param id
     * @return
     */
    public Program getProgramById(Long id) {

        Program program = programDAO.getProgramById(ObjectUtils.toLong(id));

        // 如果是查询结果中有直播视频需要从redis中获取观众总数、在线观看人数和点赞数
        if(program !=null && ProgramConstants.pType_live.equals(program.getPType())) {

            // 观众总数
            long watchNum = jedisDAO.getJedisReadTemplate().smembers(Constants.LIVE_ONLINE_TOTALUSER_KEY + program.getId()).size();
            program.setWatchNum(watchNum);

            // 在线观看人数
            Long watchOnlineUserNum = jedisDAO.getJedisReadTemplate().llen(Constants.LIVE_ONLINE_USER_LIST_KEY + program.getId());
            program.setWatchOnlineUserNum(ObjectUtils.toLong(watchOnlineUserNum, 0L));

            // 点赞数
            Long likeNum = jedisDAO.getJedisReadTemplate().getAsLong(Constants.LIVE_ONLINE_LIKE_KEY + program.getId());
            program.setLikeNum(ObjectUtils.toLong(likeNum, 0L));
        }

        return program;
    }

    /**
     * 根据预约视频ID获取预约人数
     * @param id
     * @return
     */
    public long getOrderNum(Long id) {

        // 如果是预约直播则获取预约人数
        long orderNum = subscribeDAO.countSubscribeByProgramId(id);

//        // 测试用
//        if (id == 10002201L) {
//
//            orderNum = 1349L;
//        }

        return orderNum;
    }
}
