@file:JvmName("DataUtils")

package com.iceteaviet.fastfoodfinder.utils

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by binhlt on 23/11/2016.
 */

fun getDefaultUserStoreLists(): List<UserStoreList> {
    val userStoreLists = ArrayList<UserStoreList>()

    userStoreLists.add(UserStoreList(0, ArrayList(), 1, "My Saved Places"))
    userStoreLists.add(UserStoreList(1, ArrayList(), 2, "My Favourite Places"))

    return userStoreLists
}

fun getFakeStoreList(): List<Store> {
    val stores = ArrayList<Store>()
    stores.add(Store(1, "Circle K Le Thi Rieng", "148 Le Thi Rieng, Ben Thanh Ward, District 1, Ho Chi Minh, Vietnam", "10.770379", "106.68912279999995", "3925 6620", StoreType.TYPE_CIRCLE_K))
    stores.add(Store(2, "FamilyMart - Hậu Giang", "973 Hậu Giang, P. 11, Quận 6, TP. HCM", "10.7457782220847", "106.6261117905378", "3755 0439", StoreType.TYPE_FAMILY_MART))
    stores.add(Store(3, "FamilyMart - Nguyễn Lương Bằng", "180 Nguyễn Lương Bằng, P. Tân Phú, Quận 7, TP. HCM", "10.727042", "106.722703", "5417 3390", StoreType.TYPE_FAMILY_MART))
    stores.add(Store(4, "Family Mart - Tạ Quang Bửu", "811 Tạ Quang Bửu, P. 5, Quận 8, TP. HCM", "10.736488", "106.670374", "3835 3193", StoreType.TYPE_FAMILY_MART))
    stores.add(Store(5, "Family Mart - Nguyễn Văn Công", "534 Nguyễn Văn Công, Phường 3, Quận Gò Vấp, TP. HCM", "10.819417", "106.674821", "3835 3193", StoreType.TYPE_FAMILY_MART))
    stores.add(Store(6, "Shop & Go - Phan Đình Phùng", "180 Phan Đình Phùng, P. 2, Quận Phú Nhuận, TP. HCM", "10.7955070000000", "106.6825610000000", "38 353 193", StoreType.TYPE_SHOP_N_GO))
    stores.add(Store(7, "Circle K Ly Tu Trong", "238 Ly Tu Trong, Ben Thanh Ward, District 1, Ho Chi Minh, Vietnam", "10.7721924", "106.69433409999999", "3822 7403", StoreType.TYPE_CIRCLE_K))
    stores.add(Store(8, "Familymart - Đường D2", "39 Đường D2, P. 25, Quận Bình Thạnh, TP. HCM", "10.80252", "106.715622", "35 126 283", StoreType.TYPE_FAMILY_MART))
    stores.add(Store(9, "FamilyMart - 123 Nguyễn Đình Chiểu", "123 Nguyễn Đình Chiểu, Phường 6, Quận 3, TP. HCM", "10.7775462", "106.6892408999999", "3835 3193", StoreType.TYPE_FAMILY_MART))
    stores.add(Store(10, "FamilyMart - Tôn Dật Tiến", "Tôn Dật Tiên, Quận 7, TP. HCM", "10.723322", "106.71498", "3835 3193", StoreType.TYPE_FAMILY_MART))

    return stores
}

/**
 * Get random Integer
 */
fun getRandomInt(min: Int, max: Int): Int {
    val rand = Random()
    return rand.nextInt(max - min + 1) + min
}

/**
 * Get relative time in the past
 */
fun getRelativeTimeAgo(rawJsonDate: String): String {
    val twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
    var sf = SimpleDateFormat(twitterFormat, Locale.ENGLISH)
    sf.isLenient = true
    val now = Calendar.getInstance()
    val then = Calendar.getInstance()
    try {
        val date = sf.parse(rawJsonDate)
        then.time = date
        val nowMs = now.timeInMillis
        val thenMs = then.timeInMillis
        val diff = nowMs - thenMs
        val diffMinutes = diff / (60 * 1000)
        val diffHours = diff / (60 * 60 * 1000)
        val diffDays = diff / (24 * 60 * 60 * 1000)
        if (diffMinutes <= 0) {
            return "just now"
        } else if (diffMinutes < 60) {
            return diffMinutes.toString() + "m"
        } else if (diffHours < 24) {
            return diffHours.toString() + "h"
        } else if (diffDays < 7) {
            return diffDays.toString() + "d"
        } else {
            sf = SimpleDateFormat("MMM dd", Locale.ENGLISH)
            return sf.format(date)
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return "unknown"
}

/**
 * Get store type by keyword
 */
fun getStoreType(key: String): Int {
    return if (key == "circle_k")
        StoreType.TYPE_CIRCLE_K
    else if (key == "mini_stop")
        StoreType.TYPE_MINI_STOP
    else if (key == "family_mart")
        StoreType.TYPE_FAMILY_MART
    else if (key == "bsmart")
        StoreType.TYPE_BSMART
    else if (key == "shop_n_go")
        StoreType.TYPE_SHOP_N_GO
    else
        StoreType.TYPE_CIRCLE_K
}

/**
 * Normalize district query string
 */
fun normalizeDistrictQuery(queryString: String): List<String> {
    val result = ArrayList<String>()
    val trimmedQuery = queryString.toLowerCase().trim { it <= ' ' }

    if (trimmedQuery == "gò vấp" || trimmedQuery == "go vap" || trimmedQuery == "govap") {
        result.add("Gò Vấp")
        result.add("Go Vap")
    } else if (trimmedQuery == "tân bình" || trimmedQuery == "tan binh" || trimmedQuery == "tanbinh") {
        result.add("Tân Bình")
        result.add("Tan Binh")
    } else if (trimmedQuery == "tân phú" || trimmedQuery == "tan phu" || trimmedQuery == "tanphu") {
        result.add("Tân Phú")
        result.add("Tan Phu")
    } else if (trimmedQuery == "bình thạnh" || trimmedQuery == "binh thanh" || trimmedQuery == "binhthanh") {
        result.add("Bình Thạnh")
        result.add("Binh Thanh")
    } else if (trimmedQuery == "phú nhuận" || trimmedQuery == "phu nhuan" || trimmedQuery == "phunhuan") {
        result.add("Phú Nhuận")
        result.add("Phu Nhuan")
    } else if (trimmedQuery == "quận 9" || trimmedQuery == "quan 9" || trimmedQuery == "q9") {
        result.add("Quận 9")
        result.add("Quan 9")
        result.add("District 9")
    } else if (trimmedQuery == "quận 1" || trimmedQuery == "quan 1" || trimmedQuery == "q1") {
        result.add("Quận 1")
        result.add("Quan 1")
        result.add("District 1")
    } else if (trimmedQuery == "quận 2" || trimmedQuery == "quan 2" || trimmedQuery == "q2") {
        result.add("Quận 2")
        result.add("Quan 2")
        result.add("District 2")
    } else if (trimmedQuery == "quận 3" || trimmedQuery == "quan 3" || trimmedQuery == "q3") {
        result.add("Quận 3")
        result.add("Quan 3")
        result.add("District 3")
    } else if (trimmedQuery == "quận 4" || trimmedQuery == "quan 4" || trimmedQuery == "q4") {
        result.add("Quận 4")
        result.add("Quan 4")
        result.add("District 4")
    } else if (trimmedQuery == "quận 5" || trimmedQuery == "quan 5" || trimmedQuery == "q5") {
        result.add("Quận 5")
        result.add("Quan 5")
        result.add("District 5")
    } else if (trimmedQuery == "quận 6" || trimmedQuery == "quan 6" || trimmedQuery == "q6") {
        result.add("Quận 6")
        result.add("Quan 6")
        result.add("District 6")
    } else if (trimmedQuery == "quận 7" || trimmedQuery == "quan 7" || trimmedQuery == "q7") {
        result.add("Quận 7")
        result.add("Quan 7")
        result.add("District 7")
    } else if (trimmedQuery == "quận 8" || trimmedQuery == "quan 8" || trimmedQuery == "q8") {
        result.add("Quận 8")
        result.add("Quan 8")
        result.add("District 8")
    } else if (trimmedQuery == "quận 10" || trimmedQuery == "quan 10" || trimmedQuery == "q10") {
        result.add("Quận 10")
        result.add("Quan 10")
        result.add("District 10")
    } else if (trimmedQuery == "quận 11" || trimmedQuery == "quan 11" || trimmedQuery == "q11") {
        result.add("Quận 11")
        result.add("Quan 11")
        result.add("District 11")
    } else if (trimmedQuery == "quận 12" || trimmedQuery == "quan 12" || trimmedQuery == "q12") {
        result.add("Quận 12")
        result.add("Quan 12")
        result.add("District 12")
    } else {
        result.add(trimmedQuery)
    }

    return result
}


fun filterInvalidData(stores: MutableList<Store>): MutableList<Store> {
    for (i in 0 until stores.size) {
        val store = stores.elementAt(i)
        if (store.id < 0)
            stores.removeAt(i)

        if (store.lat.isNullOrBlank() || store.lng.isNullOrBlank())
            stores.removeAt(i)
    }

    return stores
}

fun isValidUserUid(uid: String): Boolean {
    return !uid.isBlank() && uid != "null"
}