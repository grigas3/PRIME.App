package com.example.primeapp.mock;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.algos.core.Signals.SignalCollection;
import com.example.primeapp.interfaces.IDataCollector;

import java.util.Timer;
import java.util.TimerTask;

public class MockIMUDataCollector implements IDataCollector {


    private static final float FS=50;
    private static final float FN=5;
    private static final int MaxSamples=512;
    private static  final String signalName="IMU";
    NamedSignalCollection signalCollection=new NamedSignalCollection();

    public MockIMUDataCollector() throws Exception {

        signalCollection.set___idx(signalName,new SignalCollection(6,MaxSamples));
    }
    Timer timer=null;
    @Override
    public void Start() {
        timer = new Timer();
        timer.schedule(new firstTask(), 1000,20);

    }
    int sampleCount=0;
    //tells handler to send a message
    class firstTask extends TimerTask {

        @Override
        public void run() {

            if(sampleCount<MaxSamples) {
                try {
                    SignalCollection signals = signalCollection.get___idx(signalName);

                    for(int j=0;j<6;j++)
                        signals.setValue(j,sampleCount,0);

                    signals.setValue(5,sampleCount,Math.sin(sampleCount*FN/FS));

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            }
        }
    }

    @Override
    public void Stop() {

        if(timer==null)
            return;
        timer.cancel();
        timer.purge();
        timer=null;
    }

    @Override
    public NamedSignalCollection GetData() {
        return signalCollection;
    }
}
