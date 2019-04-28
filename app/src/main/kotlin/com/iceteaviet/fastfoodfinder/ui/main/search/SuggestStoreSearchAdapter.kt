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
class SuggestStoreSearchAdapter internal constructor() : BaseStoreAdapter() {
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): BaseStoreViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_suggestion, parent, false)
        return RecentlySearchStoreViewHolder(itemView)
    }

    inner class RecentlySearchStoreViewHolder(itemView: View) : BaseStoreViewHolder(itemView) {
        var txtTitle: TextView = itemView.tv_item_title
        var txtAddress: TextView = itemView.tv_item_address

        override fun setData(store: Store) {
            txtTitle.text = store.title
            txtAddress.text = store.address
        }
    }
}
