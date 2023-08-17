//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.example.primeapp.algos.core.Signals;



/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

import com.example.primeapp.algos.core.BaseMath;

/**
 * Main Signal Buffer
 */
public class SignalBuffer extends BaseMath {
    private final int size;
    private final double[] buffer;

    /**
     * Constructor
     *
     * @param s Size
     */
    public SignalBuffer(int s) throws Exception {
        size = s;
        buffer = new double[s];
    }

    /**
     * Signal Size
     */
    public int getSize() throws Exception {
        return size;
    }

    /**
     * Accessor
     *
     * @param index Index
     * @return Signal Value
     */
    public double get___idx(int index) throws Exception {
        return buffer[index];
    }

    public void set___idx(int index, double value) throws Exception {
        buffer[index] = value;
    }

    /**
     * Copy from array
     *
     * @param data source array
     */
    public void copyFrom(double[] data) throws Exception {
        if (size >= 0) System.arraycopy(data, 0, buffer, 0, size);
    }

    /**
     * Copy to data array
     *
     * @param data Array
     */
    public void copyTo(double[] data) throws Exception {
        if (size >= 0) System.arraycopy(buffer, 0, data, 0, size);
    }

    /**
     * Copy from source signal
     *
     * @param source Source signal
     */
    public void copyFrom(SignalBuffer source) throws Exception {
        for (int i = 0; i < size; i++)
            buffer[i] = source.get___idx(i);
    }

    /**
     * Signal Average
     *
     * @return Average Value
     */
    public double average() throws Exception {
        double m = 0.0f;
        double v;
        for (int i = 0; i < size; i++) {
            v = buffer[i];
            m += v;
        }
        return m / ((double) size);
    }

    /**
     * Signal Max value
     *
     * @return Max Value
     */
    public double max() throws Exception {
        double m =Double.MIN_VALUE;
        double v;
        for (int i = 0; i < size; i++) {

            if(buffer[i]>m)
                m=buffer[i];

        }
        return m;
    }

    /**
     * Signal Max value
     *
     * @return Max Value
     */
    public double min() throws Exception {
        double m =Double.MAX_VALUE;
        double v;
        for (int i = 0; i < size; i++) {

            if(buffer[i]<m)
                m=buffer[i];

        }
        return m;
    }

    /**
     * Signal Standard Deviation
     *
     * @return Std Value
     */
    public double std() throws Exception {
        double q = 0.0f;
        double m = 0.0f;
        double v;
        for (int i = 0; i < size; i++) {
            v = buffer[i];
            m += v;
        }
        m = m / ((double) size);
        for (int i = 0; i < size; i++) {
            v = buffer[i];
            q += (m - v) * (m - v);
        }
        return sqrt(q / ((double) size - 1));
    }

    /**
     * Add Scalar to signal
     *
     * @param v Scalar Value
     */
    public void addScalar(double v) throws Exception {
        for (int i = 0; i < size; i++)
            buffer[i] = buffer[i] + v;
    }

    /**
     * Mult Scalar to signal
     *
     * @param v Scalar Value
     */
    public void multScalar(double v) throws Exception {
        for (int i = 0; i < size; i++)
            buffer[i] = buffer[i] * v;
    }

    /**
     * Absolute differences
     *
     * @return
     */
    public double cdiff() throws Exception {
        double s = 0;
        for (int i = 1; i < size; i++)
            s += abs(buffer[i] - buffer[i - 1]);
        return s;
    }

    public double sum_energy() {

        double s = 0;
        for (int j = 0; j < size; j++)
            s += buffer[j] * buffer[j];
        return s;
    }

    public double sum_energy(int st, int et) {

        double s = 0;
        for (int j = st; j < et && j < size; j++)
            s += buffer[j] * buffer[j];
        return s;
    }

    public double sum_diff_energy() {

        double s = 0;
        for (int i = 0; i < size - 1; i++)
            s += (buffer[i + 1] - buffer[i]) * (buffer[i + 1] - buffer[i]);

        return s;
    }




}


