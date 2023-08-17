package com.example.primeapp.algos;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.interfaces.ISymptomEvaluator;
import com.example.primeapp.models.Observation;

public class TremorEvaluator implements ISymptomEvaluator {


    @Override
    public Observation Evaluate(NamedSignalCollection signalCollection) {

        TremorDetector detector=new TremorDetector();
        double percent=detector.tremorPercent(signalCollection,50);
        return new Observation("TREMOR",percent);
    }
}
