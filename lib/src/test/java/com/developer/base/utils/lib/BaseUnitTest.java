package com.developer.base.utils.lib;

import android.util.Base64;

import com.developer.base.utils.lib.object.BaseEntry;
import com.developer.base.utils.lib.object.BaseList;
import com.developer.base.utils.lib.object.BaseMap;
import com.developer.base.utils.lib.tool.BaseCrypto;
import com.developer.base.utils.lib.tool.BaseRandom;
import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BaseUnitTest {
    BaseList<String> Fruits = new BaseList<>();
    BaseList<Integer> Random;
    BaseMap<String, Float> FruitsPrices = new BaseMap<>();
    
    @Before
    public void setup() {
        Fruits.add("Banana");
        Fruits.add("Apple");
        Fruits.add("Orange");
        Fruits.add("Guava");
        Fruits.add("Tangerine");
        Fruits.add("Strawberry");
        Fruits.add("Pear");

        Random = new BaseList<>(
                Fruits.size(),
                index -> BaseRandom.getIntace().getRandomInt(100,0)
        );

        FruitsPrices = Fruits.extractMap((i, s, count) ->
                new BaseEntry<>(s, BaseRandom.getIntace().getRandomPositiveFloat())
        );
    }

    @Test
    public void ArrayToList() {
        String[] fArray = Fruits.toArray(new String[0]);

        Assert.assertEquals(
                new Gson().toJson(fArray), Fruits.toJson()
        );

        BaseList<String> fList = new BaseList<>(fArray);

        Assert.assertEquals(fList, Fruits);

        Integer[] rArray = Random.toArray(new Integer[0]);

        Assert.assertEquals(
                new Gson().toJson(rArray), Random.toJson()
        );

        BaseList<Integer> rList = new BaseList<>(rArray);

        Assert.assertEquals(rList, Random);
    }

    @Test
    public void searchList() {
        String f = Fruits.getRandom();

        Assert.assertEquals(Fruits.search((index, s) -> s.equals(f)), Fruits.indexOf(f));
    }

    @Test
    public void countList() {
        int pairs = 0;

        for (int i = 0; i < Random.size(); i++) {
            if (Math.abs(Random.get(i) % 2) == 0 ) pairs++;
        }

        Assert.assertEquals(pairs, Random.countIf((index, i, count) -> Math.abs(i % 2) == 0));

        Assert.assertEquals(
                pairs,
                Random.extract((index, i, count) -> (Math.abs(i % 2) == 0) ? i : null).size()
        );
    }

    @Test
    public void mapList() {
        BaseMap<Integer, String> indexMap = Fruits.map((index, s) -> index);
        BaseMap<String, Integer> fruitsMap = Fruits.extractMap((index, f, count) ->
                new BaseEntry<>(f, index)
        );

        Assert.assertEquals(indexMap.size(), fruitsMap.size());

        indexMap.forEach((i, key, value) -> {
            int index = fruitsMap.get(value, -1);

            Assert.assertEquals(index, Fruits.indexOf(value));
        });
    }

    @Test
    public void Base64Test() {
        BaseMap<String, byte[]> fB64Map = Fruits.extractMap((index, i, count) ->
            new BaseEntry<>(i, BaseCrypto.getInstance().toBase64(i.getBytes(StandardCharsets.UTF_8)))
        );

        Fruits.forEach((index, s) -> {
            System.out.println(s);
            System.out.println(Arrays.toString(fB64Map.get(s)));
            System.out.println(new String(fB64Map.get(s), StandardCharsets.UTF_8));
            System.out.println(new String(BaseCrypto.getInstance().fromBase64(fB64Map.get(s)), StandardCharsets.UTF_8));
        });
    }



}