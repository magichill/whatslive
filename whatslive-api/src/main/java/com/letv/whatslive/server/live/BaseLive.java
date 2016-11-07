package com.letv.whatslive.server.live;

import com.letv.openapi.sdk.api.LetvApiClient;
import com.letv.whatslive.server.util.Constant;

/**
 * Created by gaoshan on 15-7-7.
 */
public class BaseLive {

    protected static LetvApiClient letvApiClient;

    protected static String VER = "3.0";

    static {
        letvApiClient = LetvApiClient.getInstance();
        letvApiClient.setApiUrl(Constant.LIVE_CLOUD_URL);
        letvApiClient.setUserid(Constant.LIVE_CLOUD_USER_ID);
        letvApiClient.setSecret(Constant.LIVE_CLOUD_USER_SECRETE);
//        letvApiClient.setUserid("138921");
//        letvApiClient.setSecret("0fd6411bfb394c6301181ead2c7861aa");
        try {
            letvApiClient.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
