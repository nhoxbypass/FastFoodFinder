package com.iceteaviet.fastfoodfinder.ui.main.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.ui.main.search.model.SearchStoreItem
import kotlinx.android.synthetic.main.item_recently_location.view.*

/**
 * Created by MyPC on 11/16/2016.
 */
class SuggestStoreSearchAdapter internal constructor() : BaseSearchAdapter() {
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): BaseSearchViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_suggestion, parent, false)
        return RecentlySearchStoreViewHolder(itemView)
    }

    inner class RecentlySearchStoreViewHolder(itemView: View) : BaseSearchViewHolder(itemView) {
        var txtTitle: TextView = itemView.tv_item_title
        var txtAddress: TextView = itemView.tv_item_address

        override fun setData(item: SearchStoreItem) {
            txtTitle.text = item.store?.title
            txtAddress.text = item.store?.address
        }
    }
}
