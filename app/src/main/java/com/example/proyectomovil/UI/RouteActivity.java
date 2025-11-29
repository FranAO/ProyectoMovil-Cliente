package com.example.proyectomovil.UI;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectomovil.R;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;

public class RouteActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        findViewById(R.id.card_back_button).setOnClickListener(v -> finish());

        mapView = findViewById(R.id.mapView);
        MapboxMap mapboxMap = mapView.getMapboxMap();

        mapboxMap.loadStyleUri("mapbox://styles/mapbox/streets-v12", style -> {
            mapboxMap.setCamera(
                    new CameraOptions.Builder()
                            .center(Point.fromLngLat(-98.0, 39.5))
                            .zoom(3.0)
                            .build()
            );
        });
    }
}