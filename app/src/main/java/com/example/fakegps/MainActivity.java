package com.example.fakegps;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "";
    private static MockLocation mockNetwork;
    private static MockLocation mockGps;


    static String lat;
    static String lan;
    static Context context;
    static Button gpsStart;
    static Button gpsStop;
    static Button upload;
    static Spinner spinner;

    private Handler mHandler;
    private Runnable mRunnable;


    TextView data;
    TextView delayTimer;

    List<Coordinates> coordinatesData = new ArrayList<Coordinates>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        context = getApplicationContext();
        gpsStart = (Button) findViewById(R.id.start);
        gpsStop = (Button) findViewById(R.id.stop);
        upload = (Button) findViewById(R.id.upload);
        spinner = (Spinner) findViewById(R.id.spinnerDelayTimer);

        spinner.setOnItemSelectedListener(this);

        List<String> Seconds = new ArrayList<String>();
        Seconds.add("1");
        Seconds.add("2");
        Seconds.add("3");
        Seconds.add("4");
        Seconds.add("5");
        Seconds.add("6");
        Seconds.add("7");
        Seconds.add("8");
        Seconds.add("9");
        Seconds.add("10");
        Seconds.add("15");
        Seconds.add("20");
        Seconds.add("25");
        Seconds.add("30");
        Seconds.add("40");
        Seconds.add("50");
        Seconds.add("60");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Seconds);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        mHandler = new Handler(Looper.myLooper());
        data = (TextView) findViewById(R.id.textView);
//        delayTimer = (TextView) findViewById(R.id.delayTimer);


        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Please Upload the location data File");

        gpsStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                applyMockLocation();
                MockLocation.shutdown = false;
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

        try {
            mockNetwork = new MockLocation(LocationManager.NETWORK_PROVIDER, context);
            mockGps = new MockLocation(LocationManager.GPS_PROVIDER, context);
        } catch (SecurityException e) {
            e.printStackTrace();
            return;
        }

        new CountDownTimer(Long.MAX_VALUE, Integer.parseInt(String.valueOf(spinner.getSelectedItem())) * 1000) {
            int a = 0;

            public void onTick(long millisUntilFinished) {
                if (a < coordinatesData.size() && !MockLocation.shutdown) {
                    exec(coordinatesData.get(a).getLatitude(), coordinatesData.get(a).getLongitude());
                    a++;
                } else {
                    a = 0;
                }
            }

            public void onFinish() {
            }
        }.start();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void exec(String lat, String lan) {

        data.setText("lat: " + lat + " lan: " + lan);
        System.out.println(lat + " " + lan);

        mRunnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {
                try {
                    mockNetwork.setMockLocation(lat, lan);
                    mockGps.setMockLocation(lat, lan);
                    mHandler.postDelayed(mRunnable, 1);

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        };
        mHandler.post(mRunnable);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}