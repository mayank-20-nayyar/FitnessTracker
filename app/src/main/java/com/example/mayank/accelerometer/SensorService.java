package com.example.mayank.accelerometer;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

public class SensorService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    public Sensor sensor;
    int value = -1;
    float[] values;
    public int counter=0;
    SensorRestarterBroadcastReceiver receiver;
    IntentFilter filter;
    public static final String BROADCAST = "com.example.mayank.service_broadcast_tester.android.action.broadcast";
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;


    public SensorService(Context applicationContext) {
        super();
        Log.e("Sensor service const", "here I am!");
    }

    public SensorService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e("inside","onstart command");


        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("this is","broadcast inside");
                String message = intent.getStringExtra("Status");
                values[0] = 0;
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(mMessageReceiver, intentFilter);



        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, mStepCounterSensor,

                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor,

                SensorManager.SENSOR_DELAY_FASTEST);

        startCount();
        startStepCount();
        return START_STICKY;
    }

    public void startCount(){
        Log.e("its startCount","nice");
    }
    public void startStepCount()
    {
        Log.e("inside startstepcount","nice");
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        sensor = event.sensor;
        values = event.values;

        Log.e("inside","on sensor changed");
        if (values.length > 0) {
            value = (int) values[0];
            Log.e("the if",value + "");
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Log.e("the value", value + "");

                Intent intent = new Intent("StepUpdates");
                intent.putExtra("Status", value + "");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            Log.e("timer stopped","timer off");
        }
    }

    @Override
    public boolean stopService(Intent intent)
    {
        Log.e("inside","stop service");
        mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
        registerReceiver(receiver, filter);
        Intent intent1 = new Intent(BROADCAST);
        sendBroadcast(intent1);
        unregisterReceiver(receiver);
        return super.stopService(intent);
    }

    @Override
    public void onDestroy() {

        Log.e("EXIT", "ondestroy!");
        Log.e("after","broadcst");
        stoptimertask();

        //unregisterReceiver(receiver);
        super.onDestroy();
    }
    @Override
    public void onTaskRemoved(Intent intent) {super.onTaskRemoved(intent);}

}

