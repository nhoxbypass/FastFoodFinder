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
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.databinding.ItemStoreBinding
import com.iceteaviet.fastfoodfinder.ui.main.map.model.NearByStore
import com.iceteaviet.fastfoodfinder.utils.formatDistance
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes

class NearByStoreAdapter @JvmOverloads internal constructor(diffCallback: DiffUtil.ItemCallback<NearByStore> = DIFF_CALLBACK) : ListAdapter<NearByStore, NearByStoreAdapter.StoreViewHolder>(diffCallback) {

    private lateinit var binding: ItemStoreBinding
    private var listener: StoreListListener? = null

    fun setOnStoreListListener(listener: StoreListListener) {
        this.listener = listener
    }

    fun setStores(nearbyStores: List<NearByStore>) {
        submitList(nearbyStores)
    }

    fun clearData() {
        submitList(emptyList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        binding = ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreViewHolder(binding.root, listener)
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
                val pos = adapterPosition
                if (listener != null && pos >= 0)
                    listener.onItemClick(getItem(pos).store)
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
                return oldItem.store.id == newItem.store.id
            }

            override fun areContentsTheSame(oldItem: NearByStore, newItem: NearByStore): Boolean {
                return oldItem == newItem
            }
        }
    }
}
