package com.iceteaviet.fastfoodfinder.ui.main.map;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.perf.metrics.AddTrace;
import com.iceteaviet.fastfoodfinder.App;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.DataManager;
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.data.transport.model.SearchEventResult;
import com.iceteaviet.fastfoodfinder.ui.routing.MapRoutingActivity;
import com.iceteaviet.fastfoodfinder.ui.store.StoreInfoDialogFragment;
import com.iceteaviet.fastfoodfinder.utils.AppLogger;
import com.iceteaviet.fastfoodfinder.utils.Constant;
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
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper.PARAM_DESTINATION;
import static com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper.PARAM_ORIGIN;
import static com.iceteaviet.fastfoodfinder.ui.routing.MapRoutingActivity.KEY_DES_STORE;
import static com.iceteaviet.fastfoodfinder.ui.routing.MapRoutingActivity.KEY_ROUTE_LIST;
import static com.iceteaviet.fastfoodfinder.utils.Constant.DEFAULT_ZOOM_LEVEL;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, LocationListener {
    private static final String TAG = MainMapFragment.class.getSimpleName();
    private static final Hashtable<Integer, Bitmap> CACHE = new Hashtable<>();

    @BindView(R.id.rv_bottom_sheet)
    protected RecyclerView mNearStoreRecyclerView;
    @BindView(R.id.maps_container)
    CoordinatorLayout mCoordinatorLayoutContainer;
    @BindView(R.id.ll_bottom_sheet)
    LinearLayout mBottomSheetContainer;

    private LocationRequest mLocationRequest;
    private LatLng currLocation;
    private List<Store> mStoreList;
    private List<Store> visibleStores;
    private SparseArray<Marker> markerSparseArray; // pair storeId - marker
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private NearByStoreListAdapter mAdapter;
    private GoogleApiClient googleApiClient;
    private boolean isZoomToUser = false;

    private PublishSubject<Store> newVisibleStorePublisher;
    private PublishSubject<CameraPosition> cameraPositionPublisher;
    private DataManager dataManager;


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
        initStoreData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_map, container, false);
        ButterKnife.bind(this, rootView);
        BottomSheetBehavior.from(mBottomSheetContainer);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBottomSheet();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (mGoogleMap == null) {
            initializeMapData();
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
                    currLocation = getLastLocation();
                } else {
                    Toast.makeText(getContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            default:
                break;
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        addMarkersToMap(mStoreList, mGoogleMap);
        setMarkersListener(mGoogleMap);

        if (PermissionUtils.isLocationPermissionGranted(getContext())) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);

            currLocation = getLastLocation();

            // Showing the current location in Google Map
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, DEFAULT_ZOOM_LEVEL));
        } else {
            PermissionUtils.requestLocationPermission(this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), R.string.cannot_connect_location_service, Toast.LENGTH_SHORT).show();
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
            case SearchEventResult.SEARCH_ACTION_QUICK:
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
                                    Toast.makeText(getContext(), R.string.get_store_data_failed, Toast.LENGTH_SHORT).show();

                                addMarkersToMap(mStoreList, mGoogleMap);
                                mAdapter.setCurrCameraPosition(mGoogleMap.getCameraPosition().target);
                                visibleStores = getVisibleStore(mStoreList, mGoogleMap.getProjection().getVisibleRegion().latLngBounds);
                                mAdapter.setStores(visibleStores);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getContext(), R.string.get_store_data_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT:
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
                                    Toast.makeText(getContext(), R.string.get_store_data_failed, Toast.LENGTH_SHORT).show();

                                addMarkersToMap(mStoreList, mGoogleMap);

                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mStoreList.get(0).getPosition(), DEFAULT_ZOOM_LEVEL));

                                mAdapter.setCurrCameraPosition(mGoogleMap.getCameraPosition().target);
                                visibleStores = getVisibleStore(mStoreList, mGoogleMap.getProjection().getVisibleRegion().latLngBounds);
                                mAdapter.setStores(visibleStores);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getContext(), R.string.get_store_data_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;

            case SearchEventResult.SEARCH_ACTION_COLLAPSE:
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
                                    Toast.makeText(getContext(), R.string.get_store_data_failed, Toast.LENGTH_SHORT).show();
                                addMarkersToMap(mStoreList, mGoogleMap);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getContext(), R.string.get_store_data_failed, Toast.LENGTH_SHORT).show();
                            }
                        });

                if (currLocation != null)
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, DEFAULT_ZOOM_LEVEL));
                break;

            default:
                Toast.makeText(getContext(), R.string.search_error, Toast.LENGTH_SHORT).show();
                break;
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
                    .target(Constant.DEFAULT_MAP_TARGET)
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
        markerSparseArray = new SparseArray<>();

        mAdapter = new NearByStoreListAdapter();
        dataManager = App.getDataManager();
        newVisibleStorePublisher = PublishSubject.create();
        cameraPositionPublisher = PublishSubject.create();

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(MainMapFragment.this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        AppLogger.e(TAG, getString(R.string.cannot_get_curr_location));
                    }
                })
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationUtils.createLocationRequest();
    }

    private void initStoreData() {
        dataManager.getLocalStoreDataSource().getAllStores()
                .subscribe(new SingleObserver<List<Store>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Store> storeList) {
                        mStoreList = storeList;
                        if (mStoreList == null || mStoreList.size() <= 0)
                            Toast.makeText(getContext(), R.string.get_store_data_failed, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), R.string.get_store_data_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void initBottomSheet() {
        mNearStoreRecyclerView.setAdapter(mAdapter);
        mNearStoreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter.setOnStoreListListener(new NearByStoreListAdapter.StoreListListener() {
            @Override
            public void onItemClick(Store store) {
                getDirection(store);
            }
        });
    }

    private void initializeMapData() {
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
                        if (cameraPositionPublisher != null)
                            cameraPositionPublisher.onNext(mGoogleMap.getCameraPosition());
                    }
                });

                cameraPositionPublisher
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<CameraPosition>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(CameraPosition cameraPosition) {
                                mAdapter.setCurrCameraPosition(cameraPosition.target);
                                visibleStores = getVisibleStore(mStoreList, mGoogleMap.getProjection().getVisibleRegion().latLngBounds);
                                mAdapter.setStores(visibleStores);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });

                newVisibleStorePublisher
                        .observeOn(Schedulers.computation())
                        .map(new Function<Store, Pair<Marker, Bitmap>>() {
                            @Override
                            public Pair<Marker, Bitmap> apply(Store store) {
                                Bitmap bitmap = getStoreIcon(store.getType());
                                Marker marker = markerSparseArray.get(store.getId());

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
                                UiUtils.animateMarker(pair.second, pair.first);
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
    }


    private void addMarkersToMap(List<Store> storeList, GoogleMap googleMap) {
        if (googleMap == null)
            return;

        googleMap.clear();

        // Set icons of the store marker to green
        for (int i = 0; i < storeList.size(); i++) {
            Store store = storeList.get(i);
            Marker marker = googleMap.addMarker(new MarkerOptions().position(store.getPosition())
                    .title(store.getTitle())
                    .snippet(store.getAddress())
                    .icon(BitmapDescriptorFactory.fromBitmap(getStoreIcon(store.getType()))));
            marker.setTag(store);
            markerSparseArray.put(store.getId(), marker);
        }
    }

    private void getDirection(final Store store) {
        LatLng storeLocation = store.getPosition();
        Map<String, String> queries = new HashMap<>();

        queries.put(PARAM_ORIGIN, LocationUtils.getLatLngString(currLocation));
        queries.put(PARAM_DESTINATION, LocationUtils.getLatLngString(storeLocation));

        dataManager.getMapsRoutingApiHelper().getMapsDirection(queries, store)
                .subscribe(new SingleObserver<MapsDirection>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(MapsDirection mapsDirection) {
                        Intent intent = new Intent(getActivity(), MapRoutingActivity.class);
                        Bundle extras = new Bundle();
                        extras.putParcelable(KEY_ROUTE_LIST, mapsDirection);
                        extras.putParcelable(KEY_DES_STORE, store);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
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
                Toast.makeText(getActivity(), R.string.fav_stores_added, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(fm, "dialog-info");
    }

    public Bitmap getStoreIcon(Integer type) {
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
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, DEFAULT_ZOOM_LEVEL));

            isZoomToUser = true;
        }
    }

    @SuppressLint("MissingPermission")
    private LatLng getLastLocation() {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            return new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        } else {
            Toast.makeText(getActivity(), R.string.cannot_get_curr_location, Toast.LENGTH_SHORT).show();
            return mGoogleMap.getCameraPosition().target;
        }
    }
}