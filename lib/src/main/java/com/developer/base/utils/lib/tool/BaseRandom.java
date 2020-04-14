package com.developer.base.utils.lib.tool;

import java.util.Random;

public class BaseRandom {
    private static BaseRandom mIntance;

    private Random mRandom;

    private BaseRandom() {
        this.mRandom = new Random();
    }

    public static BaseRandom getIntace() {
        if (mIntance == null) {
            mIntance = new BaseRandom();
        }

        return mIntance;
    }

    public void setSeed(long seed) {
        this.mRandom = new Random(seed);
    }

    public Random getRandom(){
        return this.mRandom;
    }

    public int getRandomInt(int max, int min) {
        return (int) (this.mRandom.nextDouble() * (max - min + 1) + min);
    }

    public float getRandomFloat(float max, float min) {
        return (this.mRandom.nextFloat() * (max - min + 1) + min);
    }

    public int getRandomInt() {
        return this.getRandomInt(Integer.MAX_VALUE, Integer.MIN_VALUE);
    }

    public float getRandomFloat() {
        return getRandomFloat(Float.MAX_VALUE - 1, Float.MIN_VALUE);
    }

    public int getRandomPositiveInt(int max, int min) {
        return Math.abs(getRandomInt(max, min));
    }

    public int getRandomPositiveInt() {
        return getRandomPositiveInt(Integer.MAX_VALUE - 1, 0);
    }

    public float getRandomPositiveFloat(float max, float min) {
        return Math.abs(getRandomFloat(max, min));
    }

    public float getRandomPositiveFloat() {
        return getRandomPositiveFloat(Float.MAX_VALUE - 1, 0);
    }
}
