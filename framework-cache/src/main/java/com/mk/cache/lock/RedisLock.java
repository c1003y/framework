package com.mk.cache.lock;

import com.mk.cache.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * redis分布式锁
 */
@Component
@Slf4j
public class RedisLock {

    private static final String SET_IF_NOT_EXISTS = "NX";
    private static final String EXP_MILLISECONDS = "PX";
    private static final String EXP_SECONDS = "EX";
    private static final String LOCK_SUCCESS = "OK";
    private static final Long UNLOCK_SUCCESS = 1L;

    @Autowired
    private RedisService redisService;

    /**
     * 加锁
     * @param lockKey
     * @param requestId
     * @param expireMs
     * @return
     */
    public boolean lock(String lockKey, String requestId, long expireMs) {
        if (StringUtils.isEmpty(lockKey) || StringUtils.isEmpty(requestId)) {
            log.error("获取redis锁参数错误");
            return false;
        }
        boolean result = redisService.execute((jedis) -> {
            String ret = jedis.set(lockKey, requestId, SET_IF_NOT_EXISTS, EXP_MILLISECONDS, expireMs);
            if (LOCK_SUCCESS.equals(ret)) {
                log.info("获取redis锁成功，requestId = {}", requestId);
                return true;
            }
            log.warn("获取redis锁失败，requestId = {}", requestId);
            return false;
        });
        return result;
    }

    /**
     * 解锁
     * @param lockKey
     * @param requestId
     * @return
     */
    public boolean unlock(String lockKey, String requestId) {
        if (StringUtils.isEmpty(lockKey) || StringUtils.isEmpty(requestId)) {
            log.error("redis解锁参数错误");
            return false;
        }
        boolean result = redisService.execute((jedis) -> {
            //LUA脚本，判断锁的持有对象为当前请求的，解锁
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                    "then return redis.call('del', KEYS[1]) " +
                    "else return 0 end";
            Object ret = jedis.eval(script, new ArrayList<String>() {{
                add(lockKey);
            }}, new ArrayList<String>() {{
                add(requestId);
            }});
            if(UNLOCK_SUCCESS.equals(ret)){
                log.info("redis解锁成功，lockKey = {}, requestId = {}", lockKey, requestId);
                return true;
            }
            log.warn("redis解锁失败，lockKey = {}, requestId = {}", lockKey, requestId);
            return false;
        });
        return result;
    }
}