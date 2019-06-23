package com.iceteaviet.fastfoodfinder.service.eventbus

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.service.eventbus.core.Event

/**
 * Created by Genius Doan on 11/28/2016.
 */
class SearchEventResult : Event {
    var resultCode: Int = 0
        private set
    var searchString: String = ""
        private set
    var storeType: Int = 0
        private set

    var store: Store? = null
        private set

    constructor(resultCode: Int, searchString: String, storeType: Int) {
        this.resultCode = resultCode
        this.searchString = searchString
        this.storeType = storeType
    }

    constructor(resultCode: Int, searchString: String, store: Store) {
        this.resultCode = resultCode
        this.searchString = searchString
        this.store = store
    }

    constructor(resultCode: Int, storeType: Int) {
        this.resultCode = resultCode
        this.storeType = storeType
    }

    constructor(resultCode: Int, searchString: String) {
        this.resultCode = resultCode
        this.searchString = searchString
    }

    constructor(resultCode: Int) {
        this.resultCode = resultCode
    }

    override fun equals(other: Any?): Boolean {
        return if (other is SearchEventResult) {
            resultCode.equals(other.resultCode) && searchString.equals(other.searchString)
                    && storeType.equals(other.storeType) && (store == null || store!!.equals(other.store))
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = resultCode
        result = 31 * result + searchString.hashCode()
        result = 31 * result + storeType
        result = 31 * result + (store?.hashCode() ?: 0)
        return result
    }

    companion object {
        const val SEARCH_ACTION_QUICK = 0
        const val SEARCH_ACTION_QUERY_SUBMIT = 1
        const val SEARCH_ACTION_COLLAPSE = 2
        const val SEARCH_ACTION_STORE_CLICK = 3
    }
}
