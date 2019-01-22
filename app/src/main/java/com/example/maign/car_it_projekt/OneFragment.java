package com.example.maign.car_it_projekt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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

import static java.lang.Math.round;


//import info.androidhive.materialtabs.R;


public class OneFragment extends Fragment {


    private int mCurrentSpeed;

    //Layout Elements
    private View mThisFragmentView;
    private AppCompatActivity mThisAppCompat;
    private TextInputEditText mTextRuntime;
    private TextInputEditText mTextPedal;
    private TextInputEditText mTextEnvironment;


    //Variables for Bluetooth
    private BTManager mBTManager;
    private BTMsgHandler mBTHandler; // Our main handler that will receive callback notifications
    private String mBluetoothAddress;
    private boolean mIsConnected;
    private boolean mCouldGetAdress;
    private boolean mCouldSetUpManager;

    //Other Variables
    private String mShortenedString = "";
    private String mCurrentMsg;
    private View.OnClickListener mBtStartButtonListener;




    //Speedometer
    private Speedometer mSpeedometerEco;

    //Shift
    private ImageView mShiftImage;



    //Interface to communicate
    private EcoFragmentReceiver ecoFragmentReceiver;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mThisFragmentView = inflater.inflate(R.layout.fragment_one, container, false);

        //Set Up all the layout elements
        setupElements();

        //SpeedMeter
        configureSpeedMeter();


        return mThisFragmentView;
    }



    /**
     * Setting up all the elements.
     * Linking the xml elements to their java counterparts.
     * <p>
     * <p>
     * DELETE LATER
     * Setting an icon and a listener to the mock start button
     */
    private void setupElements() {

        mTextRuntime = mThisFragmentView.findViewById(R.id.runtime);
        mTextPedal = mThisFragmentView.findViewById(R.id.text_pedal);
        mTextEnvironment = mThisFragmentView.findViewById(R.id.text_remaining);
        mShiftImage = mThisFragmentView.findViewById(R.id.image_shift);
        mSpeedometerEco = mThisFragmentView.findViewById(R.id.speedometer_eco);

        mRpmCounter = 0;


    }





    /**
     * Method to set up the speedometer as wished
     */
    private void configureSpeedMeter() {

        mSpeedometerEco.setUnit("x100 rpm");
        mSpeedometerEco.setMaxSpeed((float) 70);

        mSpeedometerEco.setTickNumber(8);
        mSpeedometerEco.setTicks((float) 0.0, (float) 10.0, (float) 20.0, (float) 30.0, (float) 40.0, (float) 50.0, (float) 60.0, (float) 70.0);


    }


    /**
     * As the creation of an error toast has shown to be redundant, a simple method has been written
     * for this purpose
     *
     * @param e
     */
    private void showErrorToast(Exception e) {
        Toast.makeText(mThisFragmentView.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void handleRpm(String rpmValue,double currentSpeed) {
        try {
            Log.d("OneFrag RPM Value:", rpmValue);
            float currentRoundsPerMinute = Float.parseFloat(rpmValue.substring(1));
            giveShiftReccomendation(Math.round(currentRoundsPerMinute),currentSpeed);
            mSpeedometerEco.speedTo(currentRoundsPerMinute);

        } catch (Exception e) {
            Log.d("OneFrag HandleRPM", e.toString());
        }


    }

    public void handleRuntime(String runtimeValue) {
        try {
            String value = runtimeValue.substring(1);
            double runtime = Float.parseFloat(value);
            runtime /= 60;
            NumberFormat formatter = new DecimalFormat("#0");
            mTextRuntime.setText(String.format(getString(R.string.runtimeContent), formatter.format(runtime)));
        } catch (Exception e) {
            Log.d("OneFrag Handle Runtime", e.toString());
        }
    }

    public void handlePedalPosition(String pedalValue) {
        try {
            String value = pedalValue.substring(1);
            mTextPedal.setText(String.format(getString(R.string.pedalContent),value));
        } catch (Exception e) {
            showErrorToast(e);
        }
    }

    public void handleEnvironment(String environmentValue) {
        String value = environmentValue.substring(1);
        Log.d("Handle Environment: ", environmentValue);
        double environment = Double.parseDouble(value);
        environment /= 10;
        NumberFormat formatter = new DecimalFormat("#0.0");
        mTextEnvironment.setText(String.format(getString(R.string.environmentContent), formatter.format(environment)));
    }


    private void resetValues() {
        //
        mSpeedometerEco.speedTo((float) 0.0);
    }


    /**
     * Depending on the current rpm, the app suggests to shift up or downwards
     */
    private void giveShiftReccomendation(int rpm,double speed) {
        if (rpm > 30) {
            mShiftImage.setImageResource(R.drawable.shift_up);
            mRpmCounter++;
            if(mRpmCounter > 8){
                sendToThinkSpeak(1);
                mRpmCounter = 0;
            }
        } else if (rpm < 15) {
            try{
                if(speed <= 10){
                    mShiftImage.setImageResource(R.drawable.shift_ok);
                }else{
                    mShiftImage.setImageResource(R.drawable.shift_down);
                    if(mRpmCounter > 8){
                        sendToThinkSpeak(0);
                        mRpmCounter = 0;
                    }
                }
            }catch (Exception e){
                Log.e("OneFrag Shift", "Error occurred", e);
            }
        } else {
            mShiftImage.setImageResource(R.drawable.shift_ok);
            mRpmCounter = 0;
        }
    }

    public void sendToThinkSpeak(int value) {
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mThisFragmentView.getContext());
        String url = "https://api.thingspeak.com/update?api_key=YLGI2F4QF01D4HHJ&field1=" + value;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Snackbar.make(mThisFragmentView,"Response from server: "+response,Snackbar.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(mThisFragmentView,"Error from server: "+error,Snackbar.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }





}