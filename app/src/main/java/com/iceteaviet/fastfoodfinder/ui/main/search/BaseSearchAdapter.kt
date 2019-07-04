package com.iceteaviet.fastfoodfinder.ui.main.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.main.search.model.SearchStoreItem
import java.util.*

/**
 * Created by Tom on 15/06/2019.
 */
abstract class BaseSearchAdapter internal constructor() : RecyclerView.Adapter<BaseSearchAdapter.BaseSearchViewHolder>() {

    protected val searchItems: MutableList<SearchStoreItem>
    private var onItemClickListener: OnItemClickListener? = null

    init {
        searchItems = ArrayList()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun addSearchItems(items: List<SearchStoreItem>) {
        val pos = searchItems.size
        searchItems.addAll(items)
        notifyItemRangeInserted(pos, items.size)
    }

    fun addSearchItem(item: SearchStoreItem) {
        searchItems.add(item)
        notifyItemRangeInserted(searchItems.size, 1)
    }

    fun setSearchItems(items: List<SearchStoreItem>) {
        searchItems.clear()
        searchItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseSearchViewHolder, position: Int) {
        val store = searchItems[position]
        holder.setData(store)
    }

    override fun getItemCount(): Int {
        return searchItems.size
    }

    interface OnItemClickListener {
        fun onClick(store: Store)
    }

    abstract inner class BaseSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val item = searchItems[position]
                item.store?.let {
                    onItemClickListener?.onClick(it)
                }
            }
        }

        abstract fun setData(item: SearchStoreItem)
    }
}
