package com.letv.whatslive.redis;

import com.letv.whatslive.redis.template.AbstractJedisTemplate;

/**
 * Created by wangjian7 on 2015/8/5.
 */
public class JedisDAO {

    private AbstractJedisTemplate jedisWriteTemplate;

    private AbstractJedisTemplate jedisReadTemplate;

    public JedisDAO(AbstractJedisTemplate jedisWriteTemplate, AbstractJedisTemplate jedisReadTemplate){
        this.jedisWriteTemplate = jedisWriteTemplate;
        this.jedisReadTemplate = jedisReadTemplate;
    }
    public AbstractJedisTemplate getJedisWriteTemplate(){
        return jedisWriteTemplate;
    }
    public AbstractJedisTemplate getJedisReadTemplate(){
        return jedisReadTemplate;
    }

}
