package com.iceteaviet.fastfoodfinder.ui.main;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.transport.model.SearchEventResult;
import com.iceteaviet.fastfoodfinder.ui.storelist.StoreListActivity;
import com.iceteaviet.fastfoodfinder.utils.Constant;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    @BindView(R.id.btn_search_circle_k)
    CircleImageView quickSearchCircleK;
    @BindView(R.id.btn_search_family_mart)
    CircleImageView quickSearchFamilyMart;
    @BindView(R.id.btn_search_mini_stop)
    CircleImageView quickSearchMiniStop;
    @BindView(R.id.btn_load_more)
    CircleImageView quickSearchLoadMore;
    @BindView(R.id.btn_search_bsmart)
    CircleImageView quickSearchBsMart;
    @BindView(R.id.btn_search_shop_n_go)
    CircleImageView quickSearchShopNGo;

    @BindView(R.id.cv_action_container)
    ViewGroup cardViewQuickSearch;
    @BindView(R.id.ll_load_more_container)
    ViewGroup searchMoreLayout;
    @BindView(R.id.sv_search_container)
    ScrollView searchContainer;
    @BindView(R.id.tvTop)
    TextView tvTop;
    @BindView(R.id.tvNearest)
    TextView tvNearest;
    @BindView(R.id.tvTrending)
    TextView tvTrending;
    @BindView(R.id.tvConvenienceStore)
    TextView tvConvenienceStore;

    private boolean isLoadmoreVisible;
    private String searchString;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this, root);

        setupQuickSearchBar();

        return root;
    }

    private void setupQuickSearchBar() {
        quickSearchCircleK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchString = "Circle K";
                EventBus.getDefault().post(new SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString, Constant.TYPE_CIRCLE_K));
                searchContainer.setVisibility(View.GONE);
            }
        });

        quickSearchFamilyMart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchString = "Family Mart";
                EventBus.getDefault().post(new SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString, Constant.TYPE_FAMILY_MART));
                searchContainer.setVisibility(View.GONE);
            }
        });

        quickSearchMiniStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchString = "Mini Stop";
                EventBus.getDefault().post(new SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString, Constant.TYPE_MINI_STOP));
                searchContainer.setVisibility(View.GONE);
            }
        });

        quickSearchBsMart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchString = "BsMart";
                EventBus.getDefault().post(new SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString, Constant.TYPE_BSMART));
                searchContainer.setVisibility(View.GONE);
            }
        });

        quickSearchShopNGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchString = "Shop and Go";
                EventBus.getDefault().post(new SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString, Constant.TYPE_SHOP_N_GO));
                searchContainer.setVisibility(View.GONE);
            }
        });

        quickSearchLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(cardViewQuickSearch);
                    isLoadmoreVisible = !isLoadmoreVisible;
                    searchMoreLayout.setVisibility(isLoadmoreVisible ? View.VISIBLE : View.GONE);
                }
            }
        });

        tvTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), StoreListActivity.class);
                startActivity(intent);
            }
        });

        tvNearest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), StoreListActivity.class);
                startActivity(intent);
            }
        });
        tvTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), StoreListActivity.class);
                startActivity(intent);
            }
        });
        tvConvenienceStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), StoreListActivity.class);
                startActivity(intent);
            }
        });
    }
}
