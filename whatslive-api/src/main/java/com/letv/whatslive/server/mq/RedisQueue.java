package com.letv.whatslive.server.mq;

import com.letv.whatslive.redis.JedisDAO;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by gaoshan on 15-9-6.
 */
@Component
public class RedisQueue<T> implements InitializingBean,DisposableBean {

    @Autowired
    private JedisDAO jedisDAO;
    private String key;
    private int cap = Short.MAX_VALUE;//最大阻塞的容量，超过容量将会导致清空旧数据


    private Lock lock = new ReentrantLock();//基于底层IO阻塞考虑

    @Autowired
    private RedisQueueListener listener;//异步回调
    private Thread listenerThread;

    private boolean isClosed;



    public void setListener(RedisQueueListener listener) {
        this.listener = listener;
    }

    public void setKey(String key) {
        this.key = key;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if(listener != null){
            listenerThread = new ListenerThread();
            listenerThread.setDaemon(true);
            listenerThread.start();
        }
    }

    /**
     * 从队列的头，插入
     */
    public void pushFromHead(String value){
        jedisDAO.getJedisWriteTemplate().lpush(key,value);
    }


    /**
     * noblocking
     * @return null if no item in queue
     */
    public String removeFromHead(){
        return jedisDAO.getJedisWriteTemplate().rpop(key);
    }


    /**
     * blocking
     * remove and get first item from queue:BLPOP
     * @return
     */
    public String takeFromHead(int timeout) throws InterruptedException{
        lock.lockInterruptibly();
        try{
            String result = jedisDAO.getJedisWriteTemplate().rpop(key);
            return result;
        }finally{
            lock.unlock();
        }
    }

    public String takeFromHead() throws InterruptedException{
        return takeFromHead(0);
    }

    @Override
    public void destroy() throws Exception {
        if(isClosed){
            return;
        }
        shutdown();
    }

    private void shutdown(){
        try{
            listenerThread.interrupt();
        }catch(Exception e){
            //
        }
    }

    class ListenerThread extends Thread {

        @Override
        public void run(){
            try{
                while(true){
                    String value = takeFromHead();//cast exception? you should check.
                    //逐个执行
                    if(value != null){
                        try{
                            Thread.sleep(5000);
                            listener.onMessage(value);
                        }catch(Exception e){
                            //
                        }
                    }
                }
            }catch(InterruptedException e){
                //
            }
        }
    }

    public static void main(String[] args) throws Exception{
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        RedisQueue<String> redisQueue = (RedisQueue)context.getBean("redisQueue");
        redisQueue.pushFromHead("test:app");
        Thread.sleep(15000);
        redisQueue.pushFromHead("test:app");
        Thread.sleep(15000);
        redisQueue.destroy();
    }

}
