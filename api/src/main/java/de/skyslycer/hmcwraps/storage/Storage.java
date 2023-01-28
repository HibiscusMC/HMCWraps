package de.skyslycer.hmcwraps.storage;

public interface Storage<O, T> {

    T get(O source);

    void set(O source, T value);

}
