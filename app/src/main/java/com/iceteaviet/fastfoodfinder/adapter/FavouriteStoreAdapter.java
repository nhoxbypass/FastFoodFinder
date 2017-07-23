package com.iceteaviet.fastfoodfinder.adapter;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.interfaces.ItemTouchHelperAdapter;
import com.iceteaviet.fastfoodfinder.interfaces.ItemTouchHelperViewHolder;
import com.iceteaviet.fastfoodfinder.interfaces.OnStartDragListener;
import com.iceteaviet.fastfoodfinder.model.store.Store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MyPC on 11/16/2016.
 */
public class FavouriteStoreAdapter extends RecyclerView.Adapter<FavouriteStoreAdapter.FavouriteStoreViewHolder>
        implements ItemTouchHelperAdapter {
    private final OnStartDragListener mDragStartListener;
    View mContainerView;
    private List<Store> mStoreList;
    private OnItemClickListener mOnItemClickListener;

    public FavouriteStoreAdapter(OnStartDragListener onStartDragListener, FrameLayout view) {
        mStoreList = new ArrayList<>();
        mDragStartListener = onStartDragListener;
        mContainerView = view;
    }

    @Override
    public FavouriteStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favourite_location, parent, false);
        return new FavouriteStoreViewHolder(itemView);
    }

    @Override
    public boolean onItemMove(final int fromPosition, final int toPosition) {
        Collections.swap(mStoreList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        Snackbar.make(mContainerView, R.string.do_you_want_undo, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Collections.swap(mStoreList, toPosition, fromPosition);
                        notifyItemMoved(toPosition, fromPosition);
                    }
                })
                .setDuration(30000)
                .show();
        return true;
    }

    @Override
    public void onItemDismiss(final int position) {
        final Store store = mStoreList.get(position);
        mStoreList.remove(position);
        notifyDataSetChanged();
        notifyItemRemoved(position);
        Snackbar.make(mContainerView, R.string.do_you_want_undo, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mStoreList.add(position, store);
                        notifyItemInserted(position);
                    }
                })
                .setDuration(30000)
                .show();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void addStores(List<Store> destinations) {
        int pos = mStoreList.size();
        mStoreList.addAll(destinations);
        notifyItemRangeInserted(pos, destinations.size());
    }

    public void addStore(Store destination) {
        mStoreList.add(destination);
        notifyItemRangeInserted(mStoreList.size(), 1);
    }

    public void setStores(List<Store> destinations) {
        mStoreList.clear();
        mStoreList.addAll(destinations);
        notifyDataSetChanged();
    }

    public void updateStore(Store store) {
        for (int i = 0; i < mStoreList.size(); i++) {
            if (store.getId() == mStoreList.get(i).getId()) {
                mStoreList.set(i, store);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void removeStore(Store store) {
        int index = mStoreList.indexOf(store);
        if (index != -1) {
            mStoreList.remove(index);
            notifyItemRemoved(index);
        }
    }

    @Override
    public void onBindViewHolder(final FavouriteStoreViewHolder holder, int position) {
        Store store = mStoreList.get(position);
        holder.setData(store);
        // Start a drag whenever the handle view it touched
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStoreList.size();
    }

    private interface FavouritedListListener {
        public void onClick(Store store);

        public void onItemDismiss();
    }

    public interface OnItemClickListener {
        void onClick(Store des);
    }

    class FavouriteStoreViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        @BindView(R.id.tv_item_title)
        TextView txtTitle;
        @BindView(R.id.tv_item_address)
        TextView txtAddress;

        public FavouriteStoreViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Store store = mStoreList.get(position);
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onClick(store);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final int position = getAdapterPosition();
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle(R.string.delete_favourite_location)
                            .setMessage(R.string.are_you_sure)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mStoreList.remove(position);
                                    notifyDataSetChanged();
                                    Snackbar.make(itemView, R.string.undo, Snackbar.LENGTH_INDEFINITE).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return true;
                }
            });

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        public void setData(Store store) {
            txtTitle.setText(store.getTitle());
            txtAddress.setText(store.getAddress());
        }
    }

}
