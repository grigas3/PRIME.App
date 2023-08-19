package com.example.primeapp.algos;

import android.util.Log;

import com.example.primeapp.algos.core.BaseMath;
import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.algos.core.Signals.SignalBuffer;
import com.example.primeapp.algos.core.Signals.SignalCollection;

import org.jtransforms.fft.FloatFFT_1D;

/**
 * Dyskinesia Detector
 */
public class DyskinesiaDetector {




    public static double dysPercent(NamedSignalCollection signalCollection, double fs) {
        double threshold = 64;

        int window = 128;

        double f1 = 3.5;
        double f2 = 7.5;
        int s1 = (int) (f1 / fs * window);
        int s2 = (int) (f2 / fs * window);

        int n = 0;
        int totalDyskinesia = 0;

        try {


            float[] x = new float[2 * window];
            FloatFFT_1D fft = new FloatFFT_1D(window);

            SignalCollection signal = signalCollection.get___idx("IMU");
            for (int i = 0; i < signal.getSize(); i += window) {

                if(i+window>signal.getSize())
                    continue;

                double[] totalPS =DetectorHelpers.estimatePSDEn(fft,signal,i,window);
                double totalDysEnergyWindow1 = 0.00001;
                double totalDysEnergyWindow2 = 0.00001;
                for (int j = s1+1; j < s2-1; j++) {
                    totalDysEnergyWindow1 += totalPS[j];
                }

                for (int j = 1; j <= s1; j++) {
                    totalDysEnergyWindow2 += totalPS[j];

                }
                double w1 = BaseMath.sigmoidS(totalDysEnergyWindow2 / (totalDysEnergyWindow1 + totalDysEnergyWindow2), 10, 0.5);

                totalDyskinesia += ((w1 * totalDysEnergyWindow2) > threshold ? 1 : 0);
                n++;
            }
        } catch (Exception ex) {

            Log.d("Error", "detectTremor: ");
        }


        return 100.0 * totalDyskinesia / n;

    }

}
