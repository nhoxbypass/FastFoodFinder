package com.example.mypc.fastfoodfinder.adapter;

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

import com.example.mypc.fastfoodfinder.model.Article;
import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.helper.ItemTouchHelperAdapter;
import com.example.mypc.fastfoodfinder.helper.ItemTouchHelperViewHolder;
import com.example.mypc.fastfoodfinder.helper.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by MyPC on 11/16/2016.
 */
public class FavouriteLocationAdapter extends RecyclerView.Adapter<FavouriteLocationAdapter.FavouriteDesViewHolder>
        implements ItemTouchHelperAdapter {
    List<Article> mDes;
    private OnItemClickListener mOnItemClickListener;
    private final OnStartDragListener mDragStartListener;

    View mView;
    public FavouriteLocationAdapter(OnStartDragListener onStartDragListener, FrameLayout view){
        mDes = new ArrayList<>();
        mDragStartListener = onStartDragListener;
        mView = view;
    }

    @Override
    public FavouriteDesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favourite_location,parent,false);
        return new FavouriteDesViewHolder(itemView);
    }

    private interface MyOnClickListener{

    }
    @Override
    public boolean onItemMove(final int fromPosition, final int toPosition) {
        Collections.swap(mDes, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        Snackbar.make(mView, R.string.do_you_want_undo, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Collections.swap(mDes,toPosition, fromPosition);
                        notifyItemMoved(toPosition,fromPosition);
                    }
                })
                .setDuration(30000)
                .show();
        return true;
    }

    @Override
    public void onItemDismiss(final int position) {
        final Article article = mDes.get(position);
        mDes.remove(position);
        notifyDataSetChanged();
        notifyItemRemoved(position);
        Snackbar.make(mView, R.string.do_you_want_undo, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDes.add(position, article);
                        notifyItemInserted(position);
                    }
                })
                .setDuration(30000)
                .show();
    }


    public interface OnItemClickListener{
        void onClick(Article des);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }

    public void addDes(List<Article> destinations){
        mDes.addAll(destinations);
        notifyItemRangeInserted(mDes.size(),destinations.size());
    }

    public void addDes(Article destination){
        mDes.add(destination);
        notifyItemRangeInserted(mDes.size(),1);
    }

    public void setDesS(List<Article> destinations){
        mDes.clear();
        mDes.addAll(destinations);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final FavouriteDesViewHolder holder, int position) {

        Article destination = mDes.get(position);
        holder.tvDes.setText(destination.getDes());
        holder.tvAddress.setText(destination.getAddress());
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
        return mDes.size();
    }

    class FavouriteDesViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        TextView tvDes;
        TextView tvAddress;

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

       public FavouriteDesViewHolder(final View itemView) {
           super(itemView);
           tvDes = (TextView) itemView.findViewById(R.id.tvDestination);
           tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
           itemView.setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick(View view) {
                   int position = getAdapterPosition();
                   Article destination = mDes.get(position);
                   if (mOnItemClickListener!= null){
                       mOnItemClickListener.onClick(destination);
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
                                   mDes.remove(position);
                                   notifyDataSetChanged();
                                   Snackbar.make(itemView,R.string.undo, Snackbar.LENGTH_INDEFINITE).show();                               }
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
   }

}
