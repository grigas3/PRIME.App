package com.example.primeapp.algos;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.interfaces.ISymptomEvaluator;
import com.example.primeapp.models.Observation;

public class StepIMUEvaluator extends BaseGaitEvaluator implements ISymptomEvaluator {

    public StepIMUEvaluator() {
        super(false);
    }

    @Override
    public Observation Evaluate(NamedSignalCollection signalCollection) {


        GaitResults g = EvaluateGait(signalCollection);
        return new Observation("STEP", g.getSteps());
    }
}
