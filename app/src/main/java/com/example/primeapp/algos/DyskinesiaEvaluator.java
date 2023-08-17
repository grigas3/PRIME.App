package com.example.primeapp.algos;

import com.example.primeapp.algos.aggregators.BaseAggregator;
import com.example.primeapp.algos.core.BaseMath;
import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.algos.core.Signals.SignalBuffer;
import com.example.primeapp.algos.core.Signals.SignalCollection;
import com.example.primeapp.algos.signalProcessing.Filters.FIRD;
import com.example.primeapp.interfaces.ISymptomEvaluator;
import com.example.primeapp.models.Observation;



import android.util.Log;


import org.jtransforms.fft.FloatFFT_1D;

import java.util.ArrayList;
import java.util.Date;


/**
 * PRIME Dyskinesia Evaluator
 *  */
public class DyskinesiaEvaluator implements ISymptomEvaluator {

    private DyskinesiaDetector detector=new DyskinesiaDetector();
    /**
     * Constructor
     *

     */
    public DyskinesiaEvaluator() {

    }

    @Override
    public Observation Evaluate(NamedSignalCollection signalCollection) {


        double percent=detector.dysPercent(signalCollection,50);

        return new Observation("DYS",percent);
    }
}