package com.iceteaviet.fastfoodfinder.ui.storelist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.utils.DataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MyPC on 11/30/2016.
 */
public class StoreListActivity extends AppCompatActivity {

    @BindView(R.id.rv_top_list)
    RecyclerView recyclerView;

    private StoreListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        ButterKnife.bind(this);

        adapter = new StoreListAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        loadData();
    }


    void loadData() {
        adapter.setStores(DataUtils.getFakeStoreList());
    }
}
