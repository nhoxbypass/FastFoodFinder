package com.iceteaviet.fastfoodfinder.ui.main.search

import com.iceteaviet.fastfoodfinder.domain.model.Store
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchEventBus @Inject constructor() {
    private val _events = kotlinx.coroutines.flow.MutableSharedFlow<SearchEventResult>(extraBufferCapacity = 16)
    val events: kotlinx.coroutines.flow.SharedFlow<SearchEventResult> = _events

    fun emit(event: SearchEventResult) {
        _events.tryEmit(event)
    }
}

class SearchEventResult private constructor(
    val resultCode: Int,
    val searchString: String,
    val storeType: Int,
    val store: Store?
) {
    constructor(resultCode: Int, searchString: String, storeType: Int) : this(resultCode, searchString, storeType, null)
    constructor(resultCode: Int, searchString: String, store: Store) : this(resultCode, searchString, 0, store)
    constructor(resultCode: Int, storeType: Int) : this(resultCode, "", storeType, null)
    constructor(resultCode: Int, searchString: String) : this(resultCode, searchString, 0, null)
    constructor(resultCode: Int) : this(resultCode, "", 0, null)

    companion object {
        const val SEARCH_ACTION_QUICK = 0
        const val SEARCH_ACTION_QUERY_SUBMIT = 1
        const val SEARCH_ACTION_COLLAPSE = 2
        const val SEARCH_ACTION_STORE_CLICK = 3
    }
}
