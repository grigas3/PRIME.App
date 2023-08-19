package com.example.primeapp.algos;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.interfaces.ISymptomEvaluator;
import com.example.primeapp.models.Observation;

public class StideLengthEvaluator extends BaseGaitEvaluator implements ISymptomEvaluator {
    private float height;
    private float distance;

    private boolean insoles=true;

    public StideLengthEvaluator(int h, float d,boolean sole) {
        height = h / 100.0f;
        distance = d;
        insoles=sole;
    }

    @Override
    public Observation Evaluate(NamedSignalCollection signalCollection) {

        if(insoles) {
            GaitResults g = EvaluateGait(signalCollection);
            return new Observation("STRIDE", g.getSteps() / height / distance,"meters");
        }
        else{
            GaitResults g = EvaluateGaitFromIMU(signalCollection);
            return new Observation("STRIDE", g.getSteps() / height / distance,"meters");
        }
    }
}

