package com.iceteaviet.fastfoodfinder.ui.storelist

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_list_detail.*

/**
 * Created by MyPC on 12/6/2016.
 */
class ListDetailActivity : BaseActivity(), ListDetailContract.View {
    override lateinit var presenter: ListDetailContract.Presenter

    lateinit var rvStoreList: RecyclerView
    lateinit var cvIconList: CircleImageView

    private var mAdapter: StoreListAdapter? = null

    override val layoutId: Int
        get() = R.layout.activity_list_detail

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = ListDetailPresenter(App.getDataManager(), App.getSchedulerProvider(), this)

        if (intent != null) {
            presenter.handleExtras(intent.getParcelableExtra(KEY_USER_STORE_LIST), intent.getStringExtra(KEY_USER_PHOTO_URL))
            setupUI()
        } else {
            exit()
        }
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
        mAdapter?.setStores(storeList)
    }

    override fun setListNameText(listName: String) {
        tvListName.text = listName
    }

    override fun loadStoreIcon(@DrawableRes storeIcon: Int) {
        Glide.with(applicationContext)
                .load(storeIcon)
                .into(cvIconList)
    }

    override fun exit() {
        finish()
    }

    override fun showGeneralErrorMessage() {
        Toast.makeText(this, R.string.error_general_error_code, Toast.LENGTH_LONG).show()
    }

    private fun setupUI() {
        rvStoreList = rvList
        cvIconList = iconList

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
