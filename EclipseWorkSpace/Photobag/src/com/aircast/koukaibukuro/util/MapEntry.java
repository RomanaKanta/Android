package com.aircast.koukaibukuro.util;

import java.util.Map;

/**
 * create a object pair key and value
 * in this project support set cookie paramter
 * @author lent5
 *
 * @param <K>
 * @param <V>
 */

final public class MapEntry<K, V> implements Map.Entry<K, V> {
    /** key property */
    private final K mKey;
    /** value property */
    private V mValue;

    /** Constructor MapEntry */
    public MapEntry(K key, V value) {
        this.mKey = key;
        this.mValue = value;
    }

    @Override
    public K getKey() {
        return mKey;
    }

    @Override
    public V getValue() {
        return mValue;
    }

    @Override
    public V setValue(V value) {
        V old = this.mValue;
        this.mValue = value;
        return old;
    }

    @Override
    public String toString() {    
        return mKey + "=" + mValue;
    }
}

