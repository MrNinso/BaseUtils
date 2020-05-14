package com.developer.base.utils.lib.tool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.developer.base.utils.lib.object.BaseTask;
import com.developer.base.utils.lib.object.BaseThreadPool;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class BaseDevice {

    private static BaseApp mApp;
    private static BaseScreen mScreen;
    private static BaseVibrator mVibrator;

    private static BaseThreadPool mAsync = new BaseThreadPool();

    private BaseDevice() {
        throw new UnsupportedOperationException("u can't do this");
    }

    public static void execulteAsync(BaseTask task) {
        if (mAsync.getQueue().size()/2 >= mAsync.getCores()) {
            mAsync.addCore();
        }

        mAsync.addTask(task);
    }

    public static void execulteAsyncAndRepeat(int repeat, BaseTask task) {
        execulteAsyncAndRepeat(repeat, 0, task);
    }

    public static void execulteAsyncAndRepeat(int repeat, long delay, BaseTask task) {
        BaseTask repeatTask = new BaseTask((task1 -> {
            repeat(repeat, delay, task);
            return null;
        }));

        execulteAsync(repeatTask);
    }

    private static void repeat(int repeat, long delay, BaseTask task) {
        for (int i = 0; i < repeat; i++) {
            try {
                task.run();
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static BaseThreadPool getAsyncPool() {
        return mAsync;
    }

    public static BaseApp getApp() {
        if (mApp == null){
            throw new NullPointerException("Please do BaseDevice.getApp(Application app) first");
        }

        return mApp;
    }

    public static BaseApp getApp(Application application) {
        if (mApp == null) {
            mApp = new BaseApp(application);
        }

        return mApp;
    }

    public static BaseScreen getScreen() {
        if (mScreen == null) {
            mScreen = new BaseScreen();
        }

        return mScreen;
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    public static BaseVibrator getVibrator() {
        if (mVibrator == null) {
            if (mApp == null) {
                throw new NullPointerException(
                        "Please do BaseDevice.getApp(Application app) first," +
                                " or BaseDevice.getVibrator(Context c)"
                );
            }
            mVibrator = new BaseVibrator(mApp.getApplication());
        }

        return mVibrator;
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    public static BaseVibrator getVibrator(Context c) {
        if (mVibrator == null) {
            mVibrator = new BaseVibrator(c);
        }

        return mVibrator;
    }

    public static class BaseApp {
        private Application nApp;

        private BaseApp() {
            throw new UnsupportedOperationException("u can't do this please try BaseDevice.getApp()");
        }

        private BaseApp(Application app) {
            this.nApp = app;
        }

        public Application getApplication() {
            return this.nApp;
        }

        @SuppressLint("HardwareIds")
        public String getAndroidID() {
            return Settings.Secure.getString(this.nApp.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        public int getAppID() {
            return nApp.getApplicationInfo().uid;
        }

        public int getAppResourceIDFromName(String defType, String name) {
            return getAppResourceIDFromName(getApplication(), defType, name);
        }

        public int getAppResourceIDFromName(@NonNull Context c, String deftype, String name) {
            String appPackage = c.getApplicationContext().getPackageName();

            return getResourceIDFromName(c.getResources(), appPackage, deftype, name);
        }

        public int getAndroidResourceIDFromName(String defType, String name) {
            return getAndroidResourceIDFromName(getApplication(), defType, name);
        }

        public int getAndroidResourceIDFromName(@NonNull Context c, String defType, String name) {
            return getResourceIDFromName(c.getResources(), "android", defType, name);
        }

        public int getAndroidResourceIDFromName(@NonNull Resources resources, String defType, String name) {
            return getResourceIDFromName(resources, "android", defType, name);
        }

        public int getResourceIDFromName(@NonNull Resources resources, String pack, String defType, String name) {
            return resources.getIdentifier(name, defType, pack);
        }
    }

    public static class BaseScreen {

        public DisplayMetrics getScreenSizePixels(Activity activity) {
            DisplayMetrics dm = new DisplayMetrics();

            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

            return dm;
        }

        public Bitmap screenShotFullScreen(Activity activity) {
            DisplayMetrics dm = getScreenSizePixels(activity);

            return screenShot(
                    activity,
                    new Point(0, 0),
                    new Point(dm.widthPixels, dm.heightPixels)
            );
        }

        public Bitmap screenShotFullScreenWithoutStatusBar(Activity activity) {
            int statusBarHeight = activity.getResources().getDimensionPixelSize(
                    BaseDevice.getApp().getAndroidResourceIDFromName(
                            activity.getResources(),
                            "dimen",
                            "status_bar_height"
                    )
            );

            DisplayMetrics dm = getScreenSizePixels(activity);

            return screenShot(
                    activity,
                    new Point(0,0),
                    new Point(dm.widthPixels, dm.heightPixels - statusBarHeight)
            );
        }

        public Bitmap screenShot(Activity activity, Point start, Point end) {
            View root = activity.getWindow().getDecorView();

            boolean drawingCache = root.isDrawingCacheEnabled();
            boolean noCache = root.willNotCacheDrawing();

            root.setDrawingCacheEnabled(true);
            root.setWillNotCacheDrawing(false);

            Bitmap screen = root.getDrawingCache();

            if (screen == null) {
                root.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                root.layout(0, 0, root.getMeasuredWidth(), root.getMeasuredHeight());
                root.buildDrawingCache();
                screen = Bitmap.createBitmap(root.getDrawingCache());
            }

            if (screen == null) return null;

            Bitmap shot = Bitmap.createBitmap(screen, start.x, start.y, end.x, end.y);

            root.destroyDrawingCache();
            root.setWillNotCacheDrawing(noCache);
            root.setDrawingCacheEnabled(drawingCache);

            return shot;
        }
    }

    public static class BaseVibrator {
        private Vibrator nVibrator;

        private BaseVibrator() {
            throw new UnsupportedOperationException("u can't do this please try BaseDevice.getApp()");
        }

        private BaseVibrator(Context c) {
            this.nVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        }

        public Vibrator getVibrator() {
            return this.nVibrator;
        }

        //TODO

    }


}
