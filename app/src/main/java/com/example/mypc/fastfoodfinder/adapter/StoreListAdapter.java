package com.example.mypc.fastfoodfinder.adapter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.model.Article;
import com.example.mypc.fastfoodfinder.model.Store.Store;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MyPC on 11/30/2016.
 */
public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreListViewHolder> {

    List<Store> mList;

    public StoreListAdapter(){
        mList = new ArrayList<>();
    }

    @Override
    public StoreListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_store,parent,false);
        return new StoreListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StoreListViewHolder holder, int position) {
            Store store = mList.get(position);
        holder.tvStoreName.setText(store.getTitle());
        holder.tvStoreAddress.setText(store.getAddress());
        holder.tvTel.setText(store.getTel());
    }

    public void setDesS(List<Store> stores){
        mList.clear();
        mList.addAll(stores);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class StoreListViewHolder extends RecyclerView.ViewHolder{

        ImageView ivStoreImage;
        TextView tvStoreName;
        TextView tvStoreAddress;
        TextView tvTel;
        Button btnRate;
        public StoreListViewHolder(View itemView) {
            super(itemView);
            ivStoreImage = (ImageView) itemView.findViewById(R.id.ivStoreImage);
            tvStoreName = (TextView) itemView.findViewById(R.id.tvStoreName);
            tvStoreAddress = (TextView) itemView.findViewById(R.id.tvStoreAddress);
            tvTel = (TextView) itemView.findViewById(R.id.tvTel);
            btnRate = (Button) itemView.findViewById(R.id.btnRate);

        }
    }
}
