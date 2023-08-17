//
// Translated by Medlab: 23/1/2015 10:45:16 πμ
//

package com.example.primeapp.algos.featureExtractors;


/**
 * Class used for results of SinAmpF method
 * Maybe a struct instead could be used for memory
 */
public class SinAmpFRes {
    private final double _f;
    private final double _amp;

    /**
     * Constructor
     *
     * @param f   Frequency
     * @param amp Amplitude
     */
    public SinAmpFRes(double f, double amp) {
        _f = f;
        _amp = amp;
    }

    /**
     * Frequency
     */
    public double getF() {
        return _f;
    }

    /**
     * Amplitude
     */
    public double getAmp() {
        return _amp;
    }

}


