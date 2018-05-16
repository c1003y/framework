package com.mk.cache.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@Slf4j
public class RedisService {

    @Autowired
    private JedisPool jedisPool;

    public <T> T execute(RedisCallback<T> action){
        Jedis jedis = jedisPool.getResource();
        T result;
        try{
            result = action.doInRedis(jedis);
        }catch (Exception ex){
            log.error("redis操作异常", ex);
            result = null;
        }finally{
            jedis.close();
        }
        return result;
    }
}
