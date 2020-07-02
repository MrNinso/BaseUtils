package com.developer.base.utils.lib;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.developer.base.utils.lib.object.BaseLiveData;
import com.developer.base.utils.lib.object.BaseTask;
import com.developer.base.utils.lib.object.BaseThreadPool;
import com.developer.base.utils.lib.tool.BaseCrypto;
import com.developer.base.utils.lib.tool.BaseDevice;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    public void byteToHex() {
        byte[] bytes = new byte[] { 0xA, 0xB, 0xC, 0xD, 0xE, 0xF };
        Assert.assertEquals("0A0B0C0D0E0F",BaseCrypto.getInstance().bytesToHex(bytes));
    }

    @Test
    public void Base64Test() {
        byte[] msg = "This is a error".getBytes();

        byte[] msg1 = BaseCrypto.getInstance().fromBase64(
                BaseCrypto.getInstance().toBase64(msg)
        );

        Assert.assertEquals(
            new String(msg),
            new String(msg1)
        );
    }

    @Test
    public void HmacSHA512Test() {
        String s = BaseCrypto.getInstance().HmacSHA512Hex("key".getBytes(), "you can't see this by :)".getBytes());

        Assert.assertEquals(
                "b6b56d4ba00b9bd6e2e7ca278c07b5af1deea53bc8f2389fd53d70518a5076a31bb5d5b9361439bbc1a3f2c4fe341998b1da33b28869da0e02c030fb5e79cd6f",
                s.toLowerCase()
        );

    }

    @Test
    public void AESTest() {
        String pass = "@SuperP0werfulPassword!";
        String msg = "Please don't read";
        byte[] key = BaseCrypto.getInstance().makeAESSHA512Key(pass.getBytes(), 24);

        BaseCrypto.AESEncryptMsg encryptedMsg = BaseCrypto.getInstance().encryptAES(msg.getBytes(), key);

        Assert.assertNotNull(encryptedMsg);

        String dencryptMsg = new String(BaseCrypto.getInstance().decryptAES(encryptedMsg, key));

        Assert.assertEquals(msg, dencryptMsg);
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.developer.base.utils.lib.test", appContext.getPackageName());
    }

    @Test
    public void LiveDataMainToMain() {
        BaseLiveData<Byte> testLive = new BaseLiveData<>();

        final boolean[] notifyed = {false};

        testLive.addOnUpdateListener(data -> {
            assertNotNull(data);
            assertEquals(0x1, data.byteValue());
            notifyed[0] = true;
        });

        testLive.updateData((byte) 0x1);

        long end = System.currentTimeMillis() + 500;

        while (end > System.currentTimeMillis());

        assertTrue(notifyed[0]);
    }

    @Test
    public void LiveDataMainToBackground() {
        BaseLiveData<Byte> testLive = new BaseLiveData<>();

        final boolean[] notifyed = {false};

        testLive.addOnUpdateListener(data -> {
            assertNotNull(data);
            assertEquals(0x1, data.byteValue());
            notifyed[0] = true;
        });

        testLive.updateData((byte) 0x1, false);

        long end = System.currentTimeMillis() + 500;

        while (end > System.currentTimeMillis());

        assertTrue(notifyed[0]);
    }

    @Test
    public void LiveDataBackgroundToMain() {
        BaseLiveData<Byte> testLive = new BaseLiveData<>();

        final boolean[] notifyed = {false};

        testLive.addOnUpdateListener(data -> {
            assertNotNull(data);
            assertEquals(0x1, data.byteValue());
            notifyed[0] = true;
        });

        new Thread(() -> testLive.updateData((byte) 0x1)).start();

        long end = System.currentTimeMillis() + 500;

        while (end > System.currentTimeMillis());

        assertTrue(notifyed[0]);
    }

    @Test
    public void LiveDataBackgroundToBackground() {
        BaseLiveData<Byte> testLive = new BaseLiveData<>();

        final boolean[] notifyed = {false};

        testLive.addOnUpdateListener(data -> {
            assertNotNull(data);
            assertEquals(0x1, data.byteValue());
            notifyed[0] = true;
        });

        new Thread(() -> testLive.updateData((byte) 0x1, false)).start();

        long end = System.currentTimeMillis() + 500;

        while (end > System.currentTimeMillis());

        assertTrue(notifyed[0]);
    }

    @Test
    public void LiveDataJustNotify() {
        ArrayList<Byte> b = new ArrayList<>();
        BaseLiveData<ArrayList<Byte>> testLive = new BaseLiveData<>(b);

        final boolean[] notifyed = {false};

        testLive.addOnUpdateListener(data -> {
            assertNotNull(data);
            assertTrue(data.contains((byte) 0x1));
            notifyed[0] = true;
        });

        new Thread(() -> {
            b.add((byte) 0x1);
            testLive.notifyUpdate();
        }).start();

        long end = System.currentTimeMillis() + 500;

        while (end > System.currentTimeMillis());

        assertTrue(notifyed[0]);
    }
}
