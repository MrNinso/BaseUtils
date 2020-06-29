package com.developer.base.utils.lib.object;

import com.developer.base.utils.lib.tool.BaseDevice;

public class BaseTask implements Runnable {

    public final String PRE_RUN = "PRE_RUN";
    public final String ON_RUN = "ON_RUN";
    public final String POS_RUN = "POS_RUN";
    public final String POST_UI_THREAD = "POST_UI_THREAD";

    BaseRunnable mExit;

    private final BaseRunnable mRunnable;
    private final BaseList<FailListener> mFailListeners = new BaseList<>();
    private final BaseList<PreRunListener> mPreRunListeners = new BaseList<>();
    private final BaseList<PosRunListener> mPosRunListeners = new BaseList<>();

    public BaseTask(BaseRunnable run) {
        this.mRunnable = run;
    }

    @Override
    public void run() {
        mPreRunListeners.forEach((index, preRunListener) ->
                postInUIThread((t) -> {
                    preRunListener.onStart(t);
                    return null;
                }, PRE_RUN)
        );

        try {
            final Object result = mRunnable.run(this);

            mPosRunListeners.forEach((index, posRunListener) ->
                    postInUIThread((t) -> {
                        posRunListener.onFinish(this, result);
                        return null;
                    }, POS_RUN)
            );
        } catch (Exception e) {
            handleFail(ON_RUN, e);
        }

        try {
            if (mExit != null)
                mExit.run(BaseTask.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleFail(String stage, Exception e) {
        this.mFailListeners.forEach((index, listener) ->
            postInUIThread((t) -> {
                listener.onFail(BaseTask.this,stage, e);
                return null;
            }, stage)
        );
    }

    private void postInUIThread(BaseRunnable run, String stage) {
        BaseDevice.getMainThreadHandler().post(() -> {
            try {
                run.run(BaseTask.this);
            } catch (Exception e) {
                handleFail(stage, e);
            }
        });
    }

    public boolean addFailListener(FailListener f) {
        return mFailListeners.addIfAbsent(f);
    }

    public boolean addPreRunListener(PreRunListener p) {
        return this.mPreRunListeners.addIfAbsent(p);
    }

    public boolean addPostRunListener(PosRunListener r) {
        return this.mPosRunListeners.addIfAbsent(r);
    }

    public void updateUIThread(BaseRunnable run) {
        postInUIThread(run, POST_UI_THREAD);
    }

    public interface BaseRunnable {
        Object run(BaseTask task) throws Exception;
    }

    public interface FailListener {
        void onFail(BaseTask task, String stage, Exception e);
    }

    public interface PreRunListener {
        void onStart(BaseTask task);
    }

    public interface PosRunListener {
        void onFinish(BaseTask task, Object result);
    }
}
