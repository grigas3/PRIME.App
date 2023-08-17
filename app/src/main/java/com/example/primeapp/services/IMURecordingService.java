package com.example.primeapp.services;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.primeapp.interfaces.IDataListener;
import com.example.primeapp.interfaces.IDataShink;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.data.Quaternion;
import com.mbientlab.metawear.module.Gyro;
import com.mbientlab.metawear.module.SensorFusionBosch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bolts.Continuation;


public class IMURecordingService extends Service implements IIMURecordingService {

    private BluetoothDevice btDevice;
    private MetaWearBoard board;

    private final IBinder iBinder = new IMURecordingService.LocalBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        return iBinder;
    }

    public void setBleBinder(BtleService.LocalBinder b) {

        binder=b;

    }

    public class LocalBinder extends Binder {
        public IMURecordingService getService() {
            return IMURecordingService.this;
        }
    }


    public void setBtDevice(BluetoothDevice device) {
        this.btDevice = device;
    }

    BtleService.LocalBinder binder;

    public SensorFusionBosch configure() throws Exception {

        if (binder == null)
            throw new Exception("Binder Not Found");

        if (btDevice == null)
            throw new Exception("Device Not Found");

        board = binder.getMetaWearBoard(btDevice);
        final SensorFusionBosch sensorFusion = board.getModule(SensorFusionBosch.class);
        sensorFusion.configure()
                .mode(SensorFusionBosch.Mode.IMU_PLUS)
                .accRange(SensorFusionBosch.AccRange.AR_8G)
                .gyroRange(SensorFusionBosch.GyroRange.GR_2000DPS)

                .commit();

        return sensorFusion;
    }
    private OutputStreamWriter outputWriter;
    private FileOutputStream outputStream;
    public void createFile(){
        File appSpecificInternalStorageDirectory = getApplicationContext().getFilesDir();
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

    SensorFusionBosch sensorFusion=null;
    public void start(boolean gyro) throws Exception {
        sensorFusion = configure();

        if (gyro) {
            sensorFusion.correctedAngularVelocity().addRouteAsync(source -> source.limit(33).stream((data, env) -> {
                // mGLSurfaceView.updateRotation(data.value(Quaternion.class));

                SensorFusionBosch.CorrectedAngularVelocity d = data.value(SensorFusionBosch.CorrectedAngularVelocity.class);
                //  Log.d("IN CUBE ACtivity", data.value(SensorFusionBosch.CorrectedAngularVelocity.class).toString());
                listeners.forEach(e -> {


                    e.addData(d);
                    if (outputStream != null) {
                        try {
                            outputWriter.write(data.formattedTimestamp() + "\t" + d.x() + "\t" + d.y() + "\t" + d.z() + "\r\n");
                        } catch (Exception ex) {

                        }

                    }

                });
                // outputStreamWriter.write(data.value(Quaternion.class).toString()+'\n');

            })).continueWith((Continuation<Route, Void>) ignored -> {

                sensorFusion.correctedAngularVelocity().start();
                sensorFusion.start();
                return null;
            });
        }
        else {

            sensorFusion.correctedAcceleration().addRouteAsync(source -> source.limit(33).stream((data, env) -> {
                // mGLSurfaceView.updateRotation(data.value(Quaternion.class));

                SensorFusionBosch.CorrectedAcceleration d = data.value(SensorFusionBosch.CorrectedAcceleration.class);
                //  Log.d("IN CUBE ACtivity", data.value(SensorFusionBosch.CorrectedAngularVelocity.class).toString());
                listeners.forEach(e -> {


                    e.addData(d);
                    if (outputStream != null) {
                        try {
                            outputWriter.write(data.formattedTimestamp() + "\t" + d.x() + "\t" + d.y() + "\t" + d.z() + "\r\n");
                        } catch (Exception ex) {

                        }

                    }

                });
                // outputStreamWriter.write(data.value(Quaternion.class).toString()+'\n');

            })).continueWith((Continuation<Route, Void>) ignored -> {

                sensorFusion.correctedAcceleration().start();
                sensorFusion.start();
                return null;
            });

        }

        createFile();

    }


    public void stop() {
        if(sensorFusion==null)
            return;

        sensorFusion.correctedAngularVelocity().stop();
        sensorFusion.stop();

        closeFile();
    }

    List<IDataListener> listeners = new ArrayList<>();


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



}
