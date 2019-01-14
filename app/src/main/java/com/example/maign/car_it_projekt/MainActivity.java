package com.example.maign.car_it_projekt;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OneFragment.EcoFragmentReceiver, TwoFragment.SportFragmentReceiver {

    //Tablayout elements
    private TabLayout tabLayout;
    private OneFragment oneFrag;
    private TwoFragment twoFrag;
    private FloatingActionButton mFab;


    //Saved the layout into a view attribute in order to refer to it when using snackbars
    private View mParentView;

    //Variables for Bluetooth
    private BTManager mBTManager;
    private BTMsgHandler mBTHandler; // Our main handler that will receive callback notifications
    private String mBluetoothAddress;
    private boolean mIsConnected;
    private boolean mCouldGetAdress;
    private boolean mCouldSetUpManager;
    int counter;

    //Acceleration
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;





    //Eco Values
    private String mRpm, mRuntime, mSpaceholder, mRemaining, mShift;


    //Sport Values
    private String mVelocity, mAcceleration, mEngineLoad, mTemperature;


    //Small array in which the tabs icons are stored
    private int[] tabIcons = {
            R.drawable.ic_herbal_spa_treatment_leaves_1,
            R.drawable.ic_dashboard
    };

    private int[] tabColors ={
            R.color.lightGreen,
            R.color.materialRed
    };

    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Link the java objects to their xml counterparts and
          set an onTabSelectedListener to the tab layout*/
        setupElements();

        /* Method to get the current bluetooth address from an intent's extra
          from the menu activity and save it in a corresponding variable*/
        getBluetoothAddress();

        //Method to setup the tablayout's tab icons
        setupTabIcons();

        //Create Message Handler to handle incoming messages
        createMsgHandler();

        //Create Bluetooth Manager to handle the bluetooth connection
        createBtManager();

        initializeConnection();


    }






    /**
     * Setting up tab icons
     */
    private void setupTabIcons() {
        try {
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        } catch (NullPointerException e) {
            showErrorSnackbar(e);
        }
    }

    /**
     * Setting up a viewpager with fragments
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(oneFrag, "ECO ");
        adapter.addFragment(twoFrag, "SPORT");
        viewPager.setAdapter(adapter);
    }

    /**
     * Extract the bluetooth address attached to the intent from menu activity
     * and save it into the variable mBluetoothAddress
     */
    private void getBluetoothAddress() {
        Bundle extras = getIntent().getExtras();
        try {
            mBluetoothAddress = extras.getString("BT_ADDRESS");
        } catch (NullPointerException e) {
            showErrorSnackbar(e);
        }


    }

    /**
     * Setting up the elements.
     * Linking java objects to their xml counterparts.
     * setting viewpager
     */
    private void setupElements() {
        //Layout Elements
        mParentView = findViewById(R.id.Coordinator_Main);
        tabLayout = findViewById(R.id.tabs);
        final ViewPager viewPager = findViewById(R.id.viewpager);

        //Fragments need to be initialized before being added to the viewPager
        oneFrag = new OneFragment();
        twoFrag = new TwoFragment();

        setupViewPager(viewPager);

        //Layout Elements
        mParentView = findViewById(R.id.Coordinator_Main);
        tabLayout = findViewById(R.id.tabs);



        tabLayout.setupWithViewPager(viewPager);



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               if(tab.getPosition() == 0){
                   tabLayout.setSelectedTabIndicatorColor(tabColors[0]);
               }else if(tab.getPosition() == 1){
                   tabLayout.setSelectedTabIndicatorColor(tabColors[1]);
               }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        counter = 0;




        //Eco Values
        mRpm = "";
        mRuntime = "";
        mSpaceholder = "";
        mRemaining = "";
        mShift = "";

        //Sport Values
        mVelocity = "";
        mEngineLoad = "";
        mTemperature = "";




    }


    private void showErrorSnackbar(Exception e) {
        Snackbar.make(mParentView, "An error occured: " + e, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onEcoSent(String msg) {
       if(msg.startsWith("A")){
           oneFrag.handleRpm(msg);

       }

        if(msg.startsWith("B")){
            oneFrag.handleRuntime(msg);

        }

       if(msg.startsWith("C")){
           oneFrag.handlePedalPosition(msg);

       }

       if(msg.startsWith("D")){
           oneFrag.handleRemaining(msg);
       }








    }

    @Override
    public void onSportSent(String msg) {

        if(msg.startsWith("A")){
            twoFrag.handleRpm(msg);
        }

        if(msg.startsWith("E")){
            twoFrag.handleSpeed(msg);
        }

        if(msg.startsWith("G")){
            twoFrag.handleEngineLoad(msg);
        }

        if(msg.startsWith("H")){
            twoFrag.handleTemperature(msg);
        }


    }





    /**
     * Adapter class to handle the two fragments inside the tab layout
     * The inherited method are rather self explaining.
     * Because of that this class is not further commented
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    /**
     * Message handler to handle incoming values
     */
    private void createMsgHandler() {
        mBTHandler = new BTMsgHandler() {
            @Override
            void receiveMessage(String msg) {
                onEcoSent(msg);
                onSportSent(msg);

            }

            /**
             * Setzt einen Text in das entsprechende Feld um den Benutzer Ã¼ber den Status
             * der Verbindung zu informieren.
             * @param isConnected
             */

            @Override
            void receiveConnectStatus(boolean isConnected) {

                if (isConnected) {
                    Snackbar.make(mParentView, "Connected", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mParentView, "Connection failed", Snackbar.LENGTH_SHORT).show();
                }
            }


            /**
             * Falls Fehler auftritt, zeige ihn dem Nutzer
             * @param e
             */
            @Override
            void handleException(Exception e) {
                showErrorSnackbar(e);
            }
        };
    }

    /**
     * Creation of a bluetooth manager
     */
    private void createBtManager() {
        try {
            mBTManager = new BTManager(this, mBTHandler);
            mCouldSetUpManager = true;
            Log.d("Eco Activity", "Manager created");

        } catch (Exception e) {
            showErrorSnackbar(e);
        }
    }







    private void initializeConnection(){
        mBTManager.connect(mBluetoothAddress);
    }




}