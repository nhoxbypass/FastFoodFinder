package com.iceteaviet.fastfoodfinder.ui.store;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.utils.DataUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by binhlt on 23/11/2016.
 */

public class StoreDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER = 0;
    private static final int INFO = 1;
    private static final int TITLE = 2;
    private static final int ITEM = 3;

    private Store mStore;
    private List<Comment> mComments;
    private StoreActionListener mListener;

    StoreDetailAdapter(Store store) {
        mStore = store;
        mComments = DataUtils.getFakeComments();
    }

    public void setListener(StoreActionListener listener) {
        mListener = listener;
    }

    public int addComment(Comment comment) {
        mComments.add(0, comment);
        notifyItemInserted(3);
        return 3;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_action, parent, false), mListener);
            case INFO:
                return new InfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_info, parent, false), mListener);
            case TITLE:
                return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_title, parent, false));
            default:
                return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_comment, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case HEADER:
                ((HeaderViewHolder) holder).bind(mStore);
                break;
            case INFO:
                ((InfoViewHolder) holder).bind(mStore);
                break;
            case TITLE:
                String title = ((TitleViewHolder) holder).context.getResources().getString(R.string.tips_from_people_who_has_been_here);
                ((TitleViewHolder) holder).bind(position == 2 ? title : "");
                break;
            default:
                ((CommentViewHolder) holder).bind(mComments.get(position - 3));
                break;
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

    public interface StoreActionListener {
        void onShowComment();

        void onCall(String tel);

        void onDirect();

        void onAddToFavorite(int storeId);

        void onCheckIn(int storeId);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.save_this)
        TextView save;
        @BindView(R.id.check_in)
        TextView check;
        @BindView(R.id.rate_it)
        TextView rate;
        @BindView(R.id.comment)
        TextView comment;
        @BindView(R.id.call)
        Button btnCall;

        private Store mStore;
        private StoreActionListener listener;

        HeaderViewHolder(View itemView, StoreActionListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.listener = listener;
        }

        public void bind(Store store) {
            mStore = store;

            save.setOnClickListener(this);
            check.setOnClickListener(this);
            rate.setOnClickListener(this);
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowComment();
                }
            });
            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCall(mStore.getTel());
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save_this:
                    listener.onAddToFavorite(mStore.getId());
                    break;
                case R.id.check_in:
                    listener.onCheckIn(mStore.getId());
                    break;
                default:
                    break;
            }
            v.setSelected(!v.isSelected());
        }
    }

    static class InfoViewHolder extends RecyclerView.ViewHolder {

        private CallDirectionViewHolder cdvh;
        @BindView(R.id.store_name)
        TextView tvName;
        @BindView(R.id.store_address)
        TextView tvAddress;
        @BindView(R.id.call_direction)
        View vCallDirection;

        private StoreActionListener listener;

        InfoViewHolder(View itemView, StoreActionListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            cdvh = new CallDirectionViewHolder(vCallDirection);
            this.listener = listener;
        }

        public void bind(final Store mStore) {
            tvName.setText(mStore.getTitle());
            tvAddress.setText(mStore.getAddress());

            cdvh.btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCall(mStore.getTel());
                }
            });
            cdvh.btnDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDirect();
                }
            });
        }
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {

        public final Context context;
        @BindView(R.id.content)
        TextView content;

        TitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        public void bind(String title) {
            content.setText(title);
        }
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

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

        CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Comment comment) {
            final Context context = ivProfile.getContext();
            Glide.with(context)
                    .asBitmap()
                    .load(comment.getAvatar())
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

    public static class CallDirectionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.call)
        public FrameLayout btnCall;

        @BindView(R.id.direction)
        public FrameLayout btnDirection;

        CallDirectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
