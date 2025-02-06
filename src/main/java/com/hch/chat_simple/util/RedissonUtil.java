package com.hch.chat_simple.util;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedissonUtil {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 加锁（阻塞等待，默认超时时间）
     * @param lockKey 锁的Key
     * @param waitTime 最大等待时间（秒）
     * @param leaseTime 锁自动释放时间（秒）
     * @return 是否成功获取锁
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     * @param lockKey 锁的Key
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 自动续期的锁（看门狗机制）
     * @param lockKey 锁的Key
     */
    public void lockWithWatchdog(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
    }
}
