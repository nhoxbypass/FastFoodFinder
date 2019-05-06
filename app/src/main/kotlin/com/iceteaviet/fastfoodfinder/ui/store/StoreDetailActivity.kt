package com.iceteaviet.fastfoodfinder.ui.store

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.location.LocationListener
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.store.comment.CommentActivity
import com.iceteaviet.fastfoodfinder.ui.store.comment.CommentActivity.Companion.KEY_COMMENT
import com.iceteaviet.fastfoodfinder.utils.*
import kotlinx.android.synthetic.main.activity_store_detail.*

/**
 * Created by taq on 18/11/2016.
 */

class StoreDetailActivity : BaseActivity(), StoreDetailContract.View, LocationListener {
    override lateinit var presenter: StoreDetailContract.Presenter

    lateinit var collapsingToolbar: CollapsingToolbarLayout
    lateinit var ivBackdrop: ImageView
    lateinit var rvContent: RecyclerView

    private var adapter: StoreDetailAdapter? = null

    override val layoutId: Int
        get() = R.layout.activity_store_detail

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = StoreDetailPresenter(App.getDataManager(), this)
        presenter.handleExtras(intent)

        setupUI()
        setupEventHandlers()
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
        if (isLocationPermissionGranted(this)) {
            presenter.onLocationPermissionGranted()
        } else {
            requestLocationPermission(this)
        }
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == RC_ADD_COMMENT && data != null) {
            val comment = data.getParcelableExtra(KEY_COMMENT) as Comment?
            presenter.onAddNewComment(comment)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.onLocationPermissionGranted()
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {
            }
        }
    }

    override fun setToolbarTitle(title: String) {
        collapsingToolbar.title = title
    }

    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates() {
        GoogleLocationManager.getInstance().subscribeLocationUpdate(this)
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocation() {
        val lastLocation = GoogleLocationManager.getInstance().getCurrentLocation()
        if (lastLocation != null) {
            presenter.onCurrLocationChanged(lastLocation.latitude, lastLocation.longitude)
        } else
            Toast.makeText(this@StoreDetailActivity, R.string.cannot_get_curr_location, Toast.LENGTH_SHORT).show()
    }

    override fun setStoreComments(listComments: MutableList<Comment>) {
        adapter!!.setComments(listComments)
    }

    override fun addStoreComment(comment: Comment) {
        adapter!!.addComment(comment)
    }

    override fun setAppBarExpanded(expanded: Boolean) {
        appbar!!.setExpanded(expanded)
    }

    override fun scrollToCommentList() {
        rvContent.scrollToPosition(3)
    }

    override fun showCommentEditorView() {
        startActivityForResult(Intent(this@StoreDetailActivity, CommentActivity::class.java), RC_ADD_COMMENT)
    }

    override fun startCallIntent(tel: String) {
        makeNativeCall(this, tel)
    }

    override fun showInvalidPhoneNumbWarning() {
        Toast.makeText(this@StoreDetailActivity, R.string.store_no_phone_numb, Toast.LENGTH_SHORT).show()
    }

    override fun showMapRoutingView(currStore: Store, mapsDirection: MapsDirection) {
        openRoutingActivity(this, currStore, mapsDirection)
    }

    override fun onLocationChanged(location: Location) {
        presenter.onCurrLocationChanged(location.latitude, location.longitude)
    }

    override fun onLocationFailed(type: Int) {

    }

    override fun exit() {
        finish()
    }

    private fun setupUI() {
        collapsingToolbar = collapsing_toolbar
        ivBackdrop = backdrop
        rvContent = content

        adapter = StoreDetailAdapter()
        rvContent.adapter = adapter
        rvContent.layoutManager = LinearLayoutManager(this)

        setSupportActionBar(toolbar)
        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        Glide.with(this)
                .load(R.drawable.detail_sample_circlekcover)
                .apply(RequestOptions().centerCrop())
                .into(ivBackdrop)
    }

    private fun setupEventHandlers() {
        adapter!!.setListener(object : StoreDetailAdapter.StoreActionListener {
            override fun onCommentButtonClick() {
                presenter.onCommentButtonClick()
            }

            override fun onCallButtonClick(tel: String?) {
                presenter.onCallButtonClick(tel)
            }

            override fun onNavigationButtonClick() {
                presenter.onNavigationButtonClick()
            }

            override fun onAddToFavButtonClick(storeId: Int) {
                //TODO gọi hàm lưu vào danh sách yêu thích
                Toast.makeText(this@StoreDetailActivity, R.string.fav_stores_added, Toast.LENGTH_SHORT).show()
            }

            override fun onSaveButtonClick(storeId: Int) {
                //TODO gọi hàm save
            }

        })
    }

    companion object {
        const val KEY_STORE = "key_store"
        const val RC_ADD_COMMENT = 113
        private val TAG = StoreDetailActivity::class.java.simpleName
    }
}
