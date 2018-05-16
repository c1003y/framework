package com.mk.cache.service;

import redis.clients.jedis.Jedis;

public interface RedisCallback<T> {

    T doInRedis(Jedis jedis) throws Exception;

}
