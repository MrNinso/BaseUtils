package com.developer.base.utils.lib.extras;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

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

    @RequiresApi(api = Build.VERSION_CODES.P)
    public SQLiteBaseHelper(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    public void forEach(Cursor c, Each f) {
        if (c.moveToFirst()) {
            do {
                f.each(c);
            } while (c.moveToNext());
        }

        c.close();
    }

    public void forEachBreakable(Cursor c, EachBreakable f) {
        if (c.moveToFirst()) {
            do {
                byte r = f.each(c);
                if (r == EachBreakable.SKIP_NEXT)
                    c.moveToNext();
                else if (r == EachBreakable.BREAK)
                    break;
            } while (c.moveToNext());
        }
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

    public boolean doTransaction(Transaction t) {
        SQLiteDatabase db = getReadableDatabase();

        db.beginTransaction();

        boolean r = t.transaction(this);

        if (r) {
            db.setTransactionSuccessful();
        }

        db.endTransaction();
        return r;
    }

    public interface Each {
        void each(Cursor c);
    }

    public interface EachBreakable {
        byte BREAK = 0x0;
        byte CONTINUE = 0x1;
        byte SKIP_NEXT = 0x2;

        byte each(Cursor c);
    }

    public interface Transaction {
        boolean transaction(SQLiteBaseHelper helper);
    }

    public interface QueryToList<V> {
        V next(Cursor c, int count);
    }

    public interface QueryToMap<K, V> {
        BaseEntry<K, V> next(Cursor c, int count);
    }
}
