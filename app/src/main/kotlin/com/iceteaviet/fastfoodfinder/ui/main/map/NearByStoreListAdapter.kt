package com.iceteaviet.fastfoodfinder.ui.main.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.calcDistance
import com.iceteaviet.fastfoodfinder.utils.formatDistance
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableId
import kotlinx.android.synthetic.main.item_store.view.*
import java.util.*

/**
 * Created by tom on 7/21/18.
 */
class NearByStoreListAdapter @JvmOverloads internal constructor(@NonNull diffCallback: DiffUtil.ItemCallback<Store> = DIFF_CALLBACK) : ListAdapter<Store, NearByStoreListAdapter.StoreViewHolder>(diffCallback) {

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

    // FIXME: Called too many times
    fun setStores(listStores: List<Store>) {
        mListStore.clear()
        mListStore.addAll(listStores)

        submitList(listStores) // DiffUtil takes care of the check
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): StoreViewHolder {
        val convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_store, parent, false)
        return NearByStoreListAdapter.StoreViewHolder(convertView, listener, mListStore)
    }

    override fun onBindViewHolder(@NonNull holder: StoreViewHolder, position: Int) {
        val store = getItem(position)
        holder.setData(store, calcDistance(currCameraPosition!!, store.getPosition()))
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
            Glide.with(logo!!.context)
                    .load(getStoreLogoDrawableId(store.type))
                    .into(logo!!)
            storeName!!.text = store.title
            storeAddress!!.text = store.address
            storeDistance!!.text = formatDistance(distance)
        }
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Store>() {
            override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
                return oldItem.address == newItem.address
            }
        }
    }
}
