package com.example.mypc.fastfoodfinder.ui.main;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * Created by MyPC on 11/17/2016.
 */
public class Fragment extends ListFragment {

    public interface OnListItemClickListener {
        void onListItemClick(int position);
    }

    private OnListItemClickListener mItemClickListener;

    public Fragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mItemClickListener = (OnListItemClickListener) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mItemClickListener.onListItemClick(position);
    }
}
