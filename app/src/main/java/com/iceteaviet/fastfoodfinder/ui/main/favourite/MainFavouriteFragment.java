package com.iceteaviet.fastfoodfinder.ui.main.favourite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.iceteaviet.fastfoodfinder.App;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.DataManager;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreEvent;
import com.iceteaviet.fastfoodfinder.ui.main.OnStartDragListener;
import com.iceteaviet.fastfoodfinder.ui.main.SimpleItemTouchHelperCallback;
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created by MyPC on 11/16/2016.
 */
public class MainFavouriteFragment extends Fragment implements OnStartDragListener {
    static boolean isFABChangeClicked = false;
    @BindView(R.id.rv_favourite_stores)
    RecyclerView recyclerView;
    @BindView(R.id.fl_container)
    FrameLayout containerLayout;
    @BindView(R.id.fab_change)
    FloatingActionButton fabChangePosition;

    private FavouriteStoreAdapter mFavouriteAdapter;
    private ItemTouchHelper mItemTouchHelper;

    private DataManager dataManager;

    public static MainFavouriteFragment newInstance() {
        Bundle args = new Bundle();
        MainFavouriteFragment fragment = new MainFavouriteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataManager = App.getDataManager();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(mFavouriteAdapter);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);

        rv.addItemDecoration(decoration);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mFavouriteAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rv);
    }


    private void loadData() {
        if (User.currentUser != null) {
            dataManager.getLocalStoreDataSource()
                    .findStoresByIds(User.currentUser.getFavouriteStoreList().getStoreIdList())
                    .subscribe(new SingleObserver<List<Store>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(List<Store> storeList) {
                            mFavouriteAdapter.setStores(storeList);
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });


            dataManager.getUserDataSource().subscribeFavouriteStoresOfUser(User.currentUser.getUid())
                    .subscribe(new Observer<UserStoreEvent>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(UserStoreEvent userStoreEvent) {
                            Store store = userStoreEvent.getStore();
                            switch (userStoreEvent.getEventActionCode()) {
                                case UserStoreEvent.ACTION_ADDED:
                                    if (!User.currentUser.getFavouriteStoreList().getStoreIdList().contains(store.getId())) {
                                        mFavouriteAdapter.addStore(store);
                                        User.currentUser.getFavouriteStoreList().getStoreIdList().add(store.getId());
                                    }
                                    break;

                                case UserStoreEvent.ACTION_CHANGED:
                                    if (User.currentUser.getFavouriteStoreList().getStoreIdList().contains(store.getId())) {
                                        mFavouriteAdapter.updateStore(store);
                                    }
                                    break;

                                case UserStoreEvent.ACTION_REMOVED:
                                    if (User.currentUser.getFavouriteStoreList().getStoreIdList().contains(store.getId())) {
                                        mFavouriteAdapter.removeStore(store);
                                        User.currentUser.getFavouriteStoreList().removeStore(store.getId());
                                    }
                                    break;

                                case UserStoreEvent.ACTION_MOVED:
                                    break;

                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

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
