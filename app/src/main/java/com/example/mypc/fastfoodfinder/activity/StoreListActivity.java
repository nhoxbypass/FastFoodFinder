package com.example.mypc.fastfoodfinder.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.adapter.StoreListAdapter;
import com.example.mypc.fastfoodfinder.helper.DividerItemDecoration;
import com.example.mypc.fastfoodfinder.model.Store.Store;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MyPC on 11/30/2016.
 */
public class StoreListActivity extends AppCompatActivity {

    LinearLayoutManager layoutManager;
    StoreListAdapter adapter;
    @BindView(R.id.rvListStore)
    RecyclerView rvStoreList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        ButterKnife.bind(this);
        adapter = new StoreListAdapter();
        layoutManager = new LinearLayoutManager(getApplicationContext());
        rvStoreList.setLayoutManager(layoutManager);
        rvStoreList.setAdapter(adapter);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        rvStoreList.addItemDecoration(decoration);
        Load(); 
    }


    void Load(){
        ArrayList<Store> stores = new ArrayList<>();
        stores.add(new Store("FamilyMart - 123 Nguyễn Đình Chiểu","10.7775462","106.6892408999999","3835 3193",0));
        stores.add(new Store("Family Mart - Tạ Quang Bửu", "10.736488", "106.670374", "3835 3193",0));
        stores.add(new Store("Family Mart - Nguyễn Văn Công", "10.819417", "106.674821", "3835 3193",0));
        stores.add(new Store("FamilyMart - Hậu Giang","10.7457782220847","106.6261117905378", "3755 0439",0));
        stores.add(new Store("FamilyMart - Nguyễn Lương Bằng","10.727042", "106.722703", "5417 3390",0));
        stores.add(new Store("FamilyMart - Tôn Dật Tiến","10.723322", "106.71498", "3835 3193",0));
        adapter.setDesS(stores);
    }
}
