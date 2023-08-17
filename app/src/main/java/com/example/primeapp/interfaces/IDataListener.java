package com.example.primeapp.interfaces;

import com.example.primeapp.models.PressureData;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.data.AngularVelocity;
import com.mbientlab.metawear.module.Gyro;

public interface IDataListener {
    /***
     * Add Sole data to the data Listener
     */
    void addData(AngularVelocity data);


    void addData(Acceleration data);


    void addData(PressureData data);

}
