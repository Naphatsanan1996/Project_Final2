package com.example.officesyndrome;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private Button btnCamera, btnTime,btnCameraCapture;

    static {
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "OpenCV loaded successfully");
        } else {
            Log.i(TAG, "OpenCv not loaded");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initinstant();

    }


    public void Initinstant(){
        btnCamera = (Button) findViewById(R.id.OpenCamera);
        btnTime = (Button) findViewById(R.id.SetTime);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OpenCamera.class);
                startActivity(intent);
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settime.class);
                startActivity(intent);
            }
        });
    }

}
