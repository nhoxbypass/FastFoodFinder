package com.iceteaviet.fastfoodfinder.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.model.Store.Store;
import com.iceteaviet.fastfoodfinder.utils.MapUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MyPC on 11/30/2016.
 */
public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreListViewHolder> {

    List<Store> mStoreList;

    public StoreListAdapter() {
        mStoreList = new ArrayList<>();
    }

    @Override
    public StoreListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_store, parent, false);
        return new StoreListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StoreListViewHolder holder, int position) {
        Store store = mStoreList.get(position);
        holder.setData(store);
    }

    public void setDesS(List<Store> stores) {
        mStoreList.clear();
        mStoreList.addAll(stores);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mStoreList.size();
    }

    class StoreListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivStoreImage)
        ImageView ivStoreImage;
        @BindView(R.id.tv_item_title)
        TextView tvStoreName;
        @BindView(R.id.tv_item_address)
        TextView tvStoreAddress;
        @BindView(R.id.tv_item_tel)
        TextView tvTel;
        @BindView(R.id.btnRate)
        Button btnRate;

        public StoreListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(Store store) {
            tvStoreName.setText(store.getTitle());
            tvStoreAddress.setText(store.getAddress());
            tvTel.setText(store.getTel());
            Glide.with(ivStoreImage.getContext())
                    .load(MapUtils.getLogoDrawableId(store.getType()))
                    .into(ivStoreImage);
        }
    }
}
