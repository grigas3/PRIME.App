package com.example.primeapp.interfaces;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.models.Observation;

public interface ISymptomEvaluator {


    Observation Evaluate(NamedSignalCollection signalCollection);


}
