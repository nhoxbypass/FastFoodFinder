package com.iceteaviet.fastfoodfinder.ui.main.recently

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.databinding.FragmentMainRecentlyBinding
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.OnStartDragListener
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.SimpleItemTouchHelperCallback
import com.iceteaviet.fastfoodfinder.utils.openStoreDetailActivity

/**
 * Created by MyPC on 11/20/2016.
 */
class MainRecentlyFragment : Fragment(), MainRecentlyContract.View, OnStartDragListener {
    override lateinit var presenter: MainRecentlyContract.Presenter

    /**
     * Views Ref
     */
    private lateinit var binding: FragmentMainRecentlyBinding

    lateinit var recyclerView: RecyclerView
    lateinit var containerLayout: FrameLayout

    private var mRecentlyAdapter: RecentlyStoreAdapter? = null
    private var mItemTouchHelper: ItemTouchHelper? = null
    private val isFABChangeClicked = false // TODO: Check usage of this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainRecentlyBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_main_recently, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.rvRecentlyStores
        containerLayout = binding.flContainer

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
        mRecentlyAdapter?.setStores(stores)
    }

    override fun showStoreDetailView(store: Store) {
        openStoreDetailActivity(requireActivity(), store)
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
            mItemTouchHelper?.attachToRecyclerView(recyclerView)
        }
    }

    private fun setupEventHandlers() {
        mRecentlyAdapter?.setOnItemClickListener(object : RecentlyStoreAdapter.OnItemClickListener {
            override fun onClick(store: Store) {
                presenter.onStoreItemClick(store)
            }
        })
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        if (isFABChangeClicked)
            mItemTouchHelper?.startDrag(viewHolder)
    }

    companion object {
        fun newInstance(): MainRecentlyFragment {
            val args = Bundle()
            val fragment = MainRecentlyFragment()
            fragment.arguments = args
            fragment.presenter = MainRecentlyPresenter(App.getDataManager(), App.getSchedulerProvider(), fragment)
            return fragment
        }
    }
}