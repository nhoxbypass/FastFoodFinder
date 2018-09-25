package com.iceteaviet.fastfoodfinder.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

/**
 *
 * A custom view that extends [RelativeLayout]
 * and which places its children horizontally,
 * flowing over to a new line whenever it runs out of width.
 *
 *
 *
 * This view is a modification of Nishant Nair's Blog post here:
 * [https://nishantvnair.wordpress.com/2010/09/28/flowlayout-in-android](https://nishantvnair.wordpress.com/2010/09/28/flowlayout-in-android)
 *
 *
 *
 * TODO: modify class to allow for right-to-left placement of sub-views as well as left-to-right placement of sub-views.
 */
class HorizontalFlowLayout : RelativeLayout {

    /**
     * Constructor to use when creating the view from code.
     */
    constructor(context: Context) : super(context) {}

    /**
     * Constructor that is called when inflating the view from XML.
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    /**
     * Perform inflation from XML and apply a class-specific base style from a theme attribute.
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    //    /**
    //     * Perform inflation from XML and apply a class-specific base style from a theme attribute or style resource.
    //     *
    //     * TODO: uncomment this constructor once the minimum API level of this project is changed to 21 (Lollipop).
    //     */
    //    public HorizontalFlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    //        super(context, attrs, defStyleAttr, defStyleRes);
    //    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // need to call super.onMeasure(...) otherwise we'll get some funny behaviour
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        var height = View.MeasureSpec.getSize(heightMeasureSpec)

        val requiredHeight = measureRequiredHeight(
                width,
                paddingTop,
                paddingBottom,
                paddingLeft,
                paddingRight)

        if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.UNSPECIFIED) {
            // set height as required since there's no height restrictions
            height = requiredHeight
        } else if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.AT_MOST && requiredHeight < height) {
            // set height as required since it's less than the maximum allowed
            height = requiredHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // increment the x position as we progress through a line
        var xpos = paddingLeft
        // increment the y position as we progress through the lines
        var ypos = paddingTop
        // the height of the current line
        var line_height = 0

        var child: View
        var childMarginLayoutParams: ViewGroup.MarginLayoutParams
        var childWidth: Int
        var childHeight: Int
        var childMarginLeft: Int
        var childMarginRight: Int
        var childMarginTop: Int
        var childMarginBottom: Int

        // note: considering "margins" here...
        // ... but don't need to consider "translations" as translations are done post-layout

        for (i in 0 until childCount) {
            child = getChildAt(i)

            if (child.visibility != View.GONE) {
                childWidth = child.measuredWidth
                childHeight = child.measuredHeight

                if (child.layoutParams != null && child.layoutParams is ViewGroup.MarginLayoutParams) {
                    childMarginLayoutParams = child.layoutParams as ViewGroup.MarginLayoutParams

                    childMarginLeft = childMarginLayoutParams.leftMargin
                    childMarginRight = childMarginLayoutParams.rightMargin
                    childMarginTop = childMarginLayoutParams.topMargin
                    childMarginBottom = childMarginLayoutParams.bottomMargin
                } else {
                    childMarginLeft = 0
                    childMarginRight = 0
                    childMarginTop = 0
                    childMarginBottom = 0
                }

                if (xpos + childMarginLeft + childWidth + childMarginRight + paddingRight > r - l) {
                    // this child will need to go on a new line

                    xpos = paddingLeft
                    ypos += line_height

                    line_height = childHeight + childMarginTop + childMarginBottom
                } else {
                    // enough space for this child on the current line
                    line_height = Math.max(
                            line_height,
                            childMarginTop + childHeight + childMarginBottom)
                }

                child.layout(
                        xpos + childMarginLeft,
                        ypos + childMarginTop,
                        xpos + childMarginLeft + childWidth,
                        ypos + childMarginTop + childHeight)

                xpos += childMarginLeft + childWidth + childMarginRight
            }
        }
    }

    /**
     *
     * Measures the height required by this view
     * to fit all its children within the available width,
     * wrapping its children over multiple lines if necessary.
     *
     *
     *
     * (Package-private visibility for uint tests access.)
     *
     * @param width the width available to this view.
     * @return the height required by this view.
     */
    private fun measureRequiredHeight(width: Int,
                                      paddingTop: Int,
                                      paddingBottom: Int,
                                      paddingLeft: Int,
                                      paddingRight: Int): Int {
        // increment the x position as we progress through a line
        var xpos = paddingLeft
        // increment the y position as we progress through the lines
        var ypos = paddingTop
        // the height of the current line
        var line_height = 0

        // go through children
        // to work out the height required for this view

        // call to measure size of children not needed I think?!
        // getting child's measured height/width seems to work okay without it
        //measureChildren(widthMeasureSpec, heightMeasureSpec);

        var child: View
        var childMarginLayoutParams: ViewGroup.MarginLayoutParams
        var childWidth: Int
        var childHeight: Int
        var childMarginLeft: Int
        var childMarginRight: Int
        var childMarginTop: Int
        var childMarginBottom: Int

        for (i in 0 until childCount) {
            child = getChildAt(i)

            if (child.visibility != View.GONE) {
                childWidth = child.measuredWidth
                childHeight = child.measuredHeight

                if (child.layoutParams != null && child.layoutParams is ViewGroup.MarginLayoutParams) {
                    childMarginLayoutParams = child.layoutParams as ViewGroup.MarginLayoutParams

                    childMarginLeft = childMarginLayoutParams.leftMargin
                    childMarginRight = childMarginLayoutParams.rightMargin
                    childMarginTop = childMarginLayoutParams.topMargin
                    childMarginBottom = childMarginLayoutParams.bottomMargin
                } else {
                    childMarginLeft = 0
                    childMarginRight = 0
                    childMarginTop = 0
                    childMarginBottom = 0
                }

                if (xpos + childMarginLeft + childWidth + childMarginRight + paddingRight > width) {
                    // this child will need to go on a new line

                    xpos = paddingLeft
                    ypos += line_height

                    line_height = childMarginTop + childHeight + childMarginBottom
                } else {
                    // enough space for this child on the current line
                    line_height = Math.max(
                            line_height,
                            childMarginTop + childHeight + childMarginBottom)
                }

                xpos += childMarginLeft + childWidth + childMarginRight
            }
        }

        ypos += line_height + paddingBottom

        return ypos
    }
}