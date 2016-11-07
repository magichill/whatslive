package com.letv.whatslive.chat.listener;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.letv.whatslive.chat.server.ChatServer;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.ForbiddenWordUtils;
import com.letv.whatslive.model.ActionLog;
import com.letv.whatslive.model.Voice;
import com.letv.whatslive.model.convert.ActionLogConvert;
import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
import com.letv.whatslive.mongo.dao.ActionLogDAO;
import com.letv.whatslive.mongo.dao.VoiceDAO;
import com.letv.whatslive.redis.JedisDAO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zoran on 15-7-10.
 */
@Component
public class ChatEventListener implements DataListener<ChatEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatEventListener.class);

    @Autowired
    private JedisDAO jedisDAO;

    @Autowired
    private VoiceDAO voiceDAO;

    @Autowired
    private ActionLogDAO actionLogDAO;

    @Override
    public void onData(SocketIOClient client, ChatEvent data, AckRequest ackSender)
        throws Exception {
        if (!ackSender.isAckRequested()) {
            if (StringUtils.isNotBlank(data.getRoomId()) && StringUtils.isNotBlank(data.getUid())) {

                LOGGER.info(
                    "Received User Data <" + data.toLogString() + "> "
                        + client.getSessionId() + " "
                        + client.getRemoteAddress());
                if (data != null) {
                    if (data.getAction() == 1) {
                        if (data.getType() == 1 && StringUtils.isNotBlank(data.getContent())) {
                            if (ForbiddenWordUtils.getForbiddenWords() != null
                                && ForbiddenWordUtils.getForbiddenWords().size() > 0) {
                                if (ForbiddenWordUtils.containsForbiddenWord(data.getContent())) {
                                    data.setContent(ForbiddenWordUtils.replace(data.getContent()));
                                }
                            }
                        }
                        if (data.getType() == 2 && data.getVoice().length() > 0) {
                            byte[] raw = org.apache.commons.codec.binary.Base64.decodeBase64(data.getVoice());
                            Voice voice = Voice.newVoice(data.getContent(), raw, System.currentTimeMillis());
                            long voiceId = voiceDAO.insertVoice(voice);
                            if (voiceId > 0) {
                                String url = ChatServer.MEDIA_URL+voiceId;
                                data.setVoice(url);
                                data.setType(3);
                            }
                        }
                        //聊天消息计数器加1
                        jedisDAO.getJedisWriteTemplate()
                            .incr(Constants.LIVE_ONLINE_COMMENT_KEY + data.getRoomId());
                        jedisDAO.getJedisWriteTemplate()
                                .incr(Constants.LIVE_ONLINE_REAL_COMMENT_KEY + data.getRoomId());

                    }
                    if (data.getAction() == 2) {
                        //喜欢计数器加1
                        jedisDAO.getJedisWriteTemplate()
                            .incr(Constants.LIVE_ONLINE_LIKE_KEY + data.getRoomId());
                        jedisDAO.getJedisWriteTemplate()
                                .incr(Constants.LIVE_ONLINE_REAL_LIKE_KEY + data.getRoomId());
                    }
                    jedisDAO.getJedisWriteTemplate().publish("chat", data.toString());
                    //记录用户的行为日志
                    if(data.isRecordEnvent()){
                        ActionLog actionLog = ActionLogConvert.castChatEventToActionLog(data);
                        actionLogDAO.insertActionLog(actionLog);
                    }
                }
            } else {
                LOGGER.warn("Received User Data Error <" + data.toLogString() + "> "
                        + client.getSessionId() + " " + client.getRemoteAddress());
            }
        }
    }

}
