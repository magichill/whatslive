package com.letv.whatslive.server.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.httpclient.HttpClientUtil;
import com.letv.whatslive.common.httpclient.HttpFetchResult;
import com.letv.whatslive.common.utils.MD5Utils;
import com.letv.whatslive.common.utils.ObjectUtils;

import java.util.*;

/**
 * Created by gaoshan on 15-8-21.
 */
public class CloudUtils {

    public Map<String,String> getVideoInfo(String videoId,String uuid){
        Map<String,String> params = Maps.newHashMap();
        params.put("api","video.get");
        params.put("timestamp", ObjectUtils.toString(System.currentTimeMillis()));
        params.put("user_unique", uuid);
        params.put("format","json");
        params.put("ver","2.0");
        params.put("video_id",videoId);
        String sign = generateSign(params);
        params.put("sign",sign);
        String videoUrl = Constant.LETV_CLOUD_OPEN_URL+"?"+generateParam(params);
        try {
            HttpFetchResult result = HttpClientUtil.requestGet(videoUrl);
            String response = result.getContent();
            JSONObject resMap = JSON.parseObject(response);
            if(resMap.getIntValue("code")==0){
                String data = resMap.getString("data");
                JSONObject json = JSON.parseObject(data);
                int status = json.getIntValue("status");
                if(status==10){
                    LogUtils.commonLog("视频处理成功，videoId="+videoId);
                    Map<String,String> resultMap = Maps.newHashMap();
                    resultMap.put("video_unique",json.getString("video_unique"));
                    resultMap.put("duration",json.getString("video_duration"));
                    return resultMap;
                }else if(status == 30){
                    LogUtils.commonLog("视频正在处理中....，videoId="+videoId);
                }else{
                    LogUtils.commonLog("视频处理失败....，videoId="+videoId);
                }
            }else{
                LogUtils.logError(ObjectUtils.toString(resMap.get("message")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.logError("[get video info from open cloud]:videoId=="+videoId);
        }
        return null;
    }

    public static String generateParam(Map<String,String> params){
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()+"="+entry.getValue()+"&");
        }
        LogUtils.commonLog("generate params=="+sb.toString());
        return sb.toString();
    }

    public static String generateSign(Map<String,String> params){
        StringBuffer sb = new StringBuffer();
        Map<String,String> resultMap = sortMapByKey(params);
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            sb.append(entry.getKey()+entry.getValue());
        }
        LogUtils.commonLog("generate sign==="+sb.toString());
        return MD5Utils.md5(sb.toString()+Constant.LIVE_CLOUD_USER_SECRETE);
    }

    public static String generateCheckSign(Map<String,String> params){
        StringBuffer sb = new StringBuffer();
        params.put("user_unique",Constant.USER_UNIQUE);
        Map<String,String> resultMap = sortMapByKey(params);
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            sb.append(entry.getKey()+entry.getValue());
        }
        LogUtils.commonLog("generate check sign==="+sb.toString());
        return MD5Utils.md5(sb.toString()+Constant.LIVE_CLOUD_USER_SECRETE);
    }




    /**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, String> sortMap = new TreeMap<String, String>(
                new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });

        sortMap.putAll(map);
        return sortMap;
    }

    public static void main(String[] args){
        CloudUtils cloudUtils = new CloudUtils();

        System.out.println(cloudUtils.getVideoInfo("15448103","cd5f283012"));
//        System.out.println(cloudUtils.getVideoInfo("15448451","0c6008e37c"));
//        System.out.println(cloudUtils.getVideoInfo("15449182","0c6008e37c"));
    }
}
