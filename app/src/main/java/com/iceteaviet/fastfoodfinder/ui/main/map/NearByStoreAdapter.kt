package com.iceteaviet.fastfoodfinder.ui.main.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.databinding.ItemStoreBinding
import com.iceteaviet.fastfoodfinder.ui.main.map.model.NearByStore
import com.iceteaviet.fastfoodfinder.utils.formatDistance
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes

/**
 * Created by tom on 7/21/18.
 */
class NearByStoreAdapter @JvmOverloads internal constructor(diffCallback: DiffUtil.ItemCallback<NearByStore> = DIFF_CALLBACK) : ListAdapter<NearByStore, NearByStoreAdapter.StoreViewHolder>(diffCallback) {

    /**
     * Views Ref
     */
    private lateinit var binding: ItemStoreBinding

    private val nearByStores: MutableList<NearByStore>
    private var listener: StoreListListener? = null

    init {
        nearByStores = ArrayList()
    }

    fun setOnStoreListListener(listener: StoreListListener) {
        this.listener = listener
    }

    // FIXME: Called too many times
    fun setStores(nearbyStores: List<NearByStore>) {
        nearByStores.clear()
        nearByStores.addAll(nearbyStores)

        submitList(nearbyStores) // DiffUtil takes care of the check
    }

    fun clearData() {
        nearByStores.clear()
        submitList(nearByStores)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_store, parent, false)
        return StoreViewHolder(convertView, listener)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val nearByStore = getItem(position)
        holder.setData(nearByStore.store, nearByStore.distance)
    }

    interface StoreListListener {
        fun onItemClick(store: Store)
    }

    inner class StoreViewHolder(itemView: View, listener: StoreListListener?) : RecyclerView.ViewHolder(itemView) {
        var logo: ImageView = binding.ivItemStore
        var storeName: TextView = binding.tvItemStorename
        var storeAddress: TextView = binding.tvItemAddress
        var storeDistance: TextView = binding.tvItemDistance

        init {
            itemView.setOnClickListener {
                if (listener != null && adapterPosition >= 0)
                    listener.onItemClick(nearByStores[adapterPosition].store)
            }
        }

        fun setData(store: Store, distance: Double) {
            Glide.with(logo.context)
                .load(getStoreLogoDrawableRes(store.type))
                .into(logo)
            storeName.text = store.title
            storeAddress.text = store.address
            storeDistance.text = formatDistance(distance)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NearByStore>() {
            override fun areItemsTheSame(oldItem: NearByStore, newItem: NearByStore): Boolean {
                return oldItem.store.id == newItem.store.id && oldItem.distance == newItem.distance
            }

            override fun areContentsTheSame(oldItem: NearByStore, newItem: NearByStore): Boolean {
                return oldItem.store.address == newItem.store.address && oldItem.distance == newItem.distance
            }
        }
    }
}