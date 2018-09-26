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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.routing.MapRoutingActivity
import com.iceteaviet.fastfoodfinder.ui.routing.MapRoutingActivity.Companion.KEY_DES_STORE
import com.iceteaviet.fastfoodfinder.ui.routing.MapRoutingActivity.Companion.KEY_ROUTE_LIST
import com.iceteaviet.fastfoodfinder.ui.store.CommentActivity.Companion.KEY_COMMENT
import com.iceteaviet.fastfoodfinder.utils.*
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_store_detail.*
import java.util.*

/**
 * Created by taq on 18/11/2016.
 */

class StoreDetailActivity : AppCompatActivity(), StoreDetailAdapter.StoreActionListener, GoogleApiClient.ConnectionCallbacks {

    lateinit var collapsingToolbar: CollapsingToolbarLayout
    lateinit var ivBackdrop: ImageView
    lateinit var rvContent: RecyclerView

    private var currLocation: LatLng? = null
    private var mLocationRequest: LocationRequest? = null
    private var currentStore: Store? = null
    private var googleApiClient: GoogleApiClient? = null
    //private SupportMapFragment mMapFragment;
    //private GoogleMap mGoogleMap;
    private var mStoreDetailAdapter: StoreDetailAdapter? = null
    private var dataManager: DataManager? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_detail)

        collapsingToolbar = collapsing_toolbar
        ivBackdrop = backdrop
        rvContent = content

        currentStore = intent.getParcelableExtra(KEY_STORE)
        currentStore?.let {
            mStoreDetailAdapter = StoreDetailAdapter(it)
            mStoreDetailAdapter!!.setListener(this)
            rvContent.adapter = mStoreDetailAdapter
        }
        rvContent.layoutManager = LinearLayoutManager(this)

        setSupportActionBar(toolbar)
        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        collapsingToolbar.title = currentStore!!.title

        Glide.with(this)
                .load(R.drawable.detail_sample_circlekcover)
                .apply(RequestOptions().centerCrop())
                .into(ivBackdrop)

        dataManager = App.getDataManager()
        mLocationRequest = createLocationRequest()
        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener { e(TAG, getString(R.string.cannot_get_curr_location)) }
                .addApi(LocationServices.API)
                .build()
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
        if (resultCode == RESULT_OK && requestCode == REQUEST_COMMENT && data != null) {
            val comment = data.getSerializableExtra(KEY_COMMENT) as Comment
            if (comment != null) {
                mStoreDetailAdapter!!.addComment(comment)
                appbar!!.setExpanded(false)
                rvContent.scrollToPosition(3)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {
            }
        }
    }

    override fun onShowComment() {
        startActivityForResult(Intent(this, CommentActivity::class.java), REQUEST_COMMENT)
    }

    override fun onCall(tel: String?) {
        if (!isEmpty(tel)) {
            startActivity(getCallIntent(tel!!))
        } else {
            Toast.makeText(this, R.string.store_no_phone_numb, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDirect() {
        val storeLocation = currentStore!!.getPosition()
        val queries = HashMap<String, String>()

        val origin: String? = getLatLngString(currLocation)
        val destination: String? = getLatLngString(storeLocation)

        if (origin == null || destination == null)
            return

        queries[GoogleMapsRoutingApiHelper.PARAM_ORIGIN] = origin
        queries[GoogleMapsRoutingApiHelper.PARAM_DESTINATION] = destination

        dataManager!!.getMapsRoutingApiHelper().getMapsDirection(queries, currentStore!!)
                .subscribe(object : SingleObserver<MapsDirection> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(mapsDirection: MapsDirection) {
                        val intent = Intent(this@StoreDetailActivity, MapRoutingActivity::class.java)
                        val extras = Bundle()
                        extras.putParcelable(KEY_ROUTE_LIST, mapsDirection)
                        extras.putParcelable(KEY_DES_STORE, currentStore)
                        intent.putExtras(extras)
                        startActivity(intent)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    override fun onAddToFavorite(storeId: Int) {
        //TODO gọi hàm lưu vào danh sách yêu thích
        Toast.makeText(this, R.string.fav_stores_added, Toast.LENGTH_SHORT).show()
    }

    override fun onCheckIn(storeId: Int) {
        //TODO gọi hàm check in
    }

    override fun onConnected(@Nullable bundle: Bundle?) {
        if (isLocationPermissionGranted(this)) {
            getCurrentLocation()
        } else {
            requestLocationPermission(this)
        }
    }

    override fun onConnectionSuspended(i: Int) {
        Toast.makeText(this, R.string.cannot_connect_location_service, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest) { location ->
            currLocation = LatLng(location.latitude, location.longitude)
            // Creating a LatLng object for the current location
        }

        val lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        if (lastLocation != null) {
            currLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
        } else
            Toast.makeText(this@StoreDetailActivity, R.string.cannot_get_curr_location, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val KEY_STORE = "key_store"
        const val REQUEST_COMMENT = 113
        private val TAG = StoreDetailActivity::class.java.simpleName

        fun getIntent(context: Context, store: Store): Intent {
            val intent = Intent(context, StoreDetailActivity::class.java)
            intent.putExtra(KEY_STORE, store)
            return intent
        }
    }
}
