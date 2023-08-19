package com.example.primeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.primeapp.algos.DoubleSupportTimeEvaluator;
import com.example.primeapp.algos.DyskinesiaEvaluator;
import com.example.primeapp.algos.GaitEvaluator;
import com.example.primeapp.algos.GaitSpeedEvaluator;
import com.example.primeapp.algos.PronSupEvaluator;
import com.example.primeapp.algos.StanceTimeEvaluator;
import com.example.primeapp.algos.StepIMUEvaluator;
import com.example.primeapp.algos.StepTimeEvaluator;
import com.example.primeapp.algos.StideLengthEvaluator;
import com.example.primeapp.algos.TremorEvaluator;
import com.example.primeapp.databinding.FragmentTestrunBinding;
import com.example.primeapp.insoles.HoloMoticonAgent;
import com.example.primeapp.interfaces.IDataCollector;
import com.example.primeapp.interfaces.IDataListener;
import com.example.primeapp.interfaces.IDataShink;
import com.example.primeapp.interfaces.ISymptomEvaluator;
import com.example.primeapp.mock.MockIMUDataCollector;
import com.example.primeapp.models.Observation;
import com.example.primeapp.models.ObservationCollection;

import com.example.primeapp.models.PressureData;
import com.example.primeapp.models.TestCodes;
import com.example.primeapp.sensor.IMUDataCollector;
import com.example.primeapp.services.IIMURecordingService;
import com.example.primeapp.services.IMURecordingService;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.data.AngularVelocity;
import com.mbientlab.metawear.module.Gyro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TestRunFragment extends Fragment implements IDataListener {

    private FragmentTestrunBinding binding;

    private IDataCollector collector = null;
    private String testSelected = "";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        testSelected = getArguments().getString("test");

        initTest(testSelected);
        binding = FragmentTestrunBinding.inflate(inflater, container, false);
        return binding.getRoot();


    }

    private void initTest(String test) {

        switch (test) {
            case TestCodes.Walking:
                initWalkingTest();
                break;
            case TestCodes.PronationSup:
                initPronSupTest();
                break;
            case TestCodes.Resting:
                try {
                    initRestingTest();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                break;
        }


    }

    private LineGraphSeries<DataPoint> seriesLeft;
    private LineGraphSeries<DataPoint> seriesRight;

    private LineGraphSeries<DataPoint> seriesGyroLx;
    private LineGraphSeries<DataPoint> seriesGyroLy;
    private LineGraphSeries<DataPoint> seriesGyroLz;

    private void initChart(View view) {


        GraphView graph1 = (GraphView) view.findViewById(R.id.graph01);
        if (testSelected == TestCodes.Walking&&isInsoleSelected()) {
            seriesLeft = new LineGraphSeries<>();
            seriesRight = new LineGraphSeries<>();

            graph1.addSeries(seriesLeft);
            graph1.addSeries(seriesRight);

            seriesLeft.setColor(Color.RED);
            seriesLeft.setTitle("Left Sole");
            seriesRight.setColor(Color.GREEN);
            seriesRight.setTitle("Right Sole");

            graph1.getLegendRenderer().setVisible(true);//more edit
            graph1.getLegendRenderer().setTextSize(40);
            graph1.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

            graph1.setTitle("Left(Red) Right(Green)");
            graph1.setTitleTextSize(30);
            //graph1.getGridLabelRenderer().setHorizontalAxisTitle("sample");
            graph1.getGridLabelRenderer().setVerticalAxisTitle("Pressure");

        } else {


            seriesGyroLx = new LineGraphSeries<>();
            seriesGyroLy = new LineGraphSeries<>();
            seriesGyroLz = new LineGraphSeries<>();
            seriesGyroLx.setColor(Color.RED);
            seriesGyroLx.setTitle("GyroLx");
            seriesGyroLy.setColor(Color.GREEN);
            seriesGyroLy.setTitle("GyroLy");
            seriesGyroLz.setColor(Color.BLUE);
            seriesGyroLz.setTitle("GyroLz");
            //graph1.getGraphContentWidth();
            //graph1.getViewport().;
            //graph1.getGridLabelRenderer().setSecondScaleLabelVerticalWidth(4);
            graph1.addSeries(seriesGyroLx);
            graph1.addSeries(seriesGyroLy);
            graph1.addSeries(seriesGyroLz);
            graph1.getLegendRenderer().setVisible(true);//more edit
            graph1.getLegendRenderer().setTextSize(40);
            graph1.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

            graph1.setTitle("Gx(Red) Hy(Green) Gz(Blue)");
            graph1.setTitleTextSize(30);
            graph1.getGridLabelRenderer().setHorizontalAxisTitle("sample");
            graph1.getGridLabelRenderer().setVerticalAxisTitle("Gyro (deg/sec)");


        }

        graph1.getViewport().setXAxisBoundsManual(true);
        graph1.getViewport().setMinX(0);
        graph1.getViewport().setMaxX(1000);
        graph1.getViewport().setScrollable(true);

    }

    List<ISymptomEvaluator> evaluatorList = new ArrayList<ISymptomEvaluator>() {


    };


    private boolean isInsoleSelected()
    {
        return false;

    }
    int testDuration = 0;

    private void initWalkingTest() {

        //Init Evaluators
        evaluatorList.clear();
       // evaluatorList.add(new GaitEvaluator());
        evaluatorList.add(new StepIMUEvaluator(isInsoleSelected()));
        evaluatorList.add(new StepTimeEvaluator(isInsoleSelected()));
        //evaluatorList.add(new DoubleSupportTimeEvaluator(isInsoleSelected()));
        //  evaluatorList.add(new StanceTimeEvaluator(isInsoleSelected()));
        evaluatorList.add(new StideLengthEvaluator(100, 10,isInsoleSelected()));
        evaluatorList.add(new GaitSpeedEvaluator(100, 10,isInsoleSelected()));
        testDuration = 60;
        //Int DataCollector
        try {



            if(isInsoleSelected()) {
                agent = new HoloMoticonAgent(getContext());

                collector = agent;
            }
            else
            collector = new IMUDataCollector(getIMUService());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    HoloMoticonAgent agent = null;

    HoloMoticonAgent getHoloService() {

        return agent;
    }

    private void initPronSupTest() {
        //Init Evaluators
        evaluatorList.clear();
        evaluatorList.add(new PronSupEvaluator());
        testDuration = 30;
        //Int DataCollector
        try {
            collector = new IMUDataCollector(getIMUService());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private IIMURecordingService getIMUService() {
        MainActivity main = (MainActivity) getActivity();
        IIMURecordingService service = main.getImuService();
        return service;
    }

    private void initRestingTest() throws Exception {

        //Init Evaluators
        testDuration = 60;
        evaluatorList.clear();
        //Init Evaluators
        evaluatorList.add(new TremorEvaluator());
        evaluatorList.add(new DyskinesiaEvaluator());

        //Int DataCollector
        collector = new IMUDataCollector(getIMUService());


    }


    private void dismissProgressBar(ObservationCollection results) {

        Bundle args = new Bundle();
        args.putSerializable("results", results);
        NavHostFragment.findNavController(TestRunFragment.this)
                .navigate(R.id.action_SecondFragment_to_ThirdFragment, args);
    }

    private void displayProgressBar(String msg) {

    }

    private void updateProgressBar(int msg) {

    }


    int sample = 0;

    List<AngularVelocity> tmpdata = new ArrayList<>();
    List<PressureData> tmpPressureData = new ArrayList<>();

    List<AngularVelocity> imudata = new ArrayList<>();

    List<Acceleration> tmpAccdata = new ArrayList<>();

    List<Acceleration> accdata = new ArrayList<>();
    List<PressureData> pressureData = new ArrayList<>();

    @Override
    public void addData(AngularVelocity data) {
        tmpdata.add(data);
        //If size is more than 50 samples then update the list with a runnable
        if (tmpdata.size() >= 20) {

            imudata.addAll(tmpdata);
            tmpdata.clear();
            liveDataHandler.postDelayed(newDataUpdate, 2);
        }


    }

    @Override
    public void addData(Acceleration data) {


        tmpAccdata.add(data);
        //If size is more than 50 samples then update the list with a runnable
        if (tmpAccdata.size() >= 20) {

            accdata.addAll(tmpAccdata);
            tmpAccdata.clear();
            liveDataHandler.postDelayed(newAccDataUpdate, 2);
        }
    }

    @Override
    public void addData(PressureData data) {

        tmpPressureData.add(data);
        //If size is more than 50 samples then update the list with a runnable
        if (tmpPressureData.size() >= 50) {

            pressureData.addAll(tmpPressureData);
            tmpPressureData.clear();
            liveDataHandler.postDelayed(newDataUpdate, 2);
        }
    }

    private Runnable newDataUpdate = new Runnable() {
        @Override
        public void run() {
//            if(MainActivity.isHandlerThreadAlive==1){
//                myHandler.removeCallbacks(HeatMapDelete);
//            }
            try {


                for (AngularVelocity data :
                        imudata) {

                    seriesGyroLx.appendData(new DataPoint(sample, data.x()), true, 1000);
                    seriesGyroLy.appendData(new DataPoint(sample, data.y()), true, 1000);
                    seriesGyroLz.appendData(new DataPoint(sample, data.z()), true, 1000);

                    sample++;
                }
                imudata.clear();
                for (PressureData data :
                        pressureData) {


                    seriesLeft.appendData(new DataPoint(sample, data.left()), true, 1000);
                    seriesRight.appendData(new DataPoint(sample, data.right()), true, 1000);

                    sample++;


                }
                pressureData.clear();
                //eos edo
            } catch (Exception e) {

                imudata.clear();
                pressureData.clear();
            }
        }
    };


    private Runnable newAccDataUpdate = new Runnable() {
        @Override
        public void run() {
//            if(MainActivity.isHandlerThreadAlive==1){
//                myHandler.removeCallbacks(HeatMapDelete);
//            }
            try {


                for (Acceleration data :
                        accdata) {

                    seriesGyroLx.appendData(new DataPoint(sample, data.x()), true, 1000);
                    seriesGyroLy.appendData(new DataPoint(sample, data.y()), true, 1000);
                    seriesGyroLz.appendData(new DataPoint(sample, data.z()), true, 1000);

                    sample++;
                }
                accdata.clear();

                //eos edo
            } catch (Exception e) {

                imudata.clear();
                pressureData.clear();
            }
        }
    };
    private Handler liveDataHandler = new Handler();

    // The definition of our task class
    private class ProcessTask extends AsyncTask<List<ISymptomEvaluator>, Integer, ObservationCollection> {
        private final IDataCollector mCollector;

        public ProcessTask(IDataCollector collector) {
            mCollector = collector;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayProgressBar("Downloading...");
        }

        @Override
        protected ObservationCollection doInBackground(List<ISymptomEvaluator>... params) {
            List<ISymptomEvaluator> evaluators = params[0];

            ObservationCollection observations = new ObservationCollection();
            evaluators.forEach((e) -> {

                observations.add(e.Evaluate(mCollector.GetData()));
            });

            return observations;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            updateProgressBar(values[0]);
        }

        @Override
        protected void onPostExecute(ObservationCollection result) {
            super.onPostExecute(result);
            dismissProgressBar(result);
        }
    }

    private boolean running = false;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initChart(view);
        TestRunFragment fragment = this;

        binding.startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (running) {
                    running = false;
                    if (collector != null)
                        collector.Stop();
                    IIMURecordingService service = getIMUService();
                    if (service != null)
                        service.unregisterListener(fragment);
                    //  binding.startStop.setText(R.string.start);
                    new ProcessTask(collector).execute(evaluatorList);
                    //   NavHostFragment.findNavController(TestRunFragment.this)
                    //         .navigate(R.id.action_SecondFragment_to_ThirdFragment);

                } else {
                    //Start Data Collection
                    if (collector != null)
                        collector.Start();

                    IIMURecordingService service = getIMUService();
                    if (service != null)
                        service.registerListener(fragment);

                    HoloMoticonAgent agent = getHoloService();
                    if (agent != null)
                        agent.registerListener(fragment);


                    binding.startStop.setText(R.string.stop);
                    running = true;
                }
                //   NavHostFragment.findNavController(ResultsFragment.this)
                //         .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}