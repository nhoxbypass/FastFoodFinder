package com.iceteaviet.fastfoodfinder.ui.main.search


import android.os.Build
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.databinding.FragmentSearchBinding
import com.iceteaviet.fastfoodfinder.ui.main.search.model.SearchStoreItem
import com.iceteaviet.fastfoodfinder.utils.StoreType
import com.iceteaviet.fastfoodfinder.utils.openStoreListActivity
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Search fragment
 *
 * TODO: Research & apply https://developer.android.com/guide/topics/search/
 */
class SearchFragment : Fragment(), SearchContract.View {
    override lateinit var presenter: SearchContract.Presenter

    /**
     * Views Ref
     */
    private lateinit var binding: FragmentSearchBinding

    private lateinit var quickSearchCircleK: CircleImageView
    private lateinit var quickSearchFamilyMart: CircleImageView
    private lateinit var quickSearchMiniStop: CircleImageView
    private lateinit var quickSearchLoadMore: CircleImageView
    private lateinit var quickSearchBsMart: CircleImageView
    private lateinit var quickSearchShopNGo: CircleImageView

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupEventHandlers()
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun setSearchHistory(searchHistory: List<String>, searchItems: List<SearchStoreItem>) {
        searchAdapter?.setRecentlySearch(searchHistory)
        recentlySearchAdapter?.setSearchItems(searchItems)
    }

    override fun setSearchStores(searchItems: List<SearchStoreItem>) {
        searchAdapter?.setSearchItems(searchItems)
    }

    override fun showStoreListView() {
        openStoreListActivity(requireActivity())
    }

    override fun showGeneralErrorMessage() {
        Toast.makeText(requireActivity(), R.string.error_general_error_code, Toast.LENGTH_LONG).show()
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
        quickSearchShopNGo = binding.btnSearchShopNGo
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
                presenter.onStoreSearchClick(store)
            }

        })

        searchAdapter?.setOnItemClickListener(object : BaseSearchAdapter.OnItemClickListener {
            override fun onClick(store: Store) {
                presenter.onStoreSearchClick(store)
            }
        })

        // Quick search bars
        quickSearchCircleK.setOnClickListener {
            presenter.onQuickSearchItemClick(StoreType.TYPE_CIRCLE_K)
        }

        quickSearchFamilyMart.setOnClickListener {
            presenter.onQuickSearchItemClick(StoreType.TYPE_FAMILY_MART)
        }

        quickSearchMiniStop.setOnClickListener {
            presenter.onQuickSearchItemClick(StoreType.TYPE_MINI_STOP)
        }

        quickSearchBsMart.setOnClickListener {
            presenter.onQuickSearchItemClick(StoreType.TYPE_BSMART)
        }

        quickSearchShopNGo.setOnClickListener {
            presenter.onQuickSearchItemClick(StoreType.TYPE_SHOP_N_GO)
        }

        quickSearchLoadMore.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(cardViewQuickSearch)
                isLoadMoreVisible = !isLoadMoreVisible
                searchMoreLayout.visibility = if (isLoadMoreVisible) View.VISIBLE else View.GONE
            }
        }

        binding.tvTop.setOnClickListener {
            presenter.onTopStoreButtonClick()
        }
        binding.tvNearest.setOnClickListener {
            presenter.onNearestStoreButtonClick()
        }
        binding.tvTrending.setOnClickListener {
            presenter.onTrendingStoreButtonClick()
        }
        binding.tvConvenienceStore.setOnClickListener {
            presenter.onConvenienceStoreButtonClick()
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
        presenter.onUpdateSearchList(searchText)
    }

    companion object {
        fun newInstance(): SearchFragment {
            val args = Bundle()

            val fragment = SearchFragment()
            fragment.arguments = args
            fragment.presenter = SearchPresenter(App.getDataManager(), App.getSchedulerProvider(), App.getBus(), fragment)
            return fragment
        }
    }
}