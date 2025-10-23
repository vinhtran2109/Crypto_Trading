package me.vanvinh.cryptotranding.component;


import org.springframework.stereotype.Component;



@Component
public interface LockingManager {
    void lock(String key);

    void unlock(String key);
}