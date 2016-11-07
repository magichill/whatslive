package com.letv.whatslive.server.http;

import com.alibaba.fastjson.JSON;
import com.letv.whatslive.common.http.weibo.RespBody;
import com.letv.whatslive.common.http.weibo.data.CustomData;
import com.letv.whatslive.common.http.weibo.data.ImageData;
import com.letv.whatslive.common.http.weibo.data.StreamData;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.server.service.ProgramService;
import com.letv.whatslive.server.util.LogUtils;
import com.mongodb.DBObject;
import me.zhuoran.amoeba.netty.server.http.AbstractExecutor;
import me.zhuoran.amoeba.netty.server.http.AmoebaHttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by gaoshan on 15-9-7.
 */
@Service("wblinkcard")
public class WeiBoRequestHandler extends AbstractExecutor {

    private static final Logger logger = LoggerFactory.getLogger(WeiBoRequestHandler.class);

    @Autowired
    private ProgramService programService;

    @Override
    public Object execute(AmoebaHttpRequest amoebaHttpRequest) {

        try {
            List<String> urls = amoebaHttpRequest.getParams().get("url");
            if (urls == null || urls.size() < 0) {
                return getFailResp("Params error!");
            }

            String url = urls.get(0);
            if(StringUtils.isBlank(url)){
                return getFailResp("Params error!");
            }
            //获取短视频ID(现在只有短视频可以分享)
            String vidStr = url.substring(url.lastIndexOf("/") + 1);

            Long vid = ObjectUtils.toLong(vidStr, 0L);
//            logger.info("WeiBoRequestHandler request " + url + " vid " + vid);
            LogUtils.commonLog("WeiBoRequestHandler request " + url + " vid " + vid);
            if(vid <=0){
                return getFailResp("Not found video data!");
            }

            //查询直播视频
            DBObject program = programService.getLinkCardProgramById(vid);


            if(!checkVideo(program,vid)){
                return getFailResp("Not found video data!");
            }

            RespBody respBody = dbObj2RespBody(program, vid,null);

            return JSON.toJSONString(respBody);
        } catch (Exception e) {
            LogUtils.logError("WeiBoRequestHandler error " + e.getMessage(),e);
            return getFailResp("server error!");
        }
    }

    /**
     * 返回微博分享错误信息，直接用StringBuilder拼接字符串实现
     * {"errcode": "-1","msg": "错误消息"}
     *
     * @param errMsg
     * @return
     */
    private String getFailResp(String errMsg) {
        LogUtils.logError(errMsg);
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"errcode\":\"-1\",");
        sb.append("\"msg\":\"").append(errMsg).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private RespBody dbObj2RespBody(DBObject video, Long vid,String streamUrl) {

        RespBody respBody = new RespBody();

//        String vTitle = ObjectUtils.toString(video.get("vTitle"));
        String vTitle = "乐嗨直播";
        String source = "乐嗨直播";
        String summary = ObjectUtils.toString(video.get("pName"));
        String vPic = ObjectUtils.toString(video.get("picture"));
        String url = ProgramConstants.SHARE_URL+ObjectUtils.toString(video.get("_id"));

        respBody.setDisplayName(vTitle);
        respBody.setUrl(url);

        ImageData image = new ImageData();
        image.setUrl(vPic);
        image.setHeight(200);
        image.setWidth(200);
        respBody.setImage(image);

//        StreamData stream = new StreamData();
//        stream.setUrl(streamUrl);
//        stream.setFormat("m3u8");
//        stream.setDuration((int)(vDuration/1000));

        //todo 待确定部分
//        respBody.setStream(stream);
        CustomData customData = new CustomData();
        customData.setSource(source);
        respBody.setCustomData(customData);
        respBody.setSummary(summary);
//        respBody.setEmbedCode("http://i7.imgs.letv.com/player/swfPlayer.swf?id=" + mvid);
        respBody.setEmbedCode("");

        respBody.setObjectType("video");

        return respBody;

    }

    /**
     * 校验必填字段
     *
     * @param video
     * @return
     */
    private boolean checkVideo(DBObject video,long vid) {

        if (video == null) {
            LogUtils.logError("WeiBoRequestHandler video cant find it! " + vid);
            return false;
        }


        String vTitle = ObjectUtils.toString(video.get("pName"));

        if (StringUtils.isBlank(vTitle)) {
            LogUtils.logError("WeiBoRequestHandler pName is error " + video.get("_id"),null);
            return false;
        }

        String vPic = ObjectUtils.toString(video.get("picture"));

        if (StringUtils.isBlank(vPic)) {
            LogUtils.logError("WeiBoRequestHandler picture is error " + video.get("_id"),null);
            return false;
        }


        return true;
    }

}

