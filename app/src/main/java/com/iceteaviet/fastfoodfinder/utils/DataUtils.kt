@file:JvmName("DataUtils")

package com.iceteaviet.fastfoodfinder.utils

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by binhlt on 23/11/2016.
 */

fun getDefaultUserStoreLists(): List<UserStoreList> {
    val userStoreLists = ArrayList<UserStoreList>()

    userStoreLists.add(UserStoreList(0, ArrayList(), 1, "My Saved Places"))
    userStoreLists.add(UserStoreList(1, ArrayList(), 2, "My Favourite Places"))

    return userStoreLists
}

fun getFakeComments(): List<Comment> {
    val names = arrayOf("Scarlett", "Bà Tưng", "Ngọc Trinh", "Chi Pu", "Trâm Anh")

    val avatars = arrayOf("http://i.imgur.com/u9mpkNC.jpg", "http://i.imgur.com/qQxDOZT.jpg", "http://i.imgur.com/VJ3FAB2.jpg", "http://i.imgur.com/31vADfq.jpg", "http://i.imgur.com/XUem99n.jpg")

    val contents = arrayOf("Octopus salad with bacon, sundries tomatoes, potatoes and some other magic ingredients was absolutely or of this world delicious. I loved it so much I forgot to snap an Instagram photo.", "Loved it here. Great atmosphere, was packed during lunch time. The smoked salmon eggs on bread and salted caramel cupcake were really good. The sunrise drink was like a lukewarm smoothie though.", "Perfect place to work, delicious food and coffee, little bit expensive but staff let you work peacefully. Quite busy/noisy but a wonderful place to discover. Hey, they make their own cupcakes :)", "Great atmosphere, very nice place for a quick snack or a meal. Ask for the meat pies. The lasagna is great too. And always check out the specials on the blackboard", "Very friendly stuff, the fruit juices and food are super good! Very trendy and the shop has nice quality things ! Happy to stumble into this cafeteria :D")

    val mediaUrls = arrayOf("http://i.imgur.com/RHdsWRW.jpg", "http://i.imgur.com/IsfQQhd.jpg", "", "http://i.imgur.com/tNu5G5D.jpg", "http://i.imgur.com/QruogAF.jpg")

    val comments = ArrayList<Comment>()
    for (i in 0 until getRandomInt(3, 5)) {
        val index = getRandomInt(0, 4)
        comments.add(Comment(names[index],
                avatars[index],
                contents[getRandomInt(0, 4)],
                mediaUrls[getRandomInt(0, 4)],
                getRandomDate(),
                getRandomLong()))
    }
    return comments
}

private fun getRandomDate(): String {
    val dfDateTime = SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.getDefault())
    val year = getRandomInt(2015, 2016)
    val month = getRandomInt(0, 11)
    val hour = getRandomInt(9, 22)
    val min = getRandomInt(0, 59)
    val sec = getRandomInt(0, 59)
    val gc = GregorianCalendar(year, month, 1)
    val day = getRandomInt(1, gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH))
    gc.set(year, month, day, hour, min, sec)
    return dfDateTime.format(gc.time)
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
 * Get random Long
 */
fun getRandomLong(): Long {
    val rand = Random()
    return rand.nextLong()
}

/**
 * Get store type by keyword
 */
fun getStoreType(key: String?): Int {
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

fun getStoreSearchString(storeType: Int): String {
    return when (storeType) {
        StoreType.TYPE_CIRCLE_K -> "Circle K"
        StoreType.TYPE_MINI_STOP -> "Mini Stop"
        StoreType.TYPE_FAMILY_MART -> "Family Mart"
        StoreType.TYPE_BSMART -> "B'smart"
        StoreType.TYPE_SHOP_N_GO -> "Shop and Go"
        else -> ""
    }
}


fun filterInvalidData(stores: MutableList<Store>): MutableList<Store> {
    for (i in 0 until stores.size) {
        val store = stores.elementAt(i)
        if (store.id < 0)
            stores.removeAt(i)

        if (store.lat.isBlank() || store.lng.isBlank())
            stores.removeAt(i)
    }

    return stores
}

fun isValidUserUid(uid: String): Boolean {
    return !uid.isBlank() && uid != "null"
}

val VALID_EMAIL_ADDRESS_REGEX: Pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
fun isValidEmail(email: String): Boolean {
    val matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email)
    return matcher.find()
}

val VALID_PWD_REGEX: Pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$\n", Pattern.CASE_INSENSITIVE)
fun isValidPassword(password: String): Boolean {
    return password.trim().length >= 8
}