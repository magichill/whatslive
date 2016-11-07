package com.letv.whatslive.server.util;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Utility class for turning page.
 * Created by gaoshan on 15-2-9.
 * mailto:gaoshan@letv.com
 */
public class PageBean<T> {


    public List<T> page(int start,int limit,List<T> list){
        int length = list.size();
        int index = start -1;
        if(length >= start*limit){
            return list.subList(index*limit,start*limit);
        }else{
            if(index == 0){
                return list;
            }else{
                if(index*limit >= length){
                    return null;
                }
                return list.subList(index*limit,length);
            }
        }
    }

    public List next(){

        return null;
    }

    public List previous(){

        return null;
    }

    public static void main(String[] args){
        List<String> list = Lists.newArrayList();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        PageBean<String> pageBean = new PageBean<String>();
        List<String> page = pageBean.page(2,5,list);
        System.out.println(page==null?0:page.size());
    }
}
