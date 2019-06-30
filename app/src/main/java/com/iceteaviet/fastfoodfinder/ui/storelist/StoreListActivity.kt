package com.iceteaviet.fastfoodfinder.ui.storelist

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_store_list.*

/**
 * Created by MyPC on 11/30/2016.
 */
class StoreListActivity : BaseActivity(), StoreListContract.View {
    override lateinit var presenter: StoreListContract.Presenter

    lateinit var recyclerView: RecyclerView

    override val layoutId: Int
        get() = R.layout.activity_store_list

    private var adapter: StoreListAdapter? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = StoreListPresenter(App.getDataManager(), App.getSchedulerProvider(), this)

        setupUI()
    }

    private fun setupUI() {
        recyclerView = rv_top_list

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
