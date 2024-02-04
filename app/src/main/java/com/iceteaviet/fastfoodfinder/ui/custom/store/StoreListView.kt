package com.iceteaviet.fastfoodfinder.ui.custom.store

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
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
class StoreListView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : CardView(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, androidx.cardview.R.attr.cardViewStyle)

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
        tvName.layoutParams = LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        tvCount = TextView(context)
        tvCount.layoutParams = LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

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

                    val highlightName = getBoolean(R.styleable.StoreListView_highlightName, false)
                    if (highlightName) {
                        tvName.setTypeface(null, Typeface.BOLD)
                        tvName.setTextColor(Color.parseColor("#304FFE"))
                    }
                } finally {
                    recycle()
                }
            }

        initRandomImages()
    }

    // TODO: Check calling on onDraw()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val infoHeight = tvName.height + tvCount.height + DEFAULT_TEXT_PADDING_TOP_BOTTOM * 3
        val imgWidth = (width - DEFAULT_PADDING) / 2
        val imgHeight = (height - DEFAULT_PADDING - infoHeight) / 2
        val iconSize = imgWidth / 3

        imageView1.layoutParams.width = imgWidth
        imageView1.layoutParams.height = imgHeight

        var params = imageView2.layoutParams as LayoutParams
        imageView2.x = (imgWidth + DEFAULT_PADDING).toFloat()
        params.width = imgWidth
        params.height = imgHeight
        imageView2.layoutParams = params

        params = imageView3.layoutParams as LayoutParams
        imageView3.y = (imgHeight + DEFAULT_PADDING).toFloat()
        params.width = imgWidth
        params.height = imgHeight
        imageView3.layoutParams = params

        params = imageView4.layoutParams as LayoutParams
        imageView4.x = (imgWidth + DEFAULT_PADDING).toFloat()
        imageView4.y = (imgHeight + DEFAULT_PADDING).toFloat()
        params.width = imgWidth
        params.height = imgHeight
        imageView4.layoutParams = params

        params = tvName.layoutParams as LayoutParams
        tvName.y = (height - infoHeight + DEFAULT_TEXT_PADDING_TOP_BOTTOM).toFloat()
        tvName.x = DEFAULT_TEXT_PADDING_LEFT_RIGHT.toFloat()
        tvName.layoutParams = params

        params = tvCount.layoutParams as LayoutParams
        tvCount.y = (height - infoHeight + tvName.height + DEFAULT_TEXT_PADDING_TOP_BOTTOM * 2).toFloat()
        tvCount.x = DEFAULT_TEXT_PADDING_LEFT_RIGHT.toFloat()
        tvCount.layoutParams = params

        params = ivIcon.layoutParams as LayoutParams
        ivIcon.y = (imgHeight - iconSize / 2).toFloat()
        ivIcon.x = (imgWidth - iconSize / 2).toFloat()
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

    fun setData(name: String, @DrawableRes iconId: Int, count: String) {
        tvName.text = name
        ivIcon.setImageResource(iconId)
        tvCount.text = String.format(context.getString(R.string.count_places), count)

        requestLayout()
    }

    fun setIcon(@DrawableRes resId: Int) {
        ivIcon.setImageResource(resId)
        requestLayout()
    }

    fun setName(name: String) {
        tvName.text = name
        requestLayout()
    }

    fun setCount(count: String) {
        tvCount.text = String.format(context.getString(R.string.count_places), count)
        requestLayout()
    }
}
