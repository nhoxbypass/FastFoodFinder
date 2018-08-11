package com.iceteaviet.fastfoodfinder.ui.main.favourite;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iceteaviet.fastfoodfinder.App;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.DataManager;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreEvent;
import com.iceteaviet.fastfoodfinder.ui.main.OnStartDragListener;
import com.iceteaviet.fastfoodfinder.ui.main.SimpleItemTouchHelperCallback;
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * Created by MyPC on 11/16/2016.
 */
public class MainFavouriteFragment extends Fragment implements OnStartDragListener {
    @BindView(R.id.rv_favourite_stores)
    RecyclerView recyclerView;
    @BindView(R.id.fl_container)
    FrameLayout containerLayout;
    @BindView(R.id.fab_change)
    FloatingActionButton fabChangePosition;

    private boolean isFABChangeClicked = false;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL);

        rv.addItemDecoration(decoration);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mFavouriteAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rv);
    }


    private void loadData() {
        if (dataManager.getCurrentUser() != null) {
            dataManager.getLocalStoreDataSource()
                    .findStoresByIds(dataManager.getCurrentUser().getFavouriteStoreList().getStoreIdList())
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


            dataManager.getRemoteUserDataSource().subscribeFavouriteStoresOfUser(dataManager.getCurrentUserUid())
                    .map(new Function<Pair<Integer, Integer>, UserStoreEvent>() {
                        @Override
                        public UserStoreEvent apply(Pair<Integer, Integer> storeIdPair) throws Exception {
                            Store store = dataManager.getLocalStoreDataSource().findStoresById(storeIdPair.first).blockingGet().get(0);
                            return new UserStoreEvent(store, storeIdPair.second);
                        }
                    })
                    .subscribe(new Observer<UserStoreEvent>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(UserStoreEvent userStoreEvent) {
                            Store store = userStoreEvent.getStore();
                            switch (userStoreEvent.getEventActionCode()) {
                                case UserStoreEvent.ACTION_ADDED:
                                    if (!dataManager.getCurrentUser().getFavouriteStoreList().getStoreIdList().contains(store.getId())) {
                                        mFavouriteAdapter.addStore(store);
                                        dataManager.getCurrentUser().getFavouriteStoreList().getStoreIdList().add(store.getId());
                                    }
                                    break;

                                case UserStoreEvent.ACTION_CHANGED:
                                    if (dataManager.getCurrentUser().getFavouriteStoreList().getStoreIdList().contains(store.getId())) {
                                        mFavouriteAdapter.updateStore(store);
                                    }
                                    break;

                                case UserStoreEvent.ACTION_REMOVED:
                                    if (dataManager.getCurrentUser().getFavouriteStoreList().getStoreIdList().contains(store.getId())) {
                                        mFavouriteAdapter.removeStore(store);
                                        dataManager.getCurrentUser().getFavouriteStoreList().removeStore(store.getId());
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
