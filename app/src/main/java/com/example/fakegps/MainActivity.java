package com.example.fakegps;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    private static MockLocation mockNetwork;
    private static MockLocation mockGps;


    static String lat;
    static String lan;
    static Context context;
    static Button gpsStart;
    static Button gpsStop;
    static Button upload;

    private Handler mHandler;
    private Runnable mRunnable;


    TextView data;
    TextView delayTimer;

    List<Coordinates> coordinatesData = new ArrayList<Coordinates>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        gpsStart = (Button) findViewById(R.id.start);
        gpsStop = (Button) findViewById(R.id.stop);
        upload = (Button) findViewById(R.id.upload);

        mHandler = new Handler(Looper.myLooper());
        data = (TextView) findViewById(R.id.textView);
        delayTimer = (TextView) findViewById(R.id.delayTimer);


        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Please Upload the location data File");

        gpsStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                applyMockLocation();
            }
        });
        gpsStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mockGps.shutDownMockLocation();
                mockNetwork.shutDownMockLocation();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), 1);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void applyMockLocation() {
        new CountDownTimer(Long.MAX_VALUE, Integer.parseInt(delayTimer.getText().toString()) * 1000) {
            int a = 0;

            public void onTick(long millisUntilFinished) {
                if (a < coordinatesData.size()) {
                    exec(coordinatesData.get(a).getLatitude(), coordinatesData.get(a).getLongitude());
                    a++;
                } else {
                    a = 0;
                }
            }

            public void onFinish() {
            }
        }.start();
////        lat = 28.7041;
////        lan = 77.1025;
        try {
            mockNetwork = new MockLocation(LocationManager.NETWORK_PROVIDER, context);
            mockGps = new MockLocation(LocationManager.GPS_PROVIDER, context);
        } catch (SecurityException e) {
            e.printStackTrace();
            return;
        }
//        //exec(coordinatesData.get(0).getLatitude(), coordinatesData.get(0).getLongitude());
//        for(int i =0 ; i < coordinatesData.size();i++) {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            exec(coordinatesData.get(i).getLatitude(), coordinatesData.get(i).getLongitude());
////                    System.out.println("hello");
//
//
//
//
//
//        }


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void exec(String lat, String lan) {

        data.setText("lat: " + lat + " lan: " + lan);
        System.out.println(lat + " " + lan);

        mockNetwork.setMockLocation(lat, lan);
        mockGps.setMockLocation(lat, lan);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                if (data == null) {

                    return;
                }
                Uri selectedFileUri = data.getData();


                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));

                    for (String line; (line = r.readLine()) != null; ) {
                        Coordinates coordinates = new Coordinates();
                        coordinates.setLatitude(line.split(",")[0]);
                        coordinates.setLongitude(line.split(",")[1]);
                        coordinatesData.add(coordinates);
                    }
                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText("Data Uploaded Successfully and can Start MockGps");

                    System.out.println("below is the coordinates");
                    for (Coordinates allData : coordinatesData) {
                        System.out.println("below is the Latitude");
                        System.out.println(allData.getLatitude());
                        System.out.println("below is the Longitude");
                        System.out.println(allData.getLongitude());
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}