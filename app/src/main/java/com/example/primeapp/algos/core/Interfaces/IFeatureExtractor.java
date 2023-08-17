//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.example.primeapp.algos.core.Interfaces;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;


/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Feature Extractor Interface
 */
public interface IFeatureExtractor {
    /**
     * Process Signal
     *
     * @param signalCollection Signal Collection
     */
    void process(NamedSignalCollection signalCollection) throws Exception;

}


