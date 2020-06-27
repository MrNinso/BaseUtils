package com.developer.base.utils.lib.extras.recyclerView;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseHolder<T> extends RecyclerView.ViewHolder {

    public BaseHolder(@NonNull View itemView, int viewType) {
        super(itemView);
        itemView.setOnClickListener(this::onClick);
    }

    protected void onClick(View view) {}

    public abstract void onBind(T t, int position);

    public interface BaseHolderFactory<T> {
        BaseHolder<T> build(View itemView, int viewType);
    }
}
