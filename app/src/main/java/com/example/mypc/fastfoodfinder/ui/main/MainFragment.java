package com.example.mypc.fastfoodfinder.ui.main;


import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.adapter.ListStoreAdapter;
import com.example.mypc.fastfoodfinder.model.MapsDirection;
import com.example.mypc.fastfoodfinder.model.Route;
import com.example.mypc.fastfoodfinder.model.Step;
import com.example.mypc.fastfoodfinder.model.Store;
import com.example.mypc.fastfoodfinder.model.StoreDataSource;
import com.example.mypc.fastfoodfinder.model.StoreViewModel;
import com.example.mypc.fastfoodfinder.rest.MapsDirectionApi;
import com.example.mypc.fastfoodfinder.utils.MarkerUtils;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks{

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    private GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    RecyclerView mRecyclerView;
    CoordinatorLayout mCoordinatorLayout;
    ListStoreAdapter mAdapter;
    BottomSheetBehavior mBottomSheetBehavior;
    MapsDirectionApi mMapDirectionApi;
    GoogleApiClient googleApiClient;
    LatLng currLocation;
    Polyline currDirection;
    List<Store> mStoreList;
    List<StoreViewModel> mNearlyStores;
    LocationRequest mLocationRequest;


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

        FragmentManager fragmentManager = getChildFragmentManager();
        mFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.maps_container);
        if (mFragment == null)
        {
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(new LatLng(10.7473821, 106.6805755))
                    .zoom(15)
                    .tilt(30)
                    .build();
            GoogleMapOptions options = new GoogleMapOptions();
            options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                    .camera(cameraPosition)
                    .compassEnabled(true)
                    .rotateGesturesEnabled(true)
                    .zoomGesturesEnabled(true)
                    .tiltGesturesEnabled(true);
            mFragment = SupportMapFragment.newInstance(options);
            fragmentManager.beginTransaction().replace(R.id.map,mFragment).commit();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.requestLocaiton(getActivity());

        mStoreList = new ArrayList<Store>();
        mNearlyStores = new ArrayList<StoreViewModel>();
        mAdapter = new ListStoreAdapter();

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

        createLocationRequest();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main,container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_bottom_sheet);
        mCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.maps_container);
        mBottomSheetBehavior = BottomSheetBehavior.from(mRecyclerView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Create your items

        mAdapter.setStores(mNearlyStores);
        mRecyclerView.setAdapter(mAdapter);

        // Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleMap == null)
        {
            initMaps();
            initBottomSheet();

        }
    }

    private void initBottomSheet() {
        mAdapter.setOnStoreListListener(new ListStoreAdapter.StoreListListener() {
            @Override
            public void onItemClick(StoreViewModel store) {
                LatLng storeLocation = store.getmPosition();
                getDirection(storeLocation);
            }
        });
    }

    List<StoreViewModel> getStoreInsideScreen(GoogleMap googleMap)
    {
        mNearlyStores = new ArrayList<>();
        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng cameraPosition = googleMap.getCameraPosition().target;
        for (int i = 0; i < mStoreList.size(); i++)
        {
            if (bounds.contains(mStoreList.get(i).getPosition()))
            {
                //Inside
                mNearlyStores.add(new StoreViewModel(mStoreList.get(i), cameraPosition));
            }
            else
            {
                //Do nothing
            }
        }
        return mNearlyStores;
    }



    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 3000;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);

                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);
                //marker.setIcon(BitmapDescriptorFactory.fromBitmap(MarkerUtils.resizeMarkerIcon(getResources(),"marker", (int)interpolator.getInterpolation((float) elapsed / duration)*50, (int)interpolator.getInterpolation((float) elapsed / duration)*50)));

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
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
                    LatLng latLng =  currLocation;
                    // Showing the current location in Google Map
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Zoom in the Google Map
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            });

            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null)
            {
                currLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                final Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(currLocation).title("Marker"));
                final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_circle_k_50);
                final Bitmap target = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);


                final Canvas canvas = new Canvas(target);

                ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                animator.setDuration(500);
                animator.setStartDelay(1000);
                animator.setInterpolator(new BounceInterpolator());
                final Rect originalRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                final RectF scaledRect = new RectF();

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    int w = 0, h = 0;
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float scale = (float) animation.getAnimatedValue();
                        scaledRect.set(0, 0, originalRect.right * scale, originalRect.bottom * scale);
                        canvas.drawBitmap(bitmap, originalRect, scaledRect, null);
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(MarkerUtils.resizeMarkerIcon(bitmap, w, h)));
                        w+=5;
                        h += 5;
                    }
                });
                animator.start();
            }
            else
                currLocation = mGoogleMap.getCameraPosition().target;

            // Creating a LatLng object for the current location
            LatLng latLng =  currLocation;
            // Showing the current location in Google Map
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        } else {
            PermissionUtils.requestLocaiton(getActivity());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void initMaps()
    {
        mFragment.getMapAsync(new OnMapReadyCallback() {

            @SuppressWarnings("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;

                mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        getStoreInsideScreen(mGoogleMap);
                        mAdapter.setStores(mNearlyStores);
                    }
                });

                StoreDataSource storeDataSource = new StoreDataSource();
                mStoreList = storeDataSource.getAllObjects();

                /*
                Gson gson = new Gson();
                try {
                    JsonObject root = gson.fromJson(openJSONFromResource(), JsonObject.class);
                    mStoreList = gson.fromJson(root.getAsJsonArray("markers_add"), new TypeToken<ArrayList<Store>>(){}.getType());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */


                mGoogleMap.setBuildingsEnabled(true);
                if (PermissionUtils.checkLocation(getContext())) {
                    mGoogleMap.setMyLocationEnabled(true);
                } else {
                    PermissionUtils.requestLocaiton(getActivity());
                }
            }
        });
    }

    String openJSONFromResource() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.circle_k);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }  finally {
            is.close();
        }

        return writer.toString();
    }

    void addMarkersToMap(List<Store> list, GoogleMap googleMap)
    {
        // Set the color of the marker to green
        BitmapDescriptor markerIcon =
                BitmapDescriptorFactory.fromResource(R.drawable.logo_circle_k_50);
        for (int i = 0; i < list.size(); i++)
        {
            Store store = list.get(i);
            Marker marker = googleMap.addMarker(new MarkerOptions().position(store.getPosition())
                    .title("Circle K")
                    .snippet(store.getTitle())
                    .icon(markerIcon));

        }
    }




    void getDirection(LatLng storeLocation)
    {
        Map<String, String> queries = new HashMap<String, String>();
        Log.v("MAPP", currLocation.toString());
        queries.put("origin", getLatLngString(currLocation));
        queries.put("destination", getLatLngString(storeLocation));
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

    void drawPolylines(List<Step> steps, GoogleMap googleMap)
    {
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

    // The Map is verified. It is now safe to manipulate the map.
    protected void setMarkersListener(GoogleMap googleMap) {
        if (googleMap != null) {
            // Attach marker click listener to the map here

            /*
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                public boolean onMarkerClick(Marker marker) {

                    // Handle marker click here
                    //marker.showInfoWindow();
                    LatLng storeLocation = marker.getPosition();
                    getDirection(storeLocation);

                    return false;
                }

            });

            */


        }
    }

    String getLatLngString(LatLng latLng)
    {
        if (latLng != null)
            return String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
        else
            return null;
    }




    /*
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
    */

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    */

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    /*
    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();
    }
    */
}
