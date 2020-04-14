package com.developer.base.utils.lib.object;

import com.developer.base.utils.lib.tool.BaseRandom;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;

public class BaseList<T> extends ArrayList<T> {

    public BaseList() {
        super();
    }

    public BaseList(T[] ts) {
        super();
        this.addAll(ts);
    }

    public BaseList(String json) {
        this.addAll(json);
    }

    public BaseList(Collection<T> c) {
        super();
        this.addAll(c);
    }

    public BaseList(int size, AddAll<T> a) {
        this.addAll(size, a);
    }

    public T getRandom() {
        return this.get(BaseRandom.getIntace().getRandomPositiveInt(this.size() - 1, 0));
    }

    public boolean addIfAbsent(int index, T t) {
        if (!this.contains(t)) {
            this.add(index, t);
            return true;
        }
        return false;
    }

    public boolean addIfAbsent(T t) {
        return this.addIfAbsent(this.size(), t);
    }

    public BaseList<Boolean> addAllAbsent(int startPosition, Collection<T> ts) {
        BaseList<Boolean> r = new BaseList<>();

        ((BaseList<T>) ts).forEach((index, t) ->
                r.add(this.addIfAbsent(startPosition+index, t))
        );

        return r;
    }

    public BaseList<Boolean> addAllAbsent(int startPosition, int size, AddAll<T> a){
        return this.addAllAbsent(startPosition, new BaseList<>(size, a));
    }

    public BaseList<Boolean> addAllAbsent(int startPosition, T[] ts) {
        return this.addAllAbsent(startPosition, new BaseList<>(ts));
    }

    public BaseList<Boolean> addAllAbsent(int startPosition, String json) {
        return this.addAllAbsent(startPosition, new Gson().fromJson(json, this.getClass()));
    }

    public BaseList<Boolean> addAllAbsent(Collection<T> ts) {
        return this.addAllAbsent(this.size(), ts);
    }

    public BaseList<Boolean> addAllAbsent(T[] ts) {
        return this.addAllAbsent(this.size(), ts);
    }

    public BaseList<Boolean> addAllAbsent(String json) {
        return this.addAllAbsent(this.size(), json);
    }

    public BaseList<Boolean> addAllAbsent(int size, AddAll<T> a) {
        return this.addAllAbsent(this.size(), size, a);
    }

    public void addAll(T[] ts) {
        this.addAll(Arrays.asList(ts));
    }

    public void addAll(String json) {
        this.addAll(new Gson().fromJson(json, this.getClass()));
    }

    public void addAll(int size, AddAll<T> a) {
        this.addAll(this.size(), size, a);
    }

    public void addAll(int startPosition, int size, AddAll<T> a) {
        for (int i = 0; i < size; i++) {
            this.add(startPosition+i, a.add(i));
        }
    }

    public int search(SearchList<T> s) {
        int index = -1;

        for (int i = 0; i < this.size(); i++) {
            if (s.isItem(i, this.get(i))) {
                index = i;
                break;
            }
        }

        return index;
    }

    public int countIf(Count<T> c) {
        final int[] count = {0};

        forEach((i, t) -> count[0] += (c.onItem(i, t, count[0])) ? 1 : 0);

        return count[0];
    }

    public <K> BaseMap<K,T> map(MapList<K, T> m) {
        BaseMap<K, T> baseMap = new BaseMap<>();

        forEach((index, t) -> baseMap.put(m.onItem(index, t), t));

        return baseMap;
    }

    public <O> BaseList<O> extract(ExtractList<T, O> e) {
        BaseList<O> result = new BaseList<>();

        forEach((index, t) -> {
            O o = e.onItem(index, t, result.size());

            if (o != null) {
                result.add(o);
            }
        });

        return result;
    }

    public <K, O> BaseMap<K, O> extractMap(ExtractListToMap<K, T, O> e) {
        BaseMap<K, O> result = new BaseMap<>();

        forEach((i, t) -> {
            Entry<K, O> entry = e.onItem(i, t, result.size());

            if (entry != null) {
                result.put(entry);
            }
        });

        return result;
    }

    public void forEach(Each<T> e) {
        for (int i = 0; i < this.size(); i++) {
            e.onItem(i, this.get(i));
        }
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public interface Count<T> {
        boolean onItem(int index, T t, int count);
    }

    public interface Each<T> {
        void onItem(int index, T t);
    }

    public interface AddAll<T> {
        T add(int index);
    }

    public interface SearchList<T> {
        boolean isItem(int index, T t);
    }

    public interface MapList<K,T> {
        K onItem(int index, T t);
    }

    public interface ExtractList<I, O> {
        O onItem(int index, I i, int count);
    }

    public interface ExtractListToMap<K, IT, OT> {
        Entry<K, OT> onItem(int index, IT i, int count);
    }
}