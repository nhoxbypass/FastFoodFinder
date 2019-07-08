package com.iceteaviet.fastfoodfinder.ui.custom.snaphelper

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.iceteaviet.fastfoodfinder.ui.custom.extension.getSnapPosition

/**
 * Created by tom on 2019-07-06.
 */
class OnSnapListener(private val snapHelper: SnapHelper,
                     private var onSnapPositionChangeListener: OnSnapPositionChangeListener? = null,
                     private var behavior: Behavior = Behavior.NOTIFY_ON_SCROLL) : RecyclerView.OnScrollListener() {

    enum class Behavior {
        NOTIFY_ON_SCROLL,
        NOTIFY_ON_SCROLL_STATE_IDLE,
        NOTIFY_ON_SCROLL_STATE_IDLE_BY_DRAGGING
    }

    private var lastSnapPosition = RecyclerView.NO_POSITION
    private var fromDragging = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL) {
            checkNotifySnapPositionChange(recyclerView)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
            fromDragging = true

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (behavior == Behavior.NOTIFY_ON_SCROLL_STATE_IDLE)
                checkNotifySnapPositionChange(recyclerView)
            if (behavior == Behavior.NOTIFY_ON_SCROLL_STATE_IDLE_BY_DRAGGING && fromDragging)
                checkNotifySnapPositionChange(recyclerView)

            fromDragging = false // reset state
        }
    }

    private fun checkNotifySnapPositionChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPosition(recyclerView)
        val snapPositionChanged = this.lastSnapPosition != snapPosition
        if (snapPositionChanged) {
            onSnapPositionChangeListener?.onSnapPositionChange(snapPosition)
            this.lastSnapPosition = snapPosition
        }
    }
}