package com.iceteaviet.fastfoodfinder.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.model.Routing.Step;
import com.iceteaviet.fastfoodfinder.utils.DisplayUtils;
import com.iceteaviet.fastfoodfinder.utils.MapUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nhoxb on 11/30/2016.
 */
public class RoutingAdapter extends RecyclerView.Adapter<RoutingAdapter.VH> {
    public static final int TYPE_FULL = 0;
    public static final int TYPE_SHORT = 1;
    List<Step> mStepList;
    OnNavigationItemClickListener mListener;
    private int mType;

    public RoutingAdapter() {
        super();
        mStepList = new ArrayList<>();
    }

    public RoutingAdapter(List<Step> steps, int type) {
        super();
        this.mStepList = steps;
        mType = type;
    }

    public void setOnNavigationItemClickListener(OnNavigationItemClickListener listener) {
        mListener = listener;
    }

    public LatLng getDirectionLocationAt(int index) {
        if (index < 0 || index >= mStepList.size()) {
            index = 0;
        }
        return mStepList.get(index).getEndMapCoordination().getLocation();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routing, parent, false);
        return new VH(convertView);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.setData(mStepList.get(position));
    }

    @Override
    public int getItemCount() {
        return mStepList.size();
    }


    public interface OnNavigationItemClickListener {
        public void onClick(int index);
    }

    class VH extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_routing_guide)
        TextView routingGuide;
        @BindView(R.id.tv_routing_distance)
        TextView routingDistance;
        @BindView(R.id.iv_routing)
        ImageView routingImageView;

        public VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(getAdapterPosition());
                }
            });
        }

        public void setData(Step step) {

            int imgResId = MapUtils.getDirectionImage(step.getDirection());
            routingImageView.setImageResource(imgResId);
            Spanned instruction;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                instruction = Html.fromHtml(step.getInstruction(), Html.FROM_HTML_MODE_COMPACT);
            } else {
                instruction = Html.fromHtml(step.getInstruction());
            }

            if (mType == TYPE_FULL)
                routingGuide.setText(DisplayUtils.trimWhitespace(instruction));
            else if (mType == TYPE_SHORT)
                routingGuide.setText(DisplayUtils.getTrimmedShortInstruction(instruction));
            routingDistance.setText(step.getDistance());
        }
    }
}
