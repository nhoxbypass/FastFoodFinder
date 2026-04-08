package com.iceteaviet.fastfoodfinder.ui.main.recently

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.databinding.FragmentMainRecentlyBinding
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.OnStartDragListener
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.SimpleItemTouchHelperCallback
import com.iceteaviet.fastfoodfinder.utils.openStoreDetailActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainRecentlyFragment : Fragment(), OnStartDragListener {

    private val viewModel: MainRecentlyViewModel by viewModels()

    private lateinit var binding: FragmentMainRecentlyBinding

    lateinit var recyclerView: RecyclerView
    lateinit var containerLayout: FrameLayout

    private var mRecentlyAdapter: RecentlyStoreAdapter? = null
    private var mItemTouchHelper: ItemTouchHelper? = null
    private val isFABChangeClicked = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainRecentlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.rvRecentlyStores
        containerLayout = binding.flContainer

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
                    when (state) {
                        is MainRecentlyEvent.Idle -> {}
                        is MainRecentlyEvent.SetStores -> {
                            mRecentlyAdapter?.setStores(state.stores)
                            viewModel.markEventConsumed()
                        }
                        is MainRecentlyEvent.ShowStoreDetailView -> {
                            openStoreDetailActivity(requireActivity(), state.store)
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
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
                viewModel.onStoreItemClick(store)
            }
        })
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        if (isFABChangeClicked) {
            mItemTouchHelper?.startDrag(viewHolder)
        }
    }

    companion object {
        fun newInstance(): MainRecentlyFragment {
            val args = Bundle()
            val fragment = MainRecentlyFragment()
            fragment.arguments = args
            return fragment
        }
    }
}