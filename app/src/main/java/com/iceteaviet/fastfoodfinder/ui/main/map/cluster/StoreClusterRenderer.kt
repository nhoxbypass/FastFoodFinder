package com.iceteaviet.fastfoodfinder.ui.main.map.cluster

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreIcon

class StoreClusterRenderer(
    private val ctx: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<StoreClusterItem>,
) : DefaultClusterRenderer<StoreClusterItem>(ctx, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: StoreClusterItem, markerOptions: MarkerOptions) {
        markerOptions.icon(getStoreIcon(ctx.resources, item.getStore().type, -1, -1))
            .title(item.title)
            .snippet(item.snippet)
    }
}
