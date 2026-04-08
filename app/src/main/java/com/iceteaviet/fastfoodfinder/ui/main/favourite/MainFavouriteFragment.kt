package com.iceteaviet.fastfoodfinder.ui.main.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.databinding.FragmentMainFavouritedBinding
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.OnStartDragListener
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.SimpleItemTouchHelperCallback
import com.iceteaviet.fastfoodfinder.utils.openStoreDetailActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFavouriteFragment : Fragment(), OnStartDragListener {

    private val viewModel: MainFavViewModel by viewModels()

    private lateinit var binding: FragmentMainFavouritedBinding

    lateinit var recyclerView: RecyclerView
    lateinit var containerLayout: FrameLayout
    lateinit var fabChangePosition: FloatingActionButton

    private var isFABChangeClicked = false
    private var mFavouriteAdapter: FavouriteStoreAdapter? = null
    private var mItemTouchHelper: ItemTouchHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainFavouritedBinding.inflate(inflater, container, false)
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
                    when (state) {
                        is MainFavEvent.Idle -> {}
                        is MainFavEvent.SetStores -> {
                            mFavouriteAdapter?.setStores(state.stores)
                            viewModel.markEventConsumed()
                        }
                        is MainFavEvent.AddStore -> {
                            mFavouriteAdapter?.addStore(state.store)
                            viewModel.markEventConsumed()
                        }
                        is MainFavEvent.UpdateStore -> {
                            mFavouriteAdapter?.updateStore(state.store)
                            viewModel.markEventConsumed()
                        }
                        is MainFavEvent.RemoveStore -> {
                            mFavouriteAdapter?.removeStore(state.store)
                            viewModel.markEventConsumed()
                        }
                        is MainFavEvent.ShowWarningMessage -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                            viewModel.markEventConsumed()
                        }
                        is MainFavEvent.ShowStoreDetailView -> {
                            openStoreDetailActivity(requireActivity(), state.store)
                            viewModel.markEventConsumed()
                        }
                        is MainFavEvent.ShowGeneralErrorMessage -> {
                            Toast.makeText(requireActivity(), R.string.error_general_error_code, Toast.LENGTH_LONG).show()
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    private fun setupUI() {
        recyclerView = binding.rvFavouriteStores
        containerLayout = binding.flContainer
        fabChangePosition = binding.fabChange

        mFavouriteAdapter = FavouriteStoreAdapter(this, containerLayout)

        val llm = LinearLayoutManager(context)
        recyclerView.layoutManager = llm
        recyclerView.adapter = mFavouriteAdapter
        val decoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)

        recyclerView.addItemDecoration(decoration)
        mFavouriteAdapter?.let {
            val callback = SimpleItemTouchHelperCallback(it)
            mItemTouchHelper = ItemTouchHelper(callback)
            mItemTouchHelper?.attachToRecyclerView(recyclerView)
        }
    }

    private fun setupEventHandlers() {
        mFavouriteAdapter?.setOnItemClickListener(object : FavouriteStoreAdapter.OnItemClickListener {
            override fun onClick(des: Store) {
                viewModel.onStoreItemClick(des)
            }
        })

        fabChangePosition.setOnClickListener {
            if (isFABChangeClicked) {
                isFABChangeClicked = false
                fabChangePosition.setImageResource(R.drawable.ic_main_swap)
            } else {
                isFABChangeClicked = true
                fabChangePosition.setImageResource(R.drawable.ic_main_swap_selected)
            }
        }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        if (isFABChangeClicked) {
            mItemTouchHelper?.startDrag(viewHolder)
        }
    }

    companion object {
        fun newInstance(): MainFavouriteFragment {
            val args = Bundle()
            val fragment = MainFavouriteFragment()
            fragment.arguments = args
            return fragment
        }
    }
}