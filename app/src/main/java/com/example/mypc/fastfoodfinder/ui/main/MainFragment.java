package com.example.mypc.fastfoodfinder.ui.main;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.adapter.NearByStoreAdapter;
import com.example.mypc.fastfoodfinder.helper.SearchResult;
import com.example.mypc.fastfoodfinder.model.Routing.MapsDirection;
import com.example.mypc.fastfoodfinder.model.Routing.Route;
import com.example.mypc.fastfoodfinder.model.Routing.Step;
import com.example.mypc.fastfoodfinder.model.Store.Store;
import com.example.mypc.fastfoodfinder.model.Store.StoreDataSource;
import com.example.mypc.fastfoodfinder.model.Store.StoreViewModel;
import com.example.mypc.fastfoodfinder.rest.MapsDirectionApi;
import com.example.mypc.fastfoodfinder.ui.store.StoreInfoDialogFragment;
import com.example.mypc.fastfoodfinder.utils.Constant;
import com.example.mypc.fastfoodfinder.utils.MapUtils;
import com.example.mypc.fastfoodfinder.utils.PermissionUtils;
import com.example.mypc.fastfoodfinder.utils.RetrofitUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks {

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;


    private static final Hashtable<Integer, Bitmap> CACHE = new Hashtable<Integer, Bitmap>();
    BottomSheetBehavior mBottomSheetBehavior;
    MapsDirectionApi mMapDirectionApi;
    LocationRequest mLocationRequest;
    @BindView(R.id.rv_bottom_sheet) RecyclerView mNearStoreRecyclerView;
    @BindView(R.id.maps_container) CoordinatorLayout mCoordinatorLayoutContainer;
    @BindView(R.id.ll_bottom_sheet) LinearLayout mBottomSheetContainer;
    LatLng currLocation;
    Polyline currDirection;
    List<Store> mStoreList;
    Map<Integer,Marker> mMarkerMap;
    Map<Integer,StoreViewModel> mNearlyStoreMap;
    Bitmap currMarkerBitmap;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private NearByStoreAdapter mAdapter;
    private GoogleApiClient googleApiClient;


    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inflateSupportMapFragment(mMapFragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(getContext());
        PermissionUtils.requestLocaiton(getActivity());

        initializeVariables();

        createLocationRequest();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetContainer);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNearStoreRecyclerView.setAdapter(mAdapter);
        // Set the layout manager
        mNearStoreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (mGoogleMap == null) {
            initializeMap();
            initBottomSheet();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        addMarkersToMap(mStoreList, mGoogleMap);
        setMarkersListener(mGoogleMap);

        if (PermissionUtils.checkLocation(getContext())) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("MAPP", "Firing onLocationChanged..............................................");
                    currLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    // Creating a LatLng object for the current location
                    LatLng latLng = currLocation;
                    // Showing the current location in Google Map
                    //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Zoom in the Google Map
                    //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                }
            });

            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                currLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            } else
                currLocation = mGoogleMap.getCameraPosition().target;

            // Creating a LatLng object for the current location
            LatLng latLng = currLocation;
            // Showing the current location in Google Map
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        } else {
            PermissionUtils.requestLocaiton(getActivity());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }


    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchResult(SearchResult searchResult) {
        int resultCode = searchResult.getResultCode();
        switch (resultCode)
        {
            case SearchResult.SEARCH_QUICK:
                mStoreList.clear();
                mGoogleMap.clear();
                mStoreList = StoreDataSource.getStore(searchResult.getStoreType());
                addMarkersToMap(mStoreList, mGoogleMap);
                AnimateMarkerTask task = new AnimateMarkerTask();
                task.execute(mStoreList);
                break;
            case SearchResult.SEARCH_STORE:
                break;

            case SearchResult.SEARCH_COLLAPSE:
                mStoreList.clear();
                mGoogleMap.clear();
                mStoreList = StoreDataSource.getAllObjects();
                addMarkersToMap(mStoreList, mGoogleMap);
                break;

            default:
                Toast.makeText(getContext(), "Search error!", Toast.LENGTH_SHORT).show();
        }
    }

    public void inflateSupportMapFragment(SupportMapFragment mapFragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        mMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.maps_container);
        if (mMapFragment == null) {
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(new LatLng(10.7473821, 106.6805755))
                    .zoom(16)
                    .build();
            GoogleMapOptions options = new GoogleMapOptions();
            options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                    .camera(cameraPosition)
                    .compassEnabled(true)
                    .rotateGesturesEnabled(true)
                    .zoomGesturesEnabled(true)
                    .tiltGesturesEnabled(true);
            mMapFragment = SupportMapFragment.newInstance(options);
            fragmentManager.beginTransaction().replace(R.id.map_placeholder, mMapFragment).commit();
            fragmentManager.executePendingTransactions();
        }
    }


    public void initializeVariables() {
        mStoreList = new ArrayList<>();
        mNearlyStoreMap = new HashMap<>();
        mMarkerMap = new HashMap<>();
        mAdapter = new NearByStoreAdapter();


        mStoreList = StoreDataSource.getAllObjects();

        currMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_circle_k_50);

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(MainFragment.this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("MAPP", "Failed to get current location");
                    }
                })
                .addApi(LocationServices.API)
                .build();
    }

    private void initBottomSheet() {
        mAdapter.setOnStoreListListener(new NearByStoreAdapter.StoreListListener() {
            @Override
            public void onItemClick(StoreViewModel store) {
                LatLng storeLocation = store.getPosition();
                getDirection(storeLocation);
            }
        });
    }

    private void initializeMap() {
        mMapFragment.getMapAsync(new OnMapReadyCallback() {

            @SuppressWarnings("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mGoogleMap = googleMap;
                mGoogleMap.setBuildingsEnabled(true);
                if (PermissionUtils.checkLocation(getContext())) {
                    mGoogleMap.setMyLocationEnabled(true);
                } else {
                    PermissionUtils.requestLocaiton(getActivity());
                }

                //Animate marker when camera move
                mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        Random random = new Random();
                        if (random.nextBoolean()) {
                            AnimateMarkerTask task = new AnimateMarkerTask();
                            task.execute(mStoreList);
                        }
                    }
                });
            }
        });
    }


    void addMarkersToMap(List<Store> storeList, GoogleMap googleMap) {
        // Set icons of the marker to green
        for (int i = 0; i < storeList.size(); i++) {
            Store store = storeList.get(i);
            Marker marker = googleMap.addMarker(new MarkerOptions().position(store.getPosition())
                    .title("Circle K")
                    .snippet(store.getTitle())
                    .icon(BitmapDescriptorFactory.fromBitmap(getStoreIcon(getContext(), store.getType()))));
            marker.setTag(store);
            mMarkerMap.put(i,marker);
            //animateMarker(currMarkerBitmap,marker);
        }
    }

    void getDirection(LatLng storeLocation) {
        Map<String, String> queries = new HashMap<String, String>();

        queries.put("origin", MapUtils.getLatLngString(currLocation));
        queries.put("destination", MapUtils.getLatLngString(storeLocation));

        mMapDirectionApi = RetrofitUtils.get(getString(R.string.google_maps_browser_key)).create(MapsDirectionApi.class);

        mMapDirectionApi.getDirection(queries).enqueue(new Callback<MapsDirection>() {
            @Override
            public void onResponse(Call<MapsDirection> call, Response<MapsDirection> response) {
                List<Route> routeList = response.body().getRouteList();
                drawPolylines(routeList.get(0).getLegList().get(0).getStepList(), mGoogleMap);
            }

            @Override
            public void onFailure(Call<MapsDirection> call, Throwable t) {
                Log.e("MAPP", "Get direction failed");
            }
        });
    }

    void drawPolylines(List<Step> steps, GoogleMap googleMap) {
        PolylineOptions options = new PolylineOptions()
                .clickable(true)
                .color(ContextCompat.getColor(getContext(), R.color.googleBlue))
                .width(12)
                .geodesic(true)
                .zIndex(5f)
                .add(steps.get(0).getStartMapCoordination().getLocation());

        for (int i = 0; i < steps.size(); i++) {
            options.add(steps.get(i).getEndMapCoordination().getLocation());
        }

        if (currDirection != null)
            currDirection.remove();
        currDirection = googleMap.addPolyline(options);
    }


    private void setMarkersListener(GoogleMap googleMap) {
        if (googleMap != null) {
            // The Map is verified. It is now safe to manipulate the map.
            // Attach marker click listener to the map here
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    // Handle marker click here
                    //marker.showInfoWindow();
                    //LatLng storeLocation = marker.getPosition();
                    //getDirection(storeLocation);
                    Store store = (Store) marker.getTag();
                    showDialogStoreInfo(store);
                    return false;
                }
            });
        }
    }



    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    void animateMarker(final Bitmap bitmap, final Marker marker) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.1f, 1);
        animator.setDuration(1000);
        animator.setStartDelay(500);
        animator.setInterpolator(new BounceInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (float) animation.getAnimatedValue();
                try {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(MapUtils.resizeMarkerIcon(bitmap, Math.round(scale * 75), Math.round(scale * 75))));
                } catch (IllegalArgumentException ex) {
                    Log.e("MAPP", ex.getMessage());
                }
            }
        });
        animator.start();
    }

    private void showDialogStoreInfo(Store store) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        StoreInfoDialogFragment dialog = StoreInfoDialogFragment.newInstance(store);
        dialog.show(fm, "dialog-info");
    }

    public Bitmap getStoreIcon(Context context, Integer type) {
        synchronized (CACHE) {
            if (!CACHE.containsKey(type)) {
                int id = MapUtils.getLogoDrawableId(type);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),id);

                CACHE.put(type, bitmap);
            }
            return CACHE.get(type);
        }
    }

    class AnimateMarkerTask extends AsyncTask<List<Store>, Void, Void> {
        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng cameraPosition;
        Map<Integer,StoreViewModel> nearByStores;
        List<Integer> newStoreNearBy;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
            cameraPosition = mGoogleMap.getCameraPosition().target;
            nearByStores = new HashMap<>();
            newStoreNearBy = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(List<Store>...storeList) {
            for (int i = 0; i < storeList[0].size(); i++) {
                if (bounds.contains(storeList[0].get(i).getPosition())) {
                    nearByStores.put(i, new StoreViewModel(storeList[0].get(i), cameraPosition));
                    if (!mNearlyStoreMap.containsKey(i))
                    {
                        //New store
                        newStoreNearBy.add(i);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mNearlyStoreMap.clear();
            mNearlyStoreMap.putAll(nearByStores);
            mAdapter.setStores(new ArrayList<StoreViewModel>(mNearlyStoreMap.values()));

            //Animate marker
            for (int i = 0; i < newStoreNearBy.size(); i++) {
                Bitmap bitmap = getStoreIcon(getContext(),mStoreList.get(newStoreNearBy.get(i)).getType());
                animateMarker(bitmap, mMarkerMap.get(newStoreNearBy.get(i)));
            }
        }
    }
}