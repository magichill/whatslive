package com.letv.whatslive.website.service.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.letv.whatslive.common.httpclient.HttpClientUtil;
import com.letv.whatslive.common.httpclient.HttpFetchResult;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.website.util.String.StringUtils;
import com.letv.whatslive.website.util.configuration.PropertyGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by haojiayao on 2015/9/11.
 */
@Component
public class WeixinSignService {

    private static final Logger logger = LoggerFactory.getLogger(WeixinSignService.class);

    @Autowired
    private JedisDAO jedisDAO;

    /**
     * 获取微信签名信息
     *
     * @param url URL
     *
     * @return
     */
    public Map<String, String> getSignature(String url) {

        // 获取微信JS接口的临时票据
        String jsapiTicket = getJsapiTicket();

        Map<String, String> ret = new HashMap<String, String>();
        // 随机字符串
        String nonceStr = createNonceStr();
        // 时间戳
        String timestamp = createTimestamp();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapiTicket +
                "&noncestr=" + nonceStr +
                "&timestamp=" + timestamp +
                "&url=" + url;

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {

            logger.error("NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {

            logger.error("UnsupportedEncodingException", e);
        }

        // 生成签名的随机串
        ret.put("nonceStr", nonceStr);
        // 生成签名的时间戳
        ret.put("timestamp", timestamp);
        // 签名
        ret.put("signature", signature);

        return ret;
    }

    /**
     * 获取微信JS接口的临时票据
     *
     * @return
     */
    public String getJsapiTicket() {

        // 临时票据
        String jsapiTicket = "";

        // 从缓存中取得临时票据
        jsapiTicket = jedisDAO.getJedisReadTemplate().get("jsapiTicket");

        // 当临时票据不存在或者失效时，重新获取临时票据并存入缓存
        if (StringUtils.isBlank(jsapiTicket)) {

            // 获取公众号的全局唯一票据
            String accessToken = getAccessTokent();

            String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="
                    + accessToken + "&type=jsapi";
            try {

                HttpFetchResult result = HttpClientUtil.requestGet(url);
                String response = result.getContent();
                JSONObject jsonObject = JSON.parseObject(response);
                String errcode = jsonObject.getString("errcode");

                // 成功获取时
                if ("0".equals(errcode)) {

                    jsapiTicket = jsonObject.getString("ticket");

                    // 设定超时时间
                    int expiresIn = Integer.valueOf(jsonObject.getString("expires_in")) - 60;
                    jedisDAO.getJedisWriteTemplate().setex("jsapiTicket", jsapiTicket, expiresIn);
                } else {

                    String errorMsg = jsonObject.getString("errmsg");
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("getJsapiTicket: ").append(errorMsg);
                    logger.error(buffer.toString());
                }
            } catch (Exception e) {

                logger.error("getJsapiTicket Exception", e);
            }
        }

        return jsapiTicket;
    }

    /**
     * 获取公众号的全局唯一票据
     *
     * @return accessToken 公众号的全局唯一票据
     */
    public String getAccessTokent() {

        // 全局唯一票据
        String accessToken = "";
        // 从缓存中取得全局唯一票据
        accessToken = jedisDAO.getJedisReadTemplate().get("accessToken");

        // 当全局唯一票据不存在或者失效时，重新获取全局唯一票据并存入缓存
        if (StringUtils.isBlank(accessToken)) {

            // 公众号的唯一标识
            String appid = PropertyGetter.getString("appid");
            // 用户唯一凭证密钥
            String secret = PropertyGetter.getString("secret");
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                    + appid + "&secret=" + secret;
            try {

                HttpFetchResult result = HttpClientUtil.requestGet(url);
                String response = result.getContent();
                JSONObject jsonObject = JSON.parseObject(response);
                String errcode = jsonObject.getString("errcode");

                // 成功获取时
                if (StringUtils.isBlank(errcode)) {

                    accessToken = jsonObject.getString("access_token");
                    int expiresIn = Integer.valueOf(jsonObject.getString("expires_in")) - 60;
                    jedisDAO.getJedisWriteTemplate().setex("accessToken", accessToken, expiresIn);
                } else {

                    String errorMsg = jsonObject.getString("errmsg");
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("appid: ").append(errorMsg);
                    logger.error(buffer.toString());
                }
            } catch (Exception e) {

                logger.error("getAccessTokent Exception", e);
            }
        }

        return accessToken;
    }

    private static String byteToHex(final byte[] hash) {

        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * 获取随机字符串
     *
     * @return accessToken 公众号的全局唯一票据
     */
    private static String createNonceStr() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取当前时间戳
     *
     * @return accessToken 公众号的全局唯一票据
     */
    private static String createTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
