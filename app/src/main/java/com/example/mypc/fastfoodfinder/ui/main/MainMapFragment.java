package com.example.mypc.fastfoodfinder.ui.main;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.adapter.MainPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMapFragment extends Fragment {



    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    MainPagerAdapter mPagerAdapter;

    public MainMapFragment() {
        // Required empty public constructor
    }

    public static MainMapFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MainMapFragment fragment = new MainMapFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_main_map, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPagerAdapter = new MainPagerAdapter(getChildFragmentManager(), getContext());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager(mViewPager);


        for (int i = 0; i < mTabLayout.getTabCount(); i++)
        {
            mTabLayout.getTabAt(i).setText(null);
            mTabLayout.getTabAt(i).setIcon(mPagerAdapter.getIcon(i));
        }

        mTabLayout.getTabAt(0).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);


        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabItemColor = Color.WHITE;
                /*
                View itemView = tab.getCustomView();
                TextView textView = (TextView) itemView.findViewById(R.id.tv_tablayout);
                ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_tablayout);

                textView.setTextColor(tabItemColor);
                imageView.getDrawable().setColorFilter(tabItemColor, PorterDuff.Mode.SRC_IN);
                */
                tab.getIcon().setColorFilter(tabItemColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabItemColor = ContextCompat.getColor(getContext(),R.color.colorYouTubeDark);

                /*
                View itemView = tab.getCustomView();
                TextView textView = (TextView) itemView.findViewById(R.id.tv_tablayout);
                ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_tablayout);

                textView.setTextColor(tabItemColor);
                imageView.getDrawable().setColorFilter(tabItemColor, PorterDuff.Mode.SRC_IN);
                */

                tab.getIcon().setColorFilter(tabItemColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
}
