package com.mk.controller;

import com.mk.cache.lock.RedisLock;
import com.mk.cache.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private RedisLock redisLock;

    @GetMapping({"", "/"})
    public ResponseEntity index() {
        String requestId = UUID.randomUUID().toString();
        boolean lockResult = redisLock.lock("key", requestId, 600000L);
        boolean unlockResult = redisLock.unlock("key", requestId);
        return ResponseEntity.ok(lockResult + "-" + unlockResult);
    }

}
