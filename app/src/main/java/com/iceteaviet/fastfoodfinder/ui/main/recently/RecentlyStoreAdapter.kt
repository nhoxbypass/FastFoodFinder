package com.iceteaviet.fastfoodfinder.ui.main.recently

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.databinding.ItemRecentlyLocationBinding
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.ItemTouchHelperAdapter
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.ItemTouchHelperViewHolder
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.OnStartDragListener

/**
 * Created by MyPC on 11/16/2016.
 */
class RecentlyStoreAdapter internal constructor(private val mDragStartListener: OnStartDragListener, frameLayout: FrameLayout) : RecyclerView.Adapter<RecentlyStoreAdapter.RecentlyStoreViewHolder>(), ItemTouchHelperAdapter {
    /**
     * Views Ref
     */
    private lateinit var binding: ItemRecentlyLocationBinding

    private val mStoreList: MutableList<Store>
    private val mContainerView: View
    private var mOnItemClickListener: OnItemClickListener? = null

    init {
        mStoreList = ArrayList()
        mContainerView = frameLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyStoreViewHolder {
        binding = ItemRecentlyLocationBinding.inflate(LayoutInflater.from(parent.context))
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recently_location, parent, false)
        return RecentlyStoreViewHolder(itemView)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        return false
    }

    override fun onItemDismiss(position: Int) {
        val store = mStoreList[position]
        mStoreList.removeAt(position)
        //notifyDataSetChanged();
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

    override fun onBindViewHolder(holder: RecentlyStoreViewHolder, position: Int) {

        val store = mStoreList[position]
        holder.setData(store)
        // Start a drag whenever the handle view it touched
        holder.itemView.setOnTouchListener { view, event ->
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                mDragStartListener.onStartDrag(holder)

            }
            false
        }
    }

    override fun getItemCount(): Int {
        return mStoreList.size
    }

    interface OnItemClickListener {
        fun onClick(store: Store)
    }

    inner class RecentlyStoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {
        var txtTitle: TextView = binding.tvItemTitle
        var txtAddress: TextView = binding.tvItemAddress

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
                    .setPositiveButton(android.R.string.yes) { dialog, _ ->
                        mStoreList.removeAt(position)
                        notifyDataSetChanged()
                        Snackbar.make(itemView, R.string.undo, Snackbar.LENGTH_INDEFINITE).show()
                        dialog.dismiss()
                    }
                    .setNegativeButton(android.R.string.no) { dialog, _ ->
                        //do nothing
                        dialog.dismiss()
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                true
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