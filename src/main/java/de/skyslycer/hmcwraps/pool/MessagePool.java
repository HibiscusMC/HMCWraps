package de.skyslycer.hmcwraps.pool;

import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessagePool implements ObjectPool<UUID, Component> {

    private static final int COOLDOWN = 1000;

    private final Map<UUID, MessagePoolEntry> pool = new HashMap<>();

    @Override
    public void insert(UUID key, Component value) {
        pool.put(key, new MessagePoolEntry(value, System.currentTimeMillis()));
    }

    @Override
    public void remove(UUID key) {
        pool.remove(key);
    }

    @Override
    public void execute(UUID key, Component value, PoolMethod consumer) {
        if (pool.containsKey(key) && pool.get(key).message().equals(value) && (System.currentTimeMillis() - pool.get(key).time() < COOLDOWN)) {
            return;
        }
        consumer.execute();
        insert(key, value);
    }

}
