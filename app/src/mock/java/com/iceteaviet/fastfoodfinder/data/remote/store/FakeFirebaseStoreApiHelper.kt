package com.iceteaviet.fastfoodfinder.data.remote.store

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.utils.StoreType
import java.util.TreeMap
import kotlin.collections.ArrayList

class FakeFirebaseStoreApiHelper(private val context: Context) : StoreApiHelper {

    private var STORE_SERVICE_DATA: MutableList<Store> = ArrayList()
    private var STORE_COMMENT_SERVICE_DATA: MutableMap<String, MutableList<Comment>> = TreeMap()

    override suspend fun getAllStores(): List<Store> {
        if (STORE_SERVICE_DATA.isEmpty()) {
            STORE_SERVICE_DATA = loadStoresFromAssets().toMutableList()
        }
        return STORE_SERVICE_DATA
    }

    override suspend fun getComments(storeId: String): List<Comment> {
        return STORE_COMMENT_SERVICE_DATA[storeId] ?: ArrayList()
    }

    override fun insertOrUpdateComment(storeId: String, comment: Comment) {
        val comments = STORE_COMMENT_SERVICE_DATA.getOrPut(storeId) { ArrayList() }
        comments.add(comment)
    }

    private fun loadStoresFromAssets(): List<Store> {
        val gson = Gson()
        val stores = ArrayList<Store>()
        val fileToType = mapOf(
            "circle_k.json" to StoreType.TYPE_CIRCLE_K,
            "ministop.json" to StoreType.TYPE_MINI_STOP,
            "familymart.json" to StoreType.TYPE_FAMILY_MART,
            "bsmart.json" to StoreType.TYPE_BSMART,
            "shopngo.json" to StoreType.TYPE_SHOP_N_GO,
            "711.json" to StoreType.TYPE_7_ELEVEN,
        )

        var id = 1
        for ((fileName, type) in fileToType) {
            try {
                val json = context.assets.open("stores_data/$fileName")
                    .bufferedReader().use { it.readText() }
                val root = gson.fromJson(json, object : TypeToken<Map<String, List<Map<String, String>>>>() {}) as Map<String, *>
                @Suppress("UNCHECKED_CAST")
                val markers = root["markers_add"] as? List<Map<String, String>> ?: continue
                for (marker in markers) {
                    stores.add(Store(
                        id = id++,
                        title = marker["title"].orEmpty(),
                        address = marker["address"].orEmpty(),
                        lat = marker["lat"]?.toDoubleOrNull() ?: 0.0,
                        lng = marker["lng"]?.toDoubleOrNull() ?: 0.0,
                        tel = marker["tel"].orEmpty(),
                        type = type,
                    ))
                }
            } catch (e: Exception) {
                throw RuntimeException("Failed to load stores from stores_data/$fileName", e)
            }
        }
        return stores
    }
}
