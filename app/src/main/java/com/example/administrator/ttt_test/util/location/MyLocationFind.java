package com.example.administrator.ttt_test.util.location;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.text.DecimalFormat;

/**
 * Created by Acer on 2017/4/16.
 */

public class MyLocationFind {
    private LocationClient mLocationClient;
    private double latitude;
    private double longitude;
    private String street;
    private Handler handler=null;



    public MyLocationFind(Context context, boolean havaNetwork){
        mLocationClient=new LocationClient(context);
        mLocationClient.registerLocationListener(new MyLocationListener());
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//        if(havaNetwork){
//            option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
//        }
//        else {
//            option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
//        }
        mLocationClient.setLocOption(option);
    }

    public MyLocationFind(Context context, Handler handler){
        mLocationClient=new LocationClient(context);
        mLocationClient.registerLocationListener(new MyLocationListener());
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//        if(havaNetwork){
//            option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
//        }
//        else {
//            option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
//        }
        mLocationClient.setLocOption(option);
        this.handler=handler;
    }

    public void startService(){
        mLocationClient.start();
    }

    public void stopService(){
        mLocationClient.stop();
    }
    public boolean haveResult(){
        if(mLocationClient!=null){
            if((latitude==4.9E-324D)||(latitude==0)||(longitude==4.9E-324D)||(longitude==0)){
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }


    private class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Message msg=new Message();
            latitude=bdLocation.getLatitude();
            longitude=bdLocation.getLongitude();
            street=bdLocation.getStreet();
            if(handler!=null){
                DecimalFormat format=new DecimalFormat("##0.00");
                String longitudeString=format.format(longitude);
                String latitudeString=format.format(latitude);
                msg.obj=longitudeString+","+latitudeString;
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getStreet() {
        return street;
    }
}
