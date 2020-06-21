package com.developer.base.utils.lib.object;

import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Map.Entry;

public class BaseEntry<K, V> implements Map.Entry<K, V> {
    private final K mKey;
    private V mValue;

    public BaseEntry(K Key, V Value) {
        this.mKey = Key;
        this.mValue = Value;
    }

    @Override
    public K getKey() {
        return this.mKey;
    }

    @Override
    public V getValue() {
        return this.mValue;
    }

    @Override
    public V setValue(V value) {
        V  old = this.mValue;
        this.mValue = value;
        return old;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Map.Entry<?, ?>))
            return false;

        Entry<?, ?> e = (Entry<?, ?>) obj;

        return this.getKey().equals(e.getKey()) && this.getValue().equals(e.getValue());
    }
}
