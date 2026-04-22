package com.iceteaviet.fastfoodfinder.ui.main.map.cluster

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.iceteaviet.fastfoodfinder.domain.model.Store

class StoreClusterItem(private val store: Store) : ClusterItem {

    private val position = LatLng(store.lat, store.lng)

    override fun getPosition(): LatLng = position

    override fun getTitle(): String = store.title

    override fun getSnippet(): String = store.address

    override fun getZIndex(): Float? = null

    fun getStore(): Store = store
}
