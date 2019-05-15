package com.iceteaviet.fastfoodfinder.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.ui.custom.store.StoreListView
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreListIconDrawableRes
import java.util.*

/**
 * Created by MyPC on 12/5/2016.
 */
class UserStoreListAdapter internal constructor() : RecyclerView.Adapter<UserStoreListAdapter.StoreListViewHolder>() {

    private val mListPackets: MutableList<UserStoreList>
    private var mListener: OnItemClickListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null

    init {
        mListPackets = ArrayList()
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        mOnItemLongClickListener = listener
    }

    fun setListPackets(listPackets: List<UserStoreList>) {
        this.mListPackets.clear()
        this.mListPackets.addAll(listPackets)
        notifyDataSetChanged()
    }

    fun addListPacket(listPacket: UserStoreList) {
        mListPackets.add(listPacket)
        notifyItemRangeInserted(mListPackets.size, 1)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mListener = onItemClickListener
    }

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): StoreListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_new_list, parent, false)
        return StoreListViewHolder(itemView)
    }

    override fun onBindViewHolder(@NonNull holder: StoreListViewHolder, position: Int) {
        val listPacket = mListPackets[position]
        holder.bindData(listPacket)
    }

    override fun getItemCount(): Int {
        return mListPackets.size
    }

    interface OnItemLongClickListener {
        fun onLongClick(position: Int)
    }

    interface OnItemClickListener {
        fun onClick(listPacket: UserStoreList)
    }

    inner class StoreListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val listPacket = mListPackets[adapterPosition]
                mListener!!.onClick(listPacket)
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                //final UserStoreList userStoreList = mListPackets.get(position);
                AlertDialog.Builder(itemView.context)
                        .setTitle(R.string.delete_favourite_location)
                        .setMessage(R.string.are_you_sure)
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                            mListPackets.removeAt(position)
                            notifyDataSetChanged()
                            mOnItemLongClickListener!!.onLongClick(position)
                        }
                        .setNegativeButton(android.R.string.no) { dialog, which ->
                            //do nothing
                        }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                true
            }
        }

        fun bindData(storeList: UserStoreList) {
            (itemView as StoreListView).setData(storeList.listName, getStoreListIconDrawableRes(storeList.iconId), storeList.getStoreIdList().size.toString())
        }
    }
}
