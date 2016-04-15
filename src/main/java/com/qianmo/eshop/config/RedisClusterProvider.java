package com.qianmo.eshop.config;

import cn.dreampie.cache.CacheEvent;
import cn.dreampie.cache.CacheProvider;
import cn.dreampie.common.Constant;
import cn.dreampie.common.util.properties.Prop;
import cn.dreampie.common.util.properties.Proper;
import cn.dreampie.common.util.serialize.Serializer;
import cn.dreampie.log.Logger;
import com.alibaba.druid.util.StringUtils;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by zhangyang on 2016/3/22.
 */
public class RedisClusterProvider extends CacheProvider {
    private static final Logger logger = Logger.getLogger(RedisClusterProvider.class);

    //Jedis 集群实现
    private static JedisCluster cluster;
    private static String hosts;
    private static int timeout;
    private static int expired;

    static {
        Prop config = null;
        try {
            config = Proper.use("redis.properties");
        } catch (Exception e) {
        }
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        if (config != null) {
            //hots存放格式ip:host,ip:host;
            hosts = config.get("redis.host");
            timeout = config.getInt("redis.timeout", Protocol.DEFAULT_TIMEOUT);
            expired = config.getInt("redis.expired", -1);
            String host = hosts == null ? "" : hosts;
            String[] ipPorts = host.split(",");
            if (ipPorts != null) {
                for (String ipPort : ipPorts) {
                    nodes.add(new HostAndPort(ipPort.split(":")[0], Integer.valueOf(ipPort.split(":")[1])));
                }
                JedisPoolConfig poolConfig = new JedisPoolConfig();
                //poolConfig.setMaxTotal(2000);
                //poolConfig.setMaxIdle(200);

                poolConfig.setLifo(config.getBoolean("redis.pool.lifo", BaseObjectPoolConfig.DEFAULT_LIFO));
                poolConfig.setMaxWaitMillis(config.getLong("redis.pool.maxWaitMillis", 600000l));
                poolConfig.setMinEvictableIdleTimeMillis(config.getLong("redis.pool.minEvictableIdleTimeMillis", BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS));
                poolConfig.setSoftMinEvictableIdleTimeMillis(config.getLong("redis.pool.softMinEvictableIdleTimeMillis", BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS));
                poolConfig.setNumTestsPerEvictionRun(config.getInt("redis.pool.numTestsPerEvictionRun", BaseObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN));
                poolConfig.setTestOnBorrow(config.getBoolean("redis.pool.testOnBorrow", true));
                poolConfig.setTestOnReturn(config.getBoolean("redis.pool.testOnReturn", BaseObjectPoolConfig.DEFAULT_TEST_ON_RETURN));
                poolConfig.setTestWhileIdle(config.getBoolean("redis.pool.testWhileIdle", BaseObjectPoolConfig.DEFAULT_TEST_WHILE_IDLE));
                poolConfig.setTimeBetweenEvictionRunsMillis(config.getLong("redis.pool.timeBetweenEvictionRunsMillis", BaseObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS));
                poolConfig.setEvictionPolicyClassName(config.get("redis.pool.evictionPolicyClassName", BaseObjectPoolConfig.DEFAULT_EVICTION_POLICY_CLASS_NAME));
                poolConfig.setBlockWhenExhausted(config.getBoolean("redis.pool.blockWhenExhausted", BaseObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED));
                poolConfig.setJmxEnabled(config.getBoolean("redis.pool.jmxEnabled", BaseObjectPoolConfig.DEFAULT_JMX_ENABLE));
                poolConfig.setJmxNamePrefix(config.get("redis.pool.jmxNamePrefix", BaseObjectPoolConfig.DEFAULT_JMX_NAME_PREFIX));
                cluster = new JedisCluster(nodes, timeout, 6, poolConfig);
            }
        } else {
            hosts = "127.0.0.1:6379";
            timeout = Protocol.DEFAULT_TIMEOUT;
            expired = -1;
            nodes.add(new HostAndPort("127.0.0.1", 6379));
            cluster = new JedisCluster(nodes, timeout, 12);
        }
    }


    @Override
    public <T> T getCache(String group, String key) {
        String jkey = getRedisKey(group, key);
        try {
            T cahe = null;
            if (cluster != null) {
                cahe = (T) Serializer.unserialize(cluster.get(jkey.getBytes()));
            }
            return cahe;
        } catch (Exception e) {
            logger.warn("%s", e, e);
            return null;
        } finally {
            returnResource(cluster);
        }
    }


    /*public static void main(String[] args) {
         //RedisClusterProvider re = new RedisClusterProvider();
         System.out.println(cluster.set("test","11114567777"));
         System.out.println(cluster.get("test"));
     }
 */
    @Override
    public void addCache(String group, String key, Object cache, int expired) {

        try {
            //String jkey = getRedisKey(group, key);
            byte[] jkey = getRedisKey(group, key).getBytes();
            if (cluster != null) {
                cluster.set(jkey, Serializer.serialize(cache));
                if (expired != -1) {
                    cluster.expire(jkey, expired);
                } else {
                    if (RedisClusterProvider.expired != -1) {
                        cluster.expire(jkey, RedisClusterProvider.expired);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("%s", e, e);
        } finally {
            returnResource(cluster);
        }
    }

    @Override
    public void removeCache(String group, String key) {
        String jkey = getRedisKey(group, key);
        try {
            cluster.del(jkey.getBytes());
        } catch (Exception e) {
            logger.warn("%s", e, e);
        }

    }

    @Override
    public void doFlush(CacheEvent event) {
        if (event.getType().equals(CacheEvent.CacheEventType.ALL)) {
            cluster.flushDB();
        } else if (event.getType().equals(CacheEvent.CacheEventType.GROUP)) {
            String groupKeys = event.getGroup() + Constant.CONNECTOR + "keys";
            List<String> groupKeyList = getGroupKeys(cluster, groupKeys);

            if (groupKeyList != null && groupKeyList.size() > 0) {
                cluster.del(groupKeyList.toArray(new String[groupKeyList.size()]));
            }
        }

    }

    private String getRedisKey(String group, String key) {
        return group + Constant.CONNECTOR + key;
    }

    private void returnResource(JedisCluster jedis) {

    }

    private List<String> getGroupKeys(Object jedis, String groupKeys) {
        byte[] gkey = groupKeys.getBytes();
        return (List<String>) Serializer.unserialize(((JedisCluster)jedis).get(gkey));
    }

    private void addGroupKey(Object jedis, String group, String key) {
        String groupKeys = group + Constant.CONNECTOR + "keys";
        byte[] gkey = groupKeys.getBytes();

        List<String> groupKeyList = getGroupKeys(jedis, groupKeys);
        if (groupKeyList == null) {
            groupKeyList = new ArrayList<String>();
        }
        if (!groupKeyList.contains(key)) {
            groupKeyList.add(key);
        }
        cluster.set(gkey, Serializer.serialize(groupKeyList));
/*        if (jedis instanceof ShardedJedis) {
            ((ShardedJedis) jedis).set(gkey, Serializer.serialize(groupKeyList));
        } else if (jedis instanceof Jedis) {
            ((Jedis) jedis).set(gkey, Serializer.serialize(groupKeyList));
        }*/
    }

    private void delGroup(JedisCluster jedis, CacheEvent event, String key) {
        String groupKeys = event.getGroup() + Constant.CONNECTOR + "keys";
        byte[] gkey = groupKeys.getBytes();

        List<String> groupKeyList = getGroupKeys(jedis, groupKeys);
        if (groupKeyList != null && groupKeyList.contains(event)) {
            groupKeyList.remove(key);
            cluster.set(gkey, Serializer.serialize(groupKeyList));
        }
    }
}
