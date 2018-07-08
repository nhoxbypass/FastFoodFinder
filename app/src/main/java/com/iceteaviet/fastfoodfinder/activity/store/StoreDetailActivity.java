package com.iceteaviet.fastfoodfinder.activity.store;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.adapter.StoreDetailAdapter;
import com.iceteaviet.fastfoodfinder.model.Comment;
import com.iceteaviet.fastfoodfinder.model.store.Store;
import com.iceteaviet.fastfoodfinder.network.RestClient;
import com.iceteaviet.fastfoodfinder.utils.Constant;
import com.iceteaviet.fastfoodfinder.utils.DisplayUtils;
import com.iceteaviet.fastfoodfinder.utils.LocationUtils;
import com.iceteaviet.fastfoodfinder.utils.PermissionUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.iceteaviet.fastfoodfinder.R.id.map;

/**
 * Created by taq on 18/11/2016.
 */

public class StoreDetailActivity extends AppCompatActivity implements StoreDetailAdapter.StoreActionListener, GoogleApiClient.ConnectionCallbacks {

    public static final int REQUEST_COMMENT = 113;
    LatLng currLocation;
    LocationRequest mLocationRequest;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.backdrop)
    ImageView ivBackdrop;
    @BindView(R.id.content)
    RecyclerView rvContent;
    private Store currentStore;
    private GoogleApiClient googleApiClient;
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private StoreDetailAdapter mStoreDetailAdapter;

    public static Intent getIntent(Context context, Store store) {
        Intent intent = new Intent(context, StoreDetailActivity.class);
        intent.putExtra(Constant.STORE, store);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        ButterKnife.bind(this);

        currentStore = getIntent().getParcelableExtra(Constant.STORE);
        mStoreDetailAdapter = new StoreDetailAdapter(currentStore);
        mStoreDetailAdapter.setListener(this);
        rvContent.setAdapter(mStoreDetailAdapter);
        rvContent.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar.setTitle(currentStore.getTitle());

        Glide.with(this)
                .load(R.drawable.detail_sample_circlekcover)
                .apply(new RequestOptions().centerCrop())
                .into(ivBackdrop);

        mLocationRequest = LocationUtils.createLocationRequest();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("MAPP", "Failed to get current location");
                    }
                })
                .addApi(LocationServices.API)
                .build();
    }

    private void setUpMapIfNeeded() {
        if (mMapFragment == null) {
            mMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(map));
            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    mGoogleMap = map;
                    Marker m = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(40, -4)));
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_COMMENT) {
            Comment comment = (Comment) data.getSerializableExtra(Constant.COMMENT);
            if (comment != null) {
                mStoreDetailAdapter.addComment(comment);
                appbar.setExpanded(false);
                rvContent.scrollToPosition(3);
            }
        }
    }

    @Override
    public void onShowComment() {
        startActivityForResult(CommentActivity.getIntent(this), REQUEST_COMMENT);
    }

    @Override
    public void onCall(String tel) {
        if (tel != null && !tel.equals("")) {
            startActivity(DisplayUtils.getCallIntent(tel));
        } else {
            Toast.makeText(this, "The ic_store doesn't have number phone!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDirect() {
        LatLng storeLocation = currentStore.getPosition();
        Map<String, String> queries = new HashMap<String, String>();

        queries.put("origin", LocationUtils.getLatLngString(currLocation));
        queries.put("destination", LocationUtils.getLatLngString(storeLocation));
        RestClient.getInstance().showDirection(StoreDetailActivity.this, queries, currentStore);
    }

    @Override
    public void onAddToFavorite(int storeId) {
        //TODO gọi hàm lưu vào danh sách yêu thích
        Toast.makeText(this, "add to favorite " + storeId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckIn(int storeId) {
        //TODO gọi hàm check in
        Toast.makeText(this, "check in " + storeId, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (PermissionUtils.isLocationPermissionGranted(this)) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    // Creating a LatLng object for the current location
                }
            });

            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                currLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            } else
                Toast.makeText(StoreDetailActivity.this, "Cannot get current location!", Toast.LENGTH_SHORT).show();
        } else {
            PermissionUtils.requestLocationPermission(this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Cannot connect to Location service", Toast.LENGTH_SHORT).show();
    }
}
