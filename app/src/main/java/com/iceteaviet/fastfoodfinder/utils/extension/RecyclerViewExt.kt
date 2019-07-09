package com.iceteaviet.fastfoodfinder.utils.extension

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.iceteaviet.fastfoodfinder.ui.custom.snaphelper.OnSnapListener
import com.iceteaviet.fastfoodfinder.ui.custom.snaphelper.OnSnapPositionChangeListener

/**
 * Created by tom on 2019-07-06.
 */
fun RecyclerView.attachSnapHelperToListener(snapHelper: SnapHelper,
                                            onSnapPositionChangeListener: OnSnapPositionChangeListener,
                                            behavior: OnSnapListener.Behavior = OnSnapListener.Behavior.NOTIFY_ON_SCROLL) {
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener = OnSnapListener(snapHelper, onSnapPositionChangeListener, behavior)
    addOnScrollListener(snapOnScrollListener)
}