package com.example.mypc.fastfoodfinder.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.model.Comment;
import com.example.mypc.fastfoodfinder.model.Store.Store;
import com.example.mypc.fastfoodfinder.utils.DataUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by binhlt on 23/11/2016.
 */

public class StoreDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int HEADER = 0;
    private final int INFO = 1;
    private final int TITLE = 2;
    private final int ITEM = 3;

    private Store mStore;
    private List<Comment> mComments;

    public StoreDetailAdapter(Store store) {
        mStore = store;
        mComments = DataUtils.getComments();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_action, parent, false));
            case INFO:
                return new InfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_info, parent, false));
            case TITLE:
                return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_title, parent, false));
            default:
                return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case HEADER:
                ((HeaderViewHolder) holder).bind(mStore);
                break;
            case INFO:
                ((InfoViewHolder) holder).bind(mStore);
                break;
            case TITLE:
                String title = position == 2 ? String.valueOf(R.string.tips_from_people_who_has_been_here) : "";
                ((TitleViewHolder) holder).bind(title);
                break;
            default:
                ((CommentViewHolder) holder).bind(mComments.get(position - 3));
        }
    }

    @Override
    public int getItemCount() {
        return mComments.size() + 3;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return HEADER;
            case 1:
                return INFO;
            case 2:
                return TITLE;
            default:
                return ITEM;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Store mStore) {

        }
    }

    public class InfoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.store_name)
        TextView tvName;
        @BindView(R.id.store_address)
        TextView tvAddress;

        public InfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Store mStore) {
            tvName.setText(mStore.getTitle());
            tvAddress.setText(mStore.getTitle()); //TODO sử dụng address
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.content)
        TextView content;

        public TitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(String title) {
            content.setText(title);
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_image)
        ImageView ivProfile;
        @BindView(R.id.user_name)
        TextView tvUserName;
        @BindView(R.id.time_post)
        TextView tvTime;
        @BindView(R.id.content)
        TextView tvContent;
        @BindView(R.id.container)
        CardView container;
        @BindView(R.id.media)
        ImageView ivMedia;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Comment comment) {
            final Context context = ivProfile.getContext();
            Glide.with(context)
                    .load(comment.getAvatar())
                    .asBitmap()
                    .into(new BitmapImageViewTarget(ivProfile) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable bitmap = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            bitmap.setCornerRadius(5f);
                            ivProfile.setImageDrawable(bitmap);
                        }
                    });

            String mediaUrl = comment.getMediaUrl();
            if (!mediaUrl.isEmpty()) {
                container.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(mediaUrl)
                        .into(ivMedia);
            } else {
                container.setVisibility(View.GONE);
            }

            tvUserName.setText(comment.getUserName());
            tvContent.setText(comment.getContent());
            tvTime.setText(DataUtils.getRelativeTimeAgo(comment.getDate()));
        }
    }
}
