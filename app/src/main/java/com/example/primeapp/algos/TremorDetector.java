package com.example.primeapp.algos;

import android.util.Log;

import com.example.primeapp.algos.core.BaseMath;
import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.algos.core.Signals.SignalBuffer;
import com.example.primeapp.algos.core.Signals.SignalCollection;
import com.example.primeapp.algos.signalProcessing.SignalDictionary;
import com.google.gson.internal.JsonReaderInternalAccess;

import org.jtransforms.fft.FloatFFT_1D;

/**
 * Created by george on 2/6/2016.
 */
public class TremorDetector {


    private double extractSDSEnergy(SignalCollection gyro) throws Exception {

        return gyro.sum_energy() / gyro.getSize();

    }


    private double extractGS1Feature(SignalCollection ads) throws Exception {
        return ads.sum_diff_energy();


    }

    private double extractA19Feature(SignalCollection ads) throws Exception {

        double a19 = ads.get___idx(0).average();


        return a19;

    }


    private double extractHomogenity(SignalCollection ads) throws Exception {

        int i, j;
        int k = ads.getSize() / 5;

        double minE = 10000;
        double maxE = -10000;
        double s = 0;

        for (i = 0; i < 5; i++) {

            s = ads.sum_energy(i * k, (i + 1) * k - 1);
            if (s > maxE)
                maxE = s;
            if (s < minE)
                minE = s;

        }


        return (maxE - minE) / (maxE + minE + 0.0000001);


    }


    /**
     * First decision for tremor
     *
     * @param a3  Feature A3
     * @param aE  Feature AE
     * @param gs1 Feature GS1
     * @param a19 Feature A19
     * @return
     */
    private boolean firstDecision(double a3, double aE, double gs1, double a19) {


        boolean res = true;
        ///Changed to abs in order to avoid
        //Probably remove
        res = (a3 > 0.167) && aE > 0.2 && ((gs1 < 0.02 || a3 > 0.8) && Math.abs(a19) < 0.8);
        return res;

    }

    private boolean secondDecision(double a3, double a2) {

        return ((a3 >= 0.82) || (a3 <= 0.82 && a2 <= 0.285));

    }

    public double testGS1(NamedSignalCollection signalCollection, int o, int l) {
        try {
            return extractGS1Feature(signalCollection.get___idx(SignalDictionary.TremorAccLowPass).getWindow(o, l));
        } catch (Exception ex) {


        }

        return 0;
    }

    public double testA2(NamedSignalCollection signalCollection, int o, int l) {
        try {
            return extractSDSEnergy(signalCollection.get___idx(SignalDictionary.TremorGyroLowPass).getWindow(o, l));
        } catch (Exception ex) {


        }

        return 0;
    }

    public double testAE(NamedSignalCollection signalCollection, int o, int l) {
        try {
            return extractSDSEnergy(signalCollection.get___idx(SignalDictionary.OriginalGyro).getWindow(o, l));
        } catch (Exception ex) {


        }

        return 0;
    }

    public double testA19(NamedSignalCollection signalCollection, int o, int l) {
        try {
            return extractA19Feature(signalCollection.get___idx(SignalDictionary.TremorAccLowPass).getWindow(o, l));
        } catch (Exception ex) {


        }

        return 0;
    }


    public double testA3(NamedSignalCollection signalCollection, int o, int l) {
        try {
            double sds = extractSDSEnergy(signalCollection.get___idx(SignalDictionary.TremorGyroHighPass).getWindow(o, l));
            double sds4 = extractSDSEnergy(signalCollection.get___idx(SignalDictionary.OriginalGyro).getWindow(o, l));

            return sds / (sds4 + 0.0000001);
        } catch (Exception ex) {


        }

        return 0;
    }

    public double testSDS3(NamedSignalCollection signalCollection, int o, int l) {
        try {
            double sds3 = extractHomogenity(signalCollection.get___idx(SignalDictionary.TremorGyroHighPass).getWindow(o, l));


            return sds3;
        } catch (Exception ex) {


        }

        return 0;
    }

    public int detectTremor(NamedSignalCollection signalCollection) {


        boolean hasTremor = false;

        try {


            double sds4 = extractSDSEnergy(signalCollection.get___idx(SignalDictionary.OriginalGyro));
            double sds = extractSDSEnergy(signalCollection.get___idx(SignalDictionary.TremorGyroHighPass));
            double sds3 = extractHomogenity(signalCollection.get___idx(SignalDictionary.TremorGyroHighPass));
            double sds2 = extractSDSEnergy(signalCollection.get___idx(SignalDictionary.TremorGyroLowPass));
            double gs1 = extractGS1Feature(signalCollection.get___idx(SignalDictionary.TremorAccLowPass));
            double a19 = extractA19Feature(signalCollection.get___idx(SignalDictionary.TremorAccLowPass));
            double aE = sds4;
            double a3 = sds / (sds4 + 0.0000001);
            double a2 = sds2;


            if (((gs1 > 0.02 && a3 < 0.8) || Math.abs(a19) > 0.7))
                return -1;
            else {

                hasTremor = firstDecision(a3, aE, gs1, a19) && secondDecision(a3, a2) & sds3 < 0.5;


                return hasTremor ? 1 : 0;
            }
        } catch (Exception ex) {


            Log.d("Error", "detectTremor: ");
        }


        return -1;

    }



    public double tremorPercent(NamedSignalCollection signalCollection, double fs) {
        double threshold = 50;
        //int window = 256;
        int window = 128;

        double f1 = 3.5;
        double f2 = 7.5;
        int s1 = (int) (f1 / fs * window);
        int s2 = (int) (f2 / fs * window);

        int n = 0;
        int totalTremorEnergy = 0;

        try {


            float[] x = new float[2*window];
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
                double totalTremorEnergyWindow1 = 0.00000000001;
                double totalTremorEnergyWindow2 = 0.00000000001;
                double meanPsdx=0;
                double maxPsdx=Double.MIN_VALUE;
                for (int j = s1; j < s2; j++){
                    if(totalPS[j]>maxPsdx)
                        maxPsdx=totalPS[j];
                    meanPsdx+=totalPS[j];
                    totalTremorEnergyWindow1 += totalPS[j];

                }
                meanPsdx=meanPsdx/(s2-s1-1);
                double m1=maxPsdx/(meanPsdx+0.00000000001);

                for (int j = 1; j < s1; j++){
                    totalTremorEnergyWindow2 += totalPS[j];

                }
                double w1= BaseMath.sigmoidS(totalTremorEnergyWindow1/(totalTremorEnergyWindow1+totalTremorEnergyWindow2),10,0.5);
                double w2= BaseMath.sigmoidS(m1,2,2.5);
                totalTremorEnergy += (w1*w2*totalTremorEnergyWindow1 > threshold ? 1 : 0);
                n++;
            }


        } catch (Exception ex) {


            Log.d("Error", "detectTremor: ");
        }


        return 100.0 * totalTremorEnergy / n;

    }

    double[] getPowerSpectrum(float[] out){

        int N=out.length;
        double[] power_spectrum=new double[N/2];
        power_spectrum[0] = out[0]*out[0]; /* DC component */
        for (int k = 0; k < N/2; k++) /* (k < N/2 rounded up) */
            power_spectrum[k] = Math.sqrt(out[k]*out[k] + out[k+1]*out[k+1]);

        return power_spectrum;
    }

}
