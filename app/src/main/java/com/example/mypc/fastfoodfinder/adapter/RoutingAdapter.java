package com.example.mypc.fastfoodfinder.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.model.Routing.Step;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nhoxb on 11/30/2016.
 */
public class RoutingAdapter extends RecyclerView.Adapter<RoutingAdapter.VH>{

    List<Step> mStepList;
    OnNavigationItemClickListener mListener;

    public RoutingAdapter() {
        super();
        mStepList = new ArrayList<>();
    }

    public RoutingAdapter(List<Step> steps) {
        super();
        this.mStepList = steps;
    }

    public void setOnNavigationItemClickListener(OnNavigationItemClickListener listener)
    {
        mListener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routing,parent, false);
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

    class VH extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tv_routing_guide)
        TextView routingGuide;
        @BindView(R.id.tv_routing_distance)
        TextView routingDistance;
        public VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(mStepList.get(getAdapterPosition()).getEndMapCoordination().getLocation());
                }
            });
        }

        public void setData(Step step)
        {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                routingGuide.setText(Html.fromHtml(step.getInstruction(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                routingGuide.setText(Html.fromHtml(step.getInstruction()));
            }
            routingGuide.setText(Html.fromHtml(step.getInstruction()));
            routingDistance.setText(step.getDistance());
        }
    }

    public interface OnNavigationItemClickListener
    {
        public void onClick(LatLng latLng);
    }
}
