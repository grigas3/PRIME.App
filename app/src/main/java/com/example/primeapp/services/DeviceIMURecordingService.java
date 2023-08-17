package com.example.primeapp.services;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.telephony.ims.ImsRegistrationAttributes;

import com.example.primeapp.interfaces.IDataListener;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.data.AngularVelocity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeviceIMURecordingService implements IIMURecordingService,SensorEventListener
{


    SensorManager sensorManager;

    Sensor sensor;
    List<IDataListener> listeners = new ArrayList<>();

    File appSpecificInternalStorageDirectory;

    public DeviceIMURecordingService(Context context)
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        appSpecificInternalStorageDirectory = context.getFilesDir();
        //gyrosensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

    }
    @Override
    public void registerListener(IDataListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }



    @Override
    public void unregisterListener(IDataListener listener) {
        this.listeners.remove(listener);
    }


    private boolean isGyro=false;
    @Override
    public void start(boolean gyro) throws Exception {

        isGyro=gyro;
        if(gyro) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        }else{
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        createFile();

    }

    private OutputStreamWriter outputWriter;
    private FileOutputStream outputStream;
    public void createFile(){

        String fileName= UUID.randomUUID().toString()+".txt";
        File file = new File(appSpecificInternalStorageDirectory, fileName);
        try {

            outputStream = new FileOutputStream(file,false);
            outputWriter=new OutputStreamWriter(outputStream);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void closeFile()
    {
        if(outputStream!=null)
        {
            try {
                outputStream.close();
                outputWriter.close();
            }
            catch (Exception ex)
            {

            }

        }
    }
    @Override
    public void stop() {
        sensorManager.unregisterListener(this, sensor);

        closeFile();
        //sensorManager.unregisterListener(this, gyrosensor);
    }
    private static final float NS2S = 1.0f / 1000000000.0f;
    float timestamp;
    int count=0;

    private final static float scaleGyro=180.0f/(float)Math.PI;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(count==8) {
            count=0;
            listeners.forEach(e->{


                    final float dT = (event.timestamp - timestamp) * NS2S;
                        if(isGyro) {
                            e.addData(new Acceleration(event.values[0]*scaleGyro, event.values[1]*scaleGyro, event.values[2]*scaleGyro));
                        }
                        else {
                            e.addData(new Acceleration(event.values[0], event.values[1], event.values[2]));
                        }
                    if (outputStream != null) {
                        try {
                            outputWriter.write(dT + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\r\n");
                        } catch (Exception ex) {

                        }

                    }





            });
    }
    count++;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
