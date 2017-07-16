package com.iceteaviet.fastfoodfinder.ui.main;

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
import android.widget.Toast;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.firebase.database.DatabaseError;
import com.iceteaviet.fastfoodfinder.activity.StoreDetailActivity;
import com.iceteaviet.fastfoodfinder.helper.DividerItemDecoration;
import com.iceteaviet.fastfoodfinder.helper.OnStartDragListener;
import com.iceteaviet.fastfoodfinder.model.Store.Store;
import com.iceteaviet.fastfoodfinder.model.Store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.model.User.User;
import com.iceteaviet.fastfoodfinder.rest.FirebaseClient;
import com.iceteaviet.fastfoodfinder.utils.Constant;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.adapter.FavouriteStoreAdapter;
import com.iceteaviet.fastfoodfinder.helper.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MyPC on 11/16/2016.
 */
public class MainFavouritedFragment extends Fragment implements OnStartDragListener {
    static boolean isFABChangeClicked = false;
    @BindView(R.id.rv_favourite_stores) RecyclerView recyclerView;
    @BindView(R.id.fl_container) FrameLayout containerLayout;
    @BindView(R.id.fab_change) FloatingActionButton fabChangePosition;

    private LinearLayoutManager mLayoutManager;
    private FavouriteStoreAdapter mFavouriteAdapter;
    private ItemTouchHelper mItemTouchHelper;

    public MainFavouritedFragment() {
    };

    public static MainFavouritedFragment newInstance() {
        Bundle args = new Bundle();
        MainFavouritedFragment fragment = new MainFavouritedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_favourited, container, false);
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
                    fabChangePosition.setImageResource(R.drawable.ic_main_swap);
                } else {
                    isFABChangeClicked = true;
                    fabChangePosition.setImageResource(R.drawable.ic_main_swap_selected);
                }
            }
        });

        loadData();
    }

    private void setupRecyclerView(RecyclerView rv) {
        mFavouriteAdapter = new FavouriteStoreAdapter(this, containerLayout);

        mFavouriteAdapter.setOnItemClickListener(new FavouriteStoreAdapter.OnItemClickListener() {
            @Override
            public void onClick(Store des) {
                getActivity().startActivity(StoreDetailActivity.getIntent(getContext(), des));
            }
        });

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
        if (User.currentUser != null) {
            List<Store> stores = StoreDataSource.getStoresById(User.currentUser.getFavouriteStoreList().getStoreIdList());
            mFavouriteAdapter.setStores(stores);

            FirebaseClient.getInstance().addFavouriteStoresEventListener(User.currentUser.getUid(), new FirebaseClient.StoreValueEventListener() {
                @Override
                public void onChildAdded(Store store, String var2) {
                    if (!User.currentUser.getFavouriteStoreList().getStoreIdList().contains(store.getId())) {
                        mFavouriteAdapter.addStore(store);
                        User.currentUser.getFavouriteStoreList().getStoreIdList().add(store.getId());
                    }
                }

                @Override
                public void onChildChanged(Store store, String var2) {
                    if (User.currentUser.getFavouriteStoreList().getStoreIdList().contains(store.getId())) {
                        mFavouriteAdapter.updateStore(store);
                    }
                }

                @Override
                public void onChildRemoved(Store store) {
                    if (User.currentUser.getFavouriteStoreList().getStoreIdList().contains(store.getId())) {
                        mFavouriteAdapter.removeStore(store);
                        User.currentUser.getFavouriteStoreList().removeStore(store.getId());
                    }

                }

                @Override
                public void onChildMoved(Store store, String var2) {

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (isFABChangeClicked)
            mItemTouchHelper.startDrag(viewHolder);

    }
}
