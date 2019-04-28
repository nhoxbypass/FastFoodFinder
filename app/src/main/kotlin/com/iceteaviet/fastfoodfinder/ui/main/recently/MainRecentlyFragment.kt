package com.iceteaviet.fastfoodfinder.ui.main.recently

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.main.OnStartDragListener
import com.iceteaviet.fastfoodfinder.ui.main.SimpleItemTouchHelperCallback
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailActivity
import kotlinx.android.synthetic.main.fragment_main_recently.*
import java.util.*

/**
 * Created by MyPC on 11/20/2016.
 */
class MainRecentlyFragment : Fragment(), MainRecentlyContract.View, OnStartDragListener {
    override lateinit var presenter: MainRecentlyContract.Presenter

    lateinit var recyclerView: RecyclerView
    lateinit var containerLayout: FrameLayout

    private var mRecentlyAdapter: RecentlyStoreAdapter? = null
    private var mItemTouchHelper: ItemTouchHelper? = null
    private val isFABChangeClicked = false // TODO: Check usage of this

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main_recently, container, false)
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        recyclerView = rv_recently_stores
        containerLayout = fl_container

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

    override fun setStores(stores: ArrayList<Store>) {
        mRecentlyAdapter!!.setStores(stores)
    }

    override fun showStoreDetailView(store: Store) {
        activity?.startActivity(StoreDetailActivity.getIntent(context!!, store))
    }

    private fun setupUI() {
        mRecentlyAdapter = RecentlyStoreAdapter(this, containerLayout)
        val llm = LinearLayoutManager(context)
        recyclerView.layoutManager = llm
        recyclerView.adapter = mRecentlyAdapter
        val decoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        mRecentlyAdapter?.let {
            val callback = SimpleItemTouchHelperCallback(it)
            mItemTouchHelper = ItemTouchHelper(callback)
            mItemTouchHelper!!.attachToRecyclerView(recyclerView)
        }
    }

    private fun setupEventHandlers() {
        mRecentlyAdapter!!.setOnItemClickListener(object : RecentlyStoreAdapter.OnItemClickListener {
            override fun onClick(store: Store) {
                presenter.onStoreItemClick(store)
            }
        })
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        if (isFABChangeClicked)
            mItemTouchHelper!!.startDrag(viewHolder)
    }

    companion object {


        fun newInstance(): MainRecentlyFragment {
            val args = Bundle()
            val fragment = MainRecentlyFragment()
            fragment.arguments = args
            fragment.presenter = MainRecentlyPresenter(App.getDataManager(), fragment)
            return fragment
        }
    }
}
