package com.foolchi.safeguard.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by foolchi on 6/22/14.
 * 做成单例模式，因为手机里只有一个gps,免得每次都新开一个对象
 */
public class GPSInfoProvider {
    private static GPSInfoProvider gpsInfoProvider;
    private static Context context;
    private static MyLocationListener listener;
    private LocationManager locationManager;

    private GPSInfoProvider(){

    }

    // 加入synchronized使得此方法一定执行完
    public static synchronized GPSInfoProvider getInstance(Context context){
        if (gpsInfoProvider == null){
            gpsInfoProvider = new GPSInfoProvider();
            GPSInfoProvider.context = context;
        }
        return gpsInfoProvider;
    }

    public String getLocation(){
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        String provider = getBestProvider();
        //参数:定位方式，更新时间间隔，更新距离间隔，回调函数
        locationManager.requestLocationUpdates(provider, 60000, 50, getListener());

        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString("lastLocation", "");
    }

    // 停用gps
    public void stopGPSListener(){
        if (locationManager != null){
            locationManager.removeUpdates(getListener());
        }
    }

    private String getBestProvider(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 精确定位
        criteria.setAltitudeRequired(false); // 对海拔不敏感
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM); // 设置对手机耗电量
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true); // 定位时是否允许与运营商交换数据
        return locationManager.getBestProvider(criteria, true);
    }

    // 单例模式
    private synchronized MyLocationListener getListener(){
        if (listener == null){
            listener = new MyLocationListener();
        }
        return listener;
    }


    private class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            String latitude = "Latitude: " + location.getLatitude();
            String longitude = "Longitude: " + location.getLongitude();
            SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            sp.edit().putString("lastLocation", latitude + "-" + longitude).commit();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            // 定位设备的状态发生改变

        }

        @Override
        public void onProviderEnabled(String s) {
            // 设备打开时

        }

        @Override
        public void onProviderDisabled(String s) {
            // 设备关闭时

        }
    }
}
