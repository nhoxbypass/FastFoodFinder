package com.iceteaviet.fastfoodfinder.ui.store;

import android.view.View;
import android.widget.FrameLayout;

import com.iceteaviet.fastfoodfinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by binhlt on 30/11/2016.
 */

public class CallDirectionViewHolder {

    @BindView(R.id.call)
    public FrameLayout btnCall;

    @BindView(R.id.direction)
    public FrameLayout btnDirection;

    public CallDirectionViewHolder(View itemView) {
        ButterKnife.bind(this, itemView);
    }
}
