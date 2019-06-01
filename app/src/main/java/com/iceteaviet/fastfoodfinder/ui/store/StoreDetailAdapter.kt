package com.iceteaviet.fastfoodfinder.ui.store

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.getRelativeTimeAgo
import kotlinx.android.synthetic.main.item_store_action.view.*
import kotlinx.android.synthetic.main.item_store_comment.view.*
import kotlinx.android.synthetic.main.item_store_info.view.*
import kotlinx.android.synthetic.main.item_store_title.view.*
import kotlinx.android.synthetic.main.layout_call_direction.view.*

/**
 * Created by binhlt on 23/11/2016.
 */

class StoreDetailAdapter internal constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mComments: MutableList<Comment> = ArrayList()
    private var mListener: StoreActionListener? = null
    private var mStore: Store? = null

    fun setListener(listener: StoreActionListener) {
        mListener = listener
    }

    fun setStore(store: Store) {
        mStore = store
        notifyDataSetChanged()
    }

    fun addComment(comment: Comment) {
        mComments.add(0, comment)
        notifyItemInserted(3)
    }

    fun setComments(comments: MutableList<Comment>) {
        mComments = comments
        notifyItemRangeInserted(3, comments.size)
    }

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_store_action, parent, false), mListener)
            INFO -> InfoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_store_info, parent, false), mListener)
            TITLE -> TitleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_store_title, parent, false))
            COMMENT -> CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_store_comment, parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            HEADER -> (holder as HeaderViewHolder).bind(mStore)
            INFO -> (holder as InfoViewHolder).bind(mStore)
            TITLE -> {
                val title = (holder as TitleViewHolder).context.resources.getString(R.string.tips_from_people_who_has_been_here)
                holder.bind(if (position == 2) title else "")
            }
            COMMENT -> (holder as CommentViewHolder).bind(mComments[position - 3])
        }
    }

    override fun getItemCount(): Int {
        return mComments.size + 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HEADER
            1 -> INFO
            2 -> TITLE
            else -> COMMENT
        }
    }

    interface StoreActionListener {
        fun onCommentButtonClick()

        fun onCallButtonClick(tel: String?)

        fun onNavigationButtonClick()

        fun onAddToFavButtonClick(storeId: Int)

        fun onSaveButtonClick(storeId: Int)
    }

    internal class HeaderViewHolder(itemView: View, private val listener: StoreActionListener?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var saveButton: TextView = itemView.btn_save
        var favButton: TextView = itemView.btn_fav
        var comment: TextView = itemView.comment
        var btnCall: Button = itemView.btn_call

        private var mStore: Store? = null

        fun bind(store: Store?) {
            mStore = store

            if (store == null)
                return

            saveButton.setOnClickListener(this)
            favButton.setOnClickListener(this)
            comment.setOnClickListener {
                listener?.onCommentButtonClick()
            }
            btnCall.setOnClickListener {
                listener?.onCallButtonClick(mStore!!.tel)
            }
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.btn_fav -> listener?.onAddToFavButtonClick(mStore!!.id)
                R.id.btn_save -> listener?.onSaveButtonClick(mStore!!.id)
                else -> {
                }
            }
            v.isSelected = !v.isSelected
        }
    }

    internal class InfoViewHolder(itemView: View, private val listener: StoreActionListener?) : RecyclerView.ViewHolder(itemView) {

        private val cdvh: CallDirectionViewHolder
        var tvName: TextView = itemView.store_name
        var tvAddress: TextView = itemView.store_address
        var vCallDirection: View = itemView.call_direction

        init {
            cdvh = CallDirectionViewHolder(vCallDirection)
        }

        fun bind(mStore: Store?) {
            mStore?.let { store ->
                tvName.text = store.title
                tvAddress.text = store.address

                cdvh.btnCall.setOnClickListener {
                    listener?.onCallButtonClick(store.tel)
                }
                cdvh.btnDirection.setOnClickListener {
                    listener?.onNavigationButtonClick()
                }
            }
        }
    }

    internal class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val context: Context
        var content: TextView = itemView.content

        init {
            context = itemView.context
        }

        fun bind(title: String) {
            content.text = title
        }
    }

    internal class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivProfile: ImageView = itemView.profile_image
        var tvUserName: TextView = itemView.user_name
        var tvTime: TextView = itemView.time_post
        var tvContent: TextView = itemView.tv_content
        var container: CardView = itemView.container
        var ivMedia: ImageView = itemView.media

        fun bind(comment: Comment) {
            val context = ivProfile.context
            Glide.with(context)
                    .asBitmap()
                    .load(comment.avatar)
                    .into<BitmapImageViewTarget>(object : BitmapImageViewTarget(ivProfile) {
                        override fun setResource(resource: Bitmap?) {
                            val bitmap = RoundedBitmapDrawableFactory.create(context.resources, resource)
                            bitmap.cornerRadius = 8f
                            ivProfile.setImageDrawable(bitmap)
                        }
                    })

            val mediaUrl = comment.mediaUrl
            if (!mediaUrl.isEmpty()) {
                container.visibility = View.VISIBLE
                Glide.with(context)
                        .load(mediaUrl)
                        .into(ivMedia)
            } else {
                container.visibility = View.GONE
            }

            tvUserName.text = comment.userName
            tvContent.text = comment.content
            tvTime.text = getRelativeTimeAgo(comment.timestamp)
        }
    }

    class CallDirectionViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnCall: FrameLayout = itemView.call
        var btnDirection: FrameLayout = itemView.direction
    }

    companion object {
        private const val HEADER = 0
        private const val INFO = 1
        private const val TITLE = 2
        private const val COMMENT = 3
    }
}
