package com.iceteaviet.fastfoodfinder.ui.main;


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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.adapter.NearByStoreAdapter;
import com.iceteaviet.fastfoodfinder.helper.SearchEventResult;
import com.iceteaviet.fastfoodfinder.model.Store.Store;
import com.iceteaviet.fastfoodfinder.model.Store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.model.Store.StoreViewModel;
import com.iceteaviet.fastfoodfinder.rest.RestClient;
import com.iceteaviet.fastfoodfinder.ui.store.StoreInfoDialogFragment;
import com.iceteaviet.fastfoodfinder.utils.MapUtils;
import com.iceteaviet.fastfoodfinder.utils.PermissionUtils;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks {
    private static final Hashtable<Integer, Bitmap> CACHE = new Hashtable<Integer, Bitmap>();
    BottomSheetBehavior mBottomSheetBehavior;

    LocationRequest mLocationRequest;
    @BindView(R.id.rv_bottom_sheet) RecyclerView mNearStoreRecyclerView;
    @BindView(R.id.maps_container) CoordinatorLayout mCoordinatorLayoutContainer;
    @BindView(R.id.ll_bottom_sheet) LinearLayout mBottomSheetContainer;
    LatLng currLocation;
    List<Store> mStoreList;
    Map<Integer,Marker> mMarkerMap;
    Map<Integer,StoreViewModel> mNearlyStoreMap;
    Bitmap currMarkerBitmap;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private NearByStoreAdapter mAdapter;
    private GoogleApiClient googleApiClient;


    public MainMapFragment() {
        // Required empty public constructor
    }

    public static MainMapFragment newInstance() {

        Bundle args = new Bundle();

        MainMapFragment fragment = new MainMapFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapFragment = inflateSupportMapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(getContext());
        PermissionUtils.requestLocaiton(getActivity());

        initializeVariables();

        mLocationRequest = MapUtils.createLocationRequest();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_map, container, false);
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

        final boolean[] isZoomToUser = {false};
        if (PermissionUtils.isLocationPermissionGranted(getContext())) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    // Creating a LatLng object for the current location

                    if (!isZoomToUser[0]) {
                        // Zoom and show current location in the Google Map
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 16f));

                        isZoomToUser[0] = true;
                    }
                }
            });

            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                currLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            } else
                currLocation = mGoogleMap.getCameraPosition().target;

            // Showing the current location in Google Map
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 16f));
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
    public void onSearchResult(SearchEventResult searchEventResult) {
        int resultCode = searchEventResult.getResultCode();
        switch (resultCode)
        {
            case SearchEventResult.SEARCH_QUICK_OK:
                mStoreList.clear();
                mGoogleMap.clear();

                mStoreList = StoreDataSource.getStoresByType(searchEventResult.getStoreType());
                if (mStoreList == null || mStoreList.size() <= 0)
                    Toast.makeText(getContext(),"Failed to get stores data!",Toast.LENGTH_SHORT).show();

                addMarkersToMap(mStoreList, mGoogleMap);
                AnimateMarkerTask task = new AnimateMarkerTask();
                task.execute(mStoreList);
                break;
            case SearchEventResult.SEARCH_STORE_OK:
                mStoreList.clear();
                mGoogleMap.clear();
                mStoreList = StoreDataSource.getStore(searchEventResult.getSearchString());
                if (mStoreList == null || mStoreList.size() <= 0)
                    Toast.makeText(getContext(),"Failed to get stores data!",Toast.LENGTH_SHORT).show();

                addMarkersToMap(mStoreList, mGoogleMap);

                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mStoreList.get(0).getPosition(),16f));

                AnimateMarkerTask storeTask = new AnimateMarkerTask();
                storeTask.execute(mStoreList);
                break;

            case SearchEventResult.SEARCH_COLLAPSE:
                mStoreList.clear();
                mGoogleMap.clear();
                mStoreList = StoreDataSource.getAllObjects();
                if (mStoreList == null || mStoreList.size() <= 0)
                    Toast.makeText(getContext(),"Failed to get stores data!",Toast.LENGTH_SHORT).show();
                addMarkersToMap(mStoreList, mGoogleMap);
                if (currLocation != null)
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation,16f));
                break;

            default:
                Toast.makeText(getContext(), R.string.search_error, Toast.LENGTH_SHORT).show();
        }
    }

    public SupportMapFragment inflateSupportMapFragment() {
        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.maps_container);
        if (mapFragment == null) {
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
            mapFragment = SupportMapFragment.newInstance(options);
            fragmentManager.beginTransaction().replace(R.id.map_placeholder, mapFragment).commit();
            fragmentManager.executePendingTransactions();
        }

        return mapFragment;
    }


    public void initializeVariables() {
        mStoreList = new ArrayList<>();
        mNearlyStoreMap = new HashMap<>();
        mMarkerMap = new HashMap<>();
        mAdapter = new NearByStoreAdapter();


        mStoreList = StoreDataSource.getAllObjects();
        if (mStoreList == null || mStoreList.size() <= 0)
            Toast.makeText(getContext(),"Failed to get stores data!",Toast.LENGTH_SHORT).show();

        currMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_circlek_50);

        RestClient.getInstance().setGooglemapBrowserKey(getString(R.string.google_maps_browser_key));

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(MainMapFragment.this)
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
                getDirection(new Store(store));
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
                if (PermissionUtils.isLocationPermissionGranted(getContext())) {
                    mGoogleMap.setMyLocationEnabled(true);
                } else {
                    PermissionUtils.requestLocaiton(getActivity());
                    mGoogleMap.setMyLocationEnabled(true);
                }

                //Animate ic_map_defaultmarker when camera move
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
        googleMap.clear();

        // Set icons of the ic_map_defaultmarker to green
        for (int i = 0; i < storeList.size(); i++) {
            Store store = storeList.get(i);
            Marker marker = googleMap.addMarker(new MarkerOptions().position(store.getPosition())
                    .title(store.getTitle())
                    .snippet(store.getAddress())
                    .icon(BitmapDescriptorFactory.fromBitmap(getStoreIcon(getContext(), store.getType()))));
            marker.setTag(store);
            mMarkerMap.put(i,marker);
            //animateMarker(currMarkerBitmap,ic_map_defaultmarker);
        }
    }

    void getDirection(final Store store) {
        LatLng storeLocation = store.getPosition();
        Map<String, String> queries = new HashMap<String, String>();

        queries.put("origin", MapUtils.getLatLngString(currLocation));
        queries.put("destination", MapUtils.getLatLngString(storeLocation));

        RestClient.getInstance().showDirection(getActivity(), queries, store);
    }



    private void setMarkersListener(GoogleMap googleMap) {
        if (googleMap != null) {
            // The Map is verified. It is now safe to manipulate the map.
            // Attach ic_map_defaultmarker click listener to the map here
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    // Handle ic_map_defaultmarker click here
                    //ic_map_defaultmarker.showInfoWindow();
                    //LatLng storeLocation = ic_map_defaultmarker.getPosition();
                    //getDirection(storeLocation);
                    Store store = (Store) marker.getTag();
                    showDialogStoreInfo(store);
                    return false;
                }
            });
        }
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

    private void showDialogStoreInfo(final Store store) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        StoreInfoDialogFragment dialog = StoreInfoDialogFragment.newInstance(store);
        dialog.setDialogListen(new StoreInfoDialogFragment.StoreDialogActionListener() {
            @Override
            public void onDirection(Store store) {
                getDirection(store);
            }

            @Override
            public void onAddToFavorite(int storeId) {
                //TODO lưu vào danh sách yêu thích
                Toast.makeText(getActivity(), "add to favorite " + storeId, Toast.LENGTH_SHORT).show();
            }
        });
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
                        //New ic_store
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

            //Animate ic_map_defaultmarker
            for (int i = 0; i < newStoreNearBy.size(); i++) {
                Bitmap bitmap = getStoreIcon(getContext(),mStoreList.get(newStoreNearBy.get(i)).getType());
                animateMarker(bitmap, mMarkerMap.get(newStoreNearBy.get(i)));
            }
        }
    }
}