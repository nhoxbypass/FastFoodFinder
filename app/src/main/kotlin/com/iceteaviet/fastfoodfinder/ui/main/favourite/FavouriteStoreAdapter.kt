package com.iceteaviet.fastfoodfinder.ui.main.favourite

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.ItemTouchHelperAdapter
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.ItemTouchHelperViewHolder
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.OnStartDragListener
import kotlinx.android.synthetic.main.item_favourite_location.view.*
import java.util.*

/**
 * Created by MyPC on 11/16/2016.
 */
class FavouriteStoreAdapter internal constructor(private val mDragStartListener: OnStartDragListener, view: FrameLayout) : RecyclerView.Adapter<FavouriteStoreAdapter.FavouriteStoreViewHolder>(), ItemTouchHelperAdapter {
    private val mContainerView: View
    private val mStoreList: MutableList<Store>
    private var mOnItemClickListener: OnItemClickListener? = null

    init {
        mStoreList = ArrayList()
        mContainerView = view
    }

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): FavouriteStoreViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favourite_location, parent, false)
        return FavouriteStoreViewHolder(itemView)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(mStoreList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        Snackbar.make(mContainerView, R.string.want_undo, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    Collections.swap(mStoreList, toPosition, fromPosition)
                    notifyItemMoved(toPosition, fromPosition)
                }
                .setDuration(30000)
                .show()
        return true
    }

    override fun onItemDismiss(position: Int) {
        val store = mStoreList[position]
        mStoreList.removeAt(position)
        notifyDataSetChanged()
        notifyItemRemoved(position)
        Snackbar.make(mContainerView, R.string.want_undo, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    mStoreList.add(position, store)
                    notifyItemInserted(position)
                }
                .setDuration(30000)
                .show()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    fun addStores(destinations: List<Store>) {
        val pos = mStoreList.size
        mStoreList.addAll(destinations)
        notifyItemRangeInserted(pos, destinations.size)
    }

    fun addStore(destination: Store) {
        mStoreList.add(destination)
        notifyItemRangeInserted(mStoreList.size, 1)
    }

    fun setStores(destinations: List<Store>) {
        mStoreList.clear()
        mStoreList.addAll(destinations)
        notifyDataSetChanged()
    }

    fun updateStore(store: Store) {
        for (i in mStoreList.indices) {
            if (store.id == mStoreList[i].id) {
                mStoreList[i] = store
                notifyItemChanged(i)
                return
            }
        }
    }

    fun removeStore(store: Store) {
        val index = mStoreList.indexOf(store)
        if (index != -1) {
            mStoreList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onBindViewHolder(@NonNull holder: FavouriteStoreViewHolder, position: Int) {
        val store = mStoreList[position]
        holder.setData(store)
    }

    override fun getItemCount(): Int {
        return mStoreList.size
    }

    private interface FavouritedListListener {
        fun onClick(store: Store)

        fun onItemDismiss()
    }

    interface OnItemClickListener {
        fun onClick(des: Store)
    }

    inner class FavouriteStoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {
        var txtTitle: TextView = itemView.tv_item_title
        var txtAddress: TextView = itemView.tv_item_address

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val store = mStoreList[position]
                mOnItemClickListener?.onClick(store)
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                AlertDialog.Builder(itemView.context)
                        .setTitle(R.string.delete_favourite_location)
                        .setMessage(R.string.are_you_sure)
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                            mStoreList.removeAt(position)
                            notifyDataSetChanged()
                            Snackbar.make(itemView, R.string.undo, Snackbar.LENGTH_INDEFINITE).show()
                        }
                        .setNegativeButton(android.R.string.no) { dialog, which ->
                            //do nothing
                        }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                true
            }

            itemView.setOnTouchListener { v, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(this)
                }
                false
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }

        fun setData(store: Store) {
            txtTitle.text = store.title
            txtAddress.text = store.address
        }
    }

}
