package com.letv.whatslive.redis.factory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

/**
 * JdeisPool静态构造工厂
 * Created by wangjian7 on 2015/8/25.
 */
public class JedisPoolFactory {
    /**
     * 根据传入的参数不同调用不同的构造方法返回Jedis对象
     * @param poolConfig
     * @param host
     * @param port
     * @param timeout
     * @param password
     * @return
     */
    public static JedisPool getJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password){
        if(StringUtils.isBlank(password)){
            return new JedisPool(poolConfig, host, port, timeout);
        }else{
            return new JedisPool(poolConfig, host, port, timeout, password);
        }
    }
}
