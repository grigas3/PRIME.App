package com.example.primeapp.algos;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.interfaces.ISymptomEvaluator;
import com.example.primeapp.models.Observation;

/**
 * Double Support Time Evaluator
 */
public class DoubleSupportTimeEvaluator extends BaseGaitEvaluator implements ISymptomEvaluator {

    public DoubleSupportTimeEvaluator()
    {
        super(true);
    }
    public DoubleSupportTimeEvaluator(boolean s)
    {
        super(s);
    }
    @Override
    public Observation Evaluate(NamedSignalCollection signalCollection) {

        GaitResults g = EvaluateGait(signalCollection);

        return new Observation("DOUBLESUPPORT", g.getDoubleSupportTime());
    }
}
