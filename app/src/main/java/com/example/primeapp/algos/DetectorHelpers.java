package com.example.primeapp.algos;

import com.example.primeapp.algos.core.Signals.SignalBuffer;
import com.example.primeapp.algos.core.Signals.SignalCollection;

import org.jtransforms.fft.FloatFFT_1D;

public class DetectorHelpers {
    private static double[] getPowerSpectrum(float[] out) {

        int N = out.length;
        int N1 = N / 2;
        double[] power_spectrum = new double[N / 2];
        power_spectrum[0] = out[0] * out[0]; /* DC component */
        for (int k = 0; k < N / 2; k++) /* (k < N/2 rounded up) */
            power_spectrum[k] = 2 * Math.sqrt(out[k * 2] * out[k * 2] + out[k * 2 + 1] * out[k * 2 + 1]) / N1;

        return power_spectrum;
    }

    public static double[] estimatePSDEn(FloatFFT_1D fft, SignalCollection signal, int i, int window) throws Exception {

        float[] x = new float[2 * window];
        double[] totalPS = new double[window];
        for (int channel = 3; channel < 6; channel++) {

            SignalBuffer buffer = signal.get___idx(channel);

            for (int j = 0; j < 2 * window; j++) {
                x[j] = 0;
            }
            float mean = 0;
            for (int j = 0; j < window; j++) {
                mean += (float) buffer.get___idx(i + j);
            }
            mean /= window;
            for (int j = 0; j < window; j++) {
                x[2 * j] = (float) buffer.get___idx(i + j) - mean;
            }
            fft.complexForward(x);

            double[] ps = getPowerSpectrum(x);

            for (int k = 0; k < window; k++)
                totalPS[k] += ps[k];
        }

        return totalPS;
    }


}
