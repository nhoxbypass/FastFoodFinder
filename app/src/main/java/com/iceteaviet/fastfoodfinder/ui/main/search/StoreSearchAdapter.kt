package com.iceteaviet.fastfoodfinder.ui.main.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.ui.main.search.model.SearchStoreItem
import com.iceteaviet.fastfoodfinder.utils.Constant
import kotlinx.android.synthetic.main.item_recently_location.view.*

/**
 * Created by MyPC on 11/16/2016.
 */
class StoreSearchAdapter internal constructor() : BaseSearchAdapter() {
    companion object {
        const val TYPE_SEARCH_NORMAL = 0
        const val TYPE_SEARCH_RECENTLY = 1
        const val TYPE_SEARCH_RECENT_QUERY = 2
    }

    private var recentlySearch: List<String> = ArrayList()

    fun setRecentlySearch(recentlySearch: List<String>) {
        this.recentlySearch = recentlySearch
    }

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): BaseSearchViewHolder {
        when (viewType) {
            TYPE_SEARCH_NORMAL -> {
                val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_normal_store_search, parent, false)
                return NormalSearchViewHolder(itemView)
            }
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
        val item = searchItems[position]
        val store = item.store

        return if (store != null) {
            return if (recentlySearch.contains(Constant.SEARCH_STORE_PREFIX + store.id)) {
                TYPE_SEARCH_RECENTLY
            } else {
                TYPE_SEARCH_NORMAL
            }
        } else {
            TYPE_SEARCH_RECENT_QUERY
        }
    }

    inner class NormalSearchViewHolder(itemView: View) : BaseSearchViewHolder(itemView) {
        private var txtTitle: TextView = itemView.tv_item_title
        private var txtAddress: TextView = itemView.tv_item_address

        override fun setData(item: SearchStoreItem) {
            txtTitle.text = item.store?.title
            txtAddress.text = item.store?.address
        }
    }

    inner class RecentlySearchViewHolder(itemView: View) : BaseSearchViewHolder(itemView) {
        private var txtTitle: TextView = itemView.tv_item_title
        private var txtAddress: TextView = itemView.tv_item_address

        override fun setData(item: SearchStoreItem) {
            txtTitle.text = item.store?.title
            txtAddress.text = item.store?.address
        }
    }

    inner class RecentSearchQueryViewHolder(itemView: View) : BaseSearchViewHolder(itemView) {
        private var txtTitle: TextView = itemView.tv_item_title

        override fun setData(item: SearchStoreItem) {
            txtTitle.text = item.searchQuery
        }
    }
}
