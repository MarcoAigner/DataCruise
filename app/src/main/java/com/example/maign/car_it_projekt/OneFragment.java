package com.example.maign.car_it_projekt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
    private TextInputEditText mTextRemaining;


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

    public void testUpdateElements(int testValA, int testValB) {
        mTextRuntime.setText(String.valueOf(testValA));
        mTextPedal.setText(String.valueOf(testValB));
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
        mTextRemaining = mThisFragmentView.findViewById(R.id.text_remaining);
        mShiftImage = mThisFragmentView.findViewById(R.id.image_shift);
        mSpeedometerEco = mThisFragmentView.findViewById(R.id.speedometer_eco);

        mRpmCounter = 0;


    }


    /**
     * OnClickListener Method for the start button.
     * OnClick, a connection to the selected bt device is established
     *
     * @return
     */
    private View.OnClickListener createStartButtonListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mBTManager != null) {
                        //if (mCouldGetAdress == true) {
                        if (mIsConnected == false) {
                            //mBTManager.connect(mTestBTConnectionArduinoAdrdress);

                            mIsConnected = true;
                            //handleValues("A323B456C458D785E457F587G845H554I5418");
                            //sendStringToThinkspeak("5678");
                            mSpeedometerEco.speedTo(Float.parseFloat("30"));


                        } else {
                            mBTManager.cancel();
                            //Toast.makeText(Eco_Activity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                            resetValues();

                            mIsConnected = false;
                        }
                        /*} else {
                            Toast.makeText(mThisFragmentView.getContext(), "No Bluetooth Address found", Toast.LENGTH_LONG);
                        }*/
                    } else {
                        Toast.makeText(mThisFragmentView.getContext(), "Bluetooth Manager Null Exception", Toast.LENGTH_LONG);
                    }
                    Log.d("Eco Activity", "On Click setup");
                } catch (Exception e) {
                    Toast.makeText(mThisFragmentView.getContext(), e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        };
        return listener;
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

    public void handleRpm(String rpmValue) {
        try {
            //mShortenedString = rpmValue.substring(1);
            Log.d("OneFrag RPM Value:", rpmValue);
            float currentRoundsPerMinute = Float.parseFloat(rpmValue.substring(1));
            Log.d("Eco Activity", "Reached starts with A, Drehzahl: " + currentRoundsPerMinute);
            giveShiftReccomendation(Math.round(currentRoundsPerMinute));
            mSpeedometerEco.speedTo(currentRoundsPerMinute);

        } catch (Exception e) {
            Log.d("OneFrag HandleRPM", e.toString());
        }


    }

    public void handleRuntime(String runtimeValue) {
        try {
            mShortenedString = runtimeValue.substring(1);
            double runtime = Float.parseFloat(mShortenedString);
            runtime /= 60;
            NumberFormat formatter = new DecimalFormat("#0");
            mTextRuntime.setText(formatter.format(runtime) + "\nminutes");
        } catch (Exception e) {
            Log.d("OneFrag Handle Runtime", e.toString());
        }
    }

    public void handlePedalPosition(String pedalValue) {
        try {
            mShortenedString = pedalValue.substring(1);
            mTextPedal.setText(mShortenedString + " %");
        } catch (Exception e) {
            showErrorToast(e);
        }
    }

    public void handleRemaining(String remainingValue) {
        mShortenedString = remainingValue.substring(1);
        mTextRemaining.setText(mShortenedString + " %");
    }


    private void resetValues() {
        //
        mSpeedometerEco.speedTo((float) 0.0);
    }


    /**
     * Depending on the current rpm, the app suggests to shift up or downwards
     */
    private void giveShiftReccomendation(int rpm) {
        if (rpm > 30) {
            mShiftImage.setImageResource(R.drawable.shift_up);
            mRpmCounter++;
            if(mRpmCounter > 8){
                sendToThinkSpeak(1);
                mRpmCounter = 0;
            }
        } else if (rpm < 15) {
            mShiftImage.setImageResource(R.drawable.shift_down);
            if(mRpmCounter > 8){
                sendToThinkSpeak(0);
                mRpmCounter = 0;
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