@file:JvmName("DataUtils")

package com.iceteaviet.fastfoodfinder.utils

import com.google.gson.Gson
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.ui.ar.model.AugmentedPOI
import com.iceteaviet.fastfoodfinder.ui.main.search.model.SearchStoreItem
import com.iceteaviet.fastfoodfinder.ui.settings.discountnotify.DiscountNotifyPresenter
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

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

fun getFakeComment(): Comment {
    val contents = arrayOf("Octopus salad with bacon, sundries tomatoes, potatoes and some other magic ingredients was absolutely or of this world delicious. I loved it so much I forgot to snap an Instagram photo.", "Loved it here. Great atmosphere, was packed during lunch time. The smoked salmon eggs on bread and salted caramel cupcake were really good. The sunrise drink was like a lukewarm smoothie though.", "Perfect place to work, delicious food and coffee, little bit expensive but staff let you work peacefully. Quite busy/noisy but a wonderful place to discover. Hey, they make their own cupcakes :)", "Great atmosphere, very nice place for a quick snack or a meal. Ask for the meat pies. The lasagna is great too. And always check out the specials on the blackboard", "Very friendly stuff, the fruit juices and food are super good! Very trendy and the shop has nice quality things ! Happy to stumble into this cafeteria :D")

    val mediaUrls = arrayOf("http://i.imgur.com/RHdsWRW.jpg", "http://i.imgur.com/IsfQQhd.jpg", "", "http://i.imgur.com/tNu5G5D.jpg", "http://i.imgur.com/QruogAF.jpg")

    return Comment("Scarlett",
            "http://i.imgur.com/u9mpkNC.jpg",
            contents[getRandomInt(0, 4)],
            mediaUrls[getRandomInt(0, 4)],
            getRandomDate(),
            getRandomLong())
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


fun getFakeCircleKStoreList(): List<Store> {
    val stores = ArrayList<Store>()
    stores.add(Store(1, "Circle K Le Thi Rieng", "148 Le Thi Rieng, Ben Thanh Ward, District 1, Ho Chi Minh, Vietnam", "10.770379", "106.68912279999995", "3925 6620", StoreType.TYPE_CIRCLE_K))
    stores.add(Store(7, "Circle K Ly Tu Trong", "238 Ly Tu Trong, Ben Thanh Ward, District 1, Ho Chi Minh, Vietnam", "10.7721924", "106.69433409999999", "3822 7403", StoreType.TYPE_CIRCLE_K))
    return stores
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

fun getFakeSearchStoreItems(): List<SearchStoreItem> {
    val stores = ArrayList<SearchStoreItem>()
    stores.add(SearchStoreItem(Store(1, "Circle K Le Thi Rieng", "148 Le Thi Rieng, Ben Thanh Ward, District 1, Ho Chi Minh, Vietnam", "10.770379", "106.68912279999995", "3925 6620", StoreType.TYPE_CIRCLE_K), ""))
    stores.add(SearchStoreItem(Store(2, "FamilyMart - Hậu Giang", "973 Hậu Giang, P. 11, Quận 6, TP. HCM", "10.7457782220847", "106.6261117905378", "3755 0439", StoreType.TYPE_FAMILY_MART), ""))
    stores.add(SearchStoreItem(Store(3, "FamilyMart - Nguyễn Lương Bằng", "180 Nguyễn Lương Bằng, P. Tân Phú, Quận 7, TP. HCM", "10.727042", "106.722703", "5417 3390", StoreType.TYPE_FAMILY_MART), ""))
    stores.add(SearchStoreItem(Store(4, "Family Mart - Tạ Quang Bửu", "811 Tạ Quang Bửu, P. 5, Quận 8, TP. HCM", "10.736488", "106.670374", "3835 3193", StoreType.TYPE_FAMILY_MART), ""))
    stores.add(SearchStoreItem(Store(5, "Family Mart - Nguyễn Văn Công", "534 Nguyễn Văn Công, Phường 3, Quận Gò Vấp, TP. HCM", "10.819417", "106.674821", "3835 3193", StoreType.TYPE_FAMILY_MART), ""))
    stores.add(SearchStoreItem(Store(6, "Shop & Go - Phan Đình Phùng", "180 Phan Đình Phùng, P. 2, Quận Phú Nhuận, TP. HCM", "10.7955070000000", "106.6825610000000", "38 353 193", StoreType.TYPE_SHOP_N_GO), ""))
    stores.add(SearchStoreItem(Store(7, "Circle K Ly Tu Trong", "238 Ly Tu Trong, Ben Thanh Ward, District 1, Ho Chi Minh, Vietnam", "10.7721924", "106.69433409999999", "3822 7403", StoreType.TYPE_CIRCLE_K), ""))
    stores.add(SearchStoreItem(Store(8, "Familymart - Đường D2", "39 Đường D2, P. 25, Quận Bình Thạnh, TP. HCM", "10.80252", "106.715622", "35 126 283", StoreType.TYPE_FAMILY_MART), ""))
    stores.add(SearchStoreItem(Store(9, "FamilyMart - 123 Nguyễn Đình Chiểu", "123 Nguyễn Đình Chiểu, Phường 6, Quận 3, TP. HCM", "10.7775462", "106.6892408999999", "3835 3193", StoreType.TYPE_FAMILY_MART), ""))
    stores.add(SearchStoreItem(Store(10, "FamilyMart - Tôn Dật Tiến", "Tôn Dật Tiên, Quận 7, TP. HCM", "10.723322", "106.71498", "3835 3193", StoreType.TYPE_FAMILY_MART), ""))
    return stores
}

fun getFakeArPoints(): List<AugmentedPOI> {
    val arPoints = ArrayList<AugmentedPOI>()
    arPoints.add(AugmentedPOI("Circle K Le Thi Rieng", 10.770379, 106.68912279999995, 0.0, getStoreLogoDrawableRes(StoreType.TYPE_CIRCLE_K)))
    arPoints.add(AugmentedPOI("FamilyMart - Hậu Giang", 10.7457782220847, 106.6261117905378, 0.0, getStoreLogoDrawableRes(StoreType.TYPE_FAMILY_MART)))
    arPoints.add(AugmentedPOI("FamilyMart - Nguyễn Lương Bằng", 10.727042, 106.722703, 0.0, getStoreLogoDrawableRes(StoreType.TYPE_FAMILY_MART)))
    arPoints.add(AugmentedPOI("Family Mart - Tạ Quang Bửu", 10.736488, 106.670374, 0.0, getStoreLogoDrawableRes(StoreType.TYPE_FAMILY_MART)))
    arPoints.add(AugmentedPOI("Family Mart - Nguyễn Văn Công", 10.819417, 106.674821, 0.0, getStoreLogoDrawableRes(StoreType.TYPE_FAMILY_MART)))
    arPoints.add(AugmentedPOI("Shop & Go - Phan Đình Phùng", 10.7955070000000, 106.6825610000000, 0.0, getStoreLogoDrawableRes(StoreType.TYPE_SHOP_N_GO)))
    arPoints.add(AugmentedPOI("Circle K Ly Tu Trong", 10.7721924, 106.69433409999999, 0.0, getStoreLogoDrawableRes(StoreType.TYPE_CIRCLE_K)))
    arPoints.add(AugmentedPOI("Familymart - Đường D2", 10.80252, 106.715622, 0.0, getStoreLogoDrawableRes(StoreType.TYPE_FAMILY_MART)))
    arPoints.add(AugmentedPOI("FamilyMart - 123 Nguyễn Đình Chiểu", 10.7775462, 106.6892408999999, 0.0, getStoreLogoDrawableRes(StoreType.TYPE_FAMILY_MART)))
    arPoints.add(AugmentedPOI("FamilyMart - Tôn Dật Tiến", 10.723322, 106.71498, 0.0, getStoreLogoDrawableRes(StoreType.TYPE_FAMILY_MART)))

    return arPoints
}

fun getFakeStoreIds(): List<Int> {
    val storeIds = ArrayList<Int>()
    storeIds.add(1)
    storeIds.add(2)
    storeIds.add(3)
    storeIds.add(4)
    return storeIds
}

fun getFakeUserStoreLists(): List<UserStoreList> {
    val userStoreLists = ArrayList<UserStoreList>()

    userStoreLists.add(UserStoreList(0, getFakeStoreIds(), 1, "My Saved Places"))
    userStoreLists.add(UserStoreList(1, getFakeStoreIds(), 2, "My Favourite Places"))

    return userStoreLists
}

fun getFakeUserMultiStoreLists(): List<UserStoreList> {
    val userStoreLists = ArrayList<UserStoreList>()

    userStoreLists.add(UserStoreList(0, getFakeStoreIds(), 1, "My Saved Places"))
    userStoreLists.add(UserStoreList(1, getFakeStoreIds(), 2, "My Favourite Places"))
    userStoreLists.add(UserStoreList(2, getFakeStoreIds(), 3, "My Third List"))
    userStoreLists.add(UserStoreList(3, getFakeStoreIds(), 4, "My Forth List"))

    return userStoreLists
}

fun getFakeMapsDirection(): MapsDirection {
    val directionData = "{\n" +
            "   \"routes\":[\n" +
            "      {\n" +
            "         \"bounds\":{\n" +
            "            \"northeast\":{\n" +
            "               \"lat\":10.7687332,\n" +
            "               \"lng\":106.7391619\n" +
            "            },\n" +
            "            \"southwest\":{\n" +
            "               \"lat\":10.7357494,\n" +
            "               \"lng\":106.6997886\n" +
            "            }\n" +
            "         },\n" +
            "         \"copyrights\":\"Dữ liệu bản đồ ©2019\",\n" +
            "         \"legs\":[\n" +
            "            {\n" +
            "               \"distance\":{\n" +
            "                  \"text\":\"7,6 km\",\n" +
            "                  \"value\":7586\n" +
            "               },\n" +
            "               \"duration\":{\n" +
            "                  \"text\":\"21 phút\",\n" +
            "                  \"value\":1279\n" +
            "               },\n" +
            "               \"end_address\":\"164 Nguyễn Công Trứ, Phường Nguyễn Thái Bình, Quận 1, Hồ Chí Minh, Việt Nam\",\n" +
            "               \"end_location\":{\n" +
            "                  \"lat\":10.7687332,\n" +
            "                  \"lng\":106.7014437\n" +
            "               },\n" +
            "               \"start_address\":\"566 Nguyễn Văn Quỳ, Phú Thuận, Quận 7, Hồ Chí Minh, Việt Nam\",\n" +
            "               \"start_location\":{\n" +
            "                  \"lat\":10.7359556,\n" +
            "                  \"lng\":106.7367226\n" +
            "               },\n" +
            "               \"steps\":[\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"57 m\",\n" +
            "                        \"value\":57\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"1 phút\",\n" +
            "                        \"value\":20\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.7361299,\n" +
            "                        \"lng\":106.7372125\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Đi về hướng \\u003cb\\u003eĐông\\u003c/b\\u003e lên \\u003cb\\u003eĐường Số 1\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eĐường bị giới hạn sử dụng\\u003c/div\\u003e\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"wzo`Ao_njSa@aB\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.7359556,\n" +
            "                        \"lng\":106.7367226\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"46 m\",\n" +
            "                        \"value\":46\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"1 phút\",\n" +
            "                        \"value\":11\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.7357494,\n" +
            "                        \"lng\":106.7373757\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Rẽ \\u003cb\\u003ephải\\u003c/b\\u003e về hướng \\u003cb\\u003eĐường số 2\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eĐường bị giới hạn sử dụng\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\":\"turn-right\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"y{o`AqbnjSjAa@\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.7361299,\n" +
            "                        \"lng\":106.7372125\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"83 m\",\n" +
            "                        \"value\":83\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"1 phút\",\n" +
            "                        \"value\":26\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.7360084,\n" +
            "                        \"lng\":106.7380921\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Rẽ \\u003cb\\u003etrái\\u003c/b\\u003e tại công viên vào \\u003cb\\u003eĐường số 2\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eĐường bị giới hạn sử dụng\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\":\"turn-left\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"myo`AscnjSYiAYcA\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.7357494,\n" +
            "                        \"lng\":106.7373757\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"0,1 km\",\n" +
            "                        \"value\":98\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"1 phút\",\n" +
            "                        \"value\":26\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.7368277,\n" +
            "                        \"lng\":106.737761\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Rẽ \\u003cb\\u003etrái\\u003c/b\\u003e tại ANN CAFE vào \\u003cb\\u003eĐường D4\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eĐường bị giới hạn sử dụng\\u003c/div\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eBăng qua Milano Coffee (ở phía bên trái)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\":\"turn-left\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"a{o`AahnjScD`A\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.7360084,\n" +
            "                        \"lng\":106.7380921\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"0,2 km\",\n" +
            "                        \"value\":165\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"1 phút\",\n" +
            "                        \"value\":50\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.7373779,\n" +
            "                        \"lng\":106.7391619\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Rẽ \\u003cb\\u003ephải\\u003c/b\\u003e tại COSCO (LYG) Loading Arm vào \\u003cb\\u003eĐường N8\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eĐường bị giới hạn sử dụng\\u003c/div\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eBăng qua Shop hàng Thái Lan và hàng ngoại nhập (ở phía bên phải)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\":\"turn-right\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"e`p`A_fnjSQu@M_@Sm@Og@Oq@Yy@\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.7368277,\n" +
            "                        \"lng\":106.737761\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"0,5 km\",\n" +
            "                        \"value\":508\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"2 phút\",\n" +
            "                        \"value\":95\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.7411401,\n" +
            "                        \"lng\":106.7365321\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Rẽ \\u003cb\\u003etrái\\u003c/b\\u003e tại Công ty Thost.vn vào \\u003cb\\u003eĐường Đào Trí\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eBăng qua Cafe Ngọc Tâm (ở phía bên trái)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\":\"turn-left\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"scp`AwnnjSaAp@CBc@Vw@d@_@TuBpA}A~@_CxA_B~@s@b@KF[T]T\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.7373779,\n" +
            "                        \"lng\":106.7391619\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"0,7 km\",\n" +
            "                        \"value\":745\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"2 phút\",\n" +
            "                        \"value\":107\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.737817,\n" +
            "                        \"lng\":106.7306225\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Rẽ \\u003cb\\u003etrái\\u003c/b\\u003e tại Cafê Võng Gầm Cầu Phú Mỹ vào \\u003cb\\u003eNguyễn Văn Quỳ\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eBăng qua Công Ty TNHH SX TM DV IF (ở phía bên trái)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\":\"turn-left\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"c{p`Ai~mjSNb@HPP`@l@vAjBhE\\\\r@HNBF@@BDFFHH^r@HPp@xAlApC`@`AdA~Bl@pA~@bBLR\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.7411401,\n" +
            "                        \"lng\":106.7365321\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"2,4 km\",\n" +
            "                        \"value\":2420\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"7 phút\",\n" +
            "                        \"value\":419\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.7555296,\n" +
            "                        \"lng\":106.7232546\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Rẽ \\u003cb\\u003ephải\\u003c/b\\u003e tại Khách Sạn Gia Huy vào \\u003cb\\u003eHuỳnh Tấn Phát\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eBăng qua Clb Bida C2 (ở phía bên phải)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\":\"turn-right\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"kfp`AkyljSKl@k@BWBQ@O@}CVO@aBLe@BSB_E\\\\gCRsAJ}Fd@mAL_AJsCTE@oALI@G?K@wAJE?}@DI@u@Fe@FQ@w@HKAcAJmAHaBDWBw@D}@DK?oBHgAD_CHiBFqADaCJoADY@cADy@Hy@HM@ODMDMFOFOJONGFQVU^IZEREL?BU~AYpBE`@Gb@EVCTU|AQlACJUzAE`@W|AOrACT\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.737817,\n" +
            "                        \"lng\":106.7306225\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"0,6 km\",\n" +
            "                        \"value\":602\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"1 phút\",\n" +
            "                        \"value\":71\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.7568754,\n" +
            "                        \"lng\":106.7183758\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Rẽ \\u003cb\\u003etrái\\u003c/b\\u003e tại Tiệm Vàng Tăng Hồng vào \\u003cb\\u003eCầu Tân Thuận\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eBăng qua Cửa Hàng Nhôm Kính Cao Cấp Thuận Thành (ở phía bên trái)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\":\"turn-left\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"aus`AikkjSHHVXLVHBFDDDFHDFHNBRBJ?L?JALKZUj@EHWn@m@|AGNEN}@bCUj@Ur@]dAg@zAWt@Q`@GRITKN\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.7555296,\n" +
            "                        \"lng\":106.7232546\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"1,6 km\",\n" +
            "                        \"value\":1573\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"3 phút\",\n" +
            "                        \"value\":201\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.7651211,\n" +
            "                        \"lng\":106.7072401\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Tại Dia Warehouse, tiếp tục vào \\u003cb\\u003eNguyễn Tất Thành\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eBăng qua Công Ty Tnhh Thúy Nam Phong (ở phía bên trái)\\u003c/div\\u003e\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"o}s`A{ljjSk@vA_@dAqApD]v@mEdJA@}DbIQ\\\\}A~CqGxMS`@k@dAk@bAa@t@EHGJEDA@OFgA`@cBl@gBp@E@qBl@a@LaA^\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.7568754,\n" +
            "                        \"lng\":106.7183758\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"0,4 km\",\n" +
            "                        \"value\":433\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"2 phút\",\n" +
            "                        \"value\":90\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.763467,\n" +
            "                        \"lng\":106.7037776\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Rẽ \\u003cb\\u003etrái\\u003c/b\\u003e tại Công Ty Tnhh Trader Indochine Viet Nam vào \\u003cb\\u003eHoàng Diệu\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eBăng qua Cuốn+ (ở phía bên phải)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\":\"turn-left\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"_qu`AgghjSGBGBBLd@xAb@|AHR\\\\jARl@Vj@x@hCX^T^fAvADH\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.7651211,\n" +
            "                        \"lng\":106.7072401\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"39 m\",\n" +
            "                        \"value\":39\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"1 phút\",\n" +
            "                        \"value\":8\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.7637206,\n" +
            "                        \"lng\":106.703531\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Rẽ \\u003cb\\u003ephải\\u003c/b\\u003e tại Áo Cưới Black &amp; White vào \\u003cb\\u003eĐoàn Văn Bơ\\u003c/b\\u003e\",\n" +
            "                     \"maneuver\":\"turn-right\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"ufu`AsqgjSONONQP\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.763467,\n" +
            "                        \"lng\":106.7037776\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"0,6 km\",\n" +
            "                        \"value\":575\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"1 phút\",\n" +
            "                        \"value\":87\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.7672848,\n" +
            "                        \"lng\":106.6997886\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Chếch sang \\u003cb\\u003etrái\\u003c/b\\u003e tại Hủ tiếu Nam Vang Nhân Nghĩa vào \\u003cb\\u003eCalmette\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eBăng qua Nhà May Anh Đức (ở phía bên phải)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\":\"turn-slight-left\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"ghu`AapgjSO^A?S\\\\_AvA_BfCc@p@SV_@\\\\A@OLA@KJiAbA[ZSNYVGDoAx@cAx@]VIFe@\\\\KF\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.7637206,\n" +
            "                        \"lng\":106.703531\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\":{\n" +
            "                        \"text\":\"0,2 km\",\n" +
            "                        \"value\":242\n" +
            "                     },\n" +
            "                     \"duration\":{\n" +
            "                        \"text\":\"1 phút\",\n" +
            "                        \"value\":68\n" +
            "                     },\n" +
            "                     \"end_location\":{\n" +
            "                        \"lat\":10.7687332,\n" +
            "                        \"lng\":106.7014437\n" +
            "                     },\n" +
            "                     \"html_instructions\":\"Rẽ \\u003cb\\u003ephải\\u003c/b\\u003e tại Cửa hàng Huệ Ký vào \\u003cb\\u003eNguyễn Công Trứ\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eBăng qua Công Ty Tnhh Mitsu National (ở phía bên phải)\\u003c/div\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eĐiểm đến sẽ ở bên trái\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\":\"turn-right\",\n" +
            "                     \"polyline\":{\n" +
            "                        \"points\":\"o~u`AuxfjSGGgAqAwAaBcBuBUW\"\n" +
            "                     },\n" +
            "                     \"start_location\":{\n" +
            "                        \"lat\":10.7672848,\n" +
            "                        \"lng\":106.6997886\n" +
            "                     },\n" +
            "                     \"travel_mode\":\"DRIVING\"\n" +
            "                  }\n" +
            "               ],\n" +
            "               \"traffic_speed_entry\":[\n" +
            "\n" +
            "               ],\n" +
            "               \"via_waypoint\":[\n" +
            "\n" +
            "               ]\n" +
            "            }\n" +
            "         ],\n" +
            "         \"overview_polyline\":{\n" +
            "            \"points\":\"wzo`Ao_njSa@aBjAa@s@mCcD`AQu@a@mA_@yAYy@aAp@g@ZwAz@sLjHyBvAxAnDvCtGVXh@dA~BjFfB`El@pA~@bBLRKl@cAFoE\\\\{Ir@{E^kIr@iHp@yF^w@HcAFcAJoDNyCNaL`@}HX}BNgAJ{@Z_@ZY^U^On@u@bFcAjHcAnHCTHHd@p@PHLNNVBRBXAX_A`CyB`GqB`G{@`Cw@fBqBvFkF|K_EdIuKxT_ChEMPQHyGbCsCz@iAb@GBBLhAvDf@~Aj@xAx@hCX^|AvBDHONa@`@Q^sAtBcCxDs@t@_@\\\\sCfCwA~@aBpAo@d@KFGG_DsDyBmC\"\n" +
            "         },\n" +
            "         \"summary\":\"Huỳnh Tấn Phát\",\n" +
            "         \"warnings\":[\n" +
            "\n" +
            "         ],\n" +
            "         \"waypoint_order\":[\n" +
            "\n" +
            "         ]\n" +
            "      }\n" +
            "   ]\n" +
            "}"
    return Gson().fromJson(directionData, MapsDirection::class.java)
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
    val result = ArrayList<Store>()

    for (i in 0 until stores.size) {
        val store = stores.elementAt(i)
        if (store.id < 0) {
            continue
        }

        if (!isValidLat(store.lat) || !isValidLng(store.lng) || store.address.isBlank()) {
            continue
        }

        result.add(store)
    }

    return result
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

fun storesToArPoints(stores: List<Store>): List<AugmentedPOI> {
    val arPoints = ArrayList<AugmentedPOI>()
    for (i in stores.indices) {
        arPoints.add(AugmentedPOI(stores[i].title,
                stores[i].lat.toDouble(),
                stores[i].lng.toDouble(),
                0.0,
                getStoreLogoDrawableRes(stores[i].type)))
    }
    return arPoints
}

fun getStoreNameByKey(key: String?): String {
    when (key) {
        DiscountNotifyPresenter.KEY_CIRCLE_K -> return "Cirle K"
        DiscountNotifyPresenter.KEY_BSMART -> return "B’s mart"
        DiscountNotifyPresenter.KEY_FAMILY_MART -> return "Family mart"
        DiscountNotifyPresenter.KEY_MINI_STOP -> return "Ministop"
        DiscountNotifyPresenter.KEY_SHOP_N_GO -> return "Shop & Go"
        else -> return ""
    }
}
