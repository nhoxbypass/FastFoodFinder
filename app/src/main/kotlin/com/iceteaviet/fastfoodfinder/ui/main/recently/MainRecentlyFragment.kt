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
class MainRecentlyFragment : Fragment(), OnStartDragListener {
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

        setupRecyclerView(recyclerView)
        loadData()
    }

    private fun setupRecyclerView(rv: RecyclerView) {
        mRecentlyAdapter = RecentlyStoreAdapter(this, containerLayout)

        mRecentlyAdapter!!.setOnItemClickListener(object : RecentlyStoreAdapter.OnItemClickListener {
            override fun onClick(store: Store) {
                activity?.startActivity(StoreDetailActivity.getIntent(context!!, store))
            }
        })

        val mLayoutManager = LinearLayoutManager(context)
        rv.layoutManager = mLayoutManager
        rv.adapter = mRecentlyAdapter
        val decoration = DividerItemDecoration(rv.context, DividerItemDecoration.VERTICAL)

        rv.addItemDecoration(decoration)
        mRecentlyAdapter?.let {
            val callback = SimpleItemTouchHelperCallback(it)
            mItemTouchHelper = ItemTouchHelper(callback)
            mItemTouchHelper!!.attachToRecyclerView(rv)
        }
    }


    private fun loadData() {
        val stores = ArrayList<Store>()
        //TODO: Load recently store from Realm
        mRecentlyAdapter!!.setStores(stores)
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
            return fragment
        }
    }
}
