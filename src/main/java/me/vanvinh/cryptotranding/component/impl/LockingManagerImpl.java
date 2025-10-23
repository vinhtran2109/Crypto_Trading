package me.vanvinh.cryptotranding.component.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import me.vanvinh.cryptotranding.component.LockingManager;

@Component
public class LockingManagerImpl implements LockingManager {
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public void lock(String key) {
        locks.computeIfAbsent(key, k -> new ReentrantLock());
    }

    @Override
    public void unlock(String key) {
        locks.get(key).unlock();

    }
}
