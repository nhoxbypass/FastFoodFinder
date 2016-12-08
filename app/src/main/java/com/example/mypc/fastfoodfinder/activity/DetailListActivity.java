package com.example.mypc.fastfoodfinder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.adapter.StoreListAdapter;
import com.example.mypc.fastfoodfinder.helper.DividerItemDecoration;
import com.example.mypc.fastfoodfinder.model.Store.Store;
import com.example.mypc.fastfoodfinder.model.Store.UserStoreList;
import com.example.mypc.fastfoodfinder.ui.profile.ProfileFragment;
import com.example.mypc.fastfoodfinder.utils.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MyPC on 12/6/2016.
 */
public class DetailListActivity extends AppCompatActivity {

    @BindView(R.id.rvList)   RecyclerView rvStoreList;
    @BindView(R.id.iconList)   CircleImageView cvIconList;
    @BindView(R.id.tvListName)   TextView tvListName;
    @BindView(R.id.tvNumberPlace) TextView tvNumberPlace;
    @BindView(R.id.cvAvatar) CircleImageView avatar;
    StoreListAdapter mAdapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_list);
        ButterKnife.bind(this);
        mAdapter = new StoreListAdapter();
        layoutManager = new LinearLayoutManager(getApplicationContext());
        rvStoreList.setAdapter(mAdapter);
        rvStoreList.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        rvStoreList.addItemDecoration(decoration);
        getData();
    }

    void getData(){
        Intent intent = getIntent();

        //list name
        tvListName.setText(intent.getStringExtra(ProfileFragment.KEY_NAME));
        //icon of list
        cvIconList.setImageResource(intent.getIntExtra(ProfileFragment.KEY_IDICON,0));

        Glide.with(getApplicationContext())
                .load(intent.getStringExtra(ProfileFragment.KEY_URL))
                .into(avatar);
        //id of list
        int id = intent.getIntExtra(ProfileFragment.KEY_ID,0);
        //list id of store
        ArrayList<Integer> list = intent.getIntegerArrayListExtra(ProfileFragment.KEY_STORE);

        int numberPlace = list.size();

        //add list store to mAdapter here

    }
}
