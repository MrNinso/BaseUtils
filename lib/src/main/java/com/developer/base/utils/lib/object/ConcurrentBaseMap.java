package com.developer.base.utils.lib.object;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentBaseMap<K, V> extends ConcurrentHashMap<K, BaseOptional<V>> implements Cloneable {

    private final BaseList<PutListener<K, V>> mOnPutObservers = new BaseList<>();
    private final BaseList<RemoveListener<K, V>> mOnRemoveObservers = new BaseList<>();

    public ConcurrentBaseMap() {}

    /**
     * Put all entries returned from {@link PutAll} Interface
     * @param size How many times will reapet {@param putAll} Interface
     * @param putAll Inteface with {@code BaseEntry<K, V> put(int index)} method
     */
    public ConcurrentBaseMap(int size, PutAll<K, V> putAll) {
        this.putAll(size, putAll);
    }

    /**
     * Add all entries from a any map
     * @param map source Map
     */
    public ConcurrentBaseMap(Map<K, V> map) {
        super();
        putAllVal(map, false);
    }

    /**
     * Default way to put a entry in map
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @param notify if will notify all PutObservers from {@link #addOnPutListener(PutListener)}
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}
     */
    private BaseOptional<V> putVal(K key, BaseOptional<V> value, boolean notify) {
        if (notify) {
            boolean isNewKey = this.containsKey(key);

            mOnPutObservers.forEach((index, observer) -> observer.onPut(key, BaseOptional.from(value), isNewKey));
        }

        return super.put(key, value);
    }

    /**
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}
     */
    @Nullable
    @Override
    public BaseOptional<V> put(@NonNull K key, @NonNull BaseOptional<V> value) {
        return putVal(key, value, false);
    }

    /**
     * notify all PutObservers from {@link #addOnPutListener(PutListener)} and put the entry
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}
     */
    public BaseOptional<V> putAndNotify(@NonNull K key, BaseOptional<V> value) {
        return putVal(key, value, true);
    }

    /**
     * @param e Entry with a {@code key} and {@code value}
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}
     */
    public BaseOptional<V> put(Entry<K, V> e) {
        return this.putVal(e.getKey(), BaseOptional.of(e.getValue()), false);
    }

    /**
     * notify all PutObservers from {@link #addOnPutListener(PutListener)} and put the entry
     * @param e Entry with a {@code key} and {@code value}
     * @return the previous value associated with {@code key}, or
     *          {@code null} if there was no mapping for {@code key}
     */
    public BaseOptional<V> putAndNotify(Entry<K, V> e) {
        return this.putVal(e.getKey(), BaseOptional.of(e.getValue()), true);
    }

    /**
     *
     * @param key
     * @param value
     * @param notify
     * @return
     */
    private BaseOptional<V> putIfAbsentVal(K key, BaseOptional<V> value, boolean notify) {
        if (!this.containsKey(key) || this.get(key) == null) {
            this.putVal(key, value, notify);
            return value;
        }
        return null;
    }

    @Nullable
    @Override
    public BaseOptional<V> putIfAbsent(@NonNull K key, @NonNull BaseOptional<V> value) {
        return putIfAbsentVal(key, value, false);
    }

    public BaseOptional<V> putIfAbsentAndNotify(@NonNull K key, BaseOptional<V> value) {
        return putIfAbsentVal(key, value, true);
    }

    public BaseOptional<V> putIfAbsent(Entry<K, V> e) {
        return this.putIfAbsentVal(e.getKey(), BaseOptional.of(e.getValue()), false);
    }

    public BaseOptional<V> putIfAbsentAndNotify(Entry<K, V> e) {
        return this.putIfAbsentVal(e.getKey(), BaseOptional.of(e.getValue()), true);
    }

    private void putAllVal(Map<K, V> m, boolean notify) {
        new BaseList<>(m.keySet()).forEach((index, k) -> {
            V v = m.get(k);
            putVal(k, BaseOptional.of(v), notify);
        });
    }

    @Override
    public void putAll(@NonNull Map<? extends K, ? extends BaseOptional<V>> m) {
        super.putAll(m);
    }

    public void putAllAndNotify(Map<K, BaseOptional<V>> m) {
        new BaseList<>(m.keySet()).forEach((index, k) ->
                putVal(k, m.get(k), true)
        );
    }

    public void putAllMapEntry(@NonNull Map<K, V> m) {
        putAllVal(m, false);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public void putAllMapAndNotify(@NonNull Map<K, V> m) {
        putAllVal(m, true);
    }

    private ConcurrentBaseMap<K, V> makeMapFromPutAll(int size, PutAll<K, V> p) {
        ConcurrentBaseMap<K, V> map = new ConcurrentBaseMap<>();

        for (int i = 0; i < size; i++) {
            map.put(p.put(i));
        }

        return map;
    }

    public void putAll(int size, PutAll<K, V> p) {
        putAllVal((Map<K, V>) makeMapFromPutAll(size, p), false);
    }

    public void putAllNotify(int size, PutAll<K, V> p) {
        putAllVal((Map<K, V>) new ConcurrentBaseMap<>(size, p), true);
    }

    private BaseMap<K, Boolean> putAllAbsentVal(Map<K, V> m, boolean notify) {
        BaseMap<K, Boolean> r = new BaseMap<>();

        ((BaseMap<K, V>) m).forEach((i, key, value) -> {
            if (this.containsKey(key)) {
                if (this.get(key) == null) {
                    this.putVal(key, BaseOptional.of(value), notify);
                    r.put(key, true);
                    return;
                }
            }
            r.put(key, false);
        });

        return r;
    }

    public BaseMap<K, Boolean> putAllAbsent(Map<K, V> m) {
        return putAllAbsentVal(m, false);
    }

    public BaseMap<K, Boolean> putAllAbsentAndNotify(Map<K, V> m) {
        return putAllAbsentVal(m, true);
    }

    public BaseMap<K, Boolean> putAllAbsent(int size, PutAll<K, V> p) {
        return putAllAbsentVal((Map<K, V>) new ConcurrentBaseMap<>(size, p), false);
    }

    public BaseMap<K, Boolean> putAllAbsentAndNotify(int size, PutAll<K, V> p) {
        return putAllAbsentVal((Map<K, V>) new ConcurrentBaseMap<>(size, p), true);
    }

    public BaseList<K> getKeyList() {
        return (BaseList<K>) new BaseList<>(this.keySet()).clone();
    }

    public V get(K k, V defaultValue) {
        return (this.containsKey(k)) ? BaseOptional.from(this.get(k)) : defaultValue;
    }

    public Entry<K, V> getEntry(K k) {
        return containsKey(k) ? new BaseEntry<>(k, BaseOptional.from(get(k))) : null;
    }

    public int countIf(Count<K, V> c) {
        final int[] count = {0};

        forEach((i, k, v) -> count[0] += (c.onItem(i, k, v, count[0])) ? 1 : 0);

        return count[0];
    }

    private V removeVal(Object key, boolean notify) {
        if (key != null) {
            BaseOptional<V> temp = super.remove(key);

            if (notify && temp != null)
                mOnRemoveObservers.forEach((index, observer) -> observer.onRemove((K) key, BaseOptional.from(temp)));

            return BaseOptional.from(temp);
        }

        return null;
    }

    @Nullable
    @Override
    public BaseOptional<V> remove(@Nullable Object key) {
        if (key != null)
            return super.remove(key);
        else
            return null;
    }

    public V removeAndNotify(@Nullable Object key) {
        return removeVal(key, true);
    }

    private boolean removeVal(Object key,  Object value, boolean notify) {
        if (key != null)
            if (containsKey(key) && get(key) == value) {
                removeVal(key, notify);
                return true;
            }
        return false;
    }

    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        return removeVal(key, value, false);
    }

    public boolean removeAndNotify(@Nullable Object key, @Nullable Object value) {
        return removeVal(key, value, true);
    }

    private boolean removeIfVal(Remove<K, V> r, boolean notify) {
        final Object[] key = new Object[1];

        this.forEachBreakable((i, k, v) -> {
            if (r.remove(k, v)) {
                key[0] = k;
                return EachBreakable.BREAK;
            }
            return EachBreakable.CONTINUE;
        });

        return removeVal(key[0], notify) != null;
    }

    public boolean removeIf(Remove<K, V> r) {
        return removeIfVal(r, false);
    }

    public boolean removeIfAndNotify(Remove<K, V> r) {
        return removeIfVal(r, true);
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
                e.onItem(i, k, BaseOptional.from(this.get(k)))
        );
    }

    public void forEachBreakable(EachBreakable<K, V> e) {
        getKeyList().forEachBreakable((i, k) -> e.onItem(i, k, BaseOptional.from(this.get(k))));
    }

    public boolean addOnPutListener(PutListener<K, V> p) {
        return this.mOnPutObservers.addIfAbsent(p);
    }

    public boolean addOnRemoveListener(RemoveListener<K, V> r) {
        return this.mOnRemoveObservers.addIfAbsent(r);
    }

    public ConcurrentBaseMap<K, V> cloneMap(){
        return new ConcurrentBaseMap<>((Map<K, V>) this);
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

        if (o instanceof ConcurrentBaseMap<?, ?>) {
            ConcurrentBaseMap<?,?> other = (ConcurrentBaseMap<?, ?>) o;

            if (other.size() != this.size())
                return false;

            if (!other.mOnRemoveObservers.equals(this.mOnRemoveObservers))
                return false;

            if (!other.mOnPutObservers.equals(this.mOnPutObservers))
                return false;

            final boolean[] result = new boolean[]{true};

            this.forEachBreakable((i, key, value) -> {
                BaseOptional<?> otherValue = other.get(key);

                if (otherValue == null) {
                    result[0] = false;
                    return  EachBreakable.BREAK;
                }

                if (!value.equals(otherValue.get())) {
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
                if (!value.equals(otherValue))
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
