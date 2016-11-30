package com.example.mypc.fastfoodfinder.ui.main;

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
public class FavouritedLocationFragment extends Fragment implements OnStartDragListener {
    private RecyclerView rvDes;
    private FavouriteLocationAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private FloatingActionButton fbChangePosition;
    static boolean isFABChangeClicked = false;
    FrameLayout flLayout;
    public FavouritedLocationFragment() {
    }
;
    public static FavouritedLocationFragment newInstance(){
        Bundle args = new Bundle();
        FavouritedLocationFragment fragment = new FavouritedLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourited_location,container,false);
        rvDes = (RecyclerView) rootView.findViewById(R.id.rvFavouriteDes);
        flLayout = (FrameLayout) rootView.findViewById(R.id.flFavParent);
        fbChangePosition = (FloatingActionButton) rootView.findViewById(R.id.fbFavoriteChange);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        adapter = new FavouriteLocationAdapter(this, flLayout);
        linearLayoutManager = new LinearLayoutManager(getContext()) ;
        rvDes.setLayoutManager(linearLayoutManager);
        rvDes.setAdapter(adapter);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);

        rvDes.addItemDecoration(decoration);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvDes);
        //client = TwitterApplication.getRestClient();
        fbChangePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (isFABChangeClicked)
                {
                    isFABChangeClicked = false;
                    fbChangePosition.setImageResource(R.drawable.ic_swap_black);
                }

                else {
                    isFABChangeClicked = true;
                    fbChangePosition.setImageResource(R.drawable.ic_swap);
                }
            }
        });

        Load();
    }


    private void Load(){

        ArrayList<Article> articles = new ArrayList<>();
        articles.add(new Article("Circle K Tu nhien","227 Nguyen Van Cu. D5"));
        articles.add(new Article("Family mart Vung Tau","32 Tran Phu, Vung Tau"));
        articles.add(new Article("Circle K DH Su Pham","An Duong Vuong, D5"));
        articles.add(new Article("Circle K DH Bach Khoa","H1, DH Bach Khoa TpHcm"));
        adapter.setDesS(articles);
    }
    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (isFABChangeClicked)
        mItemTouchHelper.startDrag(viewHolder);

    }
}
