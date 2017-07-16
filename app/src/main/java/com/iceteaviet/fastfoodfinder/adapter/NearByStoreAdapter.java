package com.iceteaviet.fastfoodfinder.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.model.Store.StoreViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nhoxb on 11/9/2016.
 */
public class NearByStoreAdapter extends RecyclerView.Adapter<NearByStoreAdapter.StoreViewHolder> {

    private List<StoreViewModel> mListStore;
    private StoreListListener mLisener;

    public NearByStoreAdapter() {
        mListStore = new ArrayList<>();
    }

    public void setOnStoreListListener(StoreListListener listener) {
        mLisener = listener;
    }

    public void addStore(StoreViewModel store) {
        mListStore.add(store);
        notifyItemInserted(mListStore.size() - 1);
    }

    public void addStores(List<StoreViewModel> listStores) {
        int position = mListStore.size();
        mListStore.addAll(listStores);
        notifyItemRangeInserted(position, mListStore.size());
    }

    public void setStores(List<StoreViewModel> listStores) {
        mListStore.clear();
        mListStore.addAll(listStores);
        notifyDataSetChanged();
    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(StoreViewHolder holder, int position) {
        holder.setData(mListStore.get(position));
    }

    @Override
    public int getItemCount() {
        return mListStore.size();
    }

    public interface StoreListListener {
        void onItemClick(StoreViewModel store);
    }

    class StoreViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_item_store)
        ImageView logo;
        @BindView(R.id.tv_item_storename)
        TextView storeName;
        @BindView(R.id.tv_item_address)
        TextView storeAddress;
        @BindView(R.id.tv_item_distance)
        TextView storeDistance;

        public StoreViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLisener.onItemClick(mListStore.get(getAdapterPosition()));
                }
            });
        }

        public void setData(StoreViewModel store) {
            Glide.with(logo.getContext())
                    .load(store.getDrawableLogo())
                    .into(logo);
            storeName.setText(store.getStoreName());
            storeAddress.setText(store.getStoreAddress());
            storeDistance.setText(new DecimalFormat("##.##").format(store.getStoreDistance()) + " Km");
        }
    }
}
