package com.example.UPI.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IdempotencyService {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);
    private static final String REDIS_PREFIX = "upi:mesh:idempotency:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${upi.mesh.idempotency-ttl-seconds:86400}")
    private long ttlSeconds;

    /**
     * Try to claim a hash. Returns true if this caller is the first; false if
     * someone else already claimed it across the distributed cluster.
     */
    public boolean claim(String packetHash) {
        String key = REDIS_PREFIX + packetHash;

        // SETNX in Redis: Returns true only if the key did not already exist
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, "CLAIMED", Duration.ofSeconds(ttlSeconds));

        return Boolean.TRUE.equals(acquired);
    }

    /**
     * In a Redis setup, eviction is handled natively by Redis TTLs.
     * We no longer need the @Scheduled eviction method.
     */

    public void clear() {
        // Warning: This clears all idempotency keys. Used only for testing.
        redisTemplate.getConnectionFactory().getConnection()
                .serverCommands().flushDb();
        log.warn("Idempotency cache manually flushed.");
    }
}