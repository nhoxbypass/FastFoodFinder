package com.iceteaviet.fastfoodfinder.ui.storelist

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.databinding.ActivityStoreListBinding
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoreListActivity : BaseActivity() {

    private val viewModel: StoreListViewModel by viewModels()

    private lateinit var binding: ActivityStoreListBinding
    lateinit var recyclerView: RecyclerView
    private var adapter: StoreListAdapter? = null

    override val layoutId: Int
        get() = R.layout.activity_store_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    adapter?.setStores(state.stores)
                }
            }
        }
    }

    private fun setupUI() {
        recyclerView = binding.rvTopList

        adapter = StoreListAdapter()
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        val decoration = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }
}