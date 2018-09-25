package com.iceteaviet.fastfoodfinder.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.ui.storelist.StoreListAdapter
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_list_detail.*

/**
 * Created by MyPC on 12/6/2016.
 */
class ListDetailActivity : AppCompatActivity() {

    lateinit var rvStoreList: RecyclerView
    lateinit var cvIconList: CircleImageView

    private var mAdapter: StoreListAdapter? = null

    private var dataManager: DataManager? = null
    private var userStoreList: UserStoreList? = null
    private var photoUrl: String? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_detail)

        rvStoreList = rvList
        cvIconList = iconList


        dataManager = App.getDataManager()

        userStoreList = loadData(intent)
        setupUI()
    }

    private fun setupUI() {
        mAdapter = StoreListAdapter()
        val layoutManager = LinearLayoutManager(applicationContext)
        rvStoreList.adapter = mAdapter
        rvStoreList.layoutManager = layoutManager
        val decoration = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        rvStoreList.addItemDecoration(decoration)

        tvListName!!.text = userStoreList!!.listName
        cvIconList.setImageResource(userStoreList!!.iconId)
        Glide.with(applicationContext)
                .load(photoUrl)
                .into(cvAvatar!!)
    }

    private fun loadData(intent: Intent): UserStoreList {
        val userStoreList = intent.getParcelableExtra<UserStoreList>(KEY_USER_STORE_LIST)
        photoUrl = intent.getStringExtra(KEY_USER_PHOTO_URL)

        //add list store to mAdapter here
        dataManager!!.getLocalStoreDataSource().findStoresByIds(userStoreList.getStoreIdList()!!)
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(storeList: List<Store>) {
                        mAdapter!!.setStores(storeList)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })

        return userStoreList
    }

    companion object {
        const val KEY_USER_STORE_LIST = "store"
        const val KEY_USER_PHOTO_URL = "url"
    }
}
