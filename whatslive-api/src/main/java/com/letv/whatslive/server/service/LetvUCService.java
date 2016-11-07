package com.letv.whatslive.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.letv.whatslive.common.httpclient.HttpClientUtil;
import com.letv.whatslive.common.httpclient.HttpFetchResult;
import com.letv.whatslive.server.util.LogUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaoshan on 15-8-4.
 */
@Service
public class LetvUCService {

    @Value("${uc.appkey}")
    protected String UC_APPKEY;

//    @Value("${uc.appid}")
//    protected String UC_APPID;//100985775

    @Value("${uc.lehi.appid}")
    protected String UC_LEHI_APPID;//1102445819


    public String thirdAppLogin(String uid, String accessToken, String ip, String act, Integer from) throws Exception {
        String thridUrl = "";
        StringBuilder buffer = new StringBuilder();
        buffer.append("&access_token=").append(accessToken);
        buffer.append("&clientip=").append(ip);
        buffer.append("&dlevel=expand");
        String appkey = UC_LEHI_APPID;
//        if (from != 1) {
//            appkey = UC_APPID;
//        }
        if (act.equals("appsina")) {
            buffer.append("&uid=").append(uid);
            thridUrl = "http://sso.letv.com/oauth/appssosina?plat=" + UC_APPKEY + buffer.toString();

        }else if (act.equals("appqq")) {
            buffer.append("&openid=").append(uid);
            buffer.append("&appkey=").append(appkey);
            thridUrl = "http://sso.letv.com/oauth/appssoqq?plat=" + UC_APPKEY + buffer.toString();
        }else if (act.equals("appweixin")) {
            buffer.append("&openid=").append(uid);
            thridUrl = "http://sso.letv.com/oauth/appssoweixin?plat=" + UC_APPKEY + buffer.toString();
        } else {
            throw new IllegalArgumentException("act is error");
        }
        HttpFetchResult result = HttpClientUtil.requestGet(thridUrl);
        if (result != null) {
            LogUtils.commonLog("thirdUrl: " + thridUrl + " Result " + result.getContent());
        }else{
            return null;
        }
        return result.getContent();
    }

    public String thirdH5Login(String act) {
        String thridUrl = "";
        final String display = "mobile";
        if (act.equals("appsina")) {
            thridUrl = "http://sso.letv.com/oauth/appsina?plat=" + UC_APPKEY + "&display=" + display;
        } else if (act.equals("appqq")) {
            thridUrl = "http://sso.letv.com/oauth/appqq?plat=" + UC_APPKEY + "&display=" + display;
        }
        return thridUrl;
    }


    private String doPost(String uri, List<BasicNameValuePair> nvps) throws Exception {
        nvps.add(new BasicNameValuePair("plat", UC_APPKEY));
        HttpFetchResult httpFetchResult = HttpClientUtil.requestPost(uri, nvps);
        return httpFetchResult.getContent();

    }


    public String getUC_APPKEY() {
        return UC_APPKEY;
    }

}
