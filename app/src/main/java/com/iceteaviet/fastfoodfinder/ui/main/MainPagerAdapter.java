package com.iceteaviet.fastfoodfinder.ui.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.ui.main.favourite.MainFavouriteFragment;
import com.iceteaviet.fastfoodfinder.ui.main.map.MainMapFragment;
import com.iceteaviet.fastfoodfinder.ui.main.recently.MainRecentlyFragment;

/**
 * Created by Genius Doan on 11/8/2016.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String pageTitles[] = new String[]{String.valueOf(R.string.map), String.valueOf(R.string.recently), String.valueOf(R.string.favourite)};
    private int[] imageResId = {R.drawable.ic_main_map, R.drawable.ic_main_clock_red, R.drawable.ic_main_star_red};

    private Context mContext;


    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MainMapFragment.newInstance();
            case 1:
                return MainRecentlyFragment.newInstance();
            case 2:
                return MainFavouriteFragment.newInstance();

        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public int getIcon(int position) {
        return imageResId[position];
    }
}
