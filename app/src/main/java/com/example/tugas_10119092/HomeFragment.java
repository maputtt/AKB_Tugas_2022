package com.example.tugas_10119092;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

//Mahfudz Abdulloh
//10119092
//IF-3


public class HomeFragment extends Fragment {

    // variable
    FusedLocationProviderClient client;
    private GoogleMap map;
    ArrayList<LatLng> arrayList = new ArrayList<LatLng>();
    LatLng Resto1 = new LatLng(-6.888628924249521, 107.6131669970916);
    LatLng Resto2 = new LatLng(-6.902327059391242, 107.60969898465574);
    LatLng Resto3 = new LatLng(-6.896027268229655, 107.61617991243595);
    LatLng Resto4 = new LatLng(-6.894461526211141, 107.6174045970916);
    LatLng Resto5 = new LatLng(-6.8900332625721115, 107.59599082407524);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inisial view
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //inisial map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        //lokasi client
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        arrayList.add(Resto1);
        arrayList.add(Resto2);
        arrayList.add(Resto3);
        arrayList.add(Resto4);
        arrayList.add(Resto5);

        //async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                //When map is loaded
                map = googleMap;
                map.addMarker(new MarkerOptions().position(Resto1).title("Mie Gacoan Dago"));
                map.addMarker(new MarkerOptions().position(Resto2).title("Sambal Bakar Mang Ujang"));
                map.addMarker(new MarkerOptions().position(Resto3).title("Red Dimsum Dipatiukur"));
                map.addMarker(new MarkerOptions().position(Resto4).title("Bebek Om Haris Dipatiukur"));
                map.addMarker(new MarkerOptions().position(Resto5).title("RamenYa!"));
                for (int i=0;i<arrayList.size();i++){
                    map.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                    map.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));
                }
            }
        });

        //cek kondisi
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //memanggil
            getCurrentLocation();
        }
        else {

            //memanggil method
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 100);
        }


        return view;
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation()
    {
        //map fragment
        SupportMapFragment mapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        //inisial lokasi manajer
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        //kondisi
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //lokasi terakhir
            client.getLastLocation().addOnCompleteListener(
                    task -> {
                        //lokasi inisial
                        Location location = task.getResult();
                        //kondisi
                        if (location != null) {
                            //ketika lokasi nol
                            mapFragment.getMapAsync(googleMap -> {
                                LatLng lokasi = new LatLng(location.getLatitude(),location.getLongitude());
                                MarkerOptions options = new MarkerOptions().position(lokasi).title("Lokasi Anda");
                                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi,17));
                                googleMap.addMarker(options);
                            });
                        }
                        else {
                            //request lokasi
                            LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);

                            //memanggil kembali
                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void
                                onLocationResult(@NonNull LocationResult locationResult)
                                {
                                    mapFragment.getMapAsync(googleMap -> {
                                        LatLng lokasi = new LatLng(location.getLatitude(),location.getLongitude());
                                        MarkerOptions options = new MarkerOptions().position(lokasi).title("Lokasi Sekarang");
                                        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi,17));
                                        googleMap.addMarker(options);
                                    });
                                }
                            };

                            //update
                            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    });
        }
        else {
            //membuka setting lokasi
            startActivity(
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}