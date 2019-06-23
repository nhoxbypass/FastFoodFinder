package com.iceteaviet.fastfoodfinder.ui.main.map.model

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store

/**
 * Created by tom on 2019-04-29.
 */
class NearByStore(var store: Store, var distance: Double) {
    override fun equals(other: Any?): Boolean {
        return if (other is NearByStore) {
            store.equals(other.store) && distance.equals(other.distance)
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = store.hashCode()
        result = 31 * result + distance.hashCode()
        return result
    }
}