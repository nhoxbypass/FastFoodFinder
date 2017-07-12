package com.iceteaviet.fastfoodfinder.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iceteaviet.fastfoodfinder.ui.main.MainFavouritedFragment;
import com.iceteaviet.fastfoodfinder.ui.main.MainMapFragment;
import com.iceteaviet.fastfoodfinder.ui.main.MainRecentlyFragment;
import com.iceteaviet.fastfoodfinder.R;

/**
 * Created by nhoxb on 11/8/2016.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String pageTitles[] = new String[] { String.valueOf(R.string.map), String.valueOf(R.string.recently), String.valueOf(R.string.favourite)};
    private int[] imageResId = { R.drawable.ic_main_map, R.drawable.ic_main_clock_red, R.drawable.ic_main_star_red};

    private Context mContext;


    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return MainMapFragment.newInstance();
            case 1:
                return MainRecentlyFragment.newInstance();
            case 2:
                return MainFavouritedFragment.newInstance();

        }

        return  null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return  pageTitles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public int getIcon(int position)
    {
        return imageResId[position];
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(mContext).inflate(R.layout.view_tablayout, null);
        TextView tv = (TextView) v.findViewById(R.id.tv_tablayout);
        tv.setText(pageTitles[position]);
        ImageView img = (ImageView) v.findViewById(R.id.iv_tablayout);
        img.setImageResource(imageResId[position]);
        return v;
    }
}
