package com.mk.cache.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private String port;

    @Value("${redis.database}")
    private String database;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.timeout}")
    private String timeout;

    @ConfigurationProperties(prefix = "redis.pool")
    @Bean
    public JedisPoolConfig jedisPoolConfig(){
        return new JedisPoolConfig();
    }

    @Bean("jedisPool")
    public JedisPool jedisPool(){
        JedisPoolConfig poolConfig = jedisPoolConfig();
        return new JedisPool(poolConfig, host, Integer.valueOf(port), Integer.valueOf(timeout),
                StringUtils.isEmpty(password) ? null : password, Integer.valueOf(database));
    }
}
