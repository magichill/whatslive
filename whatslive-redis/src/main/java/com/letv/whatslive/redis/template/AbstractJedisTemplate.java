package com.letv.whatslive.redis.template;

import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangjian7 on 2015/8/5.
 */
public abstract class AbstractJedisTemplate {

    public abstract Boolean del(final String... keys) ;

    public abstract void flushDB() ;

    // / String Actions ///

    /**
     * Get the value of the specified key. If the key does not exist null is
     * returned. If the value stored at key is not a string an error is returned
     * because GET can only handle string values.
     */
    public abstract String get(final String key) ;

    /**
     * Get the value of the specified key as Long.If the key does not exist null is returned.
     */
    public abstract Long getAsLong(final String key) ;

    /**
     * Get the value of the specified key as Integer.If the key does not exist null is returned.
     */
    public abstract Integer getAsInt(final String key) ;

    /**
     * Get the values of all the specified keys. If one or more keys dont exist
     * or is not of type String, a 'nil' value is returned instead of the value
     * of the specified key, but the operation never fails.
     */
    public abstract List<String> mget(final String... keys) ;

    /**
     * Set the string value as value of the key.
     * The string can't be longer than 1073741824 bytes (1 GB).
     */
    public abstract void set(final String key, final String value) ;

    /**
     * The command is exactly equivalent to the following group of commands: {@link #set(String, String) SET} +
     * {@link #expire(String, int) EXPIRE}.
     * The operation is atomic.
     */
    public abstract void setex(final String key, final String value, final int seconds) ;

    /**
     * SETNX works exactly like {@link #setNX(String, String) SET} with the only
     * difference that if the key already exists no operation is performed.
     * SETNX actually means "SET if Not eXists".
     * <p/>
     * return true if the key was set.
     */
    public abstract Boolean setnx(final String key, final String value) ;

    /**
     * The command is exactly equivalent to the following group of commands: {@link #setex(String, String, int) SETEX} +
     * {@link #sexnx(String, String) SETNX}.
     * The operation is atomic.
     */
    public abstract Boolean setnxex(final String key, final String value, final int seconds) ;

    /**
     * GETSET is an atomic set this value and return the old value command. Set
     * key to the string value and return the old value stored at key. The
     * string can't be longer than 1073741824 bytes (1 GB).
     */
    public abstract String getSet(final String key, final String value) ;

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
    public abstract Long incr(final String key) ;

    public abstract Long incrBy(final String key, final long increment) ;

    public abstract Double incrByFloat(final String key, final double increment) ;

    /**
     * Decrement the number stored at key by one. If the key does not exist or
     * contains a value of a wrong type, set the key to the value of "0" before
     * to perform the decrement operation.
     */
    public abstract Long decr(final String key) ;

    public abstract Long decrBy(final String key, final long decrement) ;

    // / Hash Actions ///

    /**
     * If key holds a hash, retrieve the value associated to the specified
     * field.
     * <p/>
     * If the field is not found or the key does not exist, a special 'nil' value is returned.
     */
    public abstract String hget(final String key, final String fieldName) ;

    public abstract List<String> hmget(final String key, final String... fieldsNames) ;

    public abstract Map<String, String> hgetAll(final String key) ;

    public abstract void hset(final String key, final String fieldName, final String value) ;

    public abstract void hmset(final String key, final Map<String, String> map) ;

    public abstract Boolean hsetnx(final String key, final String fieldName, final String value) ;

    public abstract Long hincrBy(final String key, final String fieldName, final long increment) ;

    public abstract Double hincrByFloat(final String key, final String fieldName, final double increment) ;

    public abstract Long hdel(final String key, final String... fieldsNames) ;

    public abstract Boolean hexists(final String key, final String fieldName) ;

    public abstract Set<String> hkeys(final String key) ;

    public abstract Long hlen(final String key) ;

    // / List Actions ///

    public abstract Long lpush(final String key, final String... values) ;

    public abstract String rpop(final String key) ;

    public abstract String brpop(final String key) ;

    public abstract String brpop(final int timeout, final String key);

    /**
     * Not support for sharding.
     */
    public abstract String rpoplpush(final String sourceKey, final String destinationKey) ;

    /**
     * Not support for sharding.
     */
    public abstract String brpoplpush(final String source, final String destination, final int timeout) ;

    public abstract Long llen(final String key) ;

    public abstract String lindex(final String key, final long index);

    public abstract List<String> lrange(final String key, final int start, final int end) ;

    public abstract void ltrim(final String key, final int start, final int end) ;

    public abstract void ltrimFromLeft(final String key, final int size) ;

    public abstract Boolean lremFirst(final String key, final String value) ;

    public abstract Boolean lremAll(final String key, final String value) ;

    // / Set Actions ///
    public abstract Boolean sadd(final String key, final String member) ;

    public abstract Set<String> smembers(final String key) ;

    // / Ordered Set Actions ///

    /**
     * return true for add new element, false for only update the score.
     */
    public abstract Boolean zadd(final String key, final double score, final String member) ;

    public abstract Double zscore(final String key, final String member) ;

    public abstract Long zrank(final String key, final String member) ;

    public abstract Long zrevrank(final String key, final String member) ;

    public abstract Long zcount(final String key, final double min, final double max) ;

    public abstract Set<String> zrange(final String key, final int start, final int end) ;

    public abstract Set<Tuple> zrangeWithScores(final String key, final int start, final int end) ;

    public abstract Set<String> zrevrange(final String key, final int start, final int end) ;

    public abstract Set<Tuple> zrevrangeWithScores(final String key, final int start, final int end) ;

    public abstract Set<String> zrangeByScore(final String key, final double min, final double max) ;

    public abstract Set<Tuple> zrangeByScoreWithScores(final String key, final double min,
                                              final double max) ;

    public abstract Set<String> zrevrangeByScore(final String key, final double max, final double min) ;

    public abstract Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max,
                                                 final double min) ;

    public abstract Boolean zrem(final String key, final String member) ;

    public abstract Long zremByScore(final String key, final double start, final double end) ;

    public abstract Long zremByRank(final String key, final long start, final long end) ;

    public abstract Long zcard(final String key);
    public abstract void publish(final String channel, final String message);

}
