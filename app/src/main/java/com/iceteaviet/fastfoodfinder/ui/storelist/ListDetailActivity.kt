package com.iceteaviet.fastfoodfinder.ui.storelist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import com.bumptech.glide.Glide
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.databinding.ActivityListDetailBinding
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListDetailActivity : BaseActivity() {

    private val viewModel: ListDetailViewModel by viewModels()

    private lateinit var binding: ActivityListDetailBinding
    lateinit var rvStoreList: RecyclerView
    lateinit var cvIconList: CircleImageView
    private var mAdapter: StoreListAdapter? = null

    override val layoutId: Int
        get() = R.layout.activity_list_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent != null) {
            viewModel.handleExtras(
                intent.getParcelableExtra<UserStoreList>(KEY_USER_STORE_LIST), 
                intent.getStringExtra(KEY_USER_PHOTO_URL)
            )
            setupUI()
            setupObservers()
        } else {
            finish()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    binding.tvListName.text = state.listName
                    
                    if (state.storeIconResId != 0) {
                        Glide.with(applicationContext)
                            .load(state.storeIconResId)
                            .into(cvIconList)
                    }

                    mAdapter?.setStores(state.stores)

                    when (state.event) {
                        is ListDetailEvent.Idle -> {}
                        is ListDetailEvent.Exit -> {
                            finish()
                            viewModel.markEventConsumed()
                        }
                        is ListDetailEvent.ShowGeneralErrorMessage -> {
                            Toast.makeText(this@ListDetailActivity, R.string.error_general_error_code, Toast.LENGTH_LONG).show()
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }

    private fun setupUI() {
        rvStoreList = binding.rvList
        cvIconList = binding.iconList

        mAdapter = StoreListAdapter()
        val layoutManager = LinearLayoutManager(applicationContext)
        rvStoreList.adapter = mAdapter
        rvStoreList.layoutManager = layoutManager
        val decoration = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        rvStoreList.addItemDecoration(decoration)
    }

    companion object {
        const val KEY_USER_STORE_LIST = "store"
        const val KEY_USER_PHOTO_URL = "url"
    }
}