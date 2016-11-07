package com.letv.whatslive.redis.template;

import com.letv.whatslive.redis.utils.JedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JedisTemplate 提供了一个template方法，负责对Jedis中连接的获取与归还。用于Jedis集群的操作
 * JedisAction<T> 和 JedisActionNoResult两种回调接口，适用于有无返回值两种情况。
 * <p/>
 * 同时提供一些JedisOperation中定义的 最常用函数的封装, 如get/set/zadd等。
 */
public class JedisClusterTemplate extends AbstractJedisTemplate{

    private static Logger logger = LoggerFactory.getLogger(JedisClusterTemplate.class);

    private JedisCluster jedisCluster;

    public JedisClusterTemplate(JedisCluster jedisCluster){
        this.jedisCluster = jedisCluster;
    }

    /**
     * Callback interface for template.
     */
    public interface JedisAction<T> {
        T action(JedisCluster jedisCluster);
    }

    /**
     * Callback interface for template without result.
     */
    public interface JedisActionNoResult {
        void action(JedisCluster jedisCluster);
    }

    /**
     * Execute with a call back action with result.
     */
    public <T> T execute(JedisAction<T> jedisAction) throws JedisException {
        boolean broken = false;
        try {
            return jedisAction.action(jedisCluster);
        } catch (JedisException e) {
            broken = handleJedisException(e);
            throw e;
        } finally {
            closeResource(jedisCluster, broken);
        }
    }

    /**
     * Execute with a call back action without result.
     */
    public void execute(JedisActionNoResult jedisAction) throws JedisException {
        boolean broken = false;
        try {
            jedisAction.action(jedisCluster);
        } catch (JedisException e) {
            broken = handleJedisException(e);
            throw e;
        } finally {
            closeResource(jedisCluster, broken);
        }
    }

    /**
     * Handle jedisException, write log and return whether the connection is broken.
     */
    protected boolean handleJedisException(JedisException jedisException) {
        if (jedisException instanceof JedisConnectionException) {
            //			logger.error("Redis connection " + jedisPool.getAddress() + " lost.", jedisException);
        } else if (jedisException instanceof JedisDataException) {
            if ((jedisException.getMessage() != null) && (
                    jedisException.getMessage().indexOf("READONLY") != -1)) {
                //				logger.error("Redis connection " + jedisPool.getAddress() + " are read-only slave.", jedisException);
            } else {
                // dataException, isBroken=false
                return false;
            }
        } else {
            logger.error("Jedis exception happen.", jedisException);
        }
        return true;
    }

    /**
     * Return jedis connection to the pool, call different return methods depends on the conectionBroken status.
     */
    protected void closeResource(JedisCluster jedisCluster, boolean conectionBroken) {
        /*try {
            if (conectionBroken) {
                jedisCluster.close();
            } else {
                jedisPool.returnResource(jedisCluster);
            }
        } catch (Exception e) {
            logger.error("return back jedis failed, will fore close the jedis.", e);
            JedisUtils.destroyJedis(jedis);
        }*/

    }

    // / Common Actions ///

    /**
     * Remove the specified keys. If a given key does not exist no operation is
     * performed for this key.
     * <p/>
     * return false if one of the key is not exist.
     */
    public Boolean del(final String key) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(JedisCluster jedisCluster) {
                return jedisCluster.del(key) == 1 ? true : false;
            }
        });
    }

    public Boolean del(final String... keys){
        return execute(new JedisAction<Boolean>() {
            @Override
            public Boolean action(JedisCluster jedisCluster) {
                Boolean result = true;
                for(String k : keys){
                    if(jedisCluster.del(k) == 1){
                        continue;
                    }else{
                        result = false;
                        break;
                    }
                }
                return result;
            }
        });
    }

    public void flushDB() {
        execute(new JedisActionNoResult() {

            @Override
            public void action(JedisCluster jedisCluster) {
                jedisCluster.flushDB();
            }
        });
    }

    // / String Actions ///

    /**
     * Get the value of the specified key. If the key does not exist null is
     * returned. If the value stored at key is not a string an error is returned
     * because GET can only handle string values.
     */
    public String get(final String key) {
        return execute(new JedisAction<String>() {

            @Override
            public String action(JedisCluster jedisCluster) {
                return jedisCluster.get(key);
            }
        });
    }

    /**
     * Get the value of the specified key as Long.If the key does not exist null is returned.
     */
    public Long getAsLong(final String key) {
        String result = get(key);
        return result != null ? Long.valueOf(result) : null;
    }

    /**
     * Get the value of the specified key as Integer.If the key does not exist null is returned.
     */
    public Integer getAsInt(final String key) {
        String result = get(key);
        return result != null ? Integer.valueOf(result) : null;
    }

    @Override
    public List<String> mget(String... keys) {
        return null;
    }

    /**
     * Set the string value as value of the key.
     * The string can't be longer than 1073741824 bytes (1 GB).
     */
    public void set(final String key, final String value) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(JedisCluster jedisCluster) {
                jedisCluster.set(key, value);
            }
        });
    }

    /**
     * The command is exactly equivalent to the following group of commands: {@link #set(String, String) SET} +
     * {@link #expire(String, int) EXPIRE}.
     * The operation is atomic.
     */
    public void setex(final String key, final String value, final int seconds) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(JedisCluster jedisCluster) {
                jedisCluster.setex(key, seconds, value);
            }
        });
    }

    /**
     * SETNX works exactly like {@link #setNX(String, String) SET} with the only
     * difference that if the key already exists no operation is performed.
     * SETNX actually means "SET if Not eXists".
     * <p/>
     * return true if the key was set.
     */
    public Boolean setnx(final String key, final String value) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(JedisCluster jedisCluster) {
                return jedisCluster.setnx(key, value) == 1 ? true : false;
            }
        });
    }

    /**
     * The command is exactly equivalent to the following group of commands: {@link #setex(String, String, int) SETEX} +
     * {@link #sexnx(String, String) SETNX}.
     * The operation is atomic.
     */
    public Boolean setnxex(final String key, final String value, final int seconds) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(JedisCluster jedisCluster) {
                String result = jedisCluster.set(key, value, "NX", "EX", seconds);
                return JedisUtils.isStatusOk(result);
            }
        });
    }

    /**
     * GETSET is an atomic set this value and return the old value command. Set
     * key to the string value and return the old value stored at key. The
     * string can't be longer than 1073741824 bytes (1 GB).
     */
    public String getSet(final String key, final String value) {
        return execute(new JedisAction<String>() {

            @Override
            public String action(JedisCluster jedisCluster) {
                return jedisCluster.getSet(key, value);
            }
        });
    }

    /**
     * Increment the number stored at key by one. If the key does not exist or
     * contains a value of a wrong type, set the key to the value of "0" before
     * to perform the increment operation.
     * <p/>
     * INCR commands are limited to 64 bit signed integers.
     * <p/>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types. Simply the string
     * stored at the key is parsed as a base 10 64 bit signed integer, incremented, and then converted back as a string.
     *
     * @return Integer reply, this commands will reply with the new value of key
     * after the increment.
     */
    public Long incr(final String key) {
        return execute(new JedisAction<Long>() {
            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.incr(key);
            }
        });
    }

    public Long incrBy(final String key, final long increment) {
        return execute(new JedisAction<Long>() {
            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.incrBy(key, increment);
            }
        });
    }

    @Override
    public Double incrByFloat(String key, double increment) {
        return null;
    }

    /**
     * Decrement the number stored at key by one. If the key does not exist or
     * contains a value of a wrong type, set the key to the value of "0" before
     * to perform the decrement operation.
     */
    public Long decr(final String key) {
        return execute(new JedisAction<Long>() {
            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.decr(key);
            }
        });
    }

    public Long decrBy(final String key, final long decrement) {
        return execute(new JedisAction<Long>() {
            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.decrBy(key, decrement);
            }
        });
    }

    // / Hash Actions ///

    /**
     * If key holds a hash, retrieve the value associated to the specified
     * field.
     * <p/>
     * If the field is not found or the key does not exist, a special 'nil' value is returned.
     */
    public String hget(final String key, final String fieldName) {
        return execute(new JedisAction<String>() {
            @Override
            public String action(JedisCluster jedisCluster) {
                return jedisCluster.hget(key, fieldName);
            }
        });
    }

    public List<String> hmget(final String key, final String... fieldsNames) {
        return execute(new JedisAction<List<String>>() {
            @Override
            public List<String> action(JedisCluster jedisCluster) {
                return jedisCluster.hmget(key, fieldsNames);
            }
        });
    }

    public Map<String, String> hgetAll(final String key) {
        return execute(new JedisAction<Map<String, String>>() {
            @Override
            public Map<String, String> action(JedisCluster jedisCluster) {
                return jedisCluster.hgetAll(key);
            }
        });
    }

    public void hset(final String key, final String fieldName, final String value) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(JedisCluster jedisCluster) {
                jedisCluster.hset(key, fieldName, value);
            }
        });
    }

    public void hmset(final String key, final Map<String, String> map) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(JedisCluster jedisCluster) {
                jedisCluster.hmset(key, map);
            }
        });

    }

    public Boolean hsetnx(final String key, final String fieldName, final String value) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(JedisCluster jedisCluster) {
                return jedisCluster.hsetnx(key, fieldName, value) == 1 ? true : false;
            }
        });
    }

    public Long hincrBy(final String key, final String fieldName, final long increment) {
        return execute(new JedisAction<Long>() {
            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.hincrBy(key, fieldName, increment);
            }
        });
    }

    @Override
    public Double hincrByFloat(String key, String fieldName, double increment) {
        return null;
    }

    public Long hdel(final String key, final String... fieldsNames) {
        return execute(new JedisAction<Long>() {
            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.hdel(key, fieldsNames);
            }
        });
    }

    public Boolean hexists(final String key, final String fieldName) {
        return execute(new JedisAction<Boolean>() {
            @Override
            public Boolean action(JedisCluster jedisCluster) {
                return jedisCluster.hexists(key, fieldName);
            }
        });
    }

    public Set<String> hkeys(final String key) {
        return execute(new JedisAction<Set<String>>() {
            @Override
            public Set<String> action(JedisCluster jedisCluster) {
                return jedisCluster.hkeys(key);
            }
        });
    }

    public Long hlen(final String key) {
        return execute(new JedisAction<Long>() {
            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.hlen(key);
            }
        });
    }

    // / List Actions ///

    public Long lpush(final String key, final String... values) {
        return execute(new JedisAction<Long>() {
            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.lpush(key, values);
            }
        });
    }

    public String rpop(final String key) {
        return execute(new JedisAction<String>() {

            @Override
            public String action(JedisCluster jedisCluster) {
                return jedisCluster.rpop(key);
            }
        });
    }

    public String brpop(final String key) {
        return execute(new JedisAction<String>() {

            @Override
            public String action(JedisCluster jedisCluster) {
                List<String> nameValuePair = jedisCluster.brpop(key);
                if (nameValuePair != null) {
                    return nameValuePair.get(1);
                } else {
                    return null;
                }
            }
        });
    }

    public String brpop(final int timeout, final String key) {
        return execute(new JedisAction<String>() {

            @Override
            public String action(JedisCluster jedisCluster) {
                List<String> nameValuePair = jedisCluster.brpop(timeout, key);
                if (nameValuePair != null) {
                    return nameValuePair.get(1);
                } else {
                    return null;
                }
            }
        });
    }

    @Override
    public String rpoplpush(String sourceKey, String destinationKey) {
        return null;
    }

    @Override
    public String brpoplpush(String source, String destination, int timeout) {
        return null;
    }

    public Long llen(final String key) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.llen(key);
            }
        });
    }

    public String lindex(final String key, final long index) {
        return execute(new JedisAction<String>() {

            @Override
            public String action(JedisCluster jedisCluster) {
                return jedisCluster.lindex(key, index);
            }
        });
    }

    public List<String> lrange(final String key, final int start, final int end) {
        return execute(new JedisAction<List<String>>() {

            @Override
            public List<String> action(JedisCluster jedisCluster) {
                return jedisCluster.lrange(key, start, end);
            }
        });
    }

    public void ltrim(final String key, final int start, final int end) {
        execute(new JedisActionNoResult() {
            @Override
            public void action(JedisCluster jedisCluster) {
                jedisCluster.ltrim(key, start, end);
            }
        });
    }

    public void ltrimFromLeft(final String key, final int size) {
        execute(new JedisActionNoResult() {
            @Override
            public void action(JedisCluster jedisCluster) {
                jedisCluster.ltrim(key, 0, size - 1);
            }
        });
    }

    public Boolean lremFirst(final String key, final String value) {
        return execute(new JedisAction<Boolean>() {
            @Override
            public Boolean action(JedisCluster jedisCluster) {
                Long count = jedisCluster.lrem(key, 1, value);
                return (count == 1);
            }
        });
    }

    public Boolean lremAll(final String key, final String value) {
        return execute(new JedisAction<Boolean>() {
            @Override
            public Boolean action(JedisCluster jedisCluster) {
                Long count = jedisCluster.lrem(key, 0, value);
                return (count > 0);
            }
        });
    }

    // / Set Actions ///
    public Boolean sadd(final String key, final String member) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(JedisCluster jedisCluster) {
                return jedisCluster.sadd(key, member) == 1 ? true : false;
            }
        });
    }

    public Set<String> smembers(final String key) {
        return execute(new JedisAction<Set<String>>() {

            @Override
            public Set<String> action(JedisCluster jedisCluster) {
                return jedisCluster.smembers(key);
            }
        });
    }

    // / Ordered Set Actions ///

    /**
     * return true for add new element, false for only update the score.
     */
    public Boolean zadd(final String key, final double score, final String member) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(JedisCluster jedisCluster) {
                return jedisCluster.zadd(key, score, member) == 1 ? true : false;
            }
        });
    }

    public Double zscore(final String key, final String member) {
        return execute(new JedisAction<Double>() {

            @Override
            public Double action(JedisCluster jedisCluster) {
                return jedisCluster.zscore(key, member);
            }
        });
    }

    public Long zrank(final String key, final String member) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.zrank(key, member);
            }
        });
    }

    public Long zrevrank(final String key, final String member) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.zrevrank(key, member);
            }
        });
    }

    public Long zcount(final String key, final double min, final double max) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.zcount(key, min, max);
            }
        });
    }

    public Set<String> zrange(final String key, final int start, final int end) {
        return execute(new JedisAction<Set<String>>() {

            @Override
            public Set<String> action(JedisCluster jedisCluster) {
                return jedisCluster.zrange(key, start, end);
            }
        });
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, int start, int end) {
        return null;
    }

    public Set<String> zrevrange(final String key, final int start, final int end) {
        return execute(new JedisAction<Set<String>>() {

            @Override
            public Set<String> action(JedisCluster jedisCluster) {
                return jedisCluster.zrevrange(key, start, end);
            }
        });
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(String key, int start, int end) {
        return null;
    }

    public Set<String> zrangeByScore(final String key, final double min, final double max) {
        return execute(new JedisAction<Set<String>>() {

            @Override
            public Set<String> action(JedisCluster jedisCluster) {
                return jedisCluster.zrangeByScore(key, min, max);
            }
        });
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return null;
    }

    public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
        return execute(new JedisAction<Set<String>>() {

            @Override
            public Set<String> action(JedisCluster jedisCluster) {
                return jedisCluster.zrevrangeByScore(key, max, min);
            }
        });
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        return null;
    }

    public Boolean zrem(final String key, final String member) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(JedisCluster jedisCluster) {
                return jedisCluster.zrem(key, member) == 1 ? true : false;
            }
        });
    }

    public Long zremByScore(final String key, final double start, final double end) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.zremrangeByScore(key, start, end);
            }
        });
    }

    public Long zremByRank(final String key, final long start, final long end) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.zremrangeByRank(key, start, end);
            }
        });
    }

    public Long zcard(final String key) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(JedisCluster jedisCluster) {
                return jedisCluster.zcard(key);
            }
        });
    }

    @Override
    public void publish(String channel, String message) {

    }

}
