package com.example.primeapp.algos;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.interfaces.ISymptomEvaluator;
import com.example.primeapp.models.Observation;

public class StideLengthEvaluator extends BaseGaitEvaluator implements ISymptomEvaluator {
    private float height;
    private float distance;

    public StideLengthEvaluator(int h, float d) {
        height = h / 100.0f;
        distance = d;
    }

    @Override
    public Observation Evaluate(NamedSignalCollection signalCollection) {

        GaitResults g = EvaluateGait(signalCollection);
        return new Observation("STRIDE", g.getSteps() / height / distance);
    }
}

