@file:JvmName("StringUtils")
@file:JvmMultifileClass

package com.iceteaviet.fastfoodfinder.utils

/**
 * Created by tom on 7/19/18.
 */

/**
 * Check string is null or empty
 */
fun isEmpty(str: String?): Boolean {
    return str == null || str.isEmpty()
}