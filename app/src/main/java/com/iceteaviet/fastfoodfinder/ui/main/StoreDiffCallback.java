package com.iceteaviet.fastfoodfinder.ui.main;

import android.support.v7.util.DiffUtil;

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;

import java.util.List;

/**
 * Created by tom on 7/21/18.
 */
public class StoreDiffCallback extends DiffUtil.Callback {
    private List<Store> oldList;
    private List<Store> newList;

    public StoreDiffCallback(List<Store> oldList, List<Store> newList) {
        super();
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getAddress().equals(newList.get(newItemPosition).getAddress());
    }
}
