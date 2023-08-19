package com.example.primeapp.algos;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.interfaces.ISymptomEvaluator;
import com.example.primeapp.models.Observation;

public class GaitSpeedEvaluator extends BaseGaitEvaluator implements ISymptomEvaluator {
    private float height;
    private float distance;


    public GaitSpeedEvaluator(int h, float d,boolean s) {
        super(s);
        height = h / 100.0f;
        distance = d;

    }

    @Override
    public Observation Evaluate(NamedSignalCollection signalCollection) {

            GaitResults g = EvaluateGait(signalCollection);
            return new Observation("SPEED", (distance/g.getTotalTime())  / height,"m/sec");

    }
}
