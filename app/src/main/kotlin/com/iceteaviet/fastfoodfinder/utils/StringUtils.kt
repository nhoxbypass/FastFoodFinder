@file:JvmName("StringUtils")
@file:JvmMultifileClass

package com.iceteaviet.fastfoodfinder.utils

import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.annotation.StringRes
import com.iceteaviet.fastfoodfinder.App

/**
 * Created by tom on 7/19/18.
 */


fun getString(@StringRes resId: Int): String {
    return App.getContext().getString(resId)
}

/**
 * Check string is null or empty
 */
fun isEmpty(str: String?): Boolean {
    return str == null || str.isEmpty()
}


fun fromHtml(source: String): Spanned {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(source)
    }
}


/**
 * Trim all whitespace & keep span(s)
 */
@Deprecated(message = "Use system method instead")
fun trimWhitespace(source: CharSequence?): CharSequence {
    if (source == null || source.isEmpty())
        return ""

    val builder = SpannableStringBuilder(source)
    var c: Char

    for (i in 0 until source.length) {
        c = source[i]
        if (Character.isWhitespace(c)) {
            try {
                if (i < source.length - 1 && Character.isWhitespace(source[i + 1]))
                //Ignore next char
                //Because it is a whitespace again
                    builder.delete(i, i + 1)
            } catch (ex: IndexOutOfBoundsException) {
                ex.printStackTrace()
            }

        }
    }

    if (Character.isWhitespace(builder[builder.length - 1]))
        builder.delete(builder.length - 1, builder.length)

    return builder.subSequence(0, builder.length)
}

fun getNameFromEmail(email: String): String {
    return email.substringBefore("@")
}