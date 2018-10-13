package com.iceteaviet.fastfoodfinder.ui.custom.store

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.utils.ui.getRandomStoreImages
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.store_list.view.*

/**
 * Created by tom on 10/12/18.
 */
class StoreListView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : CardView(context, attrs, defStyleAttr) {
    companion object {
        const val DEFAULT_PADDING = 2
        const val DEFAULT_INFO_HEIGHT = 40
        const val DEFAULT_ICON_HEIGHT = 80
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.cardViewStyle)

    private var imageView1: ImageView
    private var imageView2: ImageView
    private var imageView3: ImageView
    private var imageView4: ImageView
    private var tvName: TextView
    private var tvCount: TextView
    private var ivIcon: CircleImageView

    init {
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

            tvCount = TextView(context)
            params.topMargin = width - DEFAULT_INFO_HEIGHT + 16

            ivIcon = CircleImageView(context)
            params = LinearLayout.LayoutParams(DEFAULT_ICON_HEIGHT, DEFAULT_ICON_HEIGHT)
            params.topMargin = imgHeight - DEFAULT_ICON_HEIGHT / 2

            addView(tvName)
            addView(tvCount)
            addView(ivIcon)
        }

        setupData()
    }

    private fun setupData() {
        initRandomImages()
        tvName.text = context.getString(R.string.my_save_places)
        tvCount.text = context.getString(R.string._3_places)
        ivIcon.setImageResource(R.drawable.ic_profile_saved)
    }

    private fun initRandomImages() {
        val imgs = getRandomStoreImages(4)

        imageView1.setImageResource(imgs[0])
        imageView2.setImageResource(imgs[1])
        imageView3.setImageResource(imgs[2])
        imageView4.setImageResource(imgs[3])
    }

    fun setIcon(@DrawableRes resId: Int) {
        ivIcon.setImageResource(resId)
    }

    fun setName(name: String) {
        tvName.text = name
    }

    fun setCount(count: String) {
        tvCount.text = String.format(context.getString(R.string.count_places), count)
    }
}
