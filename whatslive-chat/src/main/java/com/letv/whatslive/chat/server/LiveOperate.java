package com.letv.whatslive.chat.server;

import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
import com.letv.whatslive.mongo.dao.ProgramDAO;
import com.letv.whatslive.mongo.dao.UserDAO;
import com.letv.whatslive.redis.JedisDAO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wangjian7 on 2015/8/26.
 */
public class LiveOperate {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiveOperate.class);

    @Autowired
    private JedisDAO jedisDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ProgramDAO programDAO;

    public void endLive(final String uid, final String roomId, final String from, final String sessionId, final long delayTime) throws Exception{
        String time = jedisDAO.getJedisReadTemplate().get(Constat.LIVER_BREAK_KEY + roomId);
        if (StringUtils.isNotBlank(time)) {
            LOGGER.info("Anchor interrupt ready to close the live ! " + uid + " " + roomId + " "  + from + " ");
            if(sessionId.equals(jedisDAO.getJedisReadTemplate().get(roomId + "_" + uid))){
                jedisDAO.getJedisWriteTemplate().del(roomId+"_"+uid);
                Program program = programDAO.getProgramById(Long.valueOf(roomId));
                //判断直播结束是否是已经处理，根据数据库中直播的状态判断
                if(program.getPType() == ProgramConstants.pType_live){
                    //判断是否是中断但仍需要继续推流的直播，如果是则不中断直播
                    if(null==program.getIsContinue() || 1!=program.getIsContinue()){
                        //直播中断或结束，更新直播信息和状态
                        programDAO.updateData(Long.parseLong(roomId), delayTime);
                        userDAO.updateDataAfterClose(Long.parseLong(uid), Long.parseLong(roomId));
                        ChatEvent event = ChatEvent.createChatEvent(uid, roomId, 3);
                        jedisDAO.getJedisWriteTemplate().publish("chat",event.toString());
                        LOGGER.info("Anchor interrupt close live success ! " + uid + " " + roomId + " "  + from + " " + sessionId);
                    }else {
                        LOGGER.info("Anchor interrupt program isContinue is 1, don't need close program! " + uid + " " + roomId + " "  + from + " " + sessionId);
                    }

                }else{
                    LOGGER.info("Anchor interrupt live is closed, not need to close ! " + uid + " " + roomId + " "  + from + " " + sessionId);
                }
            }else{
                LOGGER.info("Anchor interrupt sessionId not equal ! " + uid + " " + roomId + " "  + from + " " + sessionId);
            }
        }else{
            LOGGER.info("Anchor interrupt don't close the live ! " + uid + " " + roomId + " "  + from + " " + sessionId);
        }

    }
}
