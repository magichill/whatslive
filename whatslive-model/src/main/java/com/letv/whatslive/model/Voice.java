package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by zoran on 15-8-21.
 */
@Getter
@Setter
public class Voice {



    private String time;

    private byte[] voice;

    private long createTime;


    public static Voice newVoice(String time,byte[] voice,long createTime){
        Voice obj = new Voice();
        obj.setCreateTime(createTime);
        obj.setTime(time);
        obj.setVoice(voice);
        return obj;
    }


}
