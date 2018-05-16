package com.mk.controller;

import com.mk.cache.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

@RestController
@Slf4j
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private RedisService redisService;

    @GetMapping({"", "/"})
    public ResponseEntity index() {
        redisService.execute((jedis) -> jedis.set("key", "1", "NX", "EX", 60));
        return ResponseEntity.ok("OK");
    }

}
