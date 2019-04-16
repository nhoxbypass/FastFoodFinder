package com.iceteaviet.fastfoodfinder.utils

import io.realm.RealmList
import org.junit.Assert.assertEquals
import org.junit.Test

class RealmUtilsTest {
    @Test
    fun realmListToList_size() {
        val realmList = RealmList<String>("realmObj1", "realmObj2", "realmObj3", "realmObj4", "realmObj5")
        assertEquals(5, realmListToList(realmList).size)
    }

    @Test
    fun realmListToList_items() {
        val realmList = RealmList<String>("realmObj1", "realmObj2", "realmObj3", "realmObj4", "realmObj5")
        assertEquals("[realmObj1, realmObj2, realmObj3, realmObj4, realmObj5]", realmListToList(realmList).toString())
    }

    @Test
    fun realmListToList_item() {
        val realmList = RealmList<String>("realmObj1", "realmObj2", "realmObj3", "realmObj4", "realmObj5")
        assertEquals("realmObj4", realmListToList(realmList)[3])
    }
}