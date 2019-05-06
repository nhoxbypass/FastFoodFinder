package com.iceteaviet.fastfoodfinder.ui.custom.store

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.ItemTouchHelperAdapter
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.ItemTouchHelperViewHolder
import java.util.*

/**
 * Created by MyPC on 11/16/2016.
 */
abstract class BaseStoreAdapter internal constructor() : RecyclerView.Adapter<BaseStoreAdapter.BaseStoreViewHolder>(), ItemTouchHelperAdapter {

    protected val storeList: MutableList<Store>
    private var onItemClickListener: OnItemClickListener? = null

    init {
        storeList = ArrayList()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        return false
    }

    override fun onItemDismiss(position: Int) {
        storeList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun addStores(destinations: List<Store>) {
        val pos = storeList.size
        storeList.addAll(destinations)
        notifyItemRangeInserted(pos, destinations.size)
    }

    fun addStore(destination: Store) {
        storeList.add(destination)
        notifyItemRangeInserted(storeList.size, 1)
    }

    fun setStores(destinations: List<Store>) {
        storeList.clear()
        storeList.addAll(destinations)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseStoreViewHolder, position: Int) {
        val store = storeList[position]
        holder.setData(store)
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    interface OnItemClickListener {
        fun onClick(store: Store)
    }

    abstract inner class BaseStoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val store = storeList[position]
                if (onItemClickListener != null) {
                    onItemClickListener!!.onClick(store)
                }
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }

        abstract fun setData(store: Store)
    }
}
