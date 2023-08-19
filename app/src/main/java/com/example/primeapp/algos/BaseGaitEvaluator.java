package com.example.primeapp.algos;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;
import com.example.primeapp.algos.core.Signals.SignalBuffer;
import com.example.primeapp.algos.core.Signals.SignalCollection;

import java.util.ArrayList;


/**
 * Base abstract class for a Gait Evalautor
 */
public abstract class BaseGaitEvaluator {


    private final static double amp_thresh = 1;
    private final static double dur_thresh = 5;

    private final static double height_thresh = 100;
    private final static double THRES = 3;

    private boolean insoles;

    protected BaseGaitEvaluator(boolean pinsoles) {
        insoles = pinsoles;

    }

    protected BaseGaitEvaluator() {
        insoles = true;

    }

    protected GaitResults EvaluateGaitFromIMU(NamedSignalCollection signalCollection) {

        double dt = 0.02;

        double firstStepIndex = -1;
        double lastStepIndex = -1;

        double totalStanceTimeL = 0;
        double totalSwingTimeL = 0;
        double totalStanceTimeR = 0;
        double totalSwingTimeR = 0;
        double totaldoubleSupport = 0;
        ArrayList<PeakData> current_peaks = new ArrayList<PeakData>();

        try {

            SignalCollection data = signalCollection.get___idx("IMU");


            SignalBuffer tmpBuffer = data.get___idx(5);

            double thresL = THRES;//min+0.2*(max-min);
            double min = tmpBuffer.min();
            double max = tmpBuffer.max();

            if (Math.abs(min) > Math.abs(max)) {

                tmpBuffer.multScalar(-1);
            }

            double thresR = THRES;//min+0.2*(max-min);

            boolean stancePhaseL = false;
            boolean stancePhaseR = false;
            boolean swingPhaseL = false;
            boolean swingPhaseR = false;
            boolean doubleSupportPhase = false;


            double stanceTimeL = 0;
            double swingTimeL = 0;
            double stanceTimeR = 0;
            double swingTimeR = 0;
            double doubleSupportTime = 0;


            int stanceCountL = 0;
            int swingCountL = 0;
            int stanceCountR = 0;
            int swingCountR = 0;
            int doubleSupportCount = 0;


            int i, j, k;
            int current_ind = 0;
            int max_found = 0;
            int min_found = 0;
            int min_map_x = 0;
            double min_map_y = 0;


            for (i = 0; i < data.getSize() - 1; i++) {


                double val1 = tmpBuffer.get___idx(i);
                double val2 = tmpBuffer.get___idx(i + 1);
                //  int start = nelements -((int)Fs)*window;
                if (val1 > val2) {
                    max_found = 1;
                    for (j = current_ind; j < i; j++) {
                        double valj = tmpBuffer.get___idx(j);
                        if (val1 < valj) {
                            max_found = 0;
                            break;
                        }

                    }
                    if (max_found == 1) {
                        if (min_found != 1) {
                            if (val1 - min_map_y > amp_thresh && i - min_map_x > dur_thresh && val1 > height_thresh) {

                                if (firstStepIndex == -1)
                                    firstStepIndex = i;
                                lastStepIndex = i;
                                current_peaks.add(new PeakData(i, val1));

                            }

                        }
                        current_ind = i;


                    }

                } else if (val1 < val2) {
                    min_found = 1;
                    for (k = current_ind; k < i; k++) {
                        double valk = tmpBuffer.get___idx(k);
                        if (val1 > valk) {
                            min_found = 0;
                            break;
                        }

                    }
                    if (min_found == 1) {
                        min_map_x = i;
                        min_map_y = val1;
                        current_ind = i;
                    }

                }

            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return new GaitResults(
                (lastStepIndex - firstStepIndex) * dt,
                current_peaks.size(),
                (totalStanceTimeL + totalStanceTimeR) / 2,
                (totalSwingTimeL + totalSwingTimeR) / 2,
                totaldoubleSupport);

    }

    protected GaitResults EvaluateGaitFromInsoles(NamedSignalCollection signalCollection) {

        double dt = 0.01;

        double firstStepIndex = -1;
        double lastStepIndex = -1;

        double totalStanceTimeL = 0;
        double totalSwingTimeL = 0;
        double totalStanceTimeR = 0;
        double totalSwingTimeR = 0;
        double totaldoubleSupport = 0;


        int totalForceL = 23;
        int totalForceR = 48;
        int steps = 0;
        try {

            SignalCollection data = signalCollection.get___idx("insoles");


            SignalBuffer yLBuffer = data.get_max_signal(1, 16);
            SignalBuffer yRBuffer = data.get_max_signal(26, 41);


            double minR = yRBuffer.min();
            double minL = yLBuffer.min();
            double max = yLBuffer.max();

            yLBuffer.addScalar(-minL);

            yLBuffer.addScalar(-minR);

            double thresL = THRES;//min+0.2*(max-min);
            //min=yRBuffer.min();
            //max=yRBuffer.max();

            double thresR = THRES;//min+0.2*(max-min);

            boolean stancePhaseL = false;
            boolean stancePhaseR = false;
            boolean swingPhaseL = false;
            boolean swingPhaseR = false;
            boolean doubleSupportPhase = false;


            double stanceTimeL = 0;
            double swingTimeL = 0;
            double stanceTimeR = 0;
            double swingTimeR = 0;
            double doubleSupportTime = 0;


            int stanceCountL = 0;
            int swingCountL = 0;
            int stanceCountR = 0;
            int swingCountR = 0;
            int doubleSupportCount = 0;


            for (int i = 0; i < yLBuffer.getSize(); i++) {
                double yL = yLBuffer.get___idx(i);

                double yR = yRBuffer.get___idx(i);

                //Pressure less than threshold therefore we have foot contact
                if (yL < thresL) {

                    if (firstStepIndex == -1)
                        firstStepIndex = i;

                    if (stancePhaseL) {

                        //Valid Stance Time
                        if (stanceTimeL > 0.2) {
                            totalStanceTimeL = totalStanceTimeL + stanceTimeL;
                            stanceCountL = stanceCountL + 1;
                        }
                        stanceTimeL = 0;
                    }

                    stancePhaseL = false;
                    swingPhaseL = true;
                    swingTimeL = swingTimeL + dt;
                }
                //Foot swing
                else {


                    if (swingPhaseL) {
                        if (i > lastStepIndex)
                            lastStepIndex = i;
                        //Valid Stance Time
                        if (swingTimeL > 0.2) {
                            totalSwingTimeL = totalSwingTimeL + swingTimeL;
                            swingCountL = swingCountL + 1;

                        }
                        swingTimeL = 0;
                    }

                    swingPhaseL = false;

                    stancePhaseL = true;
                    stanceTimeL = stanceTimeL + dt;


                    //Double Support
                    if (stancePhaseR) {
                        doubleSupportPhase = true;
                        doubleSupportTime = doubleSupportTime + dt;
                    } else {
                        if (doubleSupportPhase) {
                            totaldoubleSupport = totaldoubleSupport + doubleSupportTime;
                            doubleSupportCount = doubleSupportCount + 1;

                        }

                        doubleSupportPhase = false;
                        doubleSupportTime = 0;

                    }


                }


                /*
                 RIGHT LEG
                 */

                //Pressure less than threshold therefore we have foot contact
                if (yR < thresR) {
                    if (firstStepIndex == -1)
                        firstStepIndex = i;

                    if (stancePhaseR) {

                        //Valid Stance Time
                        if (stanceTimeR > 0.2) {
                            totalStanceTimeR = totalStanceTimeR + stanceTimeR;
                            stanceCountR = stanceCountR + 1;
                        }
                        stanceTimeR = 0;
                    }

                    stancePhaseR = false;
                    swingPhaseR = true;
                    swingTimeR = swingTimeR + dt;
                }
                //Foot swing
                else {


                    if (swingPhaseR) {


                        if (i > lastStepIndex)
                            lastStepIndex = i;
                        //Valid Stance Time
                        if (swingTimeR > 0.2) {
                            totalSwingTimeR = totalSwingTimeR + swingTimeR;
                            swingCountR = swingCountR + 1;

                        }
                        swingTimeR = 0;
                    }

                    swingPhaseR = false;

                    stancePhaseR = true;
                    stanceTimeR = stanceTimeR + dt;


                }


            }

            steps = stanceCountL;
            totalStanceTimeL = totalStanceTimeL / stanceCountL;
            totalStanceTimeR = totalStanceTimeR / stanceCountR;
            totalSwingTimeL = totalSwingTimeL / swingCountL;
            totalSwingTimeR = totalSwingTimeR / swingCountR;

            totaldoubleSupport = totaldoubleSupport / doubleSupportCount;

        } catch (Exception ex) {

        }


        return new GaitResults(
                (lastStepIndex - firstStepIndex) * dt,
                steps,
                (totalStanceTimeL + totalStanceTimeR) / 2,
                (totalSwingTimeL + totalSwingTimeR) / 2,
                totaldoubleSupport);

    }

    protected GaitResults EvaluateGait(NamedSignalCollection signalCollection) {


        if (insoles) {

            return EvaluateGaitFromInsoles(signalCollection);

        } else {

            return EvaluateGaitFromIMU(signalCollection);
        }

    }
}
