package com.iceteaviet.fastfoodfinder.ui.main.map;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.google.firebase.perf.metrics.AddTrace;
import com.iceteaviet.fastfoodfinder.App;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.DataManager;
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.data.transport.model.SearchEventResult;
import com.iceteaviet.fastfoodfinder.ui.routing.MapRoutingActivity;
import com.iceteaviet.fastfoodfinder.ui.store.StoreInfoDialogFragment;
import com.iceteaviet.fastfoodfinder.utils.Constant;
import com.iceteaviet.fastfoodfinder.utils.DisplayUtils;
import com.iceteaviet.fastfoodfinder.utils.LocationUtils;
import com.iceteaviet.fastfoodfinder.utils.PermissionUtils;
import com.iceteaviet.fastfoodfinder.utils.ui.UiUtils;

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
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, LocationListener {
    private static final String TAG = MainMapFragment.class.getSimpleName();
    private static final Hashtable<Integer, Bitmap> CACHE = new Hashtable<Integer, Bitmap>();
    BottomSheetBehavior mBottomSheetBehavior;

    LocationRequest mLocationRequest;
    @BindView(R.id.rv_bottom_sheet)
    RecyclerView mNearStoreRecyclerView;
    @BindView(R.id.maps_container)
    CoordinatorLayout mCoordinatorLayoutContainer;
    @BindView(R.id.ll_bottom_sheet)
    LinearLayout mBottomSheetContainer;
    LatLng currLocation;
    List<Store> mStoreList;
    List<Store> visibleStores;
    Map<Integer, Marker> mMarkerMap; // pair storeId - marker
    Bitmap currMarkerBitmap;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private NearByStoreListAdapter mAdapter;
    private GoogleApiClient googleApiClient;
    private boolean isZoomToUser = false;

    private PublishSubject<Store> newVisibleStorePublisher;
    private DataManager dataManager;


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
        initializeVariables();

        mLocationRequest = LocationUtils.createLocationRequest();
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

    @Override
    public void onDestroy() {
        newVisibleStorePublisher.onComplete();
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionUtils.REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGoogleMap.setMyLocationEnabled(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
                    getLastLocation();
                } else {
                    Toast.makeText(getContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        addMarkersToMap(mStoreList, mGoogleMap);
        setMarkersListener(mGoogleMap);

        if (PermissionUtils.isLocationPermissionGranted(getContext())) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);

            getLastLocation();

            // Showing the current location in Google Map
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 16f));
        } else {
            PermissionUtils.requestLocationPermission(this);
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
        switch (resultCode) {
            case SearchEventResult.SEARCH_QUICK_OK:
                mStoreList.clear();
                mGoogleMap.clear();

                dataManager.getLocalStoreDataSource()
                        .findStoresByType(searchEventResult.getStoreType())
                        .subscribe(new SingleObserver<List<Store>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(List<Store> storeList) {
                                mStoreList = storeList;
                                if (mStoreList == null || mStoreList.size() <= 0)
                                    Toast.makeText(getContext(), "Failed to get stores data!", Toast.LENGTH_SHORT).show();

                                addMarkersToMap(mStoreList, mGoogleMap);
                                mAdapter.setCurrCameraPosition(mGoogleMap.getCameraPosition().target);
                                visibleStores = getVisibleStore(mStoreList, mGoogleMap.getProjection().getVisibleRegion().latLngBounds);
                                mAdapter.setStores(visibleStores);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getContext(), "Failed to get stores data!", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case SearchEventResult.SEARCH_STORE_OK:
                mStoreList.clear();
                mGoogleMap.clear();
                dataManager.getLocalStoreDataSource()
                        .findStores(searchEventResult.getSearchString())
                        .subscribe(new SingleObserver<List<Store>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(List<Store> storeList) {
                                mStoreList = storeList;
                                if (mStoreList == null || mStoreList.size() <= 0)
                                    Toast.makeText(getContext(), "Failed to get stores data!", Toast.LENGTH_SHORT).show();

                                addMarkersToMap(mStoreList, mGoogleMap);

                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mStoreList.get(0).getPosition(), 16f));

                                mAdapter.setCurrCameraPosition(mGoogleMap.getCameraPosition().target);
                                visibleStores = getVisibleStore(mStoreList, mGoogleMap.getProjection().getVisibleRegion().latLngBounds);
                                mAdapter.setStores(visibleStores);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getContext(), "Failed to get stores data!", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;

            case SearchEventResult.SEARCH_COLLAPSE:
                mStoreList.clear();
                mGoogleMap.clear();
                dataManager.getLocalStoreDataSource().getAllStores()
                        .subscribe(new SingleObserver<List<Store>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(List<Store> storeList) {
                                mStoreList = storeList;
                                if (mStoreList == null || mStoreList.size() <= 0)
                                    Toast.makeText(getContext(), "Failed to get stores data!", Toast.LENGTH_SHORT).show();
                                addMarkersToMap(mStoreList, mGoogleMap);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getContext(), "Failed to get stores data!", Toast.LENGTH_SHORT).show();
                            }
                        });

                if (currLocation != null)
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 16f));
                break;

            default:
                Toast.makeText(getContext(), R.string.search_error, Toast.LENGTH_SHORT).show();
        }
    }

    @AddTrace(name = "getVisibleStore")
    private List<Store> getVisibleStore(List<Store> storeList, LatLngBounds bounds) {
        List<Store> stores = new ArrayList<>();

        for (int i = 0; i < storeList.size(); i++) {
            Store store = storeList.get(i);
            if (bounds.contains(store.getPosition())) {
                // Inside visible range
                stores.add(store);
                if (!this.visibleStores.contains(store)) {
                    // New store become visible
                    newVisibleStorePublisher.onNext(store);
                }
            }
        }

        return stores;
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
        visibleStores = new ArrayList<>();
        mMarkerMap = new HashMap<>();

        mAdapter = new NearByStoreListAdapter();
        dataManager = App.getDataManager();
        newVisibleStorePublisher = PublishSubject.create();

        dataManager.getLocalStoreDataSource().getAllStores()
                .subscribe(new SingleObserver<List<Store>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Store> storeList) {
                        mStoreList = storeList;
                        if (mStoreList == null || mStoreList.size() <= 0)
                            Toast.makeText(getContext(), "Failed to get stores data!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), "Failed to get stores data!", Toast.LENGTH_SHORT).show();
                    }
                });

        currMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_circlek_50);

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(MainMapFragment.this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG, "Failed to get current location");
                    }
                })
                .addApi(LocationServices.API)
                .build();
    }


    private void initBottomSheet() {
        mAdapter.setOnStoreListListener(new NearByStoreListAdapter.StoreListListener() {
            @Override
            public void onItemClick(Store store) {
                getDirection(store);
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
                if (!PermissionUtils.isLocationPermissionGranted(getContext())) {
                    PermissionUtils.requestLocationPermission(MainMapFragment.this);
                } else {
                    mGoogleMap.setMyLocationEnabled(true);
                }

                //Animate marker icons when camera move
                mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        Random random = new Random();
                        if (random.nextBoolean()) {
                            mAdapter.setCurrCameraPosition(mGoogleMap.getCameraPosition().target);
                            visibleStores = getVisibleStore(mStoreList, mGoogleMap.getProjection().getVisibleRegion().latLngBounds);
                            mAdapter.setStores(visibleStores);
                        }
                    }
                });

                newVisibleStorePublisher
                        .observeOn(Schedulers.computation())
                        .map(new Function<Store, Pair<Marker, Bitmap>>() {
                            @Override
                            public Pair<Marker, Bitmap> apply(Store store) {
                                Bitmap bitmap = getStoreIcon(getContext(), store.getType());
                                Marker marker = mMarkerMap.get(store.getId());

                                return new Pair<>(marker, bitmap);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Pair<Marker, Bitmap>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Pair<Marker, Bitmap> pair) {
                                animateMarker(pair.second, pair.first);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

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
            mMarkerMap.put(store.getId(), marker);
        }
    }

    void getDirection(final Store store) {
        LatLng storeLocation = store.getPosition();
        Map<String, String> queries = new HashMap<>();

        queries.put("origin", LocationUtils.getLatLngString(currLocation));
        queries.put("destination", LocationUtils.getLatLngString(storeLocation));

        dataManager.getMapsRoutingApiHelper().getMapsDirection(queries, store)
                .subscribe(new SingleObserver<MapsDirection>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(MapsDirection mapsDirection) {
                        Intent intent = new Intent(getActivity(), MapRoutingActivity.class);
                        Bundle extras = new Bundle();
                        extras.putParcelable(Constant.KEY_ROUTE_LIST, mapsDirection);
                        extras.putParcelable(Constant.KEY_DES_STORE, store);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }


    private void setMarkersListener(GoogleMap googleMap) {
        if (googleMap != null) {
            // The Map is verified. It is now safe to manipulate the map.
            // Attach marker click listener to the map here
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    // Handle store marker click click here
                    Store store = (Store) marker.getTag();
                    showDialogStoreInfo(store);
                    return false;
                }
            });
        }
    }


    void animateMarker(final Bitmap bitmap, final Marker marker) {
        if (marker == null)
            return;

        ValueAnimator animator = ValueAnimator.ofFloat(0.1f, 1);
        animator.setDuration(1000);
        animator.setStartDelay(500);
        animator.setInterpolator(new BounceInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (float) animation.getAnimatedValue();
                try {
                    // TODO: Optimize .fromBitmap & resize icons
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(DisplayUtils.resizeMarkerIcon(bitmap, Math.round(scale * 75), Math.round(scale * 75))));
                } catch (IllegalArgumentException ex) {
                    Log.e(TAG, ex.getMessage());
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
                int id = UiUtils.getStoreLogoDrawableId(type);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

                CACHE.put(type, bitmap);
            }
            return CACHE.get(type);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currLocation = new LatLng(location.getLatitude(), location.getLongitude());
        // Creating a LatLng object for the current location

        if (!isZoomToUser) {
            // Zoom and show current location in the Google Map
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 16f));

            isZoomToUser = true;
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            currLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        } else
            currLocation = mGoogleMap.getCameraPosition().target;
    }
}