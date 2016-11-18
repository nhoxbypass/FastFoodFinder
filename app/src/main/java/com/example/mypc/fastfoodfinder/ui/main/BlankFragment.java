package com.example.mypc.fastfoodfinder.ui.main;


import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.example.mypc.fastfoodfinder.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {


    ViewGroup viewRoot;
    ViewGroup viewGroup;
    boolean visible;
    ImageView loadMoreButton;

    public BlankFragment() {
        // Required empty public constructor
    }

    public static BlankFragment newInstance() {
        
        Bundle args = new Bundle();
        
        BlankFragment fragment = new BlankFragment();
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
        View root = inflater.inflate(R.layout.fragment_blank, container, false);

        viewRoot = (ViewGroup) root.findViewById(R.id.action_container);
        loadMoreButton = (ImageView) root.findViewById(R.id.load_more);
        viewGroup = (ViewGroup)root.findViewById(R.id.container);

        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(viewRoot);
                    visible = !visible;
                    viewGroup.setVisibility(visible ? View.VISIBLE : View.GONE);
                }

            }
        });



        return root;
    }

}
