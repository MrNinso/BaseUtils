package com.developer.base.utils.lib.extras.recyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.base.utils.lib.object.BaseList;

import java.util.Collection;

public class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseHolder<T>> implements Filterable {

    private final BaseList<T> mItems;
    private final BaseHolder.BaseHolderFactory<T> mHolderFactory;
    private final SearchHandle<T> mSearchHandler;
    private final int mItemLayout;
    private final Filter mItemsFilter = buildFilter();
    private BaseList<T> mItemsFiltered;
    private OnBindListener<T> mOnBindListener;


    public BaseRecyclerViewAdapter(Collection<T> items, BaseHolder.BaseHolderFactory<T> holderFactory, SearchHandle<T> searchHandler, int itemLayout) {
        mItems = new BaseList<>(items);
        mHolderFactory = holderFactory;
        mSearchHandler = searchHandler;
        mItemLayout = itemLayout;
        mItemsFiltered = mItems;
    }


    public BaseRecyclerViewAdapter(Collection<T> items, BaseHolder.BaseHolderFactory<T> holderFactory, int itemLayout) {
        mItems = new BaseList<>(items);
        mHolderFactory = holderFactory;
        mItemLayout = itemLayout;
        mSearchHandler = null;
    }

    @NonNull
    @Override
    public BaseHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mHolderFactory.build(
                LayoutInflater.from(parent.getContext()).inflate(mItemLayout, parent, false),
                viewType
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder<T> holder, int position) {
        if (mOnBindListener != null)
            mOnBindListener.onBind(holder, position);

        holder.onBind((mItemsFiltered != null ? mItemsFiltered : mItems).get(position), position);
    }

    @Override
    public int getItemCount() {
        return (mItemsFiltered != null ? mItemsFiltered : mItems).size();
    }

    @Override
    public Filter getFilter() {
        return mItemsFilter;
    }

    public void setOnBindListener(OnBindListener<T> onBindListener) {
        mOnBindListener = onBindListener;
    }

    private Filter buildFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence searcher) {
                String search = searcher.toString();
                FilterResults f = new FilterResults();
                if (mSearchHandler == null) {
                    f.values = mItems;
                    return f;
                }

                if (search.isEmpty())
                    mItemsFiltered = mItems;
                else {
                    mItemsFiltered = mItems.extract((index, t, count) -> {
                        if (mSearchHandler.onSearch(t, search, count))
                            return t;
                        return null;
                    });
                }

                f.values = mItemsFiltered;

                return f;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }

    public interface SearchHandle<T> {
        boolean onSearch(T t, String searchText, int filteredListSize);
    }

    public interface OnBindListener<T> {
        void onBind(BaseHolder<T> holder, int position);
    }
}
