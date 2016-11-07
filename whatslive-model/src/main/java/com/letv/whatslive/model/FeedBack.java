package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-8-31.
 */
@Getter
@Setter
public class FeedBack {

    private Long id;
    private String programId;
    private Long userId;
    private String imei;
    private String phone;
    private String feedback;
    private Long lastGetPlayUrlTime;
    private Long lastPlayTime;
    private Long lastTotalPlayTime;
    private String ip;
    private String network;

}
