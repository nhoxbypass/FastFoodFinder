package com.iceteaviet.fastfoodfinder.ui.custom.store

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.utils.convertDpToPx
import com.iceteaviet.fastfoodfinder.utils.ui.getRandomStoreImages
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by tom on 10/12/18.
 */
class StoreListView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : CardView(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.cardViewStyle)

    private var imageView1: ImageView
    private var imageView2: ImageView
    private var imageView3: ImageView
    private var imageView4: ImageView
    private var tvName: TextView
    private var tvCount: TextView
    private var ivIcon: CircleImageView
    private val DEFAULT_PADDING = convertDpToPx(2f).toInt()
    private val DEFAULT_TEXT_PADDING_LEFT_RIGHT = convertDpToPx(8f).toInt()
    private val DEFAULT_TEXT_PADDING_TOP_BOTTOM = convertDpToPx(4f).toInt()

    init {
        imageView1 = ImageView(context)
        imageView1.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView2 = ImageView(context)
        imageView2.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView3 = ImageView(context)
        imageView3.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView4 = ImageView(context)
        imageView4.scaleType = ImageView.ScaleType.CENTER_CROP

        tvName = TextView(context)
        tvName.layoutParams = MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        tvCount = TextView(context)
        tvCount.layoutParams = MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        ivIcon = CircleImageView(context)

        addView(imageView1)
        addView(imageView2)
        addView(imageView3)
        addView(imageView4)
        addView(tvName)
        addView(tvCount)
        addView(ivIcon)

        context.theme.obtainStyledAttributes(attrs, R.styleable.StoreListView, 0, 0)
                .apply {
                    try {
                        tvName.text = getString(R.styleable.StoreListView_name)
                        tvCount.text = String.format(context.getString(R.string.count_places),
                                getInteger(R.styleable.StoreListView_count, 0).toString())
                        ivIcon.setImageDrawable(getDrawable(R.styleable.StoreListView_icon))
                    } finally {
                        recycle()
                    }
                }

        initRandomImages()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val infoHeight = tvName.height + tvCount.height + DEFAULT_TEXT_PADDING_TOP_BOTTOM * 3
        val imgWidth = (width - DEFAULT_PADDING) / 2
        val imgHeight = (height - DEFAULT_PADDING - infoHeight) / 2
        val iconSize = imgWidth / 3

        imageView1.layoutParams.width = imgWidth
        imageView1.layoutParams.height = imgHeight

        var params = imageView2.layoutParams as MarginLayoutParams
        params.leftMargin = imgWidth + DEFAULT_PADDING
        params.width = imgWidth
        params.height = imgHeight
        imageView2.layoutParams = params

        params = imageView3.layoutParams as MarginLayoutParams
        params.topMargin = imgHeight + DEFAULT_PADDING
        params.width = imgWidth
        params.height = imgHeight
        imageView3.layoutParams = params

        params = imageView4.layoutParams as MarginLayoutParams
        params.leftMargin = imgWidth + DEFAULT_PADDING
        params.topMargin = imgHeight + DEFAULT_PADDING
        params.width = imgWidth
        params.height = imgHeight
        imageView4.layoutParams = params

        params = tvName.layoutParams as MarginLayoutParams
        params.topMargin = height - infoHeight + DEFAULT_TEXT_PADDING_TOP_BOTTOM
        params.leftMargin = DEFAULT_TEXT_PADDING_LEFT_RIGHT
        tvName.layoutParams = params

        params = tvCount.layoutParams as MarginLayoutParams
        params.topMargin = height - infoHeight + tvName.height + DEFAULT_TEXT_PADDING_TOP_BOTTOM * 2
        params.leftMargin = DEFAULT_TEXT_PADDING_LEFT_RIGHT
        tvCount.layoutParams = params

        params = ivIcon.layoutParams as MarginLayoutParams
        params.topMargin = imgHeight - iconSize / 2
        params.leftMargin = imgWidth - iconSize / 2
        params.width = iconSize
        params.height = iconSize
        ivIcon.layoutParams = params
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
