@file:JvmName("StringUtils")
@file:JvmMultifileClass

package com.iceteaviet.fastfoodfinder.utils

/**
 * Created by tom on 7/19/18.
 */

fun isEmpty(str: String?): Boolean {
    return str == null || str.isEmpty()
}