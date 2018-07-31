package com.iceteaviet.fastfoodfinder.utils.ui;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.view.animation.BounceInterpolator;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.utils.Constant;
import com.iceteaviet.fastfoodfinder.utils.DisplayUtils;

/**
 * Created by tom on 7/10/18.
 */
public final class UiUtils {
    private UiUtils() {

    }

    public static int getStoreLogoDrawableId(int type) {
        switch (type) {
            case Constant.TYPE_CIRCLE_K:
                return R.drawable.logo_circlek_50;
            case Constant.TYPE_MINI_STOP:
                return R.drawable.logo_ministop_50;
            case Constant.TYPE_FAMILY_MART:
                return R.drawable.logo_familymart_50;
            case Constant.TYPE_BSMART:
                return R.drawable.logo_bsmart_50;
            case Constant.TYPE_SHOP_N_GO:
                return R.drawable.logo_shopngo_50;
            default:
                return R.drawable.logo_circlek_50;
        }
    }

    public static int getDirectionImage(String direction) {
        if (direction == null)
            return R.drawable.ic_routing_up;

        if (direction.equals("straight")) {
            return R.drawable.ic_routing_up;
        } else if (direction.equals("turn-left")) {
            return R.drawable.ic_routing_left;
        } else if (direction.equals("turn-right")) {
            return R.drawable.ic_routing_right;
        } else if (direction.equals("merge")) {
            return R.drawable.ic_routing_merge;
        } else {
            return R.drawable.ic_routing_up;
        }
    }

    public static void animateMarker(final Bitmap bitmap, final Marker marker) {
        if (marker == null)
            return;

        ValueAnimator animator = ValueAnimator.ofFloat(0.1f, 1);
        animator.setDuration(1000);
        animator.setStartDelay(500);
        animator.setInterpolator(new BounceInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (float) animation.getAnimatedValue();
                try {
                    // TODO: Optimize .fromBitmap & resize icons
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(DisplayUtils.resizeMarkerIcon(bitmap, Math.round(scale * 75), Math.round(scale * 75))));
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }
        });
        animator.start();
    }
}
