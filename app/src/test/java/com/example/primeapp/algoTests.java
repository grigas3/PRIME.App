package com.example.primeapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.primeapp.algos.DyskinesiaEvaluator;
import com.example.primeapp.algos.PronSupEvaluator;
import com.example.primeapp.algos.TremorEvaluator;
import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.algos.core.Signals.SignalCollection;
import com.example.primeapp.mock.MockIMUDataCollector;
import com.example.primeapp.models.Observation;

import org.junit.Test;

import java.util.jar.Attributes;

public class algoTests {

    private static  final String signalName="IMU";

    private NamedSignalCollection createSignalCollection(int samples,double fn,double fs)
    {
        NamedSignalCollection signalCollection=new NamedSignalCollection();
        try {
            signalCollection.set___idx(signalName,new SignalCollection(6,samples));
            SignalCollection signals = signalCollection.get___idx(signalName);

            for(int i=0;i<samples;i++) {
                for (int j = 0; j < 6; j++)
                    signals.setValue(j, i, 0);

                signals.setValue(5, i, Math.sin(2*Math.PI*i * fn / fs));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return signalCollection;
    }
    private NamedSignalCollection createSignalCollection(int samples,double fn,double fs,double scale)
    {
        NamedSignalCollection signalCollection=new NamedSignalCollection();
        try {
            signalCollection.set___idx(signalName,new SignalCollection(6,samples));
            SignalCollection signals = signalCollection.get___idx(signalName);

            for(int i=0;i<samples;i++) {
                for (int j = 0; j < 6; j++)
                    signals.setValue(j, i, 0);

                signals.setValue(5, i, scale*Math.sin(2*Math.PI*i * fn / fs));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return signalCollection;
    }

    private NamedSignalCollection createSignalCollection2(int samples,double fn1,double fn2,double fs)
    {
        NamedSignalCollection signalCollection=new NamedSignalCollection();
        try {
            signalCollection.set___idx(signalName,new SignalCollection(6,2*samples));
            SignalCollection signals = signalCollection.get___idx(signalName);

            for(int i=0;i<samples;i++) {
                for (int j = 0; j < 6; j++)
                    signals.setValue(j, i, 0);

                signals.setValue(5, i, Math.sin(2*Math.PI*i * fn1/ fs));
            }

            for(int i=0;i<samples;i++) {
                for (int j = 0; j < 6; j++)
                    signals.setValue(j, i+samples, 0);

                signals.setValue(5, i+samples, Math.sin(2*Math.PI*i * fn2 / fs));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return signalCollection;
    }

    @Test
    public void tremorTest_000(){


        NamedSignalCollection signalCollection= createSignalCollection(512,0.5,50);

        TremorEvaluator evaluator=new TremorEvaluator();

        Observation obs=evaluator.Evaluate(signalCollection);

        assertTrue(obs.getValue()<0.01);

    }
    @Test
    public void tremorTest_100(){


        NamedSignalCollection signalCollection= createSignalCollection(512,4,50);

        TremorEvaluator evaluator=new TremorEvaluator();

        Observation obs=evaluator.Evaluate(signalCollection);

        assertTrue(obs.getValue()>0.9);


    }


    @Test
    public void tremorTest_110(){


        NamedSignalCollection signalCollection= createSignalCollection(512,1,50);

        TremorEvaluator evaluator=new TremorEvaluator();

        Observation obs=evaluator.Evaluate(signalCollection);

        assertTrue(obs.getValue()<0.1);


    }

    @Test
    public void tremorTest_50(){


        NamedSignalCollection signalCollection= createSignalCollection2(512,0.5,4,50);

        TremorEvaluator evaluator=new TremorEvaluator();

        Observation obs=evaluator.Evaluate(signalCollection);

        assertEquals(obs.getValue(),50.0,0.1);


    }

    @Test
    public void dysTest_100(){


        NamedSignalCollection signalCollection= createSignalCollection(512,1,50);

        DyskinesiaEvaluator evaluator=new DyskinesiaEvaluator();

        Observation obs=evaluator.Evaluate(signalCollection);

        assertTrue(obs.getValue()>0.9);


    }


    @Test
    public void dysTest_200(){


        NamedSignalCollection signalCollection= createSignalCollection(512,4,50);

        DyskinesiaEvaluator evaluator=new DyskinesiaEvaluator();

        Observation obs=evaluator.Evaluate(signalCollection);

        assertTrue(obs.getValue()<0.1);


    }


    @Test
    public void bradTest_200(){


        NamedSignalCollection signalCollection= createSignalCollection(512,1,50,5);

        PronSupEvaluator evaluator=new PronSupEvaluator();

        Observation obs=evaluator.Evaluate(signalCollection);

        assertTrue(obs.getValue()<0);


    }
    @Test
    public void bradTest_100(){


        NamedSignalCollection signalCollection= createSignalCollection(512,1,50,1000);

        PronSupEvaluator evaluator=new PronSupEvaluator();

        Observation obs=evaluator.Evaluate(signalCollection);

        assertTrue(obs.getValue()<1);


    }
    @Test
    public void bradTest_400(){


        NamedSignalCollection signalCollection= createSignalCollection(512,1,50,1500);

        PronSupEvaluator evaluator=new PronSupEvaluator();

        Observation obs=evaluator.Evaluate(signalCollection);

        assertTrue(obs.getValue()<0.1);


    }
    @Test
    public void bradTest_300(){


        NamedSignalCollection signalCollection= createSignalCollection(512,1,50,100);

        PronSupEvaluator evaluator=new PronSupEvaluator();

        Observation obs=evaluator.Evaluate(signalCollection);

        assertTrue(obs.getValue()>3);


    }

}
