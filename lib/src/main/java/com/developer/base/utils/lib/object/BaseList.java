package com.developer.base.utils.lib.object;

import androidx.annotation.Nullable;

import com.developer.base.utils.lib.tool.BaseRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

public class BaseList<T> extends ArrayList<T> {

    public BaseList() {
        super();
    }

    public BaseList(T[] ts) {
        super();
        this.addAll(ts);
    }

    public BaseList(Collection<T> c) {
        super();
        this.addAll(c);
    }

    public BaseList(int repeat, AddAll<T> a) {
        this.addAll(repeat, a);
    }

    @Override
    public T remove(int index) {
        if (this.size() > index)
            return super.remove(index);
        return null;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        if (this.contains(o))
            return super.remove(o);
        else
            return false;
    }

    public T getRandom() {
        if (this.size() > 0)
            return this.get(BaseRandom.getIntace().getRandomPositiveInt(this.size() - 1, 0));
        else
            return null;
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

    public BaseList<Boolean> addAllAbsent(int startPosition, int repeat, AddAll<T> a){
        return this.addAllAbsent(startPosition, new BaseList<>(repeat, a));
    }

    public BaseList<Boolean> addAllAbsent(int startPosition, T[] ts) {
        return this.addAllAbsent(startPosition, new BaseList<>(ts));
    }

    public BaseList<Boolean> addAllAbsent(Collection<T> ts) {
        return this.addAllAbsent(this.size(), ts);
    }

    public BaseList<Boolean> addAllAbsent(T[] ts) {
        return this.addAllAbsent(this.size(), ts);
    }

    public BaseList<Boolean> addAllAbsent(int repeat, AddAll<T> a) {
        return this.addAllAbsent(this.size(), repeat, a);
    }

    public void addAll(T[] ts) {
        this.addAll(this.size(), ts);
    }

    public void addAll(int startPosition,T[] ts) {
        this.addAll(startPosition, Arrays.asList(ts));
    }

    public void addAll(int repeat, AddAll<T> a) {
        this.addAll(this.size(), repeat, a);
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

        forEach((i, t) -> count[0] += (c.count(i, t, count[0])) ? 1 : 0);

        return count[0];
    }

    public T removeIf(RemoveIf<T> r) {
        T t = null;
        int pos = 0;

        for (int i = 0; i < this.size(); i++) {
            T temp = this.get(i);
            if (r.remove(i, temp, 0)) {
                t = temp;
                pos = i;
                break;
            }
        }

        if (t != null) {
            this.remove(pos);
        }

        return t;
    }

    public T removeIfExists(int index) {
        if (this.size() > index) {
            return this.remove(index);
        } else {
            return null;
        }
    }

    public BaseList<T> removeAllIf(RemoveIf<T> r) {
        BaseList<T> old = this.extract((index, t, count) -> (r.remove(index, t, count)) ? t : null);

        this.removeAll(old);

        return old;
    }

    public <K> BaseMap<K,T> map(MapList<K, T> m) {
        BaseMap<K, T> baseMap = new BaseMap<>();

        forEach((index, t) -> baseMap.put(m.getKey(index, t), t));

        return baseMap;
    }

    public <O> BaseList<O> extract(ExtractList<T, O> e) {
        BaseList<O> result = new BaseList<>();

        forEach((index, t) -> {
            O o = e.extract(index, t, result.size());

            if (o != null) {
                result.add(o);
            }
        });

        return result;
    }

    public <K, O> BaseMap<K, O> extractMap(ExtractListToMap<K, T, O> e) {
        BaseMap<K, O> result = new BaseMap<>();

        forEach((i, t) -> {
            Entry<K, O> entry = e.map(i, t, result.size());

            if (entry != null) {
                result.put(entry);
            }
        });

        return result;
    }

    public void forEach(Each<T> e) {
        for (int i = 0; i < this.size(); i++) {
            e.each(i, this.get(i));
        }
    }

    public void forEachBreakable(EachBreakable<T> e) {
        for (int i = 0; i < this.size(); i++) {
            byte r = e.each(i, this.get(i));

            if (r == EachBreakable.BREAK)
                break;
            else if (r == EachBreakable.SKIP_NEXT)
                i++;
        }
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o)
            return true;

        if (o instanceof List<?>) {
            List<?> other = (List<?>) o;

            if (other.size() != this.size())
                return false;

            final boolean[] r = new boolean[] {true};

            this.forEachBreakable((i, t) -> {
                if (!t.equals(other.get(i))) {
                    r[0] = false;
                    return EachBreakable.BREAK;
                }

                return EachBreakable.CONTINUE;
            });

            return r[0];
        }
        return false;
    }

    public interface Count<T> {
        boolean count(int index, T t, int count);
    }

    public interface RemoveIf<T> {
        boolean remove(int index, T t, int countRemoveList);
    }

    public interface Each<T> {
        void each(int index, T t);
    }

    public interface EachBreakable<T> {
        byte BREAK = 0x0;
        byte CONTINUE = 0x1;
        byte SKIP_NEXT = 0x2;

        byte each(int i, T t);
    }

    public interface AddAll<T> {
        T add(int index);
    }

    public interface SearchList<T> {
        boolean isItem(int index, T t);
    }

    public interface MapList<K,T> {
        K getKey(int index, T t);
    }

    public interface ExtractList<I, O> {
        O extract(int index, I i, int count);
    }

    public interface ExtractListToMap<K, IT, OT> {
        Entry<K, OT> map(int index, IT i, int count);
    }
}