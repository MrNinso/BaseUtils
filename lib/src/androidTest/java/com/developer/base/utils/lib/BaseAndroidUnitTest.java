package com.developer.base.utils.lib;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.developer.base.utils.lib.object.BaseTask;
import com.developer.base.utils.lib.object.BaseThreadPool;
import com.developer.base.utils.lib.tool.BaseDevice;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class BaseAndroidUnitTest {
    @Test
    public void TestDevicePool() {
        AtomicInteger count = new AtomicInteger();
        BaseTask task = new BaseTask(task1 -> {
            System.out.println("running");
            count.incrementAndGet();
            System.out.println(new Date().toString());
            return new Object();
        });

        task.addFailListener((task1, stage, e) -> {
            System.out.println(String.format("Error %s - %s", stage, e.getMessage()));
            Assert.fail();
        });

        task.addPostRunListener((task1, result) ->{
            System.out.println("Task finished");
            Assert.assertNotNull(result);
        });

        //Just execulte
        BaseDevice.execulteAsync(task);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //With Repeat
        BaseDevice.execulteAsyncAndRepeat(5, task);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //With Repeat and delay
        BaseDevice.execulteAsyncAndRepeat(5, 1000, task);

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //END
        Assert.assertEquals(BaseDevice.getAsyncPool().getCores(), 1);
        Assert.assertEquals(count.get(), 11);
    }

    @Test
    public void MyPool() {
        BaseThreadPool pool = new BaseThreadPool(3);

        int testSize = 10;
        AtomicInteger count = new AtomicInteger();

        BaseTask task = new BaseTask(task1 -> {
            count.incrementAndGet();
            return count.get();
        });

        System.out.println(new Date().toString());

        for (int i = 0; i < testSize; i++) {
            pool.addTask(task);
        }

        while (count.get() < testSize) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Assert.assertEquals(count.get(), testSize);
        pool.reboot();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.developer.base.utils.lib.test", appContext.getPackageName());
    }
}
