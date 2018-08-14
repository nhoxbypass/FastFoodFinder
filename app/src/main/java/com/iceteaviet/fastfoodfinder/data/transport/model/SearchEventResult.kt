package com.iceteaviet.fastfoodfinder.data.transport.model

/**
 * Created by Genius Doan on 11/28/2016.
 */
class SearchEventResult {
    var resultCode: Int = 0
        private set
    var searchString: String? = null
        private set
    var storeType: Int = 0
        private set

    constructor(resultCode: Int, searchString: String, storeType: Int) {
        this.resultCode = resultCode
        this.searchString = searchString
        this.storeType = storeType
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

    companion object {
        const val SEARCH_ACTION_QUICK = 0
        const val SEARCH_ACTION_QUERY_SUBMIT = 1
        const val SEARCH_ACTION_COLLAPSE = 2
    }
}
