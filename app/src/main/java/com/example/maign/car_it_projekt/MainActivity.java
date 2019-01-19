package com.example.maign.car_it_projekt;


import android.os.Bundle;

import android.util.Log;

import android.view.View;


import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;


import androidx.annotation.NonNull;
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


    //Saved the layout into a view attribute in order to refer to it when using Snackbars
    private View mParentView;


    //Variables for Bluetooth
    private BTManager mBTManager;
    private BTMsgHandler mBTHandler; // Our main handler that will receive callback notifications
    private String mBluetoothAddress;


    //Saves the current speed
    private double mSpeed;


    //Small array in which the tabs icons are stored
    private int[] tabIcons = {
            R.drawable.ic_herbal_spa_treatment_leaves_1,
            R.drawable.ic_dashboard
    };


    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
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

        //Connect on activity startup
        initializeConnection();


    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            mBTManager.cancel();
        } catch (Exception e) {
            showErrorSnackbar("On Stoö error " + e);
        }
        resetValuesOnDisconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mBTManager.cancel();
        } catch (Exception e) {
            showErrorSnackbar("On Pause error " + e);
        }
        resetValuesOnDisconnect();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            mBTManager.connect(mBluetoothAddress);
        } catch (Exception e) {
            showErrorSnackbar("On Restart error " + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mBTManager.connect(mBluetoothAddress);
        } catch (Exception e) {
            showErrorSnackbar("On Resume error " + e);
        }

    }



    private void setupTabIcons() {
        try {
            if(tabIcons != null && tabLayout != null){
                tabLayout.getTabAt(0).setIcon(tabIcons[0]);
                tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            }
        } catch (NullPointerException e) {
            showErrorSnackbar("Couldn't setup TabIcons " + e);
        }
    }


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
            if (extras != null) {
                mBluetoothAddress = extras.getString("BT_ADDRESS");
            }


        } catch (NullPointerException e) {
            showErrorSnackbar("Not able to get BT address");
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


        //Uses the custom Class TypefaceUtil to use a custom font by overwriting the existing Serif one
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/abel_regular.ttf");


    }


    private void showErrorSnackbar(String e) {
        Snackbar.make(mParentView, "An error occured: " + e, Snackbar.LENGTH_LONG).show();
    }

    //Overrides the Interfaces Method OnEcoSent to handle values used by the eco fragment
    @Override
    public void onEcoSent(String msg) {
        if (msg.startsWith("A")) {
            oneFrag.handleRpm(msg, mSpeed);

        }

        if (msg.startsWith("B")) {
            oneFrag.handleRuntime(msg);

        }

        if (msg.startsWith("C")) {
            oneFrag.handlePedalPosition(msg);

        }

        if (msg.startsWith("D")) {
            oneFrag.handleOutsideTemp(msg);
        }


    }

    //Overrides the Interfaces Method OnSportSent to handle values used by the sport fragment
    @Override
    public void onSportSent(String msg) {

        if (msg.startsWith("A")) {
            twoFrag.handleRpm(msg);
        }

        if (msg.startsWith("E")) {
            mSpeed = Double.parseDouble(msg.substring(1));
            twoFrag.handleSpeed(msg);
        }

        if (msg.startsWith("G")) {
            twoFrag.handleEngineLoad(msg);
        }

        if (msg.startsWith("H")) {
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

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        @NonNull
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
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
                Log.d("Received value:", msg);
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
                showErrorSnackbar(e.toString());
            }
        };
    }

    /**
     * Creation of a bluetooth manager
     */
    private void createBtManager() {
        try {
            mBTManager = new BTManager(this, mBTHandler);
            Log.d("Eco Activity", "Manager created");

        } catch (Exception e) {
            showErrorSnackbar("Couldn't create BT Manager: " + e);
        }
    }


    private void initializeConnection() {
        mBTManager.connect(mBluetoothAddress);
    }

    private void resetValuesOnDisconnect() {
        oneFrag.resetEcoValues();
        twoFrag.resetSportValues();
    }


}