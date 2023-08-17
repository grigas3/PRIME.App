package com.example.primeapp.algos;

import android.util.Log;

import com.example.primeapp.algos.core.BaseMath;
import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.algos.core.Signals.SignalBuffer;
import com.example.primeapp.algos.core.Signals.SignalCollection;

import org.jtransforms.fft.FloatFFT_1D;

public class DyskinesiaDetector {

    private static double[] getPowerSpectrum(float[] out) {

        int N = out.length;
        double[] power_spectrum = new double[N / 2];
        power_spectrum[0] = out[0] * out[0]; /* DC component */
        for (int k = 0; k < N / 2; k++) /* (k < N/2 rounded up) */
            power_spectrum[k] = Math.sqrt(out[k] * out[k] + out[k + 1] * out[k + 1]);

        return power_spectrum;
    }


    public static double dysPercent(NamedSignalCollection signalCollection, double fs) {
        double threshold = 70;

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


                double[] totalPS = new double[window];
                for (int channel = 3; channel < 6; channel++) {

                    SignalBuffer buffer = signal.get___idx(channel);

                    for (int j = 0; j < 2 * window; j++) {
                        x[j] = 0;
                    }

                    for (int j = 0; j < window; j++) {
                        x[2 * j] = (float) buffer.get___idx(i + j);
                    }
                    fft.realForwardFull(x);

                    double[] ps = getPowerSpectrum(x);

                    for (int k = 0; k < window; k++)
                        totalPS[k] += ps[k];
                }


                double totalDysEnergyWindow1 = 0.00000000001;
                double totalDysEnergyWindow2 = 0.00000000001;
                double meanPsdx = 0;
                double maxPsdx = Double.MIN_VALUE;
                for (int j = s1; j < s2; j++) {
                    totalDysEnergyWindow1 += totalPS[j];
                }

                for (int j = 1; j < s1; j++) {
                    totalDysEnergyWindow2 += totalPS[j];

                }
                double w1 = BaseMath.sigmoidS(totalDysEnergyWindow2 / (totalDysEnergyWindow1 + totalDysEnergyWindow2), 10, 0.5);

                totalDyskinesia += (w1 * totalDysEnergyWindow2 > threshold ? 1 : 0);
                n++;
            }
        } catch (Exception ex) {


            Log.d("Error", "detectTremor: ");
        }


        return 100.0 * totalDyskinesia / n;

    }

}
