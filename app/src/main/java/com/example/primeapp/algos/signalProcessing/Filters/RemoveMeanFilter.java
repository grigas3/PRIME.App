//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:09 μμ
//

package com.example.primeapp.algos.signalProcessing.Filters;

import com.example.primeapp.algos.core.Interfaces.ISignalProcessor;
import com.example.primeapp.algos.core.Signals.SignalBuffer;
import com.example.primeapp.algos.core.Signals.SignalCollection;


/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */
/**
 * Remove Mean Filter
 */
//
// Translated by Medlab: 23/1/2015 10:45:16 πμ
//



/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Remove Mean Filter
 */
public class RemoveMeanFilter implements ISignalProcessor {
    /**
     * Apply In Place
     *
     * @param signal Input signal
     */

    public void applyInPlace(SignalBuffer signal) throws Exception {
        Double average = signal.average();
        signal.addScalar(-average);
    }

    /**
     * Apply to source buffer
     *
     * @param source Source signal buffer
     * @param to     Dest signal buffer
     */

    public void applyTo(SignalBuffer source, SignalBuffer to) throws Exception {
        //TODO ASSERTS
        Double average = source.average();
        to.copyFrom(source);
        to.addScalar(-average);
    }

    /**
     * Apply In Place for signal collection
     *
     * @param signal Input Signal Collection
     */

    public void applyInPlace(SignalCollection signal) throws Exception {
        for (int i = 0; i < signal.getSignals(); i++) {
            Double average = signal.get___idx(i).average();
            signal.get___idx(i).addScalar(-average);
        }
    }

    /**
     * Apply to destination signal collection
     *
     * @param source Source signal buffer
     * @param to     Destination signal Buffer
     */

    public void applyTo(SignalCollection source, SignalCollection to) throws Exception {
        for (int i = 0; i < source.getSignals(); i++) {
            //TODO ASSERTS
            Double average = source.get___idx(i).average();
            to.get___idx(i).copyFrom(source.get___idx(i));
            to.get___idx(i).addScalar(-average);
        }
    }


}


