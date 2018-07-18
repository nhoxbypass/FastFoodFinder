package com.iceteaviet.fastfoodfinder.ui.settings;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.ui.custom.HorizontalFlowLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by taq on 8/12/2016.
 */

public class StoreFilterDialogFragment extends DialogFragment {

    private static final String KEY_CIRCLE_K = "circle_k";
    private static final String KEY_MINI_STOP = "mini_stop";
    private static final String KEY_FAMILY_MART = "family_mark";
    private static final String KEY_BSMART = "bsmart";
    private static final String KEY_SHOP_N_GO = "shop_n_go";
    private static final String[] LIST_STORES = new String[]{KEY_BSMART, KEY_CIRCLE_K, KEY_FAMILY_MART, KEY_MINI_STOP, KEY_SHOP_N_GO};

    @BindView(R.id.tag_container)
    HorizontalFlowLayout tagContainer;
    @BindView(R.id.btnDone)
    Button btnDone;
    @BindView(R.id.btnCancel)
    Button btnCancel;

    public static StoreFilterDialogFragment newInstance() {
        Bundle args = new Bundle();

        StoreFilterDialogFragment fragment = new StoreFilterDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_store_filter, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        getDialog().setTitle(R.string.subscription);
        setupTagContainer();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimationUpDown;
    }

    private void setupTagContainer() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (String key : LIST_STORES) {
            View view = inflater.inflate(R.layout.view_store_tag, tagContainer, false);
            TagViewHolder holder = new TagViewHolder(view);
            holder.setName(getStoreName(key));
            tagContainer.addView(view);
        }
    }

    private String getStoreName(String key) {
        switch (key) {
            case KEY_CIRCLE_K:
                return "Cirle K";
            case KEY_BSMART:
                return "B’s mart";
            case KEY_FAMILY_MART:
                return "Family mart";
            case KEY_MINI_STOP:
                return "Ministop";
            case KEY_SHOP_N_GO:
                return "Shop & Go";
            default:
                return "Unknown";
        }
    }

    public class TagViewHolder {

        View itemView;
        @BindView(R.id.tag)
        TextView tvTag;

        public TagViewHolder(View view) {
            itemView = view;
            ButterKnife.bind(this, view);
            tvTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animator animator = AnimatorInflater.loadAnimator(getContext(), R.animator.zoom_in_out);
                    animator.setTarget(itemView);
                    animator.setDuration(100);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    animator.start();
                    v.setSelected(!v.isSelected());
                }
            });
        }

        public void setName(String name) {
            tvTag.setText(name);
        }
    }
}
