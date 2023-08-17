package com.example.primeapp.algos;

public class GaitResults {

    public double totalTime;
    public double swingTime;

    public double steps;
    public double stanceTime;
    public double doubleSupportTime;

    public GaitResults(double t, double st,double stance, double swing, double doubleSupport) {
        this.totalTime = t;
        this.steps=st;
        this.swingTime = swing;
        this.stanceTime = stance;
        this.doubleSupportTime = doubleSupport;

    }
    public double getSteps() {
        return steps;
    }
    public double getTotalTime() {
        return totalTime;
    }

    public double getSwingTime() {
        return swingTime;
    }

    public double getStanceTime() {
        return stanceTime;
    }

    public double getDoubleSupportTime() {
        return doubleSupportTime;
    }
}
