package com.letv.whatslive.model.push.message.notice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.letv.whatslive.model.push.message.AbstractPushMessge;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by wangjian7 on 2015/8/14.
 */
@Setter
@Getter
public class IOSNotificationMessge extends AbstractPushMessge {
    private APS aps;
    private Map<String, Object> values;

    public IOSNotificationMessge(String title, String sound, Integer badge, Map<String, Object> values){
        aps = new APS();
        aps.setAlert(title);
        if(sound!=null){
            aps.setSound(sound);
        }
        if(badge!=null){
            aps.setBadge(badge);
        }
        if(null != values){
            this.values = values;
        }
    }

    @Override
    public String toJsonString() {
        JSONObject notification = new JSONObject();
        JSONObject jsonAPS = new JSONObject();
        jsonAPS.put("alert", aps.getAlert());
        jsonAPS.put("sound", aps.getSound()==null?"":aps.getSound());
        jsonAPS.put("badge", aps.getBadge()==null?"":aps.getBadge());
        notification.put("aps", jsonAPS);
        if(null!=values){
            for(String key:values.keySet()){
                notification.put(key,values.get(key));
            }
        }
        return notification.toString();
    }

    @Setter
    @Getter
    class APS{
        private String alert;
        private String sound;
        private Integer badge;
    }
}

