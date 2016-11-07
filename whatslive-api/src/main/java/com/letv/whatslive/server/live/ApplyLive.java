package com.letv.whatslive.server.live;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.openapi.sdk.api.LetvApiClient;
import com.letv.openapi.sdk.util.LetvApiUtil;
import com.letv.openapi.sdk.util.OrderRetainingMap;
import com.letv.whatslive.common.httpclient.HttpClientUtil;
import com.letv.whatslive.common.httpclient.HttpFetchResult;
import com.letv.whatslive.common.utils.DateUtils;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.*;

/**
 * Created by gaoshan on 15-7-7.
 */
public class ApplyLive {

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
     * 创建直播活动
     * @param liveName
     * @param startTime  20150729143000  年月日时分秒
     * @param endTime    20150829143000
     * @param desc
     * @author gaoshan
     * @date 2015-08-21
     * mailto:gaoshan@letv.com
     * @return
     */
    public String createLive(String liveName,String startTime,String endTime,String desc) {

        Map<String, String> params = new HashMap<String, String>();
        LogUtils.commonLog("[activityName]:"+liveName+", [description]:"+desc+" [endTime]:"+ endTime);
        params.put("activityName", liveName);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("coverImgUrl", Constant.VIDEO_DEFAULT_PIC);
        params.put("description", StringUtils.isBlank(desc)?liveName:desc);
        params.put("liveNum", "1");
        params.put("codeRateTypes", "10");
        params.put("needRecord", "1");
        params.put("needTimeShift", "0");
        params.put("activityCategory", "001");

        Map<String, String> map = new OrderRetainingMap();
        map.put("method", "letv.cloudlive.activity.create");
        map.put("ver", VER);
        map.put("userid", Constant.LIVE_CLOUD_USER_ID);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        if (params != null) {
            map.putAll(params);
        }
        String sign = LetvApiUtil.digest(map, Constant.LIVE_CLOUD_USER_SECRETE);

        map.put("sign",sign);

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        }
        try {
            Map<String,String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            HttpFetchResult result = HttpClientUtil.requestPost(Constant.LIVE_CLOUD_URL,nvps,headers);
            if(result != null) {
                String response = result.getContent();
                LogUtils.commonLog("create activity statusCode:"+result.getStatus().getStatusCode()+" result:" + response);
                Map data = JSON.parseObject(response, Map.class);
                String activityId = ObjectUtils.toString(data.get("activityId"));
                return activityId;
            }
        } catch (Exception e) {
            LogUtils.logError("fail to create live activity",e);
        }
        return "";
    }

    public void stopLiveActivityById(String activityId){
        Map<String, String> params = new HashMap<String, String>();
        if(StringUtils.isNotBlank(activityId)){
            params.put("activityId", activityId);
        }else{
            return;
        }
        Map<String, String> map = new OrderRetainingMap();
        map.put("method", "letv.cloudlive.activity.stop");
        map.put("ver", VER);
        map.put("userid", Constant.LIVE_CLOUD_USER_ID);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        if (params != null) {
            map.putAll(params);
        }
        String sign = LetvApiUtil.digest(map, Constant.LIVE_CLOUD_USER_SECRETE);
        map.put("sign",sign);

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        }
        try {
            Map<String,String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            HttpFetchResult result = HttpClientUtil.requestPost(Constant.LIVE_CLOUD_URL,nvps,headers);
            LogUtils.commonLog("stop activity,activityId="+activityId+",statusCode:"+result.getStatus().getStatusCode()+", result:"+result.getContent());
        } catch (Exception e) {
            LogUtils.logError("fail to stop  live activity,acivityId = " + activityId,e);
        }

    }

    public String getActivityId(String videoId){
        Map<String,String> params = new HashMap<String, String>();
        params.put("videoId",videoId);
        Map<String, String> map = new OrderRetainingMap();
        map.put("method", "letv.cloudlive.activity.getVodActivityId");
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
            HttpFetchResult result = HttpClientUtil.requestGet(Constant.LIVE_CLOUD_URL+tmp.toString());
            String response = result.getContent();

            LogUtils.commonLog("get videoInfo result:"+response);
            return response.substring(1,response.length()-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Map getActivityStatus(String activityId){
        Map<String,String> params = new HashMap<String, String>();
        params.put("activityId",activityId);
        Map<String, String> map = new OrderRetainingMap();
        map.put("method", "letv.cloudlive.activity.status.get");
        map.put("ver", VER);
        map.put("userid", Constant.LIVE_CLOUD_USER_ID);
//        map.put("userid", "257897");
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        if (params != null) {
            map.putAll(params);
        }
        String sign = LetvApiUtil.digest(map, Constant.LIVE_CLOUD_USER_SECRETE);
//        String sign = LetvApiUtil.digest(map, "541101e0d3321f43d7254a97579e7bcf");
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
            HttpFetchResult result = HttpClientUtil.requestGet(Constant.LIVE_CLOUD_URL+tmp.toString());
            String response = result.getContent();

            Map<String, Object> vMap = JSON.parseObject(response, Map.class);
            LogUtils.commonLog("ActivityId =="+activityId+",get activityStatus result:"+response);
            return vMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map getVideoInfo(String activityId){
        Map<String,String> params = new HashMap<String, String>();
        params.put("activityId",activityId);
        Map<String, String> map = new OrderRetainingMap();
        map.put("method", "letv.cloudlive.activity.getVodInfo");
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
            LogUtils.commonLog("request pushUrl===="+Constant.LIVE_CLOUD_URL+tmp.toString());
            HttpFetchResult result = HttpClientUtil.requestGet(Constant.LIVE_CLOUD_URL+tmp.toString());
            String response = result.getContent();
            LogUtils.commonLog("get videoInfo result:"+response);
            Map<String, String> vMap = JSON.parseObject(response, Map.class);
            Map<String, String> videoInfo = Maps.newHashMap();
            for (Map.Entry<String, String> entry : vMap.entrySet()) {
                videoInfo.put("user_unique", entry.getKey());
                videoInfo.put("video_id", entry.getValue());
            }
            LogUtils.commonLog("success to get videoInfo,activityId =" + activityId);
            return videoInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


//    public String createLive(String liveName,String startTime,String endTime,String desc) {
//        Map<String, String> params = new HashMap<String, String>();
//        LogUtils.commonLog("[activityName]:"+liveName+"  [description]:"+desc+"");
//        params.put("activityName", liveName);
//        params.put("startTime", startTime);
//        params.put("endTime", endTime);
//        params.put("coverImgUrl", Constant.VIDEO_DEFAULT_PIC);
//        params.put("description", StringUtils.isBlank(desc)?liveName:desc);
//        params.put("liveNum", "1");
//        params.put("codeRateTypes", "16,13,10");
//        params.put("needRecord", "1");
//        params.put("needTimeShift", "0");
//        params.put("activityCategory", "001");
//
//
//        try {
//            HttpResponse response = letvApiClient.executePost(
//                    "letv.cloudlive.activity.create", VER, params);
//            if(response != null) {
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    String resContent = LetvApiUtil.getResponseBody(response);
//                    Map data = JSON.parseObject(resContent, Map.class);
//                    String activityId = ObjectUtils.toString(data.get("activityId"));
//                    return activityId;
//                } else {
//                    String resContent = LetvApiUtil.getResponseBody(response);
//                    LogUtils.logError("fail to create live activity,statusCode=" + statusCode);
//                    LogUtils.logError("result == " + resContent);
//                    return null;
//                }
//            }else{
//                LogUtils.logError("create activity result ===null");
//                return null;
//            }
//        } catch (IOException e) {
//            LogUtils.logError("fail to create live activity");
//            return null;
//        }
//    }
//
//    /**
//     * 修改活动信息
//     * @param activityId
//     * @throws IOException
//     */
//    public void modifyLiveActivity(String activityId,String startTime,String endTime) throws IOException {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("startTime", startTime);
//        params.put("endTime", endTime);
//        params.put("activityId", activityId);
//
//        HttpResponse response = letvApiClient.executePost(
//                "letv.cloudlive.activity.modify", VER, params);
//        if(response != null) {
//            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode == 200) {
//                LogUtils.logError("success to modify activity,activityId =" + activityId);
//            } else {
//                LogUtils.logError("fail to modify live activity,acivityId = " + activityId);
//            }
//        }else{
//            LogUtils.logError("fail to modify live activity,acivityId = " + activityId);
//        }
//    }
//
//
//    /**
//     * 关闭直播
//     * @param activityId
//     * @throws IOException
//     */
//    public void stopLiveActivityById(String activityId) throws IOException  {
//        Map<String, String> params = new HashMap<String, String>();
//        if(StringUtils.isNotBlank(activityId)){
//            params.put("activityId", activityId);
//        }else{
//            return;
//        }
//
//        HttpResponse response = letvApiClient.executePost(
//                "letv.cloudlive.activity.stop", VER, params);
//        if(response != null) {
//            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode == 200) {
//                LogUtils.commonLog("success to stop activity,activityId =" + activityId);
//            } else {
//                LogUtils.logError("fail to stop live activity,acivityId = " + activityId);
//            }
//        }else{
//            LogUtils.logError("fail to stop  live activity,acivityId = " + activityId);
//        }
//
//    }
//
//    public Map<String,String> getVideoInfo(String activityId) throws IOException {
//        Map<String,String> params = new HashMap<String, String>();
//        params.put("activityId",activityId);
//
//        HttpResponse response = letvApiClient.executeGet(
//                "letv.cloudlive.activity.getVodInfo", VER, params);
//        if(response != null) {
//            int statusCode = response.getStatusLine().getStatusCode();
//            String resContent = LetvApiUtil.getResponseBody(response);
//            System.out.println(resContent);
//            if (statusCode == 200) {
//                Map<String, String> map = JSON.parseObject(resContent, Map.class);
//                Map<String, String> videoInfo = Maps.newHashMap();
//                for (Map.Entry<String, String> entry : map.entrySet()) {
//                    videoInfo.put("user_unique", entry.getKey());
//                    videoInfo.put("video_id", entry.getValue());
//                }
//                LogUtils.commonLog("success to get videoInfo,activityId =" + activityId);
//                return videoInfo;
//            } else {
//                LogUtils.logError("fail to get videoInfo,acivityId = " + activityId);
//                return null;
//            }
//        }else{
//            LogUtils.logError("fail to get videoInfo,acivityId = " + activityId);
//            return null;
//        }
//    }

    public static void main(String[] args) throws IOException {
//        String res = "{'activityId':'A2015082100044'}";
//        Map data = JSON.parseObject(res,Map.class);
//        String activityId = ObjectUtils.toString(data.get("activityId"));
//        System.out.print(activityId);
        ApplyLive applyLive = new ApplyLive();
        applyLive.getActivityStatus("A2015101500120");
//        applyLive.getActivityId("15497038");
//        applyLive.getVideoInfo("A2015082100044");
//        System.out.println(applyLive.createLive("openApilehi66666","20151023160000","20151023200000","描述信息"));
//        System.out.println(applyLive.createLive("王小然__675正在直播", DateUtils.long2DateTime(ObjectUtils.toLong("2015262316")), DateUtils.long2DateTime(ObjectUtils.toLong("2015271116")), "王小然__675正在直播"));
//        StreamLive streamLive = new StreamLive();
//        System.out.println(streamLive.getPushStreamAddress("A2015102300484"));
//            applyLive.getVideoInfo("A2015082100032");
//            for(int i=0;i<100000;i++) {
//                Map<String, String> map = applyLive.getVideoInfo("A2015082100044");
//                System.out.println(map.get("user_unique"));
//                System.out.println(map.get("video_id"));
//            }
            //{"0c6008e37c":"15449182"}

    }
}
