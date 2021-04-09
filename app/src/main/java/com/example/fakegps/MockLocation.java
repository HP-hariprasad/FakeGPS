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
    static boolean shutdown = false;


    public MockLocation(String name, Context ctx) {
        this.providerName = name;
        this.ctx = ctx;

        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        try {
            lm.addTestProvider(providerName, false, false, false, false, false,
                    true, true, 1, 2);

        } catch (SecurityException e) {
            throw new SecurityException("Not allowed to perform MOCK_LOCATION");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setMockLocation(double lat, double lan) {
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        Location mockLocation = new Location(providerName);

        mockLocation.setLatitude(lat);
        mockLocation.setLongitude(lan);
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
        lm.setTestProviderEnabled(providerName, true);
        lm.setTestProviderLocation(providerName, mockLocation);
    }

    public void shutDownMockLocation() {
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        lm.removeTestProvider(providerName);
        shutdown = true;
    }
}
