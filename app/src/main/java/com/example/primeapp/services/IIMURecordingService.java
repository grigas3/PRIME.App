package com.example.primeapp.services;

import com.example.primeapp.interfaces.IDataShink;

public interface IIMURecordingService extends IDataShink {

    void start(boolean gyro) throws Exception;

    void stop();

}
