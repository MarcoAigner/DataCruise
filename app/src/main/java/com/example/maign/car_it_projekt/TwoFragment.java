package com.example.maign.car_it_projekt;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.anastr.speedviewlib.ProgressiveGauge;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class TwoFragment extends Fragment {

    //Layout Elements
    private View mThisFragmentView;
    private TextView mTextVMax;
    private TextView mTextRpm;
    private TextView mTextEngineLoad;
    private TextView mTextTemperature;


    private float vMax;


    //Speed Meter
    private ProgressiveGauge mSpeedMeterSport;


    public interface SportFragmentReceiver {
        void onSportSent(String input);
    }

    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mThisFragmentView = inflater.inflate(R.layout.fragment_two, container, false);


        //Set Up all the layout elements
        setupElements();

        //Speed Meter
        configureSpeedMeter();


        return mThisFragmentView;
    }


    private void setupElements() {

        //mTextRpmSport = findViewById(R.id.text_rpm_sport);
        mTextVMax = mThisFragmentView.findViewById(R.id.text_v_max);
        mTextRpm = mThisFragmentView.findViewById(R.id.text_rpm);
        mTextEngineLoad = mThisFragmentView.findViewById(R.id.text_engine_load);
        mTextTemperature = mThisFragmentView.findViewById(R.id.text_temperature);
        mSpeedMeterSport = mThisFragmentView.findViewById(R.id.speedometer_sport);

        vMax = 0;

    }

    private void configureSpeedMeter() {
        mSpeedMeterSport.setMinMaxSpeed((float) 0.0, (float) 250.0);
        mSpeedMeterSport.setUnitUnderSpeedText(false);
        mSpeedMeterSport.setSpeedTextSize((float) 100.0);
        mSpeedMeterSport.setUnitTextSize((float) 100.0);


    }

    void handleSpeed(String speed) {
        float speedFloat = Float.parseFloat(speed.substring(1));
        if (speedFloat > vMax) {
            vMax = speedFloat;
        }
        mTextVMax.setText(String.format(getString(R.string.vMaxContent),speedFloat));
        mSpeedMeterSport.speedTo(speedFloat);
    }

    void handleEngineLoad(String engineLoad) {
        mTextEngineLoad.setText(String.format(getString(R.string.loadContent),engineLoad.substring(1)));
    }

    void handleTemperature(String temperature) {
        mTextTemperature.setText(String.format(getString(R.string.temperatureContent),temperature.substring(1)));
    }

    void handleRpm(String rpm) {
        double doubleRpm = Double.parseDouble(rpm.substring(1));
        doubleRpm *= 100;
        NumberFormat formatter = new DecimalFormat("#0.0");
        mTextRpm.setText(String.format(getString(R.string.rpmContent),formatter.format(doubleRpm)));
    }

    void resetSportValues() {
        mSpeedMeterSport.speedTo((float) 0.0);
        mTextRpm.setText("");
        mTextEngineLoad.setText("");
        mTextTemperature.setText("");
        vMax = 0;
    }


}
