package com.iceteaviet.fastfoodfinder.ui.storelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableId
import kotlinx.android.synthetic.main.item_list_store.view.*
import java.util.*


/**
 * Created by MyPC on 11/30/2016.
 */
class StoreListAdapter : RecyclerView.Adapter<StoreListAdapter.StoreListViewHolder>() {

    private val mStoreList: MutableList<Store>

    init {
        mStoreList = ArrayList()
    }

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): StoreListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_store, parent, false)
        return StoreListViewHolder(itemView)
    }

    override fun onBindViewHolder(@NonNull holder: StoreListViewHolder, position: Int) {
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
        var ivStoreImage: ImageView = itemView.ivStoreImage
        var tvStoreName: TextView = itemView.tv_item_title
        var tvStoreAddress: TextView = itemView.tv_item_address
        var tvTel: TextView = itemView.tv_item_tel
        var btnRate: Button = itemView.btnRate

        fun setData(store: Store) {
            tvStoreName.text = store.title
            tvStoreAddress.text = store.address
            tvTel.text = store.tel
            Glide.with(ivStoreImage.context)
                    .load(getStoreLogoDrawableId(store.type))
                    .into(ivStoreImage)
        }
    }
}
