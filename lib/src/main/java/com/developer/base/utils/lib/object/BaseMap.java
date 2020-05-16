package com.developer.base.utils.lib.object;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class BaseMap<K, V> extends HashMap<K, V> {

    private BaseList<PutListener<K, V>> mOnPutObservers = new BaseList<>();
    private BaseList<RemoveListener<K, V>> mOnRemoveObservers = new BaseList<>();

    public BaseMap() {}

    public BaseMap(int size, PutAll<K, V> p) {
        this.putAll(size, p);
    }

    public BaseMap(Map<K, V> m) {
        putAll(m);
    }

    private V putVal(@NonNull K key, V value, boolean notify) {
        if (notify) {
            boolean isNewKey = this.containsKey(key);
            mOnPutObservers.forEach((index, observer) -> observer.onPut(key, value, isNewKey));
        }
        return super.put(key, value);
    }

    @Nullable
    @Override
    public V put(@NonNull K key, V value) {
        return putVal(key, value, false);
    }

    public V putAndNotify(@NonNull K key, V value) {
        return putVal(key, value, true);
    }

    public V put(Entry<K, V> e) {
        return this.putVal(e.getKey(), e.getValue(), false);
    }

    public V putAndNotify(Entry<K, V> e) {
        return this.putVal(e.getKey(), e.getValue(), true);
    }

    private V putIfAbsentVal(@NonNull K key,@NonNull V value, boolean notify){
        if (!this.containsKey(key) || this.get(key) == null) {
            this.putVal(key, value, notify);
            return value;
        }
        return null;
    }

    @Nullable
    @Override
    public V putIfAbsent(@NonNull K key,@NonNull V value) {
        return putIfAbsentVal(key, value, false);
    }

    public V putIfAbsentAndNotify(@NonNull K key,@NonNull V value) {
        return putIfAbsentVal(key, value, true);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Override
    public void putAll(@NonNull Map<? extends K, ? extends V> m) {
        BaseMap<K, V> temp = (BaseMap<K, V>) m;

        BaseList<Boolean> isNew = new BaseList<>(temp.size(), i ->
                this.containsKey(temp.getKeyList().get(i))
        );

        temp.forEach((i, key, value) ->
                mOnPutObservers.forEach((index, observer) -> observer.onPut(key, value, isNew.get(i)))
        );

        super.putAll(temp);

    }

    @Nullable
    @Override
    public V remove(@Nullable Object key) {
        if (key != null) {
            V temp = super.remove(key);

            mOnRemoveObservers.forEach((index, observer) -> observer.onRemove((K) key, temp));

            return temp;
        }
        return null;
    }

    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        if (this.containsKey(key)) {
            if (this.get(key) == value) {
                this.remove(key);
                return true;
            }
        }
        return false;
    }



    public boolean removeIf(Remove<K, V> r) {
        Iterator<Entry<K, V>> iterator = this.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<K, V> e = iterator.next();
            if (r.remove(e.getKey(), e.getValue())) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    public V get(K k, V defaultValue) {
        return (this.containsKey(k)) ? this.get(k) : defaultValue;
    }



    public BaseMap<K, Boolean> putAllAbsent(Map<K, V> m) {
        BaseMap<K, Boolean> r = new BaseMap<>();

        ((BaseMap<K, V>) m).forEach((i, key, value) -> {
            if (this.containsKey(key)) {
                if (this.get(key) == null) {
                    this.put(key, value);
                    r.put(key, true);
                    return;
                }
            }
            r.put(key, false);
        });

        return r;
    }

    public BaseMap<K, Boolean> putAllAbsent(int size, PutAll<K, V> p) {
        return this.putAllAbsent(new BaseMap<>(size, p));
    }

    public void putAll(int size, PutAll<K, V> p) {
        for (int i = 0; i < size; i++) {
            this.put(p.put(i));
        }
    }

    public BaseList<K> getKeyList() {
        BaseList<K> keys = new BaseList<>();
        keys.addAll(this.keySet());
        return (BaseList<K>) keys.clone();
    }

    public int countIf(Count<K, V> c) {
        final int[] count = {0};

        forEach((i, k, v) -> count[0] += (c.onItem(i, k, v, count[0])) ? 1 : 0);

        return count[0];
    }

    public <OK, OV> BaseMap<OK, OV> extract(Extract<K, V, OK, OV> e) {
        BaseMap<OK, OV> reuslt = new BaseMap<>();

        forEach((i, k, v) -> {
            Entry<OK, OV> entry = e.onItem(i, k, v, reuslt.size());

            if (entry != null) {
                reuslt.put(entry);
            }
        });

        return reuslt;
    }

    public <O> BaseList<O> extractList(ExtractList<K, V, O> e) {
        BaseList<O> result = new BaseList<>();

        forEach((i, k, v) -> {
            O o = e.onItem(i, k, v, result.size());

            if (o != null) {
                result.add(o);
            }
        });

        return result;
    }

    public void forEach(Each<K, V> e) {
        getKeyList().forEach((i, k) ->
                e.onItem(i, k, this.get(k))
        );
    }

    public void forEachBreakable(EachBreakable<K, V> e) {
        getKeyList().forEachBreakable((i, k) ->
                e.onItem(i, k, this.get(k))
        );
    }

    public boolean addOnPutListener(PutListener<K, V> p) {
        return this.mOnPutObservers.addIfAbsent(p);
    }

    public boolean addOnRemoveListener(RemoveListener<K, V> r) {
        return this.mOnRemoveObservers.addIfAbsent(r);
    }

    /**
     * Remove Observers and clear the map
     */
    @Override
    public void clear() {
        clearObservers();
        clearMap();
    }

    /**
     * Remove Observers
     */
    public void clearObservers() {
        mOnPutObservers.clear();
        mOnRemoveObservers.clear();
    }

    /**
     * clear just the map
     */
    public void clearMap() {
        super.clear();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o)
            return true;

        if(!(o instanceof Map))
            return false;

        if (o instanceof BaseMap<?, ?>) {
            BaseMap<?,?> other = (BaseMap<?, ?>) o;

            if (other.size() != this.size())
                return false;

            if (!other.mOnRemoveObservers.equals(this.mOnRemoveObservers))
                return false;

            if (!other.mOnPutObservers.equals(this.mOnPutObservers))
                return false;

            final boolean[] result = new boolean[]{true};

            this.forEachBreakable((i, key, value) -> {
                Object otherVal = other.get(key);
                if (otherVal == null) {
                    result[0] = false;
                    return  EachBreakable.BREAK;
                }

                if (!Objects.equals(value, otherVal)) {
                    result[0] = false;
                    return  EachBreakable.BREAK;
                }

                return EachBreakable.CONTINUE;
            });

            return result[0];
        } else {
            Map<?, ?> other = (Map<?, ?>) o;

            if (other.size() != this.size())
                return false;

            final boolean[] result = new boolean[]{true};

            this.forEachBreakable((i, key, value) -> {
                Object otherValue = other.get(key);
                if (!Objects.equals(value, otherValue))
                    result[0] = false;

                return result[0] ? EachBreakable.CONTINUE : EachBreakable.BREAK;
            });

            return result[0];
        }
    }

    @Override
    public int hashCode() {
        return this.extract((i, key, value, count) -> new BaseEntry<>(key, value)).hashCode();
    }

    public interface Each<K, V> {
        void onItem(int i, K key, V value);
    }

    public interface EachBreakable<K, V> {
        byte BREAK = 0x0;
        byte CONTINUE = 0x1;
        byte SKIP_NEXT = 0x2;

        byte onItem(int i, K key, V value);
    }

    public interface PutAll<K, V> {
        BaseEntry<K, V> put(int index);
    }

    public interface Count<K, V> {
        boolean onItem(int i, K k, V v,int count);
    }

    public interface Remove<K, V> {
        boolean remove(K k, V v);
    }

    public interface Extract<IK, IV, OK, OV> {
        BaseEntry<OK, OV> onItem(int i, IK key, IV value, int count);
    }

    public interface ExtractList<K, IV, OV> {
        OV onItem(int index,K key, IV value, int count);
    }

    public interface PutListener<K, V> {
        void onPut(K key, V value, boolean isNewKey);
    }

    public interface RemoveListener<K, V> {
        void onRemove(K key, V value);
    }

}
