package com.example.mypc.fastfoodfinder.ui.store;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.activity.StoreDetailActivity;
import com.example.mypc.fastfoodfinder.model.Store.Store;
import com.example.mypc.fastfoodfinder.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by taq on 26/11/2016.
 */

public class StoreInfoDialogFragment extends DialogFragment {

    public static StoreInfoDialogFragment newInstance(Store store) {
        Bundle args = new Bundle();
        args.putParcelable(StoreDetailActivity.STORE, store);
        StoreInfoDialogFragment fragment = new StoreInfoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.store_name)
    TextView tvStoreName;
    @BindView(R.id.view_detail)
    TextView tvViewDetail;
    @BindView(R.id.store_address)
    TextView tvStoreAddress;
    @BindView(R.id.call_direction)
    View vCallDirection;
    @BindView(R.id.save_this)
    Button btnAddToFavorite;

    private StoreDialogActionListener mListener;
    public CallDirectionViewHolder cdvh;

    // tên chuối thiệt
    public interface StoreDialogActionListener {
        void onDirection(Store store);
        void onAddToFavorite(int storeId);
    }

    public StoreInfoDialogFragment() {

    }

    public void setDialogListen(StoreDialogActionListener listener)
    {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_info, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        cdvh = new CallDirectionViewHolder(vCallDirection);

        final Store store =  getArguments().getParcelable(StoreDetailActivity.STORE);

        tvStoreName.setText(store.getTitle());
        tvStoreAddress.setText(store.getAddress());
        tvViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(StoreDetailActivity.getIntent(getContext(), store));
            }
        });

        cdvh.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (store.getTel() != null && !store.getTel().equals("")) {
                    startActivity(DisplayUtils.getCallIntent(store.getTel()));
                } else {
                    Toast.makeText(getActivity(), "The ic_store doesn't have number phone!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cdvh.btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDirection(store);
            }
        });

        btnAddToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAddToFavorite(store.getId());
                dismiss();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int)(0.8 * size.x), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }
}
