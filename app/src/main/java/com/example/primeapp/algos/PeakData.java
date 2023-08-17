package com.example.primeapp.algos;

/**
 * Peak Data
 */
public class PeakData {
    private final long _x;
    private final double _y;

    /**
     * Constructor
     *
     * @param px Location
     * @param py Value
     */
    public PeakData(long px, double py) {
        _x = px;
        _y = py;
    }

    /**
     * Peak Location
     */
    public long getX() {
        return _x;
    }

    /**
     * Peak Value
     */
    public double getY() {
        return _y;
    }

}
