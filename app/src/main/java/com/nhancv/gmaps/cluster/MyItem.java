package com.nhancv.gmaps.cluster;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by nhancao on 12/28/16.
 */

public class MyItem implements ClusterItem {

    private final LatLng position;

    public MyItem(double lat, double lng) {
        position = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return position;
    }
}
