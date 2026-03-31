package com.iceteaviet.fastfoodfinder.ui.storelist

import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.databinding.ActivityStoreListBinding
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity

/**
 * Created by MyPC on 11/30/2016.
 */
@AndroidEntryPoint
class StoreListActivity : BaseActivity(), StoreListContract.View {
    @Inject
    lateinit var storeRepository: com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository

    @Inject
    lateinit var userRepository: com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository

    @Inject
    lateinit var clientAuth: com.iceteaviet.fastfoodfinder.data.auth.ClientAuth

    @Inject
    lateinit var schedulerProvider: com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider


    override lateinit var presenter: StoreListContract.Presenter

    /**
     * Views Ref
     */
    private lateinit var binding: ActivityStoreListBinding

    lateinit var recyclerView: RecyclerView

    override val layoutId: Int
        get() = R.layout.activity_store_list

    private var adapter: StoreListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = StoreListPresenter(clientAuth, userRepository, storeRepository, schedulerProvider, this)

        setupUI()
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
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun setStores(stores: List<Store>) {
        adapter?.setStores(stores)
    }
}