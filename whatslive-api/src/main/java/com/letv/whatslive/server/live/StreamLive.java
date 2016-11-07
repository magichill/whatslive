package com.letv.whatslive.server.live;

import com.alibaba.fastjson.JSON;
//import com.letv.openapi.sdk.util.LetvApiUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.letv.openapi.sdk.api.LetvApiClient;
import com.letv.openapi.sdk.util.LetvApiUtil;
import com.letv.openapi.sdk.util.OrderRetainingMap;
import com.letv.whatslive.common.httpclient.HttpClientUtil;
import com.letv.whatslive.common.httpclient.HttpFetchResult;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.*;

/**
 * Created by gaoshan on 15-7-8.
 */
public class StreamLive {

//    protected static LetvApiClient letvApiClient;
//
    protected static String VER = "3.0";
//
//    static {
//        letvApiClient = LetvApiClient.getInstance();
//        letvApiClient.setApiUrl(Constant.LIVE_CLOUD_URL);
//        letvApiClient.setUserid(Constant.LIVE_CLOUD_USER_ID);
//        letvApiClient.setSecret(Constant.LIVE_CLOUD_USER_SECRETE);
//        try {
//            letvApiClient.initialize();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    /**
    * 获取播放地址
    * @param activityId
    * @throws IOException
    */
//    public String getPlayAddress(String activityId) throws IOException{
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("activityId", activityId);
//        HttpResponse response = letvApiClient.executeGet(
//                "letv.cloudlive.activity.getPlayAddr", VER, params);
//        String playRes = LetvApiUtil.getResponseBody(response);
//        Map playData = JSON.parseObject(playRes,Map.class);
//        String playUrl = ObjectUtils.toString(playData.get("playPageUrl"));
//        return playUrl;
//    }


    /**
     * 获取推流地址
     * @param activityId
     * @throws IOException
     */
    public String getPushStreamAddress(String activityId) {
        Map<String,String> params = new HashMap<String, String>();
        params.put("activityId",activityId);
        Map<String, String> map = new OrderRetainingMap();
        map.put("method", "letv.cloudlive.activity.getPushUrl");
        map.put("ver", VER);
        map.put("userid", Constant.LIVE_CLOUD_USER_ID);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        if (params != null) {
            map.putAll(params);
        }
        String sign = LetvApiUtil.digest(map, Constant.LIVE_CLOUD_USER_SECRETE);
        map.put("sign",sign);

        try {
            StringBuilder tmp = new StringBuilder();
            if (params != null && params.size() > 0) {
                tmp.append("?");
                Set<Map.Entry<String, String>> entries = map.entrySet();
                for (Iterator<Map.Entry<String, String>> iterator = entries.iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, String> entry = iterator.next();
                    tmp.append(entry.getKey());
                    tmp.append("=");
                    try {
                        tmp.append(java.net.URLEncoder.encode(entry.getValue(), "UTF-8"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    if (iterator.hasNext()) {
                        tmp.append("&");
                    }
                }
            }
            LogUtils.commonLog("pushUrl==="+Constant.LIVE_CLOUD_URL+tmp.toString());
            HttpFetchResult result = HttpClientUtil.requestGet(Constant.LIVE_CLOUD_URL+tmp.toString());
            String response = result.getContent();

            LogUtils.commonLog("request push url statusCode:"+result.getStatus().getStatusCode()+",result:"+response);
            JSONObject pushData = JSON.parseObject(response);
            String lives = pushData.getString("lives");
            List liveList = JSON.parseObject(lives,List.class);
            String live = ObjectUtils.toString(liveList.get(0));
            JSONObject json = JSON.parseObject(live);
            String pushUrl = json.getString("pushUrl");
            return pushUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public String getPushStreamAddress(String activityId) throws IOException{
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("activityId", activityId);
//        LogUtils.commonLog("获取推流地址，activityId = "+activityId);
//        HttpResponse response = letvApiClient.executeGet(
//                "letv.cloudlive.activity.getPushUrl", VER, params);
//        String pushRes = LetvApiUtil.getResponseBody(response);
//        JSONObject pushData = JSON.parseObject(pushRes);
//        String lives = pushData.getString("lives");
//        List liveList = JSON.parseObject(lives,List.class);
//        String live = ObjectUtils.toString(liveList.get(0));
//        JSONObject json = JSON.parseObject(live);
//        String pushUrl = json.getString("pushUrl");
//        return pushUrl;
//    }

    public static void main(String[] args) throws IOException {
//        String activityId = "A2015082100021";
//        StreamLive streamLive = new StreamLive();
//        try {
//            /**
//             *返回示例：推流地址 {"liveNum":1,"lives":[{"machine":"1","pushUrl":"rtmp://w.gslb.lecloud.com/live/20150821300002916?sign=42349c77dccc7585e09252f43bca7eb9&tm=20150821122654","status":"0"}]}
//             * 播放地址：{"playPageUrl":"http://t.cn/RLkznps"}
//             */
//            String pushRes = streamLive.getPushStreamAddress(activityId);
//            System.out.println(pushRes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
          StreamLive streamLive = new StreamLive();
//          for(int i=0;i<1000;i++) {
              System.out.println(streamLive.getPushStreamAddress("A2015082700027"));
//          }
//          Map<String, String> params = new HashMap<String, String>();
//          params.put("activityId", "A2015082100044");
//
//          HttpResponse response = letvApiClient.executeGet(
//                    "letv.cloudlive.activity.search", VER, params);
//          System.out.println("" + response.getStatusLine().getStatusCode());
//          System.out.println(LetvApiUtil.getResponseBody(response));
    }
}
