package com.iceteaviet.fastfoodfinder.ui.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.main.search.model.SearchStoreItem
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.getStoreSearchString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val searchHistoryStrings: List<String> = emptyList(),
    val searchHistoryItems: List<SearchStoreItem> = emptyList(),
    val searchStores: List<SearchStoreItem> = emptyList(),
    val event: SearchEvent = SearchEvent.Idle
)

sealed class SearchEvent {
    object Idle : SearchEvent()
    object ShowStoreListView : SearchEvent()
    object ShowGeneralErrorMessage : SearchEvent()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val preferencesRepository: PreferencesRepository,
    private val searchEventBus: SearchEventBus
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun start() {
        val searchHistories = preferencesRepository.getSearchHistories().toList().asReversed()
        if (searchHistories.isNotEmpty()) {
            viewModelScope.launch {
                val items = getStoresFromIds(searchHistories)
                _uiState.value = _uiState.value.copy(
                    searchHistoryStrings = searchHistories,
                    searchHistoryItems = items
                )
            }
        }
    }

    fun onStoreSearchClick(store: Store) {
        if (store.id == -1) {
            searchEventBus.emit(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, store.title, store))
        } else {
            searchEventBus.emit(SearchEventResult(SearchEventResult.SEARCH_ACTION_STORE_CLICK, store.title, store))
        }
    }

    fun onQuickSearchItemClick(storeType: Int) {
        val searchString = getStoreSearchString(storeType)
        searchEventBus.emit(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString, storeType))
    }

    fun onUpdateSearchList(searchText: String) {
        viewModelScope.launch {
            try {
                val storeList = storeRepository.findStores(searchText)
                _uiState.value = _uiState.value.copy(
                    searchStores = storeListToSearchItems(storeList)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(event = SearchEvent.ShowGeneralErrorMessage)
            }
        }
    }

    fun onTopStoreButtonClick() {}
    fun onNearestStoreButtonClick() {}
    fun onTrendingStoreButtonClick() {}
    fun onConvenienceStoreButtonClick() {}

    private suspend fun getStoresFromIds(searchHistories: List<String>): List<SearchStoreItem> {
        val searchItems: MutableList<SearchStoreItem> = ArrayList()
        for (history in searchHistories) {
            if (history.contains(Constant.SEARCH_STORE_PREFIX)) {
                try {
                    val storeId = history.substring(Constant.SEARCH_STORE_PREFIX_LEN).toInt()
                    val store = storeRepository.findStoreById(storeId)
                    searchItems.add(SearchStoreItem(store, ""))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            } else {
                searchItems.add(SearchStoreItem(null, history))
            }
        }
        return searchItems
    }

    private fun storeListToSearchItems(stores: List<Store>): List<SearchStoreItem> {
        val searchItems: MutableList<SearchStoreItem> = ArrayList()
        for (store in stores) {
            searchItems.add(SearchStoreItem(store, ""))
        }
        return searchItems
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(event = SearchEvent.Idle)
    }
}
