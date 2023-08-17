package com.example.primeapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import com.example.primeapp.algos.DoubleSupportTimeEvaluator;
import com.example.primeapp.algos.GaitEvaluator;
import com.example.primeapp.algos.StanceTimeEvaluator;
import com.example.primeapp.algos.SwingTimeEvaluator;
import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.algos.core.Signals.SignalCollection;
import com.example.primeapp.models.Observation;

import org.json.JSONObject;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class gaitTests {



    private NamedSignalCollection CreateSignalCollectiom14() throws Exception {

        InputStream in = this.getClass().getClassLoader().getResourceAsStream("imuData14.txt");
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader breader = new BufferedReader(reader);

        NamedSignalCollection signalCollection = new NamedSignalCollection();
        SignalCollection s = new SignalCollection(51, 2180);
        signalCollection.set___idx("insoles",s);
        int count = 0;
        while (breader.ready()) {
            String line = breader.readLine();
            String[] vals = line.split("\t");

            for (int i = 0; i < 51; i++) {

                s.setValue(i, count, Double.parseDouble(vals[i]));
            }

            count++;
        }


        return signalCollection;


    }
    @Test
    public void TestTotalTime_Data14(){


        try {
            NamedSignalCollection signalCollection=CreateSignalCollectiom14();

            GaitEvaluator evaluator = new GaitEvaluator();


            Observation obs=evaluator.Evaluate(signalCollection);

            assertEquals(19.39,obs.getValue(),0.01);

        }
        catch (Exception ex)
        {

        }

    }


    @Test
    public void TestDoubleSupportTime_Data14(){


        try {

            NamedSignalCollection signalCollection=CreateSignalCollectiom14();

            DoubleSupportTimeEvaluator evaluator = new DoubleSupportTimeEvaluator();


            Observation obs=evaluator.Evaluate(signalCollection);

            assertEquals(0.335,obs.getValue(),0.01);

        }
        catch (Exception ex)
        {

        }

    }


    @Test
    public void TestStanceTime_Data14(){


        try {

            NamedSignalCollection signalCollection=CreateSignalCollectiom14();

            StanceTimeEvaluator evaluator = new StanceTimeEvaluator();


            Observation obs=evaluator.Evaluate(signalCollection);

            assertEquals(0.7804,obs.getValue(),0.01);

        }
        catch (Exception ex)
        {

        }

    }

    @Test
    public void TestSwingTime_Data14(){


        try {

            NamedSignalCollection signalCollection=CreateSignalCollectiom14();

            SwingTimeEvaluator evaluator = new SwingTimeEvaluator();


            Observation obs=evaluator.Evaluate(signalCollection);

            assertEquals(0.4559,obs.getValue(),0.01);

        }
        catch (Exception ex)
        {

        }

    }
}
