package com.iceteaviet.fastfoodfinder.ui.custom.processbutton

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import com.iceteaviet.fastfoodfinder.R


/**
 * Created by tom on 2019-03-23.
 */
abstract class ProcessButton : FlatButton {

    private var mProgress: Int = 0
    var maxProgress: Int = 0
        private set
    var minProgress: Int = 0
        private set

    var progressDrawable: GradientDrawable? = null
    var completeDrawable: GradientDrawable? = null
    var errorDrawable: GradientDrawable? = null

    var loadingText: CharSequence? = null
    var completeText: CharSequence? = null
    var errorText: CharSequence? = null

    var progress: Int
        get() = mProgress
        set(progress) {
            mProgress = progress

            if (mProgress == minProgress) {
                onNormalState()
            } else if (mProgress == maxProgress) {
                onCompleteState()
            } else if (mProgress < minProgress) {
                onErrorState()
            } else {
                onProgress()
            }

            invalidate()
        }

    override fun setNormalText(normalText: CharSequence?) {
        super.setNormalText(normalText)
        if (mProgress == minProgress) {
            text = normalText
        }
    }

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
        minProgress = 0
        maxProgress = 100

        progressDrawable = getDrawable(R.drawable.rect_progress).mutate() as GradientDrawable?
        progressDrawable?.cornerRadius = cornerRadius

        completeDrawable = getDrawable(R.drawable.rect_complete).mutate() as GradientDrawable?
        completeDrawable?.cornerRadius = cornerRadius

        errorDrawable = getDrawable(R.drawable.rect_error).mutate() as GradientDrawable?
        errorDrawable?.cornerRadius = cornerRadius

        if (attrs != null) {
            initAttributes(context, attrs)
        }
    }

    private fun initAttributes(context: Context, attributeSet: AttributeSet) {
        val attr = getTypedArray(context, attributeSet, R.styleable.ProcessButton) ?: return

        try {
            loadingText = attr.getString(R.styleable.ProcessButton_pb_textProgress)
            completeText = attr.getString(R.styleable.ProcessButton_pb_textComplete)
            errorText = attr.getString(R.styleable.ProcessButton_pb_textError)

            val purple = getColor(R.color.material_orange_500)
            val colorProgress = attr.getColor(R.styleable.ProcessButton_pb_colorProgress, purple)
            progressDrawable?.setColor(colorProgress)

            val green = getColor(R.color.material_light_green_500)
            val colorComplete = attr.getColor(R.styleable.ProcessButton_pb_colorComplete, green)
            completeDrawable?.setColor(colorComplete)

            val red = getColor(R.color.red_error)
            val colorError = attr.getColor(R.styleable.ProcessButton_pb_colorError, red)
            errorDrawable?.setColor(colorError)

        } finally {
            attr.recycle()
        }
    }

    protected fun onErrorState() {
        if (errorText != null) {
            text = errorText
        }
        setBackgroundCompat(errorDrawable!!)
    }

    protected fun onProgress() {
        if (loadingText != null) {
            text = loadingText
        }
        setBackgroundCompat(normalDrawable)
    }

    protected fun onCompleteState() {
        if (completeText != null) {
            text = completeText
        }
        setBackgroundCompat(completeDrawable!!)
    }

    protected fun onNormalState() {
        if (getNormalText() != null) {
            text = getNormalText()
        }
        setBackgroundCompat(normalDrawable)
    }

    override fun onDraw(canvas: Canvas) {
        // progress
        if (mProgress in (minProgress + 1) until maxProgress) {
            drawProgress(canvas)
        }

        super.onDraw(canvas)
    }

    abstract fun drawProgress(canvas: Canvas)

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.mProgress = mProgress

        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            val savedState = state
            mProgress = savedState.mProgress
            super.onRestoreInstanceState(savedState.superState)
            progress = mProgress
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    /**
     * A [android.os.Parcelable] representing the [com.dd.processbutton.ProcessButton]'s
     * state.
     */
    class SavedState : View.BaseSavedState {

        var mProgress: Int = 0

        constructor(parcel: Parcelable) : super(parcel)

        private constructor(`in`: Parcel) : super(`in`) {
            mProgress = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(mProgress)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}