package com.example.mypc.fastfoodfinder.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mypc.fastfoodfinder.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mypc.fastfoodfinder.R.id.map;

/**
 * Created by taq on 18/11/2016.
 */

public class StoreDetailActivity extends AppCompatActivity {

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.backdrop)
    ImageView backdrop;

    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, StoreDetailActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String storeName = "Vin Mart";
        collapsingToolbar.setTitle(storeName);

        Glide.with(this)
                .load(R.drawable.circle_k_cover)
                .centerCrop()
                .into(backdrop);

        setUpMapIfNeeded();
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
}
