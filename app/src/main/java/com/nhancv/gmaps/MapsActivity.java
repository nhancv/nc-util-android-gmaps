package com.nhancv.gmaps;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.nhancv.gmaps.cluster.MyItem;
import com.nhancv.gmaps.cluster.MyRenderer;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        ClusterManager.OnClusterClickListener<MyItem>,
        ClusterManager.OnClusterInfoWindowClickListener<MyItem>,
        ClusterManager.OnClusterItemClickListener<MyItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<MyItem> {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap map;
    private ClusterManager<MyItem> clusterManager;
    private Random random = new Random(1984);

    public void rotateMarker(final Marker marker, final float toRotation) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1555;

        final Interpolator interpolator = new AccelerateInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    // loop
                    rotateMarker(marker, toRotation * (-1));
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        setMap(googleMap);

        //@nhancv TODO: Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        getMap().addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        getMap().moveCamera(CameraUpdateFactory.newLatLng(sydney));


        //@nhancv TODO: Import example data
        try {
            GeoJsonLayer layer = new GeoJsonLayer(getMap(), R.raw.earthquake,
                    getApplicationContext());
            layer.addLayerToMap();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        //@nhancv TODO: Setup Cluster
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 9.5f));
        clusterManager = new ClusterManager<>(this, getMap());

        MyRenderer myRenderer = new MyRenderer(getApplicationContext(), getMap(), clusterManager);
        clusterManager.setRenderer(myRenderer);
        getMap().setOnCameraIdleListener(this);
        getMap().setOnMarkerClickListener(clusterManager);
        getMap().setOnInfoWindowClickListener(clusterManager);
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterInfoWindowClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();

//        PulsatorLayout pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
//        pulsator.start();
    }

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    private void addItems() {

        final List<MyItem> myItemsCollection = new ArrayList<MyItem>() {
            {
                add(new MyItem(position(), "Walter", R.drawable.walter));
                add(new MyItem(position(), "Gran", R.drawable.gran));
                add(new MyItem(position(), "Ruth", R.drawable.ruth));
                add(new MyItem(position(), "Stefan", R.drawable.stefan));
                add(new MyItem(position(), "Mechanic", R.drawable.mechanic));
                add(new MyItem(position(), "Yeats", R.drawable.yeats));
                add(new MyItem(position(), "John", R.drawable.john));
                add(new MyItem(position(), "Trevor the Turtle", R.drawable.turtle));
                add(new MyItem(position(), "Teach", R.drawable.teacher));
            }
        };

        clusterManager.addItems(myItemsCollection);
        clusterManager.cluster();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Marker marker : clusterManager.getClusterMarkerCollection().getMarkers()) {
                    rotateMarker(marker, -25f);
                    break;
                }
            }
        }, 1000);
    }

    private LatLng position() {
        return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
    }

    private double random(double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    @Override
    public boolean onClusterClick(Cluster<MyItem> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //@nhancv TODO: add circle animation
        final float density = getResources().getDisplayMetrics().density;
        final float radius = 1500 * density;
        final Circle circle = getMap().addCircle(new CircleOptions().center(bounds.getCenter())
                .strokeColor(Color.CYAN).radius(radius).strokeWidth(1f));

        ValueAnimator vAnimator = new ValueAnimator();
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.RESTART);  /* PULSE */
        vAnimator.setFloatValues(0, radius);
        vAnimator.setDuration(2000);
        vAnimator.setEvaluator(new FloatEvaluator());
        vAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                circle.setRadius(animatedFraction * radius);
            }
        });
        vAnimator.start();

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MyItem> cluster) {

    }

    @Override
    public boolean onClusterItemClick(MyItem myItem) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MyItem myItem) {

    }

    @Override
    public void onCameraIdle() {
        String zoomInfo = String.format("onMapReady:zoom: %s, minZoom: %s, maxZoom: %s",
                getMap().getCameraPosition().zoom,
                getMap().getMinZoomLevel(),
                getMap().getMaxZoomLevel());
        Log.d(TAG, zoomInfo);
        clusterManager.onCameraIdle();
    }

}
