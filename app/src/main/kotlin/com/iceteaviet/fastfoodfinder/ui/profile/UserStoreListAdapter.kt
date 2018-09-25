package com.iceteaviet.fastfoodfinder.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import kotlinx.android.synthetic.main.view_new_list.view.*
import java.util.*

/**
 * Created by MyPC on 12/5/2016.
 */
class UserStoreListAdapter internal constructor() : RecyclerView.Adapter<UserStoreListAdapter.ListViewHolder>() {

    private val mListPackets: MutableList<UserStoreList>
    private var mListener: OnItemClickListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null

    init {
        mListPackets = ArrayList()
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        mOnItemLongClickListener = listener
    }

    fun addListPacket(listPacket: UserStoreList) {
        mListPackets.add(listPacket)
        notifyItemRangeInserted(mListPackets.size, 1)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mListener = onItemClickListener
    }

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_new_list, parent, false)
        return ListViewHolder(itemView)
    }

    override fun onBindViewHolder(@NonNull holder: ListViewHolder, position: Int) {
        val listPacket = mListPackets[position]
        holder.itemView.tvNameList!!.text = listPacket.listName
        holder.itemView.iconNewList!!.setImageResource(listPacket.iconId)
    }

    override fun getItemCount(): Int {
        return mListPackets.size
    }

    interface OnItemLongClickListener {
        fun onClick(position: Int)
    }

    interface OnItemClickListener {
        fun onClick(listPacket: UserStoreList)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                            mOnItemLongClickListener!!.onClick(position)
                        }
                        .setNegativeButton(android.R.string.no) { dialog, which ->
                            //do nothing
                        }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                true
            }
        }
    }
}
