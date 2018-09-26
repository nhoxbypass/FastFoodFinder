package com.iceteaviet.fastfoodfinder.ui.routing

import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.Step
import com.iceteaviet.fastfoodfinder.utils.getTrimmedShortInstruction
import com.iceteaviet.fastfoodfinder.utils.trimWhitespace
import com.iceteaviet.fastfoodfinder.utils.ui.getDirectionImage
import kotlinx.android.synthetic.main.item_routing.view.*

/**
 * Created by Genius Doan on 11/30/2016.
 */
class RoutingAdapter internal constructor(steps: List<Step>, type: Int) : RecyclerView.Adapter<RoutingAdapter.RoutingViewHolder>() {

    private var mStepList: List<Step>? = steps
    private var mListener: OnNavigationItemClickListener? = null
    private val mType: Int = type

    fun setOnNavigationItemClickListener(listener: OnNavigationItemClickListener) {
        mListener = listener
    }

    fun getDirectionLocationAt(index: Int): LatLng? {
        return if (index < 0 || index >= mStepList!!.size) {
            null
        } else mStepList!![index].endMapCoordination.location
    }

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RoutingViewHolder {
        val convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_routing, parent, false)
        return RoutingViewHolder(convertView, mListener)
    }

    override fun onBindViewHolder(@NonNull holder: RoutingViewHolder, position: Int) {
        holder.bindData(mStepList!![position], mType)
    }

    override fun getItemCount(): Int {
        return mStepList!!.size
    }


    interface OnNavigationItemClickListener {
        fun onClick(index: Int)
    }

    class RoutingViewHolder(itemView: View, listener: OnNavigationItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        var routingGuide: TextView = itemView.tv_routing_guide
        var routingDistance: TextView = itemView.tv_routing_distance
        var routingImageView: ImageView = itemView.iv_routing

        init {
            itemView.setOnClickListener {
                listener?.onClick(adapterPosition)
            }
        }

        fun bindData(step: Step, type: Int) {
            val imgResId = getDirectionImage(step.direction)
            val instruction: Spanned
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                instruction = Html.fromHtml(step.instruction, Html.FROM_HTML_MODE_COMPACT)
            } else {
                instruction = Html.fromHtml(step.instruction)
            }

            routingImageView.setImageResource(imgResId)
            if (type == TYPE_FULL)
                routingGuide.text = trimWhitespace(instruction)
            else if (type == TYPE_SHORT)
                routingGuide.text = getTrimmedShortInstruction(instruction)
            routingDistance.text = step.getDistance()
        }
    }

    companion object {
        const val TYPE_FULL = 0
        const val TYPE_SHORT = 1
    }
}
