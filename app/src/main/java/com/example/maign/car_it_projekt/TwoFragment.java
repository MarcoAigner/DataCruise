package com.example.maign.car_it_projekt;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.anastr.speedviewlib.ProgressiveGauge;

//import info.androidhive.materialtabs.R;


public class TwoFragment extends Fragment {

    //Layout Elements
    private View mThisFragmentView;
    private TextView mTextRpmSport;
    private TextView mTextVMax;
    private TextView mTextRpm;
    private TextView mTextEngineLoad;
    private TextView mTextTemperature;


    //Variables for Bluetooth
    private BTManager mBTManager;
    private BTMsgHandler mBTHandler; // Our main handler that will receive callback notifications
    private String mBluetoothAddress;
    private boolean mIsConnected;
    private boolean mCouldGetAdress;
    private boolean mCouldSetUpManager;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    //Other Variables
    private String mshortenedString = "";
    private String mCurrentMsg;
    private float vMax;


    //Speed Meter
    ProgressiveGauge mSpeedoMeterSport;

    //Interface to communicate
    private SportFragmentReceiver sportFragmentReceiver;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mThisFragmentView = inflater.inflate(R.layout.fragment_two, container, false);


        //Set Up all the layout elements
        setupElements();

        //Speed Meter
        configureSpeedMeter();



        return mThisFragmentView;
    }

   /* @Override
    public void onPause(){
        super.onPause();
        mBTManager.cancel();
    }

    @Override
    public void onResume(){
        super.onResume();
        mBTManager.connect(mBluetoothAddress);
    }*/

    private void setupElements() {

        //mTextRpmSport = findViewById(R.id.text_rpm_sport);
        mTextVMax = mThisFragmentView.findViewById(R.id.text_v_max);
        mTextRpm = mThisFragmentView.findViewById(R.id.text_rpm);
        mTextEngineLoad = mThisFragmentView.findViewById(R.id.text_engine_load);
        mTextTemperature = mThisFragmentView.findViewById(R.id.text_temperature);
        mIsConnected = false;
        mCouldGetAdress = false;
        mCouldSetUpManager = false;
        mSpeedoMeterSport = mThisFragmentView.findViewById(R.id.speedometer_sport);

        vMax = 0;

    }

    private void configureSpeedMeter() {
        mSpeedoMeterSport.setSpeedometerBackColor(Color.GRAY);
        mSpeedoMeterSport.setSpeedometerColor(Color.RED);
        mSpeedoMeterSport.setMinMaxSpeed((float) 0.0, (float) 250.0);
        mSpeedoMeterSport.setUnitUnderSpeedText(false);
        mSpeedoMeterSport.setSpeedTextColor(Color.WHITE);
        mSpeedoMeterSport.setSpeedTextSize((float) 100.0);
        mSpeedoMeterSport.setUnitTextSize((float) 100.0);
        mSpeedoMeterSport.setUnitTextColor(Color.WHITE);


    }

    public void handleSpeed(String speed){
        float speedFloat = Float.parseFloat(speed.substring(1));
        if(speedFloat > vMax){
            vMax = speedFloat;
        }
        mTextVMax.setText(vMax+" km/h");
        mSpeedoMeterSport.speedTo(speedFloat);
    }

    public void handleEngineLoad(String engineLoad){
        mTextEngineLoad.setText(engineLoad.substring(1)+" %");
    }

    public void handleTemperature(String temperature) {
        mTextTemperature.setText(temperature.substring(1) + " °C");
    }
    public void handleRpm(String rpm){
        double doubleRpm = Double.parseDouble(rpm.substring(1));
        doubleRpm *= 100;
        mTextRpm.setText(doubleRpm +" U/min");
    }











}
