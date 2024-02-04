package com.iceteaviet.fastfoodfinder.ui.storelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes

/**
 * Created by MyPC on 11/30/2016.
 */
class StoreListAdapter : RecyclerView.Adapter<StoreListAdapter.StoreListViewHolder>() {

    private val mStoreList: MutableList<Store>

    init {
        mStoreList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_store, parent, false)
        return StoreListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StoreListViewHolder, position: Int) {
        val store = mStoreList[position]
        holder.setData(store)
    }

    fun setStores(stores: List<Store>) {
        mStoreList.clear()
        mStoreList.addAll(stores)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mStoreList.size
    }

    class StoreListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivStoreImage: ImageView = itemView.findViewById(R.id.ivStoreImage)
        var tvStoreName: TextView = itemView.findViewById(R.id.tv_item_title)
        var tvStoreAddress: TextView = itemView.findViewById(R.id.tv_item_address)
        var tvTel: TextView = itemView.findViewById(R.id.tv_item_tel)
        var btnRate: Button = itemView.findViewById(R.id.btnRate)

        fun setData(store: Store) {
            tvStoreName.text = store.title
            tvStoreAddress.text = store.address
            tvTel.text = store.tel
            Glide.with(ivStoreImage.context)
                .load(getStoreLogoDrawableRes(store.type))
                .into(ivStoreImage)
        }
    }
}