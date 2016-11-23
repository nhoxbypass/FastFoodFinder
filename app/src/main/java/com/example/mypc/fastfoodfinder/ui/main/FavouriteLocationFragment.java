package com.example.mypc.fastfoodfinder.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mypc.fastfoodfinder.helper.DividerItemDecoration;
import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.adapter.FavouriteLocationAdapter;
import com.example.mypc.fastfoodfinder.helper.OnStartDragListener;
import com.example.mypc.fastfoodfinder.helper.SimpleItemTouchHelperCallback;
import com.example.mypc.fastfoodfinder.model.Article;

import java.util.ArrayList;

/**
 * Created by MyPC on 11/16/2016.
 */
public class FavouriteLocationFragment extends Fragment implements OnStartDragListener {
    private RecyclerView rvDes;
    private FavouriteLocationAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    public FavouriteLocationFragment() {
    }
;
    public static FavouriteLocationFragment newInstance(){
        Bundle args = new Bundle();
        FavouriteLocationFragment fragment = new FavouriteLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourited_location,container,false);
        rvDes = (RecyclerView) rootView.findViewById(R.id.rvFavouriteDes);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        adapter = new FavouriteLocationAdapter(this);
        linearLayoutManager = new LinearLayoutManager(getContext()) ;
        rvDes.setLayoutManager(linearLayoutManager);
        rvDes.setAdapter(adapter);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);

        rvDes.addItemDecoration(decoration);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvDes);
        //client = TwitterApplication.getRestClient();
        Load();
    }


    private void Load(){

        ArrayList<Article> articles = new ArrayList<>();
        articles.add(new Article("Circle K Nguyen van cu","1A"));
        articles.add(new Article("Nha","ho thi ky"));
        articles.add(new Article("tam","quan 8"));
        articles.add(new Article("some place","I don't know"));
        articles.add(new Article("hihi","haha"));
        adapter.setDesS(articles);
    }
    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
