package com.example.primeapp.algos;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.algos.core.Signals.SignalBuffer;
import com.example.primeapp.algos.core.Signals.SignalCollection;
import com.example.primeapp.interfaces.ISymptomEvaluator;
import com.example.primeapp.models.Observation;


public class GaitEvaluator extends BaseGaitEvaluator implements ISymptomEvaluator
{
    @Override
    public Observation Evaluate(NamedSignalCollection signalCollection) {


        GaitResults g=EvaluateGait(signalCollection);

        return new Observation("TIME",g.totalTime);
    }
}

