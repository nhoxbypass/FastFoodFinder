package com.iceteaviet.fastfoodfinder.activity.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.adapter.StoreListAdapter;
import com.iceteaviet.fastfoodfinder.model.store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.ui.DividerItemDecoration;
import com.iceteaviet.fastfoodfinder.utils.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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
    StoreListAdapter mAdapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);
        ButterKnife.bind(this);
        mAdapter = new StoreListAdapter();
        layoutManager = new LinearLayoutManager(getApplicationContext());
        rvStoreList.setAdapter(mAdapter);
        rvStoreList.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        rvStoreList.addItemDecoration(decoration);
        getData();
    }

    void getData() {
        Intent intent = getIntent();

        //list name
        tvListName.setText(intent.getStringExtra(Constant.KEY_NAME));
        //icon of list
        cvIconList.setImageResource(intent.getIntExtra(Constant.KEY_IDICON, 0));

        Glide.with(getApplicationContext())
                .load(intent.getStringExtra(Constant.KEY_URL))
                .into(avatar);
        //id of list
        int id = intent.getIntExtra(Constant.KEY_ID, 0);
        //list id of store
        ArrayList<Integer> list = intent.getIntegerArrayListExtra(Constant.KEY_STORE);

        int numberPlace = list.size();

        //add list store to mAdapter here
        mAdapter.setStores(StoreDataSource.getStoresById(list));
    }
}
