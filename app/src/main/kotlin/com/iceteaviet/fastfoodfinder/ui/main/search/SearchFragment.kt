package com.iceteaviet.fastfoodfinder.ui.main.search


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.transport.model.SearchEventResult
import com.iceteaviet.fastfoodfinder.ui.storelist.StoreListActivity
import com.iceteaviet.fastfoodfinder.utils.Constant
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_search.*
import org.greenrobot.eventbus.EventBus


/**
 * Search fragment
 *
 * TODO: Research & apply https://developer.android.com/guide/topics/search/
 */
class SearchFragment : Fragment() {

    lateinit var quickSearchCircleK: CircleImageView
    lateinit var quickSearchFamilyMart: CircleImageView
    lateinit var quickSearchMiniStop: CircleImageView
    lateinit var quickSearchLoadMore: CircleImageView
    lateinit var quickSearchBsMart: CircleImageView
    lateinit var quickSearchShopNGo: CircleImageView

    lateinit var cardViewQuickSearch: ViewGroup
    lateinit var searchMoreLayout: ViewGroup
    lateinit var searchContainer: ScrollView

    lateinit var rvRecentlyStores: RecyclerView
    lateinit var rvSuggestedStores: RecyclerView

    private var recentlySearchAdapter: RecentlySearchStoreAdapter? = null
    private var suggestedSearchAdapter: SuggestedSearchStoreAdapter? = null

    private var isLoadMoreVisible: Boolean = false
    private var searchString: String? = null

    private lateinit var dataManager: DataManager

    override fun onCreateView(@NonNull inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = App.getDataManager()

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

        setupUI()
        setupQuickSearchBar()
    }

    private fun setupUI() {
        recentlySearchAdapter = RecentlySearchStoreAdapter()
        rvRecentlyStores.layoutManager = LinearLayoutManager(context)
        rvRecentlyStores.adapter = recentlySearchAdapter
        recentlySearchAdapter!!.setData(dataManager.getSearchHistories())

        suggestedSearchAdapter = SuggestedSearchStoreAdapter()
        rvSuggestedStores.layoutManager = LinearLayoutManager(context)
        rvSuggestedStores.adapter = suggestedSearchAdapter
    }

    private fun setupQuickSearchBar() {
        quickSearchCircleK.setOnClickListener {
            searchString = "Circle K"
            EventBus.getDefault().post(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString!!, Constant.TYPE_CIRCLE_K))
            searchContainer.visibility = View.GONE
        }

        quickSearchFamilyMart.setOnClickListener {
            searchString = "Family Mart"
            EventBus.getDefault().post(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString!!, Constant.TYPE_FAMILY_MART))
            searchContainer.visibility = View.GONE
        }

        quickSearchMiniStop.setOnClickListener {
            searchString = "Mini Stop"
            EventBus.getDefault().post(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString!!, Constant.TYPE_MINI_STOP))
            searchContainer.visibility = View.GONE
        }

        quickSearchBsMart.setOnClickListener {
            searchString = "BsMart"
            EventBus.getDefault().post(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString!!, Constant.TYPE_BSMART))
            searchContainer.visibility = View.GONE
        }

        quickSearchShopNGo.setOnClickListener {
            searchString = "Shop and Go"
            EventBus.getDefault().post(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString!!, Constant.TYPE_SHOP_N_GO))
            searchContainer.visibility = View.GONE
        }

        quickSearchLoadMore.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(cardViewQuickSearch)
                isLoadMoreVisible = !isLoadMoreVisible
                searchMoreLayout.visibility = if (isLoadMoreVisible) View.VISIBLE else View.GONE
            }
        }

        tvTop!!.setOnClickListener {
            val intent = Intent(context, StoreListActivity::class.java)
            startActivity(intent)
        }

        tvNearest!!.setOnClickListener {
            val intent = Intent(context, StoreListActivity::class.java)
            startActivity(intent)
        }
        tvTrending!!.setOnClickListener {
            val intent = Intent(context, StoreListActivity::class.java)
            startActivity(intent)
        }
        tvConvenienceStore!!.setOnClickListener {
            val intent = Intent(context, StoreListActivity::class.java)
            startActivity(intent)
        }
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
