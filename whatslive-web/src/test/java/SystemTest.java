import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.web.util.util.MD5Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjian7 on 2015/8/22.
 */
public class SystemTest {
    public static void main(String[] args){
        System.getProperty("java.library.path");
        System.out.println(MD5Util.md5("whatslive@admin"));
        System.out.println( ObjectUtils.toInteger("-1"));
        List<Integer> a = new ArrayList<Integer>();
        for(int i =0 ;i<26; i++){
            a.add(i);
        }
        System.out.println(getProgramListByActId(a,4,10));
    }
    public static List<Integer> getProgramListByActId(List<Integer> list, Integer start,Integer limit){
        if(list.size()<(start-1)*limit){
            list.clear();
            return list;
        }else if(list.size()<start*limit){
            return list.subList((start-1)*limit, list.size());
        }else{
            return list.subList((start-1)*limit, start*limit);
        }
    }
}
