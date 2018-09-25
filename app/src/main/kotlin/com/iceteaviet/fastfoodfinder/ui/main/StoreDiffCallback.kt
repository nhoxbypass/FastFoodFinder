package com.iceteaviet.fastfoodfinder.ui.main

import androidx.recyclerview.widget.DiffUtil
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store

/**
 * Created by tom on 7/21/18.
 */
class StoreDiffCallback(private val oldList: List<Store>?, private val newList: List<Store>?) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newList?.size ?: 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList!![oldItemPosition].id == newList!![newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList!![oldItemPosition].address == newList!![newItemPosition].address
    }
}
