package com.iceteaviet.fastfoodfinder.ui.main.map;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.utils.LocationUtils;
import com.iceteaviet.fastfoodfinder.utils.ui.UiUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Genius Doan on 11/9/2016.
 */
public class NearByStoreAdapter extends RecyclerView.Adapter<NearByStoreAdapter.StoreViewHolder> {
    private LatLng currCameraPosition;
    private List<Store> mListStore;
    private StoreListListener listener;

    public NearByStoreAdapter() {
        mListStore = new ArrayList<>();
    }

    public void setOnStoreListListener(StoreListListener listener) {
        this.listener = listener;
    }

    public void setCurrCameraPosition(LatLng currCameraPosition) {
        this.currCameraPosition = currCameraPosition;
    }

    public void addStore(Store store) {
        mListStore.add(store);
        notifyItemInserted(mListStore.size() - 1);
    }

    public void addStores(List<Store> listStores) {
        int position = mListStore.size();
        mListStore.addAll(listStores);
        notifyItemRangeInserted(position, mListStore.size());
    }

    public void setStores(List<Store> listStores) {
        mListStore.clear();
        mListStore.addAll(listStores);
        notifyDataSetChanged();
    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(convertView, listener, mListStore);
    }

    @Override
    public void onBindViewHolder(StoreViewHolder holder, int position) {
        Store store = mListStore.get(position);
        holder.setData(store, LocationUtils.calcDistance(currCameraPosition, store.getPosition()));
    }

    @Override
    public int getItemCount() {
        return mListStore.size();
    }

    public interface StoreListListener {
        void onItemClick(Store store);
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_item_store)
        ImageView logo;
        @BindView(R.id.tv_item_storename)
        TextView storeName;
        @BindView(R.id.tv_item_address)
        TextView storeAddress;
        @BindView(R.id.tv_item_distance)
        TextView storeDistance;


        public StoreViewHolder(View itemView, final StoreListListener listener, final List<Store> storeList) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(storeList.get(getAdapterPosition()));
                }
            });
        }

        public void setData(Store store, double distance) {
            Glide.with(logo.getContext())
                    .load(UiUtils.getStoreLogoDrawableId(store.getType()))
                    .into(logo);
            storeName.setText(store.getTitle());
            storeAddress.setText(store.getAddress());
            storeDistance.setText(new DecimalFormat("##.## Km").format(distance));
        }
    }
}
