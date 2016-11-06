package com.example.fan.terrynavigation;


import android.content.pm.PackageManager;
import android.location.Criteria;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Fan on 2016/10/26.
 */

public class MapViewFragment extends Fragment implements OnMapReadyCallback
{
    View myView;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        myView = inflater.inflate(R.layout.map, container, false);
        //取得系統定位服務
        LocationManager status = (LocationManager) (this.getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE)); // =___=''
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            locationServiceInitial();
        } else
        {
            Toast.makeText(this.getActivity(), "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));    //開啟設定頁面
        }
        //取得系統定位服務
        //getActivity().getSystemService(Context.LOCATION_SERVICE);

        //this.getContext().getSystemService(Context.LOCATION_SERVICE);

        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        super.onViewCreated(view, savedInstanceState);
        //this.getChildFragmentManager();
        //getActivity().getFragmentManager();
        final com.google.android.gms.maps.MapFragment fragment = (com.google.android.gms.maps.MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    private void locationServiceInitial()
    {
        lms = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);    //取得系統定位服務
        Criteria criteria = new Criteria();    //資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者
        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lms.getLastKnownLocation(bestProvider);
        getLocation(location);
    }

    Double longitude;
    Double latitude;

    private void getLocation(Location location)
    {    //將定位資訊顯示在畫面中
        if (location != null)
        {
            TextView longitude_txt = (TextView) myView.findViewById(R.id.longitude);
            TextView latitude_txt = (TextView) myView.findViewById(R.id.latitude);

            longitude = location.getLongitude();    //取得經度
            latitude = location.getLatitude();    //取得緯度

            longitude_txt.setText(String.valueOf(longitude));
            latitude_txt.setText(String.valueOf(latitude));
        } else
        {
            Toast.makeText(getActivity(), "無法定位座標", Toast.LENGTH_LONG).show();
        }
    }

    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;    //最佳資訊提供者


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        //LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // Double longitude = location.getLongitude();	//取得經度
        // Double latitude = location.getLatitude();	//取得緯度
        LatLng marker = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 13));
        googleMap.addMarker(new MarkerOptions().title("Hello Terry!").position(marker));
    }
}
