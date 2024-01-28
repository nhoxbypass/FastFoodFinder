package com.iceteaviet.fastfoodfinder.ui.custom.processbutton

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.iceteaviet.fastfoodfinder.R


/**
 * Created by tom on 2019-03-23.
 */
open class FlatButton : AppCompatButton {

    lateinit var normalDrawable: StateListDrawable
        private set
    var cornerRadius: Float = 0.toFloat()
        private set
    private var normalText: CharSequence? = null

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        normalDrawable = StateListDrawable()
        if (attrs != null) {
            initAttributes(context, attrs)
        }
        normalText = text.toString()
        setBackgroundCompat(normalDrawable)
    }

    private fun initAttributes(context: Context, attributeSet: AttributeSet) {
        val attr = getTypedArray(context, attributeSet, R.styleable.FlatButton) ?: return

        try {

            val defValue = getDimension(R.dimen.corner_radius)
            cornerRadius = attr.getDimension(R.styleable.FlatButton_pb_cornerRadius, defValue)

            normalDrawable.addState(intArrayOf(android.R.attr.state_pressed),
                createPressedDrawable(attr))
            normalDrawable.addState(intArrayOf(android.R.attr.state_focused),
                createPressedDrawable(attr))
            normalDrawable.addState(intArrayOf(android.R.attr.state_selected),
                createPressedDrawable(attr))
            normalDrawable.addState(intArrayOf(), createNormalDrawable(attr))

        } finally {
            attr.recycle()
        }
    }

    fun getNormalText(): CharSequence? {
        return normalText
    }

    open fun setNormalText(normalText: CharSequence?) {
        this.normalText = normalText
    }

    private fun createNormalDrawable(attr: TypedArray): LayerDrawable {
        val drawableNormal = getDrawable(R.drawable.rect_normal).mutate() as LayerDrawable

        val drawableTop = drawableNormal.getDrawable(0).mutate() as GradientDrawable
        drawableTop.cornerRadius = cornerRadius

        val blueDark = getColor(R.color.blue_pressed)
        val colorPressed = attr.getColor(R.styleable.FlatButton_pb_colorPressed, blueDark)
        drawableTop.setColor(colorPressed)

        val drawableBottom = drawableNormal.getDrawable(1).mutate() as GradientDrawable
        drawableBottom.cornerRadius = cornerRadius

        val blueNormal = getColor(R.color.blue_normal)
        val colorNormal = attr.getColor(R.styleable.FlatButton_pb_colorNormal, blueNormal)
        drawableBottom.setColor(colorNormal)
        return drawableNormal
    }

    private fun createPressedDrawable(attr: TypedArray): Drawable {
        val drawablePressed = getDrawable(R.drawable.rect_pressed).mutate() as GradientDrawable
        drawablePressed.cornerRadius = cornerRadius

        val blueDark = getColor(R.color.blue_pressed)
        val colorPressed = attr.getColor(R.styleable.FlatButton_pb_colorPressed, blueDark)
        drawablePressed.setColor(colorPressed)

        return drawablePressed
    }

    protected fun getDrawable(id: Int): Drawable {
        return resources.getDrawable(id)
    }

    protected fun getDimension(id: Int): Float {
        return resources.getDimension(id)
    }

    protected fun getColor(id: Int): Int {
        return resources.getColor(id)
    }

    protected fun getTypedArray(context: Context, attributeSet: AttributeSet, attr: IntArray): TypedArray {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0)
    }

    /**
     * Set the View's background. Masks the API changes made in Jelly Bean.
     *
     * @param drawable
     */
    @SuppressLint("NewApi")
    fun setBackgroundCompat(drawable: Drawable) {
        val pL = paddingLeft
        val pT = paddingTop
        val pR = paddingRight
        val pB = paddingBottom

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            background = drawable
        } else {
            setBackgroundDrawable(drawable)
        }
        setPadding(pL, pT, pR, pB)
    }
}