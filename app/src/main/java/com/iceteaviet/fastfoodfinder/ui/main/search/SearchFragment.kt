package com.iceteaviet.fastfoodfinder.ui.main.search


import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.custom.store.BaseStoreAdapter
import com.iceteaviet.fastfoodfinder.utils.StoreType
import com.iceteaviet.fastfoodfinder.utils.openStoreListActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_search.*


/**
 * Search fragment
 *
 * TODO: Research & apply https://developer.android.com/guide/topics/search/
 */
class SearchFragment : Fragment(), SearchContract.View {
    override lateinit var presenter: SearchContract.Presenter

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

    override fun onCreateView(@NonNull inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
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

    override fun setSearchHistory(searchHistory: List<String>, recentlyStores: List<Store>) {
        searchAdapter?.setRecentlySearch(searchHistory)
        recentlySearchAdapter?.setStores(recentlyStores)
    }

    override fun setSearchStores(searchStores: List<Store>) {
        searchAdapter?.setStores(searchStores)
    }

    override fun showStoreListView() {
        openStoreListActivity(activity!!)
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
        cvActionContainer = cv_action_container
        cvRecentlyContainer = cv_recently_container
        cvTimeSuggestionContainer = cv_time_suggestion_container
        cvSuggestionContainer = cv_suggestion_container
        cvSearchContainer = cv_search_container
        tvRecently = tv_recently
        tvTimeSuggestion = tv_time_suggestion
        tvSuggestion = tv_suggestion
        quickSearchCircleK = btn_search_circle_k
        quickSearchFamilyMart = btn_search_family_mart
        quickSearchMiniStop = btn_search_mini_stop
        quickSearchLoadMore = btn_load_more
        quickSearchBsMart = btn_search_bsmart
        quickSearchShopNGo = btn_search_shop_n_go
        cardViewQuickSearch = cv_action_container
        searchMoreLayout = ll_load_more_container
        searchContainer = sv_search_container
        rvRecentlyStores = rv_recently_stores
        rvSuggestedStores = rv_suggested_stores
        rvSearch = rv_search
    }

    private fun setupEventHandlers() {
        recentlySearchAdapter?.setOnItemClickListener(object : BaseStoreAdapter.OnItemClickListener {
            override fun onClick(store: Store) {
                presenter.onStoreSearchClick(store)
            }

        })

        searchAdapter?.setOnItemClickListener(object : BaseStoreAdapter.OnItemClickListener {
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

        tvTop.setOnClickListener {
            presenter.onTopStoreButtonClick()
        }

        tvNearest.setOnClickListener {
            presenter.onNearestStoreButtonClick()
        }
        tvTrending.setOnClickListener {
            presenter.onTrendingStoreButtonClick()
        }
        tvConvenienceStore.setOnClickListener {
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
            fragment.presenter = SearchPresenter(App.getDataManager(), App.getSchedulerProvider(), fragment)
            return fragment
        }
    }
}
