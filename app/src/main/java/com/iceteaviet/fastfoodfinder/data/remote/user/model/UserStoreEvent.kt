package com.iceteaviet.fastfoodfinder.data.remote.user.model

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store

/**
 * Created by tom on 7/17/18.
 */
class UserStoreEvent(val store: Store, val eventActionCode: Int) {
    companion object {
        const val ACTION_ADDED = 0
        const val ACTION_CHANGED = 1
        const val ACTION_REMOVED = 2
        const val ACTION_MOVED = 3
    }
}
