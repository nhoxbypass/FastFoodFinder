package com.iceteaviet.fastfoodfinder.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;

import com.iceteaviet.fastfoodfinder.R;

/**
 * Created by taq on 27/11/2016.
 */

public class DisplayUtils {

    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Context context, Bitmap image) {
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    public static int convertDpToPx(DisplayMetrics displayMetrics, int dp) {
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static Intent getCallIntent(String tel) {
        tel = tel.replaceAll("\\s", "");
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:08" + tel));
        return callIntent;
    }

    public static CharSequence trimWhitespace(CharSequence source) {

        if (source == null)
            return "";


        SpannableStringBuilder builder = new SpannableStringBuilder(source);
        char c;

        for (int i = 0; i < source.length(); i++) {
            c = source.charAt(i);
            if (Character.isWhitespace(c)) {
                try {
                    if (i < (source.length() - 1) && Character.isWhitespace(source.charAt(i + 1)))
                        //Ignore next char
                        //Because it is a whitespace again
                        builder.delete(i, i + 1);
                } catch (Exception ex) {
                }
            }
        }

        if (Character.isWhitespace(builder.charAt(builder.length() - 1)))
            builder.delete(builder.length() - 1, builder.length());

        return builder.subSequence(0, builder.length());
    }

    public static CharSequence getTrimmedShortInstruction(CharSequence source) {

        if (source == null)
            return "";

        int i = 0;
        int newLen = 0;

        // loop back to the first non-whitespace character
        for (i = 0; i < source.length() - 1; i++) {
            if (Character.isWhitespace(source.charAt(i)) && Character.isWhitespace(source.charAt(i + 1))) {
                newLen = i;
                break;
            }
        }

        if (newLen <= 1)
            newLen = source.length();

        return source.subSequence(0, newLen);
    }

    public static Bitmap resizeMarkerIcon(Bitmap imageBitmap, int width, int height) {
        if (width > 100)
            width = 200 - width;
        if (height > 100)
            height = 200 - height;

        if (width == 0)
            width = 1;
        if (height == 0)
            height = 1;


        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public static int getStoreLogoDrawableId(int type) {
        int id = R.drawable.logo_circlek_50;
        switch (type) {
            case Constant.TYPE_CIRCLE_K:
                id = R.drawable.logo_circlek_50;
                break;
            case Constant.TYPE_MINI_STOP:
                id = R.drawable.logo_ministop_50;
                break;
            case Constant.TYPE_FAMILY_MART:
                id = R.drawable.logo_familymart_50;
                break;
            case Constant.TYPE_BSMART:
                id = R.drawable.logo_bsmart_50;
                break;
            case Constant.TYPE_SHOP_N_GO:
                id = R.drawable.logo_shopngo_50;
                break;
        }

        return id;
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
}