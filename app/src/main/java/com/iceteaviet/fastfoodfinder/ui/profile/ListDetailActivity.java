package com.iceteaviet.fastfoodfinder.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iceteaviet.fastfoodfinder.App;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.DataManager;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList;
import com.iceteaviet.fastfoodfinder.ui.storelist.StoreListAdapter;
import com.iceteaviet.fastfoodfinder.utils.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created by MyPC on 12/6/2016.
 */
public class ListDetailActivity extends AppCompatActivity {

    @BindView(R.id.rvList)
    RecyclerView rvStoreList;
    @BindView(R.id.iconList)
    CircleImageView cvIconList;
    @BindView(R.id.tvListName)
    TextView tvListName;
    @BindView(R.id.tvNumberPlace)
    TextView tvNumberPlace;
    @BindView(R.id.cvAvatar)
    CircleImageView avatar;

    private StoreListAdapter mAdapter;

    private DataManager dataManager;
    private UserStoreList userStoreList;
    private String photoUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);
        ButterKnife.bind(this);

        dataManager = App.getDataManager();

        userStoreList = loadData(getIntent());
        setupUI();
    }

    private void setupUI() {
        mAdapter = new StoreListAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvStoreList.setAdapter(mAdapter);
        rvStoreList.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        rvStoreList.addItemDecoration(decoration);

        tvListName.setText(userStoreList.getListName());
        cvIconList.setImageResource(userStoreList.getIconId());
        Glide.with(getApplicationContext())
                .load(photoUrl)
                .into(avatar);
    }

    private UserStoreList loadData(Intent intent) {
        UserStoreList userStoreList = intent.getParcelableExtra(Constant.KEY_USER_STORE_LIST);
        photoUrl = intent.getStringExtra(Constant.KEY_USER_PHOTO_URL);

        //add list store to mAdapter here
        dataManager.getLocalStoreDataSource().findStoresByIds(userStoreList.getStoreIdList())
                .subscribe(new SingleObserver<List<Store>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Store> storeList) {
                        mAdapter.setStores(storeList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });

        return userStoreList;
    }
}
