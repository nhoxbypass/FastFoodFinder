package com.iceteaviet.fastfoodfinder.ui.main;

import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.ui.main.favourite.MainFavouriteFragment;
import com.iceteaviet.fastfoodfinder.ui.main.map.MainMapFragment;
import com.iceteaviet.fastfoodfinder.ui.main.recently.MainRecentlyFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by Genius Doan on 11/8/2016.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 3;
    private String pageTitles[] = new String[]{String.valueOf(R.string.map), String.valueOf(R.string.recently), String.valueOf(R.string.favourite)};
    private int[] imageResId = {R.drawable.ic_main_map, R.drawable.ic_main_clock_red, R.drawable.ic_main_star_red};

    MainPagerAdapter(FragmentManager fm) {
        super(fm);
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
            default:
                return null;
        }
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
