package com.developer.base.utils.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.developer.base.utils.lib.extras.SQLiteBaseHelper;
import com.developer.base.utils.lib.object.BaseEntry;
import com.developer.base.utils.lib.object.BaseList;
import com.developer.base.utils.lib.object.BaseMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
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

            cv.put("A", String.valueOf(integer));
            cv.put("B", integer);

            db.insert("TEST", null, cv);
        });
    }

    @Test
    public void CheackDB() {
        helper.forEach(helper.getReadableDatabase().rawQuery("SELECT * FROM TEST", null), c -> {
            Assert.assertEquals(dataBase.size(), c.getCount());
            String A = c.getString(0);
            int B = c.getInt(1);

            Assert.assertEquals(String.valueOf(B), A);
            Assert.assertTrue(dataBase.contains(B));
        });
    }

    @Test
    public void readToList() {
        BaseList<Integer> i = helper.readList("SELECT * FROM TEST", null,
                (c, count) -> c.getInt(1)
        );

        Assert.assertEquals(dataBase, i);
    }

    @Test
    public void readToMap() {
        BaseMap<Integer, String> i = helper.readMap("SELECT * FROM TEST", null,
                (c, count) -> {
                    Assert.assertEquals(String.valueOf(c.getInt(0)),  c.getString(0));
                    return new BaseEntry<>(c.getInt(1), c.getString(0));
                }
        );

        BaseList<Integer> keys = i.getKeyList();

        dataBase.forEach((index, integer) ->
                Assert.assertTrue(keys.contains(integer))
        );
    }

    @Test
    public void doTransaction() {
        Assert.assertTrue(helper.doTransaction(
                (helper) -> {
                    int i = helper.getWritableDatabase()
                            .delete(
                                    "TEST",
                                    String.format("B >= %d", dataBase.size()+1),
                                    null
                            );
                    Assert.assertEquals(0, i);
                    return true;
                }
        ));

        Assert.assertFalse(helper.doTransaction(
                (helper) -> {
                    int i = helper.getWritableDatabase()
                            .delete(
                                    "TEST",
                                    String.format("B >= %d", dataBase.size()+1),
                                    null
                            );
                    Assert.assertEquals(0, i);
                    return false;
                }
        ));
    }
}
