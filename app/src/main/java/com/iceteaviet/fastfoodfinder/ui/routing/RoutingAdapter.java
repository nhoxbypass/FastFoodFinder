package com.iceteaviet.fastfoodfinder.ui.routing;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.Step;
import com.iceteaviet.fastfoodfinder.utils.FormatUtils;
import com.iceteaviet.fastfoodfinder.utils.ui.UiUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Genius Doan on 11/30/2016.
 */
public class RoutingAdapter extends RecyclerView.Adapter<RoutingAdapter.ViewHolder> {
    public static final int TYPE_FULL = 0;
    public static final int TYPE_SHORT = 1;

    private List<Step> mStepList;
    private OnNavigationItemClickListener mListener;
    private int mType;

    RoutingAdapter() {
        super();
        mStepList = new ArrayList<>();
    }

    RoutingAdapter(List<Step> steps, int type) {
        super();
        this.mStepList = steps;
        mType = type;
    }

    public void setOnNavigationItemClickListener(OnNavigationItemClickListener listener) {
        mListener = listener;
    }

    public LatLng getDirectionLocationAt(int index) {
        if (index < 0 || index >= mStepList.size()) {
            return null;
        }
        return mStepList.get(index).getEndMapCoordination().getLocation();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routing, parent, false);
        return new ViewHolder(convertView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mStepList.get(position), mType);
    }

    @Override
    public int getItemCount() {
        return mStepList.size();
    }


    public interface OnNavigationItemClickListener {
        void onClick(int index);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_routing_guide)
        TextView routingGuide;
        @BindView(R.id.tv_routing_distance)
        TextView routingDistance;
        @BindView(R.id.iv_routing)
        ImageView routingImageView;

        public ViewHolder(View itemView, final OnNavigationItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onClick(getAdapterPosition());
                }
            });
        }

        private void bindData(Step step, int type) {
            int imgResId = UiUtils.getDirectionImage(step.getDirection());
            Spanned instruction;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                instruction = Html.fromHtml(step.getInstruction(), Html.FROM_HTML_MODE_COMPACT);
            } else {
                instruction = Html.fromHtml(step.getInstruction());
            }

            routingImageView.setImageResource(imgResId);
            if (type == TYPE_FULL)
                routingGuide.setText(FormatUtils.trimWhitespace(instruction));
            else if (type == TYPE_SHORT)
                routingGuide.setText(FormatUtils.getTrimmedShortInstruction(instruction));
            routingDistance.setText(step.getDistance());
        }
    }
}
