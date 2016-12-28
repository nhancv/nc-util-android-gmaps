package com.nhancv.gmaps.cluster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.nhancv.gmaps.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhancao on 12/28/16.
 */

public class MyRenderer extends DefaultClusterRenderer<MyItem> {

    private IconGenerator iconGenerator;
    private IconGenerator clusterIconGenerator;
    private ImageView imageView;
    private ImageView clusterImageView;
    private int dimension;

    private Context context;

    public MyRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        iconGenerator = new IconGenerator(context);
        clusterIconGenerator = new IconGenerator(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View multiProfile = inflater.inflate(R.layout.multi_profile, null);
            clusterIconGenerator.setContentView(multiProfile);

            clusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            imageView = new ImageView(context);
            dimension = (int) context.getResources().getDimension(R.dimen.custom_profile_image);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(dimension, dimension));
            int padding = (int) context.getResources().getDimension(R.dimen.custom_profile_padding);
            imageView.setPadding(padding, padding, padding, padding);
            iconGenerator.setContentView(imageView);

            //@nhancv TODO: turn off background default of marker
            iconGenerator.setBackground(null);
            clusterIconGenerator.setBackground(null);

        }
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem myItem, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.
        imageView.setImageResource(myItem.profilePhoto);
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(myItem.name);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<MyItem> cluster, MarkerOptions markerOptions) {
        // Draw multiple people.
        // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
        List<Drawable> profilePhotos = new ArrayList<>(Math.min(4, cluster.getSize()));
        int width = dimension;
        int height = dimension;

        for (MyItem p : cluster.getItems()) {
            // Draw 4 at most.
            if (profilePhotos.size() == 4) break;
            Drawable drawable = context.getResources().getDrawable(p.profilePhoto);
            drawable.setBounds(0, 0, width, height);
            profilePhotos.add(drawable);
        }
        MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
        multiDrawable.setBounds(0, 0, width, height);

        clusterImageView.setImageDrawable(multiDrawable);
        Bitmap icon = clusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }
}
