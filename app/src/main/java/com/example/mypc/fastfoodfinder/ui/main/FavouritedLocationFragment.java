package com.example.mypc.fastfoodfinder.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.adapter.FavouriteLocationAdapter;
import com.example.mypc.fastfoodfinder.helper.DividerItemDecoration;
import com.example.mypc.fastfoodfinder.helper.OnStartDragListener;
import com.example.mypc.fastfoodfinder.helper.SimpleItemTouchHelperCallback;
import com.example.mypc.fastfoodfinder.model.Store.Store;
import com.example.mypc.fastfoodfinder.utils.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MyPC on 11/16/2016.
 */
public class FavouritedLocationFragment extends Fragment implements OnStartDragListener {
    static boolean isFABChangeClicked = false;
    @BindView(R.id.rv_favourite_stores) RecyclerView recyclerView;
    @BindView(R.id.fl_container) FrameLayout frameLayout;
    @BindView(R.id.fab_change) FloatingActionButton fabChangePosition;

    private LinearLayoutManager mLayoutManager;
    private FavouriteLocationAdapter mFavouriteAdapter;
    private ItemTouchHelper mItemTouchHelper;

    public FavouritedLocationFragment() {
    }

    ;

    public static FavouritedLocationFragment newInstance() {
        Bundle args = new Bundle();
        FavouritedLocationFragment fragment = new FavouritedLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourited_location, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupRecyclerView(recyclerView);
        //client = TwitterApplication.getRestClient();
        fabChangePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFABChangeClicked) {
                    isFABChangeClicked = false;
                    fabChangePosition.setImageResource(R.drawable.ic_swap_black);
                } else {
                    isFABChangeClicked = true;
                    fabChangePosition.setImageResource(R.drawable.ic_swap);
                }
            }
        });

        loadData();
    }

    private void setupRecyclerView(RecyclerView rv) {
        mFavouriteAdapter = new FavouriteLocationAdapter(this, frameLayout);
        mLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(mFavouriteAdapter);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);

        rv.addItemDecoration(decoration);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mFavouriteAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rv);
    }


    private void loadData() {
        ArrayList<Store> stores = new ArrayList<>();
        stores.add(new Store("Circle K Le Thi Rieng", "148 Le Thi Rieng, Ben Thanh Ward, District 1, Ho Chi Minh, Vietnam", "10.770379", "106.68912279999995", "3925 6620", Constant.TYPE_CIRCLE_K));
        stores.add(new Store("Shop & Go - Phan Đình Phùng", "180 Phan Đình Phùng, P. 2, Quận Phú Nhuận, TP. HCM", "10.7955070000000", "106.6825610000000", "38 353 193", Constant.TYPE_SHOP_N_GO));
        stores.add(new Store("Circle K Ly Tu Trong", "238 Ly Tu Trong, Ben Thanh Ward, District 1, Ho Chi Minh, Vietnam", "10.7721924", "106.69433409999999", "3822 7403", Constant.TYPE_CIRCLE_K));
        stores.add(new Store("Familymart - Đường D2", "39 Đường D2, P. 25, Quận Bình Thạnh, TP. HCM", "10.80252", "106.715622", "35 126 283", Constant.TYPE_FAMILY_MART));
        mFavouriteAdapter.setStores(stores);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (isFABChangeClicked)
            mItemTouchHelper.startDrag(viewHolder);

    }
}
