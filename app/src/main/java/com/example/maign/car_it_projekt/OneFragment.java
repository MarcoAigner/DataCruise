package com.example.maign.car_it_projekt;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

//Speed Gauge
import com.github.anastr.speedviewlib.Speedometer;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class OneFragment extends Fragment {


    //Layout Elements
    private View mThisFragmentView;
    private TextInputEditText mTextRuntime;
    private TextInputEditText mTextPedal;
    private TextInputEditText mTextOutside;


    //Speedometer
    private Speedometer mSpeedometerEco;

    //Shift
    private ImageView mShiftImage;


    //Counter on how long rpm is too high
    private int mRpmCounter;


    public interface EcoFragmentReceiver {
        void onEcoSent(String msg);
    }


    public OneFragment() {
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
        mThisFragmentView = inflater.inflate(R.layout.fragment_one, container, false);

        //Set Up all the layout elements
        setupElements();

        //SpeedMeter
        configureSpeedMeter();


        return mThisFragmentView;
    }


    /*
     * Setting up all the elements.
     * Linking the xml elements to their java counterparts.
     */
    private void setupElements() {

        mTextRuntime = mThisFragmentView.findViewById(R.id.runtime);
        mTextPedal = mThisFragmentView.findViewById(R.id.text_pedal);
        mTextOutside = mThisFragmentView.findViewById(R.id.text_remaining);
        mShiftImage = mThisFragmentView.findViewById(R.id.image_shift);
        mSpeedometerEco = mThisFragmentView.findViewById(R.id.speedometer_eco);

        mRpmCounter = 0;


    }


    /*
     * Method to set up the speedometer as wished
     */
    private void configureSpeedMeter() {

        mSpeedometerEco.setUnit("x100 rpm");
        mSpeedometerEco.setMaxSpeed((float) 70);

        mSpeedometerEco.setTickNumber(8);
        mSpeedometerEco.setTicks((float) 0.0, (float) 10.0, (float) 20.0, (float) 30.0, (float) 40.0, (float) 50.0, (float) 60.0, (float) 70.0);


    }


    //takes the current rpm and sets the speedmeter accordingly
    void handleRpm(String rpmValue, double currentSpeed) {
        try {
            Log.d("OneFrag RPM Value:", rpmValue);
            float currentRoundsPerMinute = Float.parseFloat(rpmValue.substring(1));
            giveShiftRecommendation(Math.round(currentRoundsPerMinute), currentSpeed);
            mSpeedometerEco.speedTo(currentRoundsPerMinute);

        } catch (Exception e) {
            Log.d("OneFrag HandleRPM", e.toString());
        }


    }

    //Takes the current runtime and formats it into it's fitting textView
    void handleRuntime(String runtimeValue) {
        try {
            double runtime = Float.parseFloat(runtimeValue.substring(1));
            runtime /= 60;
            NumberFormat formatter = new DecimalFormat("#0.0");
            mTextRuntime.setText(String.format(getString(R.string.runtimeContent), formatter.format(runtime)));
        } catch (Exception e) {
            Log.d("OneFrag Handle Runtime", e.toString());
        }
    }

    //Takes the current pedalPosition and formats it into it's fitting textView
    void handlePedalPosition(String pedalValue) {
        try {
            mTextPedal.setText(String.format(getString(R.string.pedalContent), pedalValue.substring(1)));
        } catch (Exception e) {

            Log.d("OneFrag handlePedal", e.toString());
        }
    }

    //Takes the current temperature and formats it into it's fitting textView
    void handleOutsideTemp(String temp) {
        double currentTemp = Double.parseDouble(temp.substring(1));
        currentTemp /= 10;
        mTextOutside.setText(String.format(getString(R.string.outsideContent), currentTemp));

    }


    /*
     * Depending on the current rpm, the app suggests to shift up or downwards
     */
    private void giveShiftRecommendation(int rpm, double speed) {
        if (rpm > 30) {
            mShiftImage.setImageResource(R.drawable.shift_up);
            mRpmCounter++;
            if (mRpmCounter > 8) {
                sendToThinkSpeak(1);
                mRpmCounter = 0;
            }
        } else if (rpm < 15) {
            try {
                if (speed <= 10) {
                    mShiftImage.setImageResource(R.drawable.shift_ok);
                } else {
                    mShiftImage.setImageResource(R.drawable.shift_down);
                    if (mRpmCounter > 8) {
                        sendToThinkSpeak(0);
                        mRpmCounter = 0;
                    }
                }
            } catch (Exception e) {
                Log.e("OneFrag Shift", "Error occurred", e);
            }
        } else {
            mShiftImage.setImageResource(R.drawable.shift_ok);
            mRpmCounter = 0;
        }
    }

    //Takes an integer as an argument which is then sent to the connected thingspeak server
    private void sendToThinkSpeak(int value) {
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mThisFragmentView.getContext());
        String url = "https://api.thingspeak.com/update?api_key=YLGI2F4QF01D4HHJ&field1=" + value;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Snackbar.make(mThisFragmentView, "Response from server: " + response, Snackbar.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(mThisFragmentView, "Error from server: " + error, Snackbar.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //sets all values back to default
    void resetEcoValues() {
        mSpeedometerEco.speedTo((float) 0.0);
        mTextRuntime.setText("");
        mTextPedal.setText("");
        mTextOutside.setText("");
    }


}