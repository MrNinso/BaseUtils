package com.developer.base.utils.lib;

import com.developer.base.utils.lib.object.BaseEntry;
import com.developer.base.utils.lib.object.BaseOptional;
import com.developer.base.utils.lib.object.ConcurrentBaseMap;
import com.developer.base.utils.lib.tool.BaseRandom;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.TreeMap;

public class MapTest {
    TreeMap<Integer, String> Reference = new TreeMap<>();

    @Before
    public void setup() {

        int size = BaseRandom.getIntace().getRandomPositiveInt(5, 1);

        for (int i = 0; i < size; i++) {
            BaseEntry<Integer, String> e = new BaseEntry<>(i, String.format("Item %d/%d", i, size));
            Reference.put(e.getKey(), e.getValue());
        }

    }

    /* *************** ConcurrentBaseMap *************** */
    private static int ConcurrentPutCount = 0;

    @Test
    public void ConcurrentPut() {
        TreeMap<Integer, String> r = (TreeMap<Integer,  String>) Reference.clone();

        /* *************** Put with constructor *************** */

        // Using Interface
        ConcurrentBaseMap<Integer, String> testMap = new ConcurrentBaseMap<>(r.size(), index ->
            new BaseEntry<>(index, r.get(index))
        );
        Assert.assertNotSame(testMap, r);
        Assert.assertEquals(testMap, r);

        // Using Map
        testMap = new ConcurrentBaseMap<>(r);
        Assert.assertNotSame(testMap, r);
        Assert.assertEquals(testMap, r);

        // Clear Test
        testMap.clear();
        Assert.assertNotEquals(testMap, r);

        /* *************** Put *************** */

        //add OnPutListener
        testMap.addOnPutListener((key, value, isNewKey) -> ConcurrentPutCount++);

        // Using Entry
        BaseEntry<Integer, String> e = new BaseEntry<>(ConcurrentPutCount, r.get(ConcurrentPutCount));
        testMap.putAndNotify(e);
        Assert.assertTrue(testMap.containsKey(e.getKey()));
        Assert.assertEquals(testMap.getEntry(e.getKey()), e);
        Assert.assertEquals(1,ConcurrentPutCount);

        // Using Entry - without notify
        e = new BaseEntry<>(ConcurrentPutCount, r.get(ConcurrentPutCount));
        testMap.put(e);
        Assert.assertTrue(testMap.containsKey(e.getKey()));
        Assert.assertEquals(testMap.getEntry(e.getKey()), e);

        // Clear Map test
        testMap.clearMap();
        Assert.assertEquals(0, testMap.getKeyList().size());

        // Using key and value
        testMap.putAndNotify(e.getKey(), BaseOptional.of(e.getValue()));
        Assert.assertTrue(testMap.containsKey(e.getKey()));
        Assert.assertEquals(testMap.getEntry(e.getKey()), e);
        Assert.assertEquals(2, ConcurrentPutCount);

        // Using key and value - without notify
        e = new BaseEntry<>(ConcurrentPutCount, r.get(ConcurrentPutCount));
        testMap.put(e.getKey(), BaseOptional.of(e.getValue()));
        Assert.assertTrue(testMap.containsKey(e.getKey()));
        Assert.assertEquals(testMap.getEntry(e.getKey()), e);

        /* *************** PutIfAbsent *************** */

        // Fail with Entry
        Assert.assertNull(testMap.putIfAbsentAndNotify(e));
        Assert.assertEquals(2, ConcurrentPutCount);

        // Fail with Entry - without notify
        Assert.assertNull(testMap.putIfAbsent(e));

        // Fail with key and value
        Assert.assertNull(testMap.putIfAbsentAndNotify(e.getKey(), BaseOptional.of(e.getValue())));
        Assert.assertEquals(2, ConcurrentPutCount);

        // Fail with key and value - without notify
        Assert.assertNull(testMap.putIfAbsent(e.getKey(), BaseOptional.of(e.getValue())));

        // Using Entry
        e = new BaseEntry<>(0, r.get(0));
        Assert.assertNotNull(testMap.putIfAbsentAndNotify(e));
        Assert.assertEquals(3, ConcurrentPutCount);

        // Using Entry - without notify
        e = new BaseEntry<>(ConcurrentPutCount, r.get(ConcurrentPutCount));
        Assert.assertNotNull(testMap.putIfAbsent(e));
        Assert.assertEquals(3, ConcurrentPutCount);

        // Using key and value
        e = new BaseEntry<>(ConcurrentPutCount+1, r.get(ConcurrentPutCount+1));
        Assert.assertNotNull(testMap.putIfAbsentAndNotify(e.getKey(), BaseOptional.of(e.getValue())));
        Assert.assertEquals(4, ConcurrentPutCount);

        // Using key and value - without notify
        e = new BaseEntry<>(ConcurrentPutCount+1, r.get(ConcurrentPutCount+1));
        Assert.assertNotNull(testMap.putIfAbsent(e.getKey(), BaseOptional.of(e.getValue())));

        /* *************** PutAll *************** */

        ConcurrentBaseMap<Integer, String> testCloneMap = testMap.cloneMap();
        Assert.assertNotSame(testCloneMap, testMap); // Clone Test
        Assert.assertEquals(testCloneMap, testMap);

        testMap.clear();


    }
}
