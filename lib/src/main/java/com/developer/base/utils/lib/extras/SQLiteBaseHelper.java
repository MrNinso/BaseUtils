package com.developer.base.utils.lib.extras;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.developer.base.utils.lib.object.BaseEntry;
import com.developer.base.utils.lib.object.BaseList;
import com.developer.base.utils.lib.object.BaseMap;

public abstract class SQLiteBaseHelper extends SQLiteOpenHelper {

    public SQLiteBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public SQLiteBaseHelper(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    public void forEach(Cursor c, forEach f) {
        if (c.moveToFirst()) {
            do {
                f.each(c);
            } while (c.moveToNext());
        }

        c.close();
    }

    public <V> BaseList<V> readList(String query, String[] args, QueryToList<V> q) {
        BaseList<V> result = new BaseList<>();

        forEach(getReadableDatabase().rawQuery(query, args), c ->
                result.add(q.next(c, result.size()))
        );

        return result;
    }

    public <K, V> BaseMap<K,V> readMap(String query, String[] args, QueryToMap<K, V> q) {
        BaseMap<K,V> result = new BaseMap<>();

        forEach(getReadableDatabase().rawQuery(query, args), c ->
                result.put(q.next(c, result.size()))
        );

        return result;
    }

    protected interface forEach {
        void each(Cursor c);
    }

    protected interface QueryToList<V> {
        V next(Cursor c, int count);
    }

    protected interface QueryToMap<K, V> {
        BaseEntry<K, V> next(Cursor c, int count);
    }
}
