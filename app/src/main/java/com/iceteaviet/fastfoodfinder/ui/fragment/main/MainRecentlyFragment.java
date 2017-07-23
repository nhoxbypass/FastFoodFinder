package com.iceteaviet.fastfoodfinder.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.activity.store.StoreDetailActivity;
import com.iceteaviet.fastfoodfinder.adapter.RecentlyStoreAdapter;
import com.iceteaviet.fastfoodfinder.interfaces.OnStartDragListener;
import com.iceteaviet.fastfoodfinder.model.store.Store;
import com.iceteaviet.fastfoodfinder.ui.DividerItemDecoration;
import com.iceteaviet.fastfoodfinder.ui.SimpleItemTouchHelperCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MyPC on 11/20/2016.
 */
public class MainRecentlyFragment extends Fragment implements OnStartDragListener {
    private static boolean isFABChangeClicked = false;
    @BindView(R.id.rv_recently_stores)
    RecyclerView recyclerView;
    @BindView(R.id.fl_container)
    FrameLayout containerLayout;
    private RecentlyStoreAdapter mRecentlyAdapter;
    private LinearLayoutManager mLayoutManager;
    private ItemTouchHelper mItemTouchHelper;

    public MainRecentlyFragment() {
    }

    public static MainRecentlyFragment newInstance() {
        Bundle args = new Bundle();
        MainRecentlyFragment fragment = new MainRecentlyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_recently, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupRecyclerView(recyclerView);
        loadData();
    }

    private void setupRecyclerView(RecyclerView rv) {
        mRecentlyAdapter = new RecentlyStoreAdapter(this, containerLayout);

        mRecentlyAdapter.setOnItemClickListener(new RecentlyStoreAdapter.OnItemClickListener() {
            @Override
            public void onClick(Store store) {
                getActivity().startActivity(StoreDetailActivity.getIntent(getContext(), store));
            }
        });

        mLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(mRecentlyAdapter);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);

        rv.addItemDecoration(decoration);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mRecentlyAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rv);
    }


    private void loadData() {
        ArrayList<Store> stores = new ArrayList<>();
        //TODO: Load recently store from Realm
        mRecentlyAdapter.setStores(stores);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (isFABChangeClicked)
            mItemTouchHelper.startDrag(viewHolder);
    }
}
