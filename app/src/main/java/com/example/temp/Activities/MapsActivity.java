package com.example.temp.Activities;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.example.temp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_maps);

        // Lấy SupportMapFragment và thông báo khi bản đồ đã sẵn sàng
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Đặt vị trí và marker
        LatLng dhbk = new LatLng(10.762622, 106.660172); // Tọa độ Đại học Bách Khoa
        mMap.addMarker(new MarkerOptions().position(dhbk).title("Đại học Tôn Đức Thắng"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhbk, 15)); // Zoom vào vị trí
    }
}
