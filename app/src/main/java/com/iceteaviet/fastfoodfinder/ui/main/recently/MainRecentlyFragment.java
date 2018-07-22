package com.iceteaviet.fastfoodfinder.ui.main.recently;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.ui.main.OnStartDragListener;
import com.iceteaviet.fastfoodfinder.ui.main.SimpleItemTouchHelperCallback;
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MyPC on 11/20/2016.
 */
public class MainRecentlyFragment extends Fragment implements OnStartDragListener {
    @BindView(R.id.rv_recently_stores)
    RecyclerView recyclerView;
    @BindView(R.id.fl_container)
    FrameLayout containerLayout;

    private RecentlyStoreAdapter mRecentlyAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private boolean isFABChangeClicked = false;


    public static MainRecentlyFragment newInstance() {
        Bundle args = new Bundle();
        MainRecentlyFragment fragment = new MainRecentlyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_recently, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(mRecentlyAdapter);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);

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
