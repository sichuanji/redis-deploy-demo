package com.minbo.redis.multi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

//RedisClientMulti: 是 Redis 的客户端类，用于执行多个命令的事务或批处理操作。
//Note： 所有的类型的方法
public class RedisClientMulti {
    //
    private static RedisServiceMultiImpl redisService = new RedisServiceMultiImpl();

    //断开与 Redis      服务器     的连接。 客户端不再与服务器通信
    public void disconnect() {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        shardedJedis.disconnect();
    }
//Note：字符串操作 超时 锁 还有这个 过滤器都可以在这里实现
    /**
     * 设置单个值
     */
    public String set(String key, String value) {
        String result = null; // 初始化结果为 null

        ShardedJedis shardedJedis = redisService.getRedisClient(); // 从 redisService 获取 ShardedJedis 实例
        if (shardedJedis == null) { // 如果获取到的 ShardedJedis 实例为空
            return result; // 直接返回 null
        }
        boolean flag = false; // 初始化一个标志位为 false，用于标记操作是否成功
        try {
            result = shardedJedis.set(key, value); // 调用 ShardedJedis 实例的 set 方法，设置键值对，并将返回值赋给 result
        } catch (Exception e) {
            flag = true; // 如果发生异常，将标志位设置为 true
        } finally {
            redisService.returnResource(shardedJedis, flag); // 无论是否发生异常，都要归还 ShardedJedis 实例给连接池
        }
        return result; // 返回设置结果
    }


    /**
     * 获取单个值
     */
    public String get(String key) {
        String result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }

        boolean flag = false;
        try {
            result = shardedJedis.get(key);
        } catch (Exception e) {
            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Boolean exists(String key) {
        Boolean result = false;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.exists(key);
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public String type(String key) {
        String result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.type(key);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    /**
     * 在某段时间后失效 设置键值在指定时间后失效
     */
    public Long expire(String key, int seconds) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.expire(key, seconds);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    /**
     * 在某个时间点失效
     */
    public Long expireAt(String key, long time) {//UNIX 时间戳
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.expireAt(key, time);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long ttl(String key) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.ttl(key);//// 调用 ShardedJedis 实例的 ttl 方法，获取键的剩余过期时间，并将返回值赋给 result

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    // note： 将指定偏移量上的位值设置为给定的布尔值 布隆过滤器 位图索引
    public boolean setbit(String key, long offset, boolean value) {

        ShardedJedis shardedJedis = redisService.getRedisClient();
        boolean result = false;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.setbit(key, offset, value);//// 调用 ShardedJedis 实例的 setbit 方法，设置指定键的偏移量上的位值，并将返回值赋给 result
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public boolean getbit(String key, long offset) {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        boolean result = false;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;

        try {
            result = shardedJedis.getbit(key, offset);
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }
//用指定字符串覆盖指定键的字符串值的一部分
    public long setrange(String key, long offset, String value) {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        long result = 0L;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.setrange(key, offset, value);
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public String getrange(String key, long startOffset, long endOffset) {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        String result = null;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.getrange(key, startOffset, endOffset);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public String getSet(String key, String value) {
        String result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.getSet(key, value);//设置指定键的新值，并返回该键原来的旧值
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long setnx(String key, String value) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.setnx(key, value);//键不存在的情况下设置键的值 这里key不存在
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public String setex(String key, int seconds, String value) {//设置过期时间同时设置键值对
        String result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.setex(key, seconds, value);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long decrBy(String key, long integer) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.decrBy(key, integer);//用于对存储在指定键的值进行减法操作

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long decr(String key) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.decr(key);//减1

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long incrBy(String key, long integer) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.incrBy(key, integer);//加指定值

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long incr(String key) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.incr(key);//

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long append(String key, String value) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.append(key, value);//追加

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public String substr(String key, int start, int end) {
        String result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.substr(key, start, end);//截取

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }
//Note: 键值对集合操作 存储对象
//    Key: user
//    Field: name    -> Value: John
//    age     -> Value: 30
//    email   -> Value: john@example.com

    public Long hset(String key, String field, String value) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.hset(key, field, value);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public String hget(String key, String field) {
        String result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.hget(key, field);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long hsetnx(String key, String field, String value) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.hsetnx(key, field, value);//字段在哈希中不存在 0 1 根据 hash的key来判断是否存在

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public String hmset(String key, Map<String, String> hash) {
        String result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.hmset(key, hash);//设置哈希中多个字段的值的命令

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public List<String> hmget(String key, String... fields) { //批量获取
        List<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.hmget(key, fields);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long hincrBy(String key, String field, long value) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.hincrBy(key, field, value);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Boolean hexists(String key, String field) {
        Boolean result = false;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.hexists(key, field);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long del(String key) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.del(key);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long hdel(String key, String field) { //hash删除
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.hdel(key, field);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long hlen(String key) {//hash长度
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.hlen(key);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<String> hkeys(String key) {
        Set<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.hkeys(key);//这个位置的所有key

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public List<String> hvals(String key) {
        List<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.hvals(key);//所有字段值

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Map<String, String> hgetAll(String key) {
        Map<String, String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.hgetAll(key);//键值对

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }
//Note： list
    /**
     * 在redis list尾部增加一个String
     */
    public Long rpush(String key, String string) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.rpush(key, string);//分左右

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    /**
     * 在redis list头部增加一个String
     */
    public Long lpush(String key, String string) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.lpush(key, string);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long llen(String key) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.llen(key);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public List<String> lrange(String key, long start, long end) {
        List<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.lrange(key, start, end);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public String ltrim(String key, long start, long end) {
        String result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.ltrim(key, start, end);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public String lIndex(String key, long index) {
        String result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.lindex(key, index);//获取列表中指定位置的元素

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public String lset(String key, long index, String value) {//指定位置
        String result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.lset(key, index, value);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long lrem(String key, long count, String value) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.lrem(key, count, value);//count 0 删所有

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    /**
     * 从redis list头部取出一个key
     */
    public String lpop(String key) {
        String result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.lpop(key);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    /**
     * 从redis list尾部取出一个key
     */
    public String rpop(String key) {
        String result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.rpop(key);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }
//Note:set
    public Long sadd(String key, String member) {// 不重复 无序
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.sadd(key, member);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<String> smembers(String key) {
        Set<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.smembers(key);//获取所有成员

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long srem(String key, String member) {
        ShardedJedis shardedJedis = redisService.getRedisClient();

        Long result = null;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.srem(key, member);//删除指定的一个或多个成员
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }
// Note: 随机从集合中取出成员，常见的应用场景包括随机抽奖、随机推送  默认从小到大
    public String spop(String key) {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        String result = null;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.spop(key);
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long scard(String key) {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        Long result = null;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.scard(key);//成员 数量

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Boolean sismember(String key, String member) {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        Boolean result = null;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.sismember(key, member);
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public String srandmember(String key) {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        String result = null;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.srandmember(key);//随机地从集合中返回一个或多个成员
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }
//Note: 有序集合 Z  sorted set 有序 关联着一个分数 排序和排行榜
    public Long zadd(String key, double score, String member) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.zadd(key, score, member);
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<String> zrange(String key, int start, int end) {
        Set<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.zrange(key, start, end);
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long zrem(String key, String member) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.zrem(key, member);
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Double zincrby(String key, double score, String member) {
        Double result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zincrby(key, score, member);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long zrank(String key, String member) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrank(key, member);//指定排名

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long zrevrank(String key, String member) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrevrank(key, member);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<String> zrevrange(String key, int start, int end) {
        Set<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrevrange(key, start, end);//起始和结束索引 zrange返回结果的排序顺序。

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<Tuple> zrangeWithScores(String key, int start, int end) {
        Set<Tuple> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrangeWithScores(key, start, end);//分数范围 结果中成员的排序顺序

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<Tuple> zrevrangeWithScores(String key, int start, int end) {
        Set<Tuple> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrevrangeWithScores(key, start, end);//

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long zcard(String key) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zcard(key);//成员数量 和 这个list不同 llen

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Double zscore(String key, String member) {
        Double result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zscore(key, member);//获取分数

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }
//Note:排序功能
    public List<String> sort(String key) { //
        List<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.sort(key);//对 Redis 中指定键对应的列表、集合或有序集合进行排序

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public List<String> sort(String key, SortingParams sortingParameters) {
        List<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.sort(key, sortingParameters);  // 参数来指定排序的方式和选项

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long zcount(String key, double min, double max) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zcount(key, min, max);//指定分数范围内的成员数量

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<String> zrangeByScore(String key, double min, double max) {
        Set<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrangeByScore(key, min, max);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<String> zrevrangeByScore(String key, double max, double min) {
        Set<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrevrangeByScore(key, max, min);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        Set<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrangeByScore(key, min, max, offset, count);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        Set<String> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrevrangeByScore(key, max, min, offset, count);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {

        Set<Tuple> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrangeByScoreWithScores(key, min, max);//成员及其分数

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        Set<Tuple> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrevrangeByScoreWithScores(key, max, min);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        Set<Tuple> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrangeByScoreWithScores(key, min, max, offset, count);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        Set<Tuple> result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zrevrangeByScoreWithScores(key, max, min, offset, count);//offset 和 count 参数进行分页控制

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long zremrangeByRank(String key, int start, int end) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zremrangeByRank(key, start, end);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long zremrangeByScore(String key, double start, double end) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.zremrangeByScore(key, start, end);

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Long linsert(String key, LIST_POSITION where, String pivot, String value) {
        Long result = null;
        ShardedJedis shardedJedis = redisService.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {

            result = shardedJedis.linsert(key, where, pivot, value);//指定元素 之前或之后 插入新元素 的命令

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    /**
     * Note:pipelined 是 Redis 客户端库中的一个特性，用于提高多个 Redis 命令执行的效率。通过使用流水线技术，客户端可以将多个命令打包成一个请求发送给 Redis 服务器，在一次网络往返中执行多个命令，从而减少了通信开销，提高了性能。
     * @param shardedJedisPipeline
     * @return
     */
    @SuppressWarnings("deprecation")
    public List<Object> pipelined(ShardedJedisPipeline shardedJedisPipeline) {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        List<Object> result = null;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.pipelined(shardedJedisPipeline);
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }
// Note: 获取分片位置 确定特定键（Key）应该存储在哪个 Redis 分片上
    public Jedis getShard(String key) {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        Jedis result = null;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.getShard(key);
        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public JedisShardInfo getShardInfo(String key) {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        JedisShardInfo result = null;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.getShardInfo(key);//分片方法  一致性哈希算法、键的哈希值、预定义的分片规则
        } catch (Exception e) {
            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }
//Note: 从一个给定的键中提取键标签（key tag）。   键标签是一种在Redis中用于分区（sharding）的技术，允许你在进行数据分片时控制数据如何被分布到不同的节点上。
    public String getKeyTag(String key) {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        String result = null;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.getKeyTag(key);
        } catch (Exception e) {
            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Collection<JedisShardInfo> getAllShardInfo() {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        Collection<JedisShardInfo> result = null;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.getAllShardInfo();

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }

    public Collection<Jedis> getAllShards() {
        ShardedJedis shardedJedis = redisService.getRedisClient();
        Collection<Jedis> result = null;
        if (shardedJedis == null) {
            return result;
        }
        boolean flag = false;
        try {
            result = shardedJedis.getAllShards();

        } catch (Exception e) {

            flag = true;
        } finally {
            redisService.returnResource(shardedJedis, flag);
        }
        return result;
    }
}
