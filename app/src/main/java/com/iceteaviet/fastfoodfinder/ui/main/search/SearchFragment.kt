package com.iceteaviet.fastfoodfinder.ui.main.search

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.databinding.FragmentSearchBinding
import com.iceteaviet.fastfoodfinder.utils.StoreType
import com.iceteaviet.fastfoodfinder.utils.openStoreListActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var binding: FragmentSearchBinding

    private lateinit var quickSearchCircleK: CircleImageView
    private lateinit var quickSearchFamilyMart: CircleImageView
    private lateinit var quickSearchMiniStop: CircleImageView
    private lateinit var quickSearchLoadMore: CircleImageView
    private lateinit var quickSearchBsMart: CircleImageView
    private lateinit var quickSearch7Eleven: CircleImageView

    private lateinit var cvActionContainer: CardView
    private lateinit var cvRecentlyContainer: CardView
    private lateinit var cvTimeSuggestionContainer: CardView
    private lateinit var cvSuggestionContainer: CardView
    private lateinit var cvSearchContainer: CardView
    private lateinit var cardViewQuickSearch: ViewGroup
    private lateinit var searchMoreLayout: ViewGroup
    private lateinit var searchContainer: ScrollView

    private lateinit var tvRecently: TextView
    private lateinit var tvTimeSuggestion: TextView
    private lateinit var tvSuggestion: TextView

    private lateinit var rvRecentlyStores: RecyclerView
    private lateinit var rvSuggestedStores: RecyclerView
    private lateinit var rvSearch: RecyclerView

    private var recentlySearchAdapter: RecentlyStoreSearchAdapter? = null
    private var suggestedSearchAdapter: SuggestStoreSearchAdapter? = null
    private var searchAdapter: StoreSearchAdapter? = null

    private var isLoadMoreVisible: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupEventHandlers()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (state.searchHistoryStrings.isNotEmpty() || state.searchHistoryItems.isNotEmpty()) {
                        searchAdapter?.setRecentlySearch(state.searchHistoryStrings)
                        recentlySearchAdapter?.setSearchItems(state.searchHistoryItems)
                    }

                    if (state.searchStores.isNotEmpty()) {
                        searchAdapter?.setSearchItems(state.searchStores)
                    }

                    when (state.event) {
                        is SearchEvent.Idle -> {}
                        is SearchEvent.ShowStoreListView -> {
                            openStoreListActivity(requireActivity())
                            viewModel.markEventConsumed()
                        }
                        is SearchEvent.ShowGeneralErrorMessage -> {
                            Toast.makeText(requireActivity(), R.string.error_general_error_code, Toast.LENGTH_LONG).show()
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    private fun setupUI() {
        findViews()

        recentlySearchAdapter = RecentlyStoreSearchAdapter()
        rvRecentlyStores.layoutManager = LinearLayoutManager(context)
        rvRecentlyStores.adapter = recentlySearchAdapter

        suggestedSearchAdapter = SuggestStoreSearchAdapter()
        rvSuggestedStores.layoutManager = LinearLayoutManager(context)
        rvSuggestedStores.adapter = suggestedSearchAdapter

        searchAdapter = StoreSearchAdapter()
        rvSearch.layoutManager = LinearLayoutManager(context)
        rvSearch.adapter = searchAdapter
    }

    private fun findViews() {
        cvActionContainer = binding.cvActionContainer
        cvRecentlyContainer = binding.cvRecentlyContainer
        cvTimeSuggestionContainer = binding.cvTimeSuggestionContainer
        cvSuggestionContainer = binding.cvSuggestionContainer
        cvSearchContainer = binding.cvSearchContainer
        tvRecently = binding.tvRecently
        tvTimeSuggestion = binding.tvTimeSuggestion
        tvSuggestion = binding.tvSuggestion
        quickSearchCircleK = binding.btnSearchCircleK
        quickSearchFamilyMart = binding.btnSearchFamilyMart
        quickSearchMiniStop = binding.btnSearchMiniStop
        quickSearchLoadMore = binding.btnLoadMore
        quickSearchBsMart = binding.btnSearchBsmart
        quickSearch7Eleven = binding.btnSearch7Eleven
        cardViewQuickSearch = binding.cvActionContainer
        searchMoreLayout = binding.llLoadMoreContainer
        searchContainer = binding.svSearchContainer
        rvRecentlyStores = binding.rvRecentlyStores
        rvSuggestedStores = binding.rvSuggestedStores
        rvSearch = binding.rvSearch
    }

    private fun setupEventHandlers() {
        recentlySearchAdapter?.setOnItemClickListener(object : BaseSearchAdapter.OnItemClickListener {
            override fun onClick(store: Store) {
                viewModel.onStoreSearchClick(store)
            }
        })

        searchAdapter?.setOnItemClickListener(object : BaseSearchAdapter.OnItemClickListener {
            override fun onClick(store: Store) {
                viewModel.onStoreSearchClick(store)
            }
        })

        quickSearchCircleK.setOnClickListener {
            viewModel.onQuickSearchItemClick(StoreType.TYPE_CIRCLE_K)
        }

        quickSearchFamilyMart.setOnClickListener {
            viewModel.onQuickSearchItemClick(StoreType.TYPE_FAMILY_MART)
        }

        quickSearchMiniStop.setOnClickListener {
            viewModel.onQuickSearchItemClick(StoreType.TYPE_MINI_STOP)
        }

        quickSearchBsMart.setOnClickListener {
            viewModel.onQuickSearchItemClick(StoreType.TYPE_BSMART)
        }

        quickSearch7Eleven.setOnClickListener {
            viewModel.onQuickSearchItemClick(StoreType.TYPE_7_ELEVEN)
        }

        quickSearchLoadMore.setOnClickListener {
            TransitionManager.beginDelayedTransition(cardViewQuickSearch)
            isLoadMoreVisible = !isLoadMoreVisible
            searchMoreLayout.visibility = if (isLoadMoreVisible) View.VISIBLE else View.GONE
        }

        binding.tvTop.setOnClickListener {
            viewModel.onTopStoreButtonClick()
        }
        binding.tvNearest.setOnClickListener {
            viewModel.onNearestStoreButtonClick()
        }
        binding.tvTrending.setOnClickListener {
            viewModel.onTrendingStoreButtonClick()
        }
        binding.tvConvenienceStore.setOnClickListener {
            viewModel.onConvenienceStoreButtonClick()
        }
    }

    fun hideOptionsContainer() {
        cvActionContainer.visibility = View.GONE
        cvRecentlyContainer.visibility = View.GONE
        cvTimeSuggestionContainer.visibility = View.GONE
        cvSuggestionContainer.visibility = View.GONE
        tvRecently.visibility = View.GONE
        tvTimeSuggestion.visibility = View.GONE
        tvSuggestion.visibility = View.GONE
    }

    fun showSearchContainer() {
        cvSearchContainer.visibility = View.VISIBLE
    }

    fun showOptionsContainer() {
        cvActionContainer.visibility = View.VISIBLE
        cvRecentlyContainer.visibility = View.VISIBLE
        cvTimeSuggestionContainer.visibility = View.VISIBLE
        cvSuggestionContainer.visibility = View.VISIBLE
        tvRecently.visibility = View.VISIBLE
        tvTimeSuggestion.visibility = View.VISIBLE
        tvSuggestion.visibility = View.VISIBLE
    }

    fun hideSearchContainer() {
        cvSearchContainer.visibility = View.GONE
    }

    fun updateSearchList(searchText: String) {
        viewModel.onUpdateSearchList(searchText)
    }

    companion object {
        fun newInstance(): SearchFragment {
            val args = Bundle()
            val fragment = SearchFragment()
            fragment.arguments = args
            return fragment
        }
    }
}