package com.iceteaviet.fastfoodfinder.ui.store

import android.graphics.Bitmap
import android.graphics.Color
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
import com.iceteaviet.fastfoodfinder.ui.custom.roboto.RobotoTextView
import com.iceteaviet.fastfoodfinder.utils.getRelativeTimeAgo
import com.iceteaviet.fastfoodfinder.utils.getString
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
    private var mStore: Store? = null
    private var isSignedIn = false

    private var mListener: StoreActionListener? = null

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
        notifyDataSetChanged()
    }

    fun setIsSignedIn(isSigned: Boolean) {
        if (this.isSignedIn == isSigned) return
        this.isSignedIn = isSigned
        notifyItemChanged(0)
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
            HEADER -> if (holder is HeaderViewHolder) {
                holder.bind(mStore, isSignedIn)
            }
            INFO -> if (holder is InfoViewHolder) {
                holder.bind(mStore)
            }
            TITLE -> {
                if (holder is TitleViewHolder) {
                    val title = getString(R.string.tips_from_people_who_has_been_here)
                    holder.bind(if (position == 2) title else "")
                }
            }
            COMMENT -> if (holder is CommentViewHolder) {
                holder.bind(mComments[position - 3])
            }
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

        fun onCallButtonClick()

        fun onNavigationButtonClick()

        fun onAddToFavButtonClick()

        fun onSaveButtonClick()
    }

    internal class HeaderViewHolder(itemView: View, private val listener: StoreActionListener?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var saveButton: TextView = itemView.btn_save
        private var favButton: TextView = itemView.btn_fav
        private var comment: RobotoTextView = itemView.comment
        private var btnCall: Button = itemView.btn_call

        fun bind(store: Store?, isSigned: Boolean) {
            if (!isSigned) {
                saveButton.setCompoundDrawablesWithIntrinsicBounds(null, itemView.context.resources.getDrawable(R.drawable.ic_detail_bookmark_disable), null, null);
                saveButton.setTextColor(Color.parseColor("#BDBDBD"))

                favButton.setCompoundDrawablesWithIntrinsicBounds(null, itemView.context.resources.getDrawable(R.drawable.ic_detail_love_disable), null, null);
                favButton.setTextColor(Color.parseColor("#BDBDBD"))
//
                comment.setCompoundDrawablesWithIntrinsicBounds(null, itemView.context.resources.getDrawable(R.drawable.ic_detail_pencil_disable), null, null);
                comment.setTextColor(Color.parseColor("#BDBDBD"))
            } else {
                saveButton.setCompoundDrawablesWithIntrinsicBounds(null, itemView.context.resources.getDrawable(R.drawable.ic_detail_bookmark), null, null);
                saveButton.setTextColor(Color.parseColor("#757575"))

                favButton.setCompoundDrawablesWithIntrinsicBounds(null, itemView.context.resources.getDrawable(R.drawable.state_rate_click), null, null);
                favButton.setTextColor(Color.parseColor("#757575"))

                comment.setCompoundDrawablesWithIntrinsicBounds(null, itemView.context.resources.getDrawable(R.drawable.ic_detail_pencil), null, null);
                comment.setTextColor(Color.parseColor("#757575"))
            }

            saveButton.setOnClickListener(this)
            favButton.setOnClickListener(this)
            comment.setOnClickListener {
                listener?.onCommentButtonClick()
            }
            btnCall.setOnClickListener {
                listener?.onCallButtonClick()
            }
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.btn_fav -> listener?.onAddToFavButtonClick()
                R.id.btn_save -> listener?.onSaveButtonClick()
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
            }

            cdvh.btnCall.setOnClickListener {
                listener?.onCallButtonClick()
            }
            cdvh.btnDirection.setOnClickListener {
                listener?.onNavigationButtonClick()
            }
        }
    }

    internal class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var content: TextView = itemView.content

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
