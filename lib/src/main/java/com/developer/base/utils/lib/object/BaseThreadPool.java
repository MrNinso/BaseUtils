package com.developer.base.utils.lib.object;

import androidx.annotation.NonNull;

public class BaseThreadPool {

    private ConcurrentBaseMap<Integer, BaseTask> mTaskPool = new ConcurrentBaseMap<>();
    private BaseList<BaseTask> mQueue = new BaseList<>();
    private BaseList<Thread> mThreads = new BaseList<>();

    private boolean mRemovingThread = false;
    private int mCores = 0;

    public BaseThreadPool() {
        init(1);
    }

    public BaseThreadPool(int cores) {
        init(cores);
    }

    private void init(int cores) {
        this.mCores = cores;
        this.mTaskPool.putAll(cores, index -> new BaseEntry<>(index, null));

        mTaskPool.addOnPutListener((key, task, isNewKey) -> {
            if (task != null) {
                task.mExit = task1 -> {
                    mTaskPool.removeIf((integer, task2) ->
                            task1 == task2
                    );

                    mThreads.remove(Thread.currentThread());

                    Integer nextCore = getNextFreeCore();
                    while (nextCore != null && mQueue.size() > 0) {
                        mTaskPool.put(nextCore, BaseOptional.of(mQueue.removeIfExists(0)));
                        nextCore = getNextFreeCore();
                    }
                    return null;
                };

                Thread thread = new Thread(task, String.valueOf(key));
                thread.start();
                mThreads.add(thread);
            }
        });
    }

    private Integer getNextFreeCore() {
        for (int i = 0; i < this.mCores; i++) {
            if (mTaskPool.get(i, null) == null) {
                return i;
            }
        }
        return  null;
    }

    public void addTask(@NonNull BaseTask task) {
        this.mQueue.add(task);
        Integer nextFreeCore = this.getNextFreeCore();
        if (nextFreeCore != null && this.mQueue.size() == 1) {
            mTaskPool.put(nextFreeCore, BaseOptional.of(this.mQueue.removeIfExists(0)));
        }
    }

    public void addCore() {
        this.mTaskPool.put(this.mTaskPool.size(), BaseOptional.empty());
    }

    public void addCores(int cores) {
        for (int i = 0; i < cores; i++) {
            this.mTaskPool.put(this.mTaskPool.size(), BaseOptional.empty());
        }
    }

    public BaseList<BaseTask> getQueue() {
        return new BaseList<>(mQueue);
    }

    public int getCores() {
        return this.mCores;
    }

    public int getFreeCores() {
        int free = 0;

        for (int i = 0; i < this.mCores; i++) {
            if (this.mTaskPool.get(i, null) == null) {
                free++;
            }
        }

        return free;
    }

    public void reboot() {
        mQueue.clear();
        mQueue = new BaseList<>();

        mThreads.forEach((index, thread) -> {

            if (thread != null) {
                if (thread.isAlive())
                    thread.interrupt();
            }
        });
        mThreads.clear();
        mThreads = new BaseList<>();

        int cores = mTaskPool.size();
        mTaskPool.clear();

        init(cores);
    }
}
