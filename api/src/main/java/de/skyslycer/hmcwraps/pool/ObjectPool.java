package de.skyslycer.hmcwraps.pool;

public interface ObjectPool<K, V> {

    void insert(K key, V value);

    void remove(K key);

    void execute(K key, V value, PoolMethod consumer);

}
