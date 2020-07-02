package com.developer.base.utils.lib;

import com.developer.base.utils.lib.object.BaseMatrix;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BaseMatrixTest {

    @Test
    public void ReferenceTest() {
        TestObj t1 = new TestObj(0, "0");
        TestObj t2 = t1;

        t1.I = 1;
        t2.I = 2;

        assertEquals(2, t1.I);

    }

    @Test
    public void getPutTest() {
        BaseMatrix<String> s = new BaseMatrix<>();

        s.put(0,0, "MASTER");
        s.put(1,0, "RIGHT");
        s.put(0,1, "BELOW");
        s.put(5, 3, "WHAT???");
        s.put(2222,3333, "RIP");

        Assert.assertEquals("MASTER", s.get(0,0));
        Assert.assertEquals("RIGHT", s.get(1,0));
        Assert.assertEquals("WHAT???", s.get(5,3));
        Assert.assertEquals("RIP", s.get(2222,3333));
    }


    @Test
    public void getMatrixSize() {
        BaseMatrix<String> s = new BaseMatrix<>();

        s.put(0,0, "MASTER");
        s.put(1,0, "RIGHT");
        s.put(0,1, "BELOW");
        s.put(2222,3333, "RIP");
        s.put(5, 3, "WHAT???");

        int[] size = s.getMatrixSize();
        Assert.assertEquals(2222, size[0]);
        Assert.assertEquals(3333, size[1]);
    }

    @Test
    public void putIfAbsentTest() {
        BaseMatrix<String> s = new BaseMatrix<>();

        s.put(0,0, "MASTER");
        s.put(1,0, "RIGHT");
        s.put(0,1, "BELOW");
        s.put(2222,3333, "RIP");

        assertFalse(s.putIfAbsent(0, 0, "FAKE MASTER"));
        assertTrue(s.putIfAbsent(5, 3, "WHAT???"));

        assertEquals(s.get(0,0), "MASTER");
        assertEquals(s.get(5,3), "WHAT???");
    }

    @Test
    public void testRemove() {
        BaseMatrix<String> s = new BaseMatrix<>();

        s.put(0,0, "MASTER");
        s.put(1,0, "RIGHT");
        s.put(0,1, "BELOW");
        s.put(2222,3333, "RIP");
        s.put(5, 3, "WHAT???");
        s.put(5,4, "WHY????");

        assertFalse(s.remove(3,0));
        assertTrue(s.remove(0,1));
        assertTrue(s.remove(5,3));
        assertNull(s.get(5,3));
        assertTrue(s.remove(1,0));
        assertEquals("RIP", s.get(2222, 3333));
        assertEquals("WHY????", s.get(5, 4));
    }

    @Test
    public void forEach() {
        BaseMatrix<String> s = new BaseMatrix<>();

        s.put(0,0, "MASTER");
        s.put(1,0, "RIGHT");
        s.put(0,1, "BELOW");
        s.put(2222,3333, "RIP");

        int[] counts = new int[] { 0, 0 };

        s.forEach(true, (i, j, s1) -> counts[0]++);
        s.forEach(false, (i, j, s1) -> counts[1]++);

        assertEquals(4, counts[0]);
        assertEquals(7405926, counts[1]);
    }

    @Test
    public void forEachBreakable() {
        BaseMatrix<String> s = new BaseMatrix<>();

        s.put(0,0, "MASTER");
        s.put(1,0, "RIGHT");
        s.put(0,1, "BELOW");
        s.put(2222,3333, "RIP");

        int[] counts = new int[] { 0, 0 };

        s.forEachBreakable(true, (i, j, s1) -> {
            counts[0]++;

            if (counts[0] == 3)
                return BaseMatrix.EachBreakable.BREAK;

            return BaseMatrix.EachBreakable.CONTINUE;
        });

        s.forEachBreakable(true, (i, j, s1) -> {
            counts[1]++;

            if (counts[1] == 3)
                return BaseMatrix.EachBreakable.BREAK;

            return BaseMatrix.EachBreakable.CONTINUE;
        });

        assertEquals(3, counts[0]);
        assertEquals(3, counts[1]);
    }

    private static class TestObj {
        int I;
        String S;

        public TestObj(int anInt, String string) {
            I = anInt;
            S = string;
        }
    }

}
