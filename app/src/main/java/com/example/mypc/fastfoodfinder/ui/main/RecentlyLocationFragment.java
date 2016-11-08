package com.example.mypc.fastfoodfinder.ui.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mypc.fastfoodfinder.R;


public class RecentlyLocationFragment extends Fragment {
    public RecentlyLocationFragment() {
        // Required empty public constructor
    }

    public static RecentlyLocationFragment newInstance() {

        Bundle args = new Bundle();

        RecentlyLocationFragment fragment = new RecentlyLocationFragment();
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
        return inflater.inflate(R.layout.fragment_recently_location, container, false);
    }
}
