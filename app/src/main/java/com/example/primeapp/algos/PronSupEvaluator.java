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
        double bradScore = -0.9157 * Math.log(en) + 6.4;

        if (bradScore > 4)
            bradScore = 4;
        if (bradScore < 0)
            bradScore = 0;

        return bradScore;
    }
    private double evaluate(NamedSignalCollection signalCollection) throws Exception {


        double totalBradScore=0;
        int bradScoreEvaluations=0;
        int window=128;
        int slidewindow=50;
        SignalCollection signal = signalCollection.get___idx("IMU");


        for (int i = 0; i < signal.getSize(); i += slidewindow) {

            if(i+window>=signal.getSize())
                continue;;

                float[] res=DetectorHelpers.estimateRMS(signal,i,window);

                double en=res[0];
                double percentMotion=res[1];
                if(percentMotion>0.8) {
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
        return new Observation("BRAD",value,"UPDRS");
    }
}
