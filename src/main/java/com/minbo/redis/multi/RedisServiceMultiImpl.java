package com.minbo.redis.multi;

import java.util.ArrayList;
import java.util.List;

import com.minbo.redis.common.GlobalParams;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

/**
 * 分布式模式 ShardedJedis是基于  一致性哈希算法  实现的分布式Redis集群客户端
 * Note: 它帮助你将数据分散存储在多个服务器上，并且能够有效地找到数据存储在哪个服务器上，以及如何访问它。
 * 一致性哈希算法
 */
// 我现在已经有了三个主节点的集群，下一步，当用户请求来到后端的时候，就通过shardeJedisPool来进行请求，就等同于实现hash slot的一个工具
public class RedisServiceMultiImpl implements RedisServiceMulti {
	//用于管理多个分片连接
	private ShardedJedisPool shardedJedisPool;  //分片链接池
    //配置了 Jedis 连接池的相关参数，包括最大活动对象个数、最大空闲时间、最大等待时间等。
	public RedisServiceMultiImpl() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();// Jedis池配置  JedisShardInfo 对象，指定了 Redis 服务器的 IP 地址和端口号
		poolConfig.setMaxTotal(500);// 最大活动的对象个数
		poolConfig.setMaxIdle(1000 * 60);// 对象最大空闲时间
		poolConfig.setMaxWaitMillis(1000 * 10);// 获取对象时最大等待时间
		poolConfig.setTestOnBorrow(true);
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(1);
		JedisShardInfo infoA = new JedisShardInfo(GlobalParams.HOST_IP_A, GlobalParams.PORT_A);
//		// infoA.setPassword("123456");
//		 JedisShardInfo infoB = new JedisShardInfo(GlobalParams.HOST_IP_B,
//		 GlobalParams.PORT_B);

		// infoB.setPassword("123456");
		shards.add(infoA);
//		shards.add(infoB);
		// shards.add(infoB); 使用这些 JedisShardInfo 对象创建了一个 ShardedJedisPool 对象，该对象用于管理多个分片连接，并指定了一致性哈希算法和默认的键标签模式。
		//Note：指定什么 分片算法  Hashing.MURMUR_HASH ：计算键的哈希值。在分片技术中，哈希算法常用于确定键应该被分配到哪个分片
		shardedJedisPool = new ShardedJedisPool(poolConfig, shards, Hashing.MURMUR_HASH,
				Sharded.DEFAULT_KEY_TAG_PATTERN);

	}
	//从连接池中获取资源。
	public ShardedJedis getRedisClient() {
		try {
			return shardedJedisPool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//将 ShardedJedis 实例返回到连接池，并根据参数确定是否将资源标记为失效。
	public void returnResource(ShardedJedis shardedJedis) {
		shardedJedisPool.returnResource(shardedJedis);
	}

	public void returnResource(ShardedJedis shardedJedis, boolean broken) {
		if (broken) {
			shardedJedisPool.returnBrokenResource(shardedJedis);
		} else {
			shardedJedisPool.returnResource(shardedJedis);
		}
	}
}
