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
        tvListName.setText(intent.getStringExtra(ProfileFragment.KEY_NAME));
        cvIconList.setImageResource(intent.getIntExtra(ProfileFragment.KEY_ID,R.drawable.ic_new_list));
        Glide.with(getApplicationContext())
                .load(intent.getStringExtra(ProfileFragment.KEY_URL))
                .into(avatar);
        int Num = intent.getIntExtra(ProfileFragment.KEY_NUMBER_PLACES,0);
        if (Num == 0){
            tvNumberPlace.setText("0 Place");
        }
        else {
            tvNumberPlace.setText(String.valueOf(Num)+" Places");
            ArrayList<Store> stores = new ArrayList<>();
            stores.add(new Store("Circle K Ly Tu Trong","238 Ly Tu Trong, Ben Thanh Ward, District 1, Ho Chi Minh, Vietnam","10.7721924", "106.69433409999999", "3822 7403", Constant.TYPE_CIRCLE_K));
            stores.add(new Store("Familymart - Đường D2", "39 Đường D2, P. 25, Quận Bình Thạnh, TP. HCM","10.80252", "106.715622", "35 126 283", Constant.TYPE_FAMILY_MART));
            mAdapter.setDesS(stores);
        }
    }
}
