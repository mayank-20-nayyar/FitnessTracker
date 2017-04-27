package com.example.mayank.accelerometer;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Mayank on 4/27/2017.
 */
public class MainActivity extends AppCompatActivity {

    Intent mServiceIntent;
    private TextView textView;
    private SensorService mSensorService;

    Context ctx;

    SensorRestarterBroadcastReceiver receiver;
    public static final String BROADCAST = "com.example.mayank.service_broadcast_tester.android.action.broadcast";

    public Context getCtx() {
        return ctx;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_step);
        textView = (TextView)findViewById(R.id.textview);
        textView.setText("walk");
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("this is","broadcast inside");
                String message = intent.getStringExtra("Status");
                textView.setText("Number of steps: " + message);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("StepUpdates"));


        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }



    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }


    @Override
    protected void onDestroy() {

        mSensorService = new SensorService(getCtx());
        mSensorService.stopService(new Intent(this, SensorService.class));

        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }
}
