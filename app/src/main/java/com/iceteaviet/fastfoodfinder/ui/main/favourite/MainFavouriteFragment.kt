package com.iceteaviet.fastfoodfinder.ui.main.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.databinding.FragmentMainFavouritedBinding
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.OnStartDragListener
import com.iceteaviet.fastfoodfinder.ui.custom.itemtouchhelper.SimpleItemTouchHelperCallback
import com.iceteaviet.fastfoodfinder.utils.openStoreDetailActivity

/**
 * Created by MyPC on 11/16/2016.
 */
class MainFavouriteFragment : Fragment(), MainFavContract.View, OnStartDragListener {
    override lateinit var presenter: MainFavContract.Presenter

    /**
     * Views Ref
     */
    private lateinit var binding: FragmentMainFavouritedBinding

    lateinit var recyclerView: RecyclerView
    lateinit var containerLayout: FrameLayout
    lateinit var fabChangePosition: FloatingActionButton

    private var isFABChangeClicked = false
    private var mFavouriteAdapter: FavouriteStoreAdapter? = null
    private var mItemTouchHelper: ItemTouchHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main_favourited, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun setStores(storeList: List<Store>) {
        mFavouriteAdapter?.setStores(storeList)
    }

    override fun addStore(store: Store) {
        mFavouriteAdapter?.addStore(store)
    }

    override fun updateStore(store: Store) {
        mFavouriteAdapter?.updateStore(store)
    }

    override fun removeStore(store: Store) {
        mFavouriteAdapter?.removeStore(store)
    }

    override fun showWarningMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showStoreDetailView(store: Store) {
        openStoreDetailActivity(requireActivity(), store)
    }

    override fun showGeneralErrorMessage() {
        Toast.makeText(requireActivity(), R.string.error_general_error_code, Toast.LENGTH_LONG).show()
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
                presenter.onStoreItemClick(des)
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
        if (isFABChangeClicked)
            mItemTouchHelper?.startDrag(viewHolder)

    }

    companion object {

        fun newInstance(): MainFavouriteFragment {
            val args = Bundle()
            val fragment = MainFavouriteFragment()
            fragment.arguments = args
            fragment.presenter = MainFavPresenter(App.getDataManager(), App.getSchedulerProvider(), fragment)
            return fragment
        }
    }
}