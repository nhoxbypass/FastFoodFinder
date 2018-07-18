package com.iceteaviet.fastfoodfinder.utils;

import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;

/**
 * Created by tom on 7/10/18.
 */
public final class FormatUtils {
    private FormatUtils() {

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

    public static Intent getCallIntent(String tel) {
        tel = tel.replaceAll("\\s", "");
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:08" + tel));
        return callIntent;
    }
}
