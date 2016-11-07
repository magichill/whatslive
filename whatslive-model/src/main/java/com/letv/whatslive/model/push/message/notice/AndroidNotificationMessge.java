package com.letv.whatslive.model.push.message.notice;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.letv.whatslive.model.push.message.AbstractPushMessge;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by wangjian7 on 2015/8/14.
 */
@Setter
@Getter
public class AndroidNotificationMessge extends AbstractPushMessge {
    private String title; //通知标题，可以为空；如果为空则设为appid对应的应用名;
    private String description; //通知文本内容，不能为空;
    private int notification_builder_id = 0; //android客户端自定义通知样式，如果没有设置默认为0;
    private int notification_basic_style = 0; //只有notification_builder_id为0时有效，可以设置通知的基本样式包括(响铃：0x04;振动：0x02;可清除：0x01;),这是一个flag整形，每一位代表一种样式;
    private int open_type = 3; //点击通知后的行为(1：打开Url; 2：自定义行为；3：默认打开应用;);
    private String url; //需要打开的Url地址，open_type为1时才有效;
    private String pkg_content; //pen_type为2时才有效，Android端SDK会把pkg_content字符串转换成Android Intent,通过该Intent打开对应app组件，所以pkg_content字符串格式必须遵循Intent uri格式，最简单的方法可以通过Intent方法toURI()获取
    private JSONObject custom_content; //自定义内容，键值对，Json对象形式(可选)；在android客户端，这些键值对将以Intent中的extra进行传递。




    public AndroidNotificationMessge(String title, String description, JSONObject custom_content){
        if(StringUtils.isEmpty(description)){
            throw new InstantiationError("description must be not null!");
        }
        this.title = title;
        this.description = description;
        this.custom_content = custom_content;

    }

    public AndroidNotificationMessge(String json){
        JSONObject jsonObject = JSON.parseObject(json);
        this.title = jsonObject.getString("title");
        this.description = jsonObject.getString("description");
        this.notification_builder_id = jsonObject.getIntValue("notification_builder_id");
        this.notification_basic_style = jsonObject.getIntValue("notification_basic_style");
        this.open_type = jsonObject.getInteger("open_type")==null?0:jsonObject.getInteger("open_type");
        this.url = jsonObject.getString("url");
        this.pkg_content = jsonObject.getString("pkg_content");
        this.custom_content = jsonObject.getJSONObject("custom_content");
    }

    @Override
    public String toJsonString(){
        return JSON.toJSONString(this);
    }

}
