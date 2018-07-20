package com.iceteaviet.fastfoodfinder.ui.main.map;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
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
 * Created by tom on 7/21/18.
 */
public class NearByStoreListAdapter extends ListAdapter<Store, NearByStoreListAdapter.StoreViewHolder> {

    public static final DiffUtil.ItemCallback<Store> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Store>() {
                @Override
                public boolean areItemsTheSame(Store oldItem, Store newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(Store oldItem, Store newItem) {
                    return oldItem.getAddress().equals(newItem.getAddress());
                }
            };

    private LatLng currCameraPosition;
    private List<Store> mListStore;
    private StoreListListener listener;

    public NearByStoreListAdapter() {
        this(DIFF_CALLBACK);
    }

    protected NearByStoreListAdapter(@NonNull DiffUtil.ItemCallback<Store> diffCallback) {
        super(diffCallback);

        mListStore = new ArrayList<>();
    }

    public void setOnStoreListListener(StoreListListener listener) {
        this.listener = listener;
    }

    public void setCurrCameraPosition(LatLng currCameraPosition) {
        this.currCameraPosition = currCameraPosition;
    }

    public void setStores(List<Store> listStores) {
        mListStore.clear();
        mListStore.addAll(listStores);

        submitList(listStores); // DiffUtil takes care of the check
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new NearByStoreListAdapter.StoreViewHolder(convertView, listener, mListStore);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = getItem(position);
        holder.setData(store, LocationUtils.calcDistance(currCameraPosition, store.getPosition()));
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
                    if (listener != null)
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
