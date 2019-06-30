@file:JvmName("RealmUtils")

package com.iceteaviet.fastfoodfinder.utils

import io.realm.RealmList

/**
 * Created by binhlt on 23/11/2016.
 */

fun <T> realmListToList(realmList: RealmList<T>): MutableList<T> {

    val list = ArrayList<T>()

    for (e in realmList) {
        list.add(e)
    }

    return list
}