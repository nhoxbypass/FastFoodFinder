package com.iceteaviet.fastfoodfinder.ui.main.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.main.StoreDiffCallback
import com.iceteaviet.fastfoodfinder.utils.calcDistance
import com.iceteaviet.fastfoodfinder.utils.formatDistance
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableId
import kotlinx.android.synthetic.main.item_store.view.*
import java.util.*

/**
 * Created by Genius Doan on 11/9/2016.
 *
 * @see androidx.recyclerview.widget.ListAdapter
 */

@Deprecated("due to the publish of ListAdapter\n")
class NearByStoreAdapter : RecyclerView.Adapter<NearByStoreAdapter.StoreViewHolder>() {
    private var currCameraPosition: LatLng? = null
    private val mListStore: MutableList<Store>
    private var listener: StoreListListener? = null

    init {
        mListStore = ArrayList()
    }

    fun setOnStoreListListener(listener: StoreListListener) {
        this.listener = listener
    }

    fun setCurrCameraPosition(currCameraPosition: LatLng) {
        this.currCameraPosition = currCameraPosition
    }

    fun addStore(store: Store) {
        mListStore.add(store)
        notifyItemInserted(mListStore.size - 1)
    }

    fun addStores(listStores: List<Store>) {
        val position = mListStore.size
        mListStore.addAll(listStores)
        notifyItemRangeInserted(position, mListStore.size)
    }

    fun setStores(listStores: List<Store>) {
        val diffResult = DiffUtil.calculateDiff(StoreDiffCallback(this.mListStore, listStores))

        mListStore.clear()
        mListStore.addAll(listStores)

        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): StoreViewHolder {
        val convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_store, parent, false)
        return StoreViewHolder(convertView, listener, mListStore)
    }

    override fun onBindViewHolder(@NonNull holder: StoreViewHolder, position: Int) {
        val store = mListStore[position]
        holder.setData(store, calcDistance(currCameraPosition!!, store.getPosition()))
    }

    override fun getItemCount(): Int {
        return mListStore.size
    }

    interface StoreListListener {
        fun onItemClick(store: Store)
    }

    class StoreViewHolder(itemView: View, listener: StoreListListener?, storeList: List<Store>) : RecyclerView.ViewHolder(itemView) {
        var logo: ImageView = itemView.iv_item_store
        var storeName: TextView = itemView.tv_item_storename
        var storeAddress: TextView = itemView.tv_item_address
        var storeDistance: TextView = itemView.tv_item_distance


        init {
            itemView.setOnClickListener {
                listener?.onItemClick(storeList[adapterPosition])
            }
        }

        fun setData(store: Store, distance: Double) {
            Glide.with(logo.context)
                    .load(getStoreLogoDrawableId(store.type))
                    .into(logo)
            storeName.text = store.title
            storeAddress.text = store.address
            storeDistance.text = formatDistance(distance)
        }
    }
}
