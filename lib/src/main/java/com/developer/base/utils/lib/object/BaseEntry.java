package com.developer.base.utils.lib.object;

import com.google.gson.Gson;

import java.util.Map;

public class BaseEntry<K, V> implements Map.Entry<K, V> {
    private K mKey;
    private V mValue;

    public BaseEntry(String json) {
        BaseEntry<K, V> temp = new Gson().fromJson(json, this.getClass());
        this.mKey = temp.mKey;
        this.mValue = temp.mValue;
    }

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
        this.mValue = value;
        return value;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
