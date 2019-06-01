package com.iceteaviet.fastfoodfinder.ui.main.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.custom.store.BaseStoreAdapter
import kotlinx.android.synthetic.main.item_recently_location.view.*

/**
 * Created by MyPC on 11/16/2016.
 */
class RecentlyStoreSearchAdapter internal constructor() : BaseStoreAdapter() {
    companion object {
        const val TYPE_SEARCH_RECENTLY = 0
        const val TYPE_SEARCH_RECENT_QUERY = 1
    }

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): BaseStoreViewHolder {
        when (viewType) {
            TYPE_SEARCH_RECENTLY -> {
                val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recently_store_search, parent, false)
                return RecentlySearchViewHolder(itemView)
            }
            TYPE_SEARCH_RECENT_QUERY -> {
                val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recently_store_search_text, parent, false)
                return RecentSearchQueryViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recently_store_search_text, parent, false)
                return RecentSearchQueryViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val store = storeList[position]

        return if (store.id != -1) {
            TYPE_SEARCH_RECENTLY
        } else {
            TYPE_SEARCH_RECENT_QUERY
        }
    }

    inner class RecentlySearchViewHolder(itemView: View) : BaseStoreViewHolder(itemView) {
        var txtTitle: TextView = itemView.tv_item_title
        var txtAddress: TextView = itemView.tv_item_address

        override fun setData(store: Store) {
            txtTitle.text = store.title
            txtAddress.text = store.address
        }
    }

    inner class RecentSearchQueryViewHolder(itemView: View) : BaseStoreViewHolder(itemView) {
        var txtTitle: TextView = itemView.tv_item_title

        override fun setData(store: Store) {
            txtTitle.text = store.title
        }
    }
}
