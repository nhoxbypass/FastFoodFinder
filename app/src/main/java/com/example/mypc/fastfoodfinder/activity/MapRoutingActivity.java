package com.example.mypc.fastfoodfinder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.adapter.RoutingAdapter;
import com.example.mypc.fastfoodfinder.model.Routing.MapsDirection;
import com.example.mypc.fastfoodfinder.model.Routing.Step;
import com.example.mypc.fastfoodfinder.model.Store.Store;
import com.example.mypc.fastfoodfinder.ui.main.MainFragment;
import com.example.mypc.fastfoodfinder.utils.DisplayUtils;
import com.example.mypc.fastfoodfinder.utils.MapUtils;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapRoutingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_routing_time)
    TextView travelTime;
    @BindView(R.id.tv_routing_distance)
    TextView travelDistance;
    @BindView(R.id.tv_routing_overview)
    TextView travelOverview;
    @BindView(R.id.rv_bottom_sheet)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_bottom_sheet) LinearLayout mBottomSheetContainer;


    BottomSheetBehavior mBottomSheetBehavior;

    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;

    MapsDirection mMapsDirection;
    Polyline currDirection;
    LatLng currLocation;
    Store mCurrStore;
    RoutingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_routing);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);



        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetContainer);

        Bundle extras = getIntent().getExtras();
        mMapsDirection = extras.getParcelable(MainFragment.KEY_ROUTE_LIST);
        mCurrStore = (Store) extras.getSerializable(MainFragment.KEY_DES_STORE);
        
        if (mMapsDirection == null || mCurrStore == null || mMapsDirection.getRouteList().size() <= 0) {
            Toast.makeText(MapRoutingActivity.this, "Failed to open Routing screen!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(mCurrStore.getTitle());
        }

        mAdapter = new RoutingAdapter(mMapsDirection.getRouteList().get(0).getLegList().get(0).getStepList());
        LinearLayoutManager layoutManager = new LinearLayoutManager(MapRoutingActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        currLocation = mMapsDirection.getRouteList().get(0).getLegList().get(0).getStepList().get(0).getStartMapCoordination().getLocation();


        setUpMapIfNeeded();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMapFragment == null) {
            mMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            // Check if we were successful in obtaining the map.
            if (mMapFragment != null) {
                mMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map);
                    }
                });
            }
        }
    }

    // The Map is verified. It is now safe to manipulate the map.
    protected void loadMap(GoogleMap googleMap) {
        if (googleMap != null) {
            // ... use map here
            mGoogleMap = googleMap;

            mAdapter.setOnNavigationItemClickListener(new RoutingAdapter.OnNavigationItemClickListener() {
                @Override
                public void onClick(LatLng latLng) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation,16));

            Marker currMarker = googleMap.addMarker(new MarkerOptions().position(currLocation)
                    .title("Your location")
                    .snippet("Your current location, please follow the line")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));



            drawPolylines(mMapsDirection.getRouteList().get(0).getLegList().get(0).getStepList(), mGoogleMap);


            Marker storeMarker = googleMap.addMarker(new MarkerOptions().position(mCurrStore.getPosition())
                    .title(mCurrStore.getTitle())
                    .snippet(mCurrStore.getAddress())
                    .icon(BitmapDescriptorFactory.fromResource(MapUtils.getLogoDrawableId(mCurrStore.getType()))));





            travelTime.setText(mMapsDirection.getRouteList().get(0).getLegList().get(0).getDuration());
            travelDistance.setText(mMapsDirection.getRouteList().get(0).getLegList().get(0).getDistance());
            travelOverview.setText("Via " + mMapsDirection.getRouteList().get(0).getSummary());
        }
    }

    void drawPolylines(List<Step> steps, GoogleMap googleMap) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(currLocation);

        PolylineOptions options = new PolylineOptions()
                .clickable(true)
                .color(ContextCompat.getColor(MapRoutingActivity.this, R.color.googleBlue))
                .width(12)
                .geodesic(true)
                .zIndex(5f);

        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            options.add(step.getStartMapCoordination().getLocation());
            options.add(step.getEndMapCoordination().getLocation());
            builder.include(step.getEndMapCoordination().getLocation());
        }

        if (currDirection != null)
            currDirection.remove();
        currDirection = googleMap.addPolyline(options);


        LatLngBounds bounds = builder.build();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels - DisplayUtils.convertDpToPx(displayMetrics, 160);
        int padding = 24; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width, height ,padding);

        googleMap.animateCamera(cu);
    }
}
