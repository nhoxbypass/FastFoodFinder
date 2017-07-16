package com.iceteaviet.fastfoodfinder.activity;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.adapter.RoutingAdapter;
import com.iceteaviet.fastfoodfinder.helper.DividerItemDecoration;
import com.iceteaviet.fastfoodfinder.model.Routing.MapsDirection;
import com.iceteaviet.fastfoodfinder.model.Routing.Step;
import com.iceteaviet.fastfoodfinder.model.Store.Store;
import com.iceteaviet.fastfoodfinder.utils.DisplayUtils;
import com.iceteaviet.fastfoodfinder.utils.Keys;
import com.iceteaviet.fastfoodfinder.utils.MapUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapRoutingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_routing_time)
    TextView txtTravelTime;
    @BindView(R.id.tv_routing_distance)
    TextView txtTravelDistance;
    @BindView(R.id.tv_routing_overview)
    TextView txtTravelOverview;
    @BindView(R.id.rv_bottom_sheet)
    RecyclerView bottomRecyclerView;
    @BindView(R.id.rv_direction_instruction)
    RecyclerView topRecyclerView;
    @BindView(R.id.ll_bottom_sheet)
    LinearLayout mBottomSheetContainer;
    @BindView(R.id.btn_prev_instruction)
    ImageButton prevInstruction;
    @BindView(R.id.btn_next_instruction)
    ImageButton nextInstruction;
    @BindView(R.id.ll_routing_button_container)
    LinearLayout routingButtonContainer;
    RoutingAdapter.OnNavigationItemClickListener mListener;
    DividerItemDecoration divider;
    boolean isPreviewMode = false;
    private BottomSheetBehavior mBottomSheetBehavior;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private MapsDirection mMapsDirection;
    private Polyline mCurrDirection;
    private LatLng mCurrLocation;
    private Store mCurrStore;
    private RoutingAdapter mBottomRoutingAdapter;
    private RoutingAdapter mTopRoutingAdapter;
    private List<Step> mStepList;
    private List<LatLng> mGeoPointList;
    private int currDirectionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_routing);

        ButterKnife.bind(this);

        mStepList = new ArrayList<>();
        mGeoPointList = new ArrayList<>();

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetContainer);

        getExtrasBundle();

        setupToolbar(mToolbar);

        mStepList = mMapsDirection.getRouteList().get(0).getLegList().get(0).getStepList();
        mCurrLocation = mStepList.get(0).getStartMapCoordination().getLocation();
        mGeoPointList = PolyUtil.decode(mMapsDirection.getRouteList().get(0).getEncodedPolylineString());


        mBottomRoutingAdapter = new RoutingAdapter(mStepList, RoutingAdapter.TYPE_FULL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MapRoutingActivity.this);
        bottomRecyclerView.setLayoutManager(layoutManager);
        bottomRecyclerView.setAdapter(mBottomRoutingAdapter);

        mTopRoutingAdapter = new RoutingAdapter(mStepList, RoutingAdapter.TYPE_SHORT);
        final LinearLayoutManager topLayoutManager = new LinearLayoutManager(MapRoutingActivity.this, LinearLayoutManager.HORIZONTAL, false);
        topRecyclerView.setLayoutManager(topLayoutManager);
        topRecyclerView.setAdapter(mTopRoutingAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(topRecyclerView);

        mListener = new RoutingAdapter.OnNavigationItemClickListener() {
            @Override
            public void onClick(int index) {
                showDirectionAt(index);
                currDirectionIndex = index;
                enterPreviewMode();
            }
        };

        prevInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currDirectionIndex--;
                if (currDirectionIndex >= 0 && currDirectionIndex < mBottomRoutingAdapter.getItemCount()) {
                    topRecyclerView.smoothScrollToPosition(currDirectionIndex);
                    showDirectionAt(currDirectionIndex);
                } else {
                    currDirectionIndex = 0;
                }
            }
        });

        nextInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currDirectionIndex++;
                if (currDirectionIndex >= 0 && currDirectionIndex < mBottomRoutingAdapter.getItemCount()) {
                    topRecyclerView.smoothScrollToPosition(currDirectionIndex);
                    showDirectionAt(currDirectionIndex);
                } else {
                    currDirectionIndex = mBottomRoutingAdapter.getItemCount() - 1;
                }
            }
        });


        setUpMapIfNeeded();
    }

    private void showDirectionAt(int currDirectionIndex) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mBottomRoutingAdapter.getDirectionLocationAt(currDirectionIndex), 18));
    }

    private void getExtrasBundle() {
        Bundle extras = getIntent().getExtras();
        mMapsDirection = extras.getParcelable(Keys.KEY_ROUTE_LIST);
        mCurrStore = extras.getParcelable(Keys.KEY_DES_STORE);

        if (mMapsDirection == null || mCurrStore == null || mMapsDirection.getRouteList().size() <= 0) {
            Toast.makeText(MapRoutingActivity.this, "Failed to open Routing screen!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);

        // add back arrow to mToolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(mCurrStore.getTitle());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            if (isPreviewMode) {
                exitPreviewMode();
            } else {
                finish(); // close this activity and return to preview activity (if there is any)

            }
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
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrLocation, 16));

            Marker currMarker = googleMap.addMarker(new MarkerOptions().position(mCurrLocation)
                    .title("Your location")
                    .snippet("Your current location, please follow the line")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_bluedot)));

            if (mCurrStore != null && mCurrStore.getPosition() != null) {
                Marker storeMarker = googleMap.addMarker(new MarkerOptions().position(mCurrStore.getPosition())
                        .title(mCurrStore.getTitle())
                        .snippet(mCurrStore.getAddress())
                        .icon(BitmapDescriptorFactory.fromResource(MapUtils.getLogoDrawableId(mCurrStore.getType()))));
            }

            drawPolylines(mGeoPointList, mGoogleMap);


            txtTravelTime.setText(mMapsDirection.getRouteList().get(0).getLegList().get(0).getDuration());
            txtTravelDistance.setText(mMapsDirection.getRouteList().get(0).getLegList().get(0).getDistance());
            txtTravelOverview.setText("Via " + mMapsDirection.getRouteList().get(0).getSummary());


            mBottomRoutingAdapter.setOnNavigationItemClickListener(mListener);
            mTopRoutingAdapter.setOnNavigationItemClickListener(mListener);
        }
    }

    void drawPolylines(List<LatLng> geoPointList, GoogleMap googleMap) {
        //Add position to viewBounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mCurrLocation);

        PolylineOptions options = new PolylineOptions()
                .clickable(true)
                .color(ContextCompat.getColor(MapRoutingActivity.this, R.color.googleBlue))
                .width(12)
                .geodesic(true)
                .zIndex(5f);

        for (int i = 0; i < geoPointList.size(); i++) {
            LatLng geoPoint = geoPointList.get(i);
            options.add(geoPoint);
            builder.include(geoPoint);
        }

        if (mCurrDirection != null)
            mCurrDirection.remove();

        mCurrDirection = googleMap.addPolyline(options);

        //Build the viewbounds contain all markers
        LatLngBounds bounds = builder.build();

        zoomToShowAllMarker(bounds, googleMap);

    }

    private void zoomToShowAllMarker(LatLngBounds bounds, GoogleMap googleMap) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels - DisplayUtils.convertDpToPx(displayMetrics, 160);
        int padding = 24; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        googleMap.animateCamera(cu);
    }

    private void enterPreviewMode() {
        routingButtonContainer.setVisibility(View.VISIBLE);
        topRecyclerView.setVisibility(View.VISIBLE);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        isPreviewMode = true;
    }

    private void exitPreviewMode() {
        routingButtonContainer.setVisibility(View.GONE);
        topRecyclerView.setVisibility(View.GONE);
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        isPreviewMode = false;
    }
}
