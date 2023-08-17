package com.example.primeapp.sensor;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.algos.core.Signals.SignalBuffer;
import com.example.primeapp.algos.core.Signals.SignalCollection;
import com.example.primeapp.interfaces.IDataCollector;
import com.example.primeapp.interfaces.IDataListener;
import com.example.primeapp.models.PressureData;
import com.example.primeapp.services.IIMURecordingService;
import com.example.primeapp.services.IMURecordingService;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.data.AngularVelocity;
import com.mbientlab.metawear.data.Sign;
import com.mbientlab.metawear.module.Gyro;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class IMUDataCollector implements IDataCollector, IDataListener {


    ArrayList<AngularVelocity> dataList=new ArrayList<>();

    IIMURecordingService service;
    public IMUDataCollector(IIMURecordingService recordingService)
    {
        this.service=recordingService;


    }

    @Override
    public void Start() {

        dataList.clear();
        this.service.registerListener(this);

        try {
            this.service.start(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void Stop() {

        this.service.stop();
        this.service.unregisterListener(this);
    }


    private static NamedSignalCollection createData(ArrayList<AngularVelocity> data) throws Exception {


        NamedSignalCollection nsignalCollection=new NamedSignalCollection();

        SignalCollection signalCollection=new SignalCollection(6,data.size());
        SignalBuffer gyroX=new SignalBuffer(data.size());
        SignalBuffer gyroY=new SignalBuffer(data.size());
        SignalBuffer gyroZ=new SignalBuffer(data.size());

        int count=0;
        for (AngularVelocity d: data
             ) {

            gyroX.set___idx(count,d.x());
            gyroY.set___idx(count,d.y());
            gyroZ.set___idx(count,d.z());
            count++;
        }

        signalCollection.set___idx(3,gyroX);
        signalCollection.set___idx(4,gyroY);
        signalCollection.set___idx(5,gyroZ);
        nsignalCollection.set___idx("IMU",signalCollection);

        return nsignalCollection;
    }
    @Override
    public NamedSignalCollection GetData() {
        NamedSignalCollection res=null;
        try{
            res=createData(dataList);
        }
        catch (Exception ex)
        {

        }
        return res;
    }

    @Override
    public void addData(AngularVelocity data) {

        dataList.add(data);

    }
    @Override
    public void addData(Acceleration data) {
        dataList.add(new AngularVelocity(data.x(),data.y(),data.z()));
    }

    @Override
    public void addData(PressureData data) {

    }
}
