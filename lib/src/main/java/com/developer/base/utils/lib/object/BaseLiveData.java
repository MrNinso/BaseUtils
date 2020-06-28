package com.developer.base.utils.lib.object;

import com.developer.base.utils.lib.tool.BaseDevice;

public class BaseLiveData<T> {
    private final BaseList<OnUpdateListener<T>> UpdateListeners = new BaseList<>();
    private BaseOptional<T> Data = BaseOptional.empty();

    public void updateData(T data) {
        updateData(data, true, 0);
    }

    public void updateData(T data, boolean inMainThread) {
        updateData(data, inMainThread, 0);
    }

    public void updateData(T data, long delay) {
        updateData(data, true, delay);
    }

    public void updateData(T data, boolean inMainThread, long delay) {
        Data = BaseOptional.of(data);

        if (inMainThread) {
            BaseDevice.getMainThreadHandler().postDelayed(() ->
                    UpdateListeners.forEach((i, l) -> l.onUpdate(Data.get())), delay)
            ;
        } else {
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                    UpdateListeners.forEach((i, l) -> l.onUpdate(Data.get()));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public boolean addOnUpdateListener(OnUpdateListener<T> u) {
        return UpdateListeners.addIfAbsent(u);
    }

    public boolean removeOnUpdateListener(OnUpdateListener<T> u) {
        return UpdateListeners.remove(u);
    }

    public interface OnUpdateListener<T> {
        void onUpdate(T data);
    }
}
