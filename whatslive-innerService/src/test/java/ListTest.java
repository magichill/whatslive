import com.google.common.collect.Maps;
import com.letv.whatslive.model.push.message.notice.IOSNotificationMessge;
import com.letv.whatslive.model.redis.push.PushEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/8/17.
 */
public class ListTest {
    public static void main(String[] args){
//        List<Integer> a = new ArrayList<Integer>(){};
//        a.add(new Integer(1));
//        a.add(new Integer(2));
//        a.add(new Integer(3));
//        a.add(new Integer(4));
//        a.add(new Integer(5));
//
//        List<Integer> b = new ArrayList<Integer>(){};
//        b.add(new Integer(3));
//        b.add(new Integer(4));
//        b.add(new Integer(6));
//        System.out.println(a.addAll(b));
//
//        a.subList(3,100);
//        PushEvent pushEvent = new PushEvent();
//        pushEvent.setAction(1);
//        pushEvent.setProgramId(111111111L);
//        System.out.println(pushEvent.toString());

        Map<String, Object> values = Maps.newHashMap();
        values.put("tag",1);
        IOSNotificationMessge iosMessage = new IOSNotificationMessge("<"+"测试"+">即将开始!", null, 1, values);
        System.out.println(iosMessage.toJsonString());
        System.out.println(new Date(1441183022535L));

    }
}
