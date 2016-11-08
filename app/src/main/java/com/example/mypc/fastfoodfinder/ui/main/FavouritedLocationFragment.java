package com.example.mypc.fastfoodfinder.ui.main;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mypc.fastfoodfinder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritedLocationFragment extends Fragment {


    public FavouritedLocationFragment() {
        // Required empty public constructor
    }

    public static FavouritedLocationFragment newInstance() {
        
        Bundle args = new Bundle();
        
        FavouritedLocationFragment fragment = new FavouritedLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourited_location, container, false);
    }

}
