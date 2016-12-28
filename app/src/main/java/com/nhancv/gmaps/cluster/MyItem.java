package com.nhancv.gmaps.cluster;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by nhancao on 12/28/16.
 */

public class MyItem implements ClusterItem {

    public final String name;
    public final int profilePhoto;
    private final LatLng mPosition;

    public MyItem(LatLng position, String name, int pictureResource) {
        this.name = name;
        profilePhoto = pictureResource;
        mPosition = position;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return null;
    }

    public String getSnippet() {
        return null;
    }
}
