package com.iceteaviet.fastfoodfinder.ui.main.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreEvent
import com.iceteaviet.fastfoodfinder.ui.main.OnStartDragListener
import com.iceteaviet.fastfoodfinder.ui.main.SimpleItemTouchHelperCallback
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailActivity
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_main_favourited.*

/**
 * Created by MyPC on 11/16/2016.
 */
class MainFavouriteFragment : Fragment(), OnStartDragListener {
    lateinit var recyclerView: RecyclerView
    lateinit var containerLayout: FrameLayout
    lateinit var fabChangePosition: FloatingActionButton

    private var isFABChangeClicked = false
    private var mFavouriteAdapter: FavouriteStoreAdapter? = null
    private var mItemTouchHelper: ItemTouchHelper? = null
    private lateinit var dataManager: DataManager

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataManager = App.getDataManager()
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main_favourited, container, false)
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        recyclerView = rv_favourite_stores
        containerLayout = fl_container
        fabChangePosition = fab_change

        setupRecyclerView(recyclerView)
        //client = TwitterApplication.getRestClient();
        fabChangePosition.setOnClickListener {
            if (isFABChangeClicked) {
                isFABChangeClicked = false
                fabChangePosition.setImageResource(R.drawable.ic_main_swap)
            } else {
                isFABChangeClicked = true
                fabChangePosition.setImageResource(R.drawable.ic_main_swap_selected)
            }
        }

        loadData()
    }

    private fun setupRecyclerView(rv: RecyclerView) {
        mFavouriteAdapter = FavouriteStoreAdapter(this, containerLayout)

        mFavouriteAdapter!!.setOnItemClickListener(object : FavouriteStoreAdapter.OnItemClickListener {
            override fun onClick(des: Store) {
                activity?.startActivity(StoreDetailActivity.getIntent(context!!, des))
            }
        })

        val mLayoutManager = LinearLayoutManager(context)
        rv.layoutManager = mLayoutManager
        rv.adapter = mFavouriteAdapter
        val decoration = DividerItemDecoration(rv.context, DividerItemDecoration.VERTICAL)

        rv.addItemDecoration(decoration)
        mFavouriteAdapter?.let {
            val callback = SimpleItemTouchHelperCallback(it)
            mItemTouchHelper = ItemTouchHelper(callback)
            mItemTouchHelper!!.attachToRecyclerView(rv)
        }
    }


    private fun loadData() {
        val currUser = dataManager.getCurrentUser()
        if (currUser != null) {
            dataManager.getLocalStoreDataSource()
                    .findStoresByIds(currUser.favouriteStoreList.getStoreIdList()!!)
                    .subscribe(object : SingleObserver<List<Store>> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onSuccess(storeList: List<Store>) {
                            mFavouriteAdapter!!.setStores(storeList)
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }
                    })


            dataManager.getRemoteUserDataSource().subscribeFavouriteStoresOfUser(dataManager.getCurrentUserUid())
                    .map { storeIdPair ->
                        val store = dataManager.getLocalStoreDataSource().findStoresById(storeIdPair.first).blockingGet()[0]
                        UserStoreEvent(store, storeIdPair.second)
                    }
                    .subscribe(object : Observer<UserStoreEvent> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(userStoreEvent: UserStoreEvent) {
                            val store = userStoreEvent.store
                            when (userStoreEvent.eventActionCode) {
                                UserStoreEvent.ACTION_ADDED -> if (!dataManager.getCurrentUser()!!.favouriteStoreList.getStoreIdList()!!.contains(store.id)) {
                                    mFavouriteAdapter!!.addStore(store)
                                    dataManager.getCurrentUser()!!.favouriteStoreList.getStoreIdList()!!.add(store.id)
                                }

                                UserStoreEvent.ACTION_CHANGED -> if (dataManager.getCurrentUser()!!.favouriteStoreList.getStoreIdList()!!.contains(store.id)) {
                                    mFavouriteAdapter!!.updateStore(store)
                                }

                                UserStoreEvent.ACTION_REMOVED -> if (dataManager.getCurrentUser()!!.favouriteStoreList.getStoreIdList()!!.contains(store.id)) {
                                    mFavouriteAdapter!!.removeStore(store)
                                    dataManager.getCurrentUser()!!.favouriteStoreList.removeStore(store.id)
                                }

                                UserStoreEvent.ACTION_MOVED -> {
                                }

                                else -> {
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }

                        override fun onComplete() {

                        }
                    })
        }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        if (isFABChangeClicked)
            mItemTouchHelper!!.startDrag(viewHolder)

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
