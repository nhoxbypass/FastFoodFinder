package com.iceteaviet.fastfoodfinder.ui.store

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.routing.MapRoutingActivity
import com.iceteaviet.fastfoodfinder.ui.store.comment.CommentActivity
import com.iceteaviet.fastfoodfinder.ui.store.comment.CommentActivity.Companion.KEY_COMMENT
import com.iceteaviet.fastfoodfinder.utils.*
import kotlinx.android.synthetic.main.activity_store_detail.*

/**
 * Created by taq on 18/11/2016.
 */

class StoreDetailActivity : BaseActivity(), StoreDetailContract.View, GoogleApiClient.ConnectionCallbacks {
    override lateinit var presenter: StoreDetailContract.Presenter

    lateinit var collapsingToolbar: CollapsingToolbarLayout
    lateinit var ivBackdrop: ImageView
    lateinit var rvContent: RecyclerView

    private var mLocationRequest: LocationRequest? = null
    private var googleApiClient: GoogleApiClient? = null
    private var adapter: StoreDetailAdapter? = null

    override val layoutId: Int
        get() = R.layout.activity_store_detail

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = StoreDetailPresenter(App.getDataManager(), this)

        setupUI()
        setupEventHandlers()
        setupLocationServices()
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun onStart() {
        super.onStart()
        googleApiClient!!.connect()
    }

    override fun onStop() {
        super.onStop()
        googleApiClient!!.disconnect()
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
                    requestLocationUpdates()
                    getLastLocation()
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
        startActivity(newCallIntent(tel))
    }

    override fun showInvalidPhoneNumbWarning() {
        Toast.makeText(this@StoreDetailActivity, R.string.store_no_phone_numb, Toast.LENGTH_SHORT).show()
    }

    override fun showMapRoutingView(currStore: Store, mapsDirection: MapsDirection) {
        val intent = Intent(this@StoreDetailActivity, MapRoutingActivity::class.java)
        val extras = Bundle()
        extras.putParcelable(MapRoutingActivity.KEY_ROUTE_LIST, mapsDirection)
        extras.putParcelable(MapRoutingActivity.KEY_DES_STORE, currStore)
        intent.putExtras(extras)
        startActivity(intent)
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

    private fun setupLocationServices() {
        mLocationRequest = createLocationRequest()
        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener { e(TAG, getString(R.string.cannot_get_curr_location)) }
                .addApi(LocationServices.API)
                .build()
    }

    override fun onConnected(@Nullable bundle: Bundle?) {
        if (isLocationPermissionGranted(this)) {
            requestLocationUpdates()
            getLastLocation()
        } else {
            requestLocationPermission(this)
        }
    }

    override fun onConnectionSuspended(i: Int) {
        Toast.makeText(this, R.string.cannot_connect_location_service, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest) { location ->
            presenter.onCurrLocationChanged(location.latitude, location.longitude)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        if (lastLocation != null) {
            presenter.onCurrLocationChanged(lastLocation.latitude, lastLocation.longitude)
        } else
            Toast.makeText(this@StoreDetailActivity, R.string.cannot_get_curr_location, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val KEY_STORE = "key_store"
        const val RC_ADD_COMMENT = 113
        private val TAG = StoreDetailActivity::class.java.simpleName

        fun getIntent(context: Context, store: Store): Intent {
            val intent = Intent(context, StoreDetailActivity::class.java)
            val args = Bundle()
            args.putParcelable(KEY_STORE, store)
            intent.putExtras(args)
            return intent
        }
    }
}
