package com.developer.base.utils.lib;

import com.developer.base.utils.lib.object.BaseEntry;
import com.developer.base.utils.lib.tool.BaseRandom;

import org.junit.Before;

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
}
