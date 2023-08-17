//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.example.primeapp.algos.featureExtractors;


/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

import com.example.primeapp.algos.core.Interfaces.IWindowFeatureExtractor;
import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.algos.core.Signals.SignalFeature;
import com.example.primeapp.algos.signalProcessing.SignalProcess;

/**
 * Base Feature Extractor
 */
public abstract class BaseWindowFeatureExtractor extends SignalProcess implements IWindowFeatureExtractor {
    /**
     * Feature vector
     */
    protected SignalFeature[] features;

    /**
     * Void Constructor
     */
    public BaseWindowFeatureExtractor() throws Exception {
        super();
    }

    /**
     * Constructor
     *
     * @param pFN Buffer size
     */
    public BaseWindowFeatureExtractor(int pFN) throws Exception {
        super(pFN);
    }

    /**
     * Constructor
     *
     * @return
     */
    public SignalFeature[] getFeatures() throws Exception {
        return features;
    }

    /**
     * Main Process method
     *
     * @param signals Named Signal collection (after preprocessing)
     */
    public abstract void process(NamedSignalCollection signals) throws Exception;

}


