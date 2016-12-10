package com.example.mypc.fastfoodfinder.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.adapter.StoreDetailAdapter;
import com.example.mypc.fastfoodfinder.model.Comment;
import com.example.mypc.fastfoodfinder.model.store.Store;
import com.example.mypc.fastfoodfinder.utils.DisplayUtils;
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

public class StoreDetailActivity extends AppCompatActivity implements StoreDetailAdapter.StoreActionListener {

    public static final String STORE = "ic_store";
    public static final int REQUEST_COMMENT = 113;
    public static final String COMMENT = "comment";

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

    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private StoreDetailAdapter mStoreDetailAdapter;

    public static Intent getIntent(Context context, Store store) {
        Intent intent = new Intent(context, StoreDetailActivity.class);
        intent.putExtra(STORE, store);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        ButterKnife.bind(this);

        Store store = getIntent().getParcelableExtra(STORE);
        mStoreDetailAdapter = new StoreDetailAdapter(store);
        mStoreDetailAdapter.setListener(this);
        rvContent.setAdapter(mStoreDetailAdapter);
        rvContent.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar.setTitle(store.getTitle());

        Glide.with(this)
                .load(R.drawable.detail_sample_circlekcover)
                .centerCrop()
                .into(ivBackdrop);

        //setUpMapIfNeeded();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_COMMENT) {
            Comment comment = (Comment) data.getSerializableExtra(COMMENT);
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
        Toast.makeText(this, "direction", Toast.LENGTH_SHORT).show();
        //TODO gọi hàm chỉ đường
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
}
