package com.example.user.ludifit.MainScreen;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class StepCountingService extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepCounterSensor;
    Sensor stepDetector;

    //int currentStepCount;
    int currentStepsDetected;

    int stepCounter;
    int newStepCounter;

    boolean serviceStopped; // Boolean variable to control the repeating timer.

    Intent intent;
    private static final String TAG = "StepService";
    public static final String BROADCAST_ACTION = "com.example.user.ludifit"; // ver
    private final Handler handler = new Handler();


    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    /* this is called when the main activity calls the startService */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepCounterSensor, 0);
        sensorManager.registerListener(this, stepDetector, 0);

        //currentStepCount = 0;
        currentStepsDetected = 0;
        stepCounter = 0;
        newStepCounter = 0;

        serviceStopped = false;

        // --------------------------------------------------------------------------- \\
        // ___ (3) start handler ___ \\
        /////if (serviceStopped == false) {
        // remove any existing callbacks to the handler
        handler.removeCallbacks(updateBroadcastData);
        // call our handler with or without delay.
        handler.post(updateBroadcastData); // 0 seconds
        /////}
        // ___________________________________________________________________________ \\

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceStopped = true;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // *** Step Counting does not restart until the device is restarted - therefore, an algorithm for restarting the counting must be implemented.
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int countSteps = (int) event.values[0];

            if (stepCounter == 0) {
                stepCounter = (int) event.values[0];
            }
            newStepCounter = countSteps - stepCounter;
        }

        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            int detectSteps = (int) event.values[0];
            currentStepsDetected += detectSteps;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // never happens
    }

    // ______________________________________________________________________________________ \\


    // --------------------------------------------------------------------------- \\
    // ___ (4) repeating timer ___ \\
    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
            if (!serviceStopped) { // Only allow the repeating timer while service is running (once service is stopped the flag state will change and the code inside the conditional statement here will not execute).
                // Call the method that broadcasts the data to the Activity..
                broadcastSensorValue();
                // Call "handler.postDelayed" again, after a specified delay.
                handler.postDelayed(this, 1000);
            }
        }
    };
    private void broadcastSensorValue() {
        // add step counter to intent.
        intent.putExtra("Counted_Step_Int", newStepCounter);
        intent.putExtra("Counted_Step", String.valueOf(newStepCounter));
        // add step detector to intent.
        intent.putExtra("Detected_Step_Int", currentStepsDetected);
        intent.putExtra("Detected_Step", String.valueOf(currentStepsDetected));
        // call sendBroadcast with that intent  - which sends a message to whoever is registered to receive it.
        sendBroadcast(intent);
    }
}