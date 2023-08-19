package com.example.primeapp.algos;

import android.util.Log;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.algos.core.Signals.SignalBuffer;
import com.example.primeapp.algos.core.Signals.SignalCollection;
import com.example.primeapp.interfaces.ISymptomEvaluator;
import com.example.primeapp.models.Observation;

/**
 * PRIME Pronation/Supination Evaluator
 */
public class PronSupEvaluator implements ISymptomEvaluator {


    /**
     * Function for estimating bradykinesia UPDRS Pronation-Supination based on the data and the paper of
     * Kim, Jiwon & Lee, Joseph & Shin, Jin-Young & Lee, Jae-Ho & Kwon, Yu-Ri & Kwon, Do-Young & Park, Kun-Woo & Eom, gwang moon. (2009). Measurement of Angular Velocity of Forearm Pronation/Supination Movement for the Quantification of the Bradykinesia in Idiopathic Parkinson's Disease Patients. Journal of Biomedical Engineering Research. 30.
     * @param en
     * @return
     */
    private static double getBradScore(double en){
        double bradScore = -0.0044 * (en) + 4.2;

        if (bradScore > 4)
            bradScore = 4;
        if (bradScore < 0)
            bradScore = 0;

        return bradScore;
    }
    private double evaluate(NamedSignalCollection signalCollection) throws Exception {


        double totalBradScore=0;
        int bradScoreEvaluations=0;
        int window=256;
        SignalCollection signal = signalCollection.get___idx("IMU");

        for(int i=0;i<6;i++){

            SignalBuffer yt = signal.get___idx(i);
            double average=yt.average();
            yt.addScalar(-average);
        }
        SignalBuffer y = signal.get_energy_signal(3,6);


        for (int i = 0; i < y.getSize()-window; i += window) {

                double en=0;
                for (int k = 0; k < window; k++)
                    en += y.get___idx(i+k);

                en=en/window;

                if(en>10) {
                    double bradScore =getBradScore(en);
                    bradScoreEvaluations++;
                    totalBradScore += bradScore;
                }
            }


        if(bradScoreEvaluations>0)
        return totalBradScore/bradScoreEvaluations;
        else
            return -1;

    }

    @Override
    public Observation Evaluate(NamedSignalCollection signalCollection) {

        double value=-1;
        try {
            value = evaluate(signalCollection);
        }
        catch (Exception ex)
        {

            Log.e("IMU", "Evaluation ",ex );
        }
        return new Observation("BRAD",value);
    }
}
