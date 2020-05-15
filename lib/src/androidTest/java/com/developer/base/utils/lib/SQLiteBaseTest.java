package com.developer.base.utils.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.platform.app.InstrumentationRegistry;

import com.developer.base.utils.lib.extras.SQLiteBaseHelper;
import com.developer.base.utils.lib.object.BaseList;

import org.junit.Before;

public class SQLiteBaseTest {
    private static SQLiteBaseHelper helper;
    private static BaseList<Integer> dataBase;
    @Before
    public void setup() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        helper = new SQLiteBaseHelper(appContext, "tests.db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                String table =
                        "CREATE TABLE TEST(" +
                                "A TEXT," +
                                "B INTEGER" +
                                ")";

                db.execSQL(table);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        };

        dataBase = new BaseList<>(66, (i) -> i);

        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("TEST", null, null);

        dataBase.forEach((index, integer) -> {
            ContentValues cv = new ContentValues();

            cv.put("B", integer);
            cv.put("A", String.valueOf(integer));

            db.insert("TEST", null, cv);
        });
    }

    
}
