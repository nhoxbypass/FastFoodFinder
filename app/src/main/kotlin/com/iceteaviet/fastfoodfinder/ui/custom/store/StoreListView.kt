package com.iceteaviet.fastfoodfinder.ui.custom.store

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.iceteaviet.fastfoodfinder.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.store_list.view.*

/**
 * Created by tom on 10/12/18.
 */
class StoreListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.cardViewStyle) : CardView(context, attrs, defStyleAttr) {
    companion object {
        const val DEFAULT_PADDING = 2
        const val DEFAULT_INFO_HEIGHT = 40
        const val DEFAULT_ICON_HEIGHT = 80
    }

    var imageView1: ImageView
    var imageView2: ImageView
    var imageView3: ImageView
    var imageView4: ImageView
    var tvName: TextView
    var tvCount: TextView
    var ivIcon: CircleImageView

    init {
        Log.e("Genius", "w: " + width + ", h: " + height)
        if (width == LinearLayout.LayoutParams.WRAP_CONTENT && height == LinearLayout.LayoutParams.WRAP_CONTENT) {
            View.inflate(context, R.layout.store_list, this)

            imageView1 = iv_1
            imageView2 = iv_2
            imageView3 = iv_3
            imageView4 = iv_4

            tvName = tv_list_name
            tvCount = tv_list_count

            ivIcon = iv_icon
        } else {

            imageView1 = ImageView(context)
            imageView2 = ImageView(context)
            imageView3 = ImageView(context)
            imageView4 = ImageView(context)

            val imgWidth = (width - DEFAULT_PADDING) / 2
            val imgHeight = (height - DEFAULT_PADDING - DEFAULT_INFO_HEIGHT) / 2
            var params = LinearLayout.LayoutParams(imgWidth, imgHeight)
            imageView1.layoutParams = params

            params = LinearLayout.LayoutParams(imgWidth, imgHeight)
            params.leftMargin = imgWidth + DEFAULT_PADDING
            imageView2.layoutParams = params

            params = LinearLayout.LayoutParams(imgWidth, imgHeight)
            params.topMargin = imgHeight + DEFAULT_PADDING
            imageView3.layoutParams = params

            params = LinearLayout.LayoutParams(imgWidth, imgHeight)
            params.leftMargin = imgWidth + DEFAULT_PADDING
            params.topMargin = imgHeight + DEFAULT_PADDING
            imageView4.layoutParams = params

            addView(imageView1)
            addView(imageView2)
            addView(imageView3)
            addView(imageView4)

            tvName = TextView(context)
            params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.topMargin = width - DEFAULT_INFO_HEIGHT
            tvName.text = context.getString(R.string.my_save_places)

            tvCount = TextView(context)
            params.topMargin = width - DEFAULT_INFO_HEIGHT + 16
            tvCount.text = context.getString(R.string._3_places)

            ivIcon = CircleImageView(context)
            params = LinearLayout.LayoutParams(DEFAULT_ICON_HEIGHT, DEFAULT_ICON_HEIGHT)
            params.topMargin = imgHeight - DEFAULT_ICON_HEIGHT / 2
            ivIcon.setImageResource(R.drawable.ic_profile_saved)
        }
    }
}
