package com.iceteaviet.fastfoodfinder.ui.storelist

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import kotlinx.android.synthetic.main.activity_store_list.*

/**
 * Created by MyPC on 11/30/2016.
 */
class StoreListActivity : BaseActivity() {
    lateinit var recyclerView: RecyclerView

    private var adapter: StoreListAdapter? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView = rv_top_list

        adapter = StoreListAdapter()
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        val decoration = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)
        loadData()
    }

    override val layoutId: Int
        get() = R.layout.activity_store_list


    private fun loadData() {
        adapter!!.setStores(getFakeStoreList())
    }
}
