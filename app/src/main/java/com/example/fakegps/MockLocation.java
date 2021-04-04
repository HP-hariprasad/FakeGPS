package com.example.fakegps;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;

public class MockLocation {

    String providerName;
    Context ctx;
    

    public MockLocation(String name, Context ctx){
        this.providerName= name;
        this.ctx = ctx;

        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        try{
            lm.addTestProvider(providerName, false, false, false, false, false,
                    true, true, 0, 5);

        }
        catch (SecurityException e){
            throw new SecurityException("Not allowed to perform MOCK_LOCATION");
        }

        }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setMockLocation(String lat, String lan){
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        Location mockLocation = new Location(providerName);

        mockLocation.setLatitude(Double.parseDouble(lat));
        mockLocation.setLongitude(Double.parseDouble(lan));
        mockLocation.setAltitude(3F);
        mockLocation.setTime(System.currentTimeMillis());
        //mockLocation.setAccuracy(16F);
        mockLocation.setSpeed(0.01F);
        mockLocation.setBearing(1F);
        mockLocation.setAccuracy(3F);
        mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mockLocation.setBearingAccuracyDegrees(0.1F);
            mockLocation.setVerticalAccuracyMeters(0.1F);
            mockLocation.setSpeedAccuracyMetersPerSecond(0.01F);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mockLocation.setVerticalAccuracyMeters(0.1F);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mockLocation.setSpeedAccuracyMetersPerSecond(0.01F);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//
//        }

        lm.setTestProviderEnabled(providerName, true);

        lm.setTestProviderLocation(providerName, mockLocation);


        //lm.removeUpdates((LocationListener) this);
    }

    public void shutDownMockLocation() {
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        lm.removeTestProvider(providerName);
    }
}
