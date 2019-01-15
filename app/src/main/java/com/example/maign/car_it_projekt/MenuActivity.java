package com.example.maign.car_it_projekt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MenuActivity extends AppCompatActivity {

    //Xml elements
    private ListView mListView;
    private SwitchMaterial mSwitch;
    private TextInputEditText mTextInputEditText;
    private MaterialButton mConnectButton;
    private MaterialButton mReloadButton;


    //Listener variables
    private View.OnClickListener mSwitchToTabsListener;
    private CompoundButton.OnCheckedChangeListener mSwitchOnChangeListener;

    //Bluetooth related variables
    private BTManager mBTManager;
    private BTMsgHandler mBTHandler;
    private BluetoothAdapter bluetoothAdapter;
    private String mBtAddress;
    private String mDeviceName;


    private ArrayAdapter mAdapter;

    /*Two array lists in which the devices lists are saved into
      The second list only saves devices that start with 'H' followed by a 'C'*/
    private ArrayList<String> mAllDevicesList;
    private ArrayList<String> mArduinoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menue);

        //Create a Bluetooth Manager
        createBtManager(this, mBTHandler);

        //Setup all Layout elements
        setupElements();

        //Show the welcome dialog
        showWelcomeDialog();

        //Get lists of connected devices
        pairedDevicesLists();


    }

    /**
     * Setting up all the elements.
     * Linking xml elements to their java counterparts
     * Setting connect button to not be enabled on startup
     * Setting various listeners through methods
     * Getting the bluetooth adapter
     */
    private void setupElements() {
        mListView = findViewById(R.id.listView);
        mTextInputEditText = findViewById(R.id.textInput);
        mSwitch = findViewById(R.id.switchHC);
        mConnectButton = findViewById(R.id.materialButton);
        mReloadButton = findViewById(R.id.reloadButton);

        mAllDevicesList = new ArrayList<>();
        mArduinoList = new ArrayList<>();

        mConnectButton.setEnabled(false);

        mReloadButton.setOnClickListener(setReloadListener());
        mConnectButton.setOnClickListener(setSwitchToTabsListener());
        mSwitch.setOnCheckedChangeListener(setSwitchOnChangeListener());

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    }


    /**
     * Show a welcome dialog which in a funny way encourages the user to not risk
     * his or her life and so not control the phone while driving
     */
    private void showWelcomeDialog() {
        AlertDialog.Builder welcomeAlert = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.welcome_dialog, null);
        welcomeAlert.setView(view);
        welcomeAlert.setTitle(R.string.welcomeDiagTitle);
        welcomeAlert.setMessage(R.string.welcomeDiagMsg);
        welcomeAlert.setPositiveButton(R.string.welcomeButtonAccept, createAcceptWelcomeListener());
        welcomeAlert.show();


    }


    /**
     * Method creating a bluetooth manager instance
     */
    private void createBtManager(Activity activity, BTMsgHandler handler) {
        try {
            mBTManager = new BTManager(activity, handler);
            Log.d("Menu Activity", "Manager created");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private DialogInterface.OnClickListener createAcceptWelcomeListener() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case BUTTON_POSITIVE:

                        if (!bluetoothAdapter.isEnabled()) {
                            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(turnBTon, 1);
                        }
                }


            }
        };

        return listener;
    }


    /**
     * Method to get two lists of paired bluetooth devices
     * The first includes all paired devices
     * The second only includes ones which start with 'HC' to increase comfort
     */
    private void pairedDevicesLists() {

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth is currently off!", Toast.LENGTH_LONG).show();
            return;
        }

        if (mAllDevicesList.isEmpty()) {
            mAllDevicesList = mBTManager.getPairedDeviceInfos();
        }
        if (mArduinoList.isEmpty()) {
            extractArduinoDevices(mAllDevicesList, mArduinoList);
        }


        try {
            Log.d("Menu Activity", "mAllDevicesList (0)" + mAllDevicesList);
            Log.d("Menu Activity", "mArduinoList (0)" + mArduinoList.get(0));
        } catch (Exception e) {
            Log.d("Menu Activity", "Devices List log failed");
        }

        if (mAllDevicesList.size() < 0) {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        } else {


            mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mArduinoList);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
        }
    }

    /**
     * Method to extract all 'HC*' Arduino devices from the greater allDevices list
     * Put this code into a separate method to not having to rerun it again on its parent method's rerun
     *
     * @param extractFromList
     * @param extractIntoList
     */
    private void extractArduinoDevices(ArrayList<String> extractFromList, ArrayList<String> extractIntoList) {
        for (String string : extractFromList) {

            Character firstChar = string.charAt(0);
            Character secondChar = string.charAt(1);

            if (firstChar.equals('H') && secondChar.equals('C')) {
                extractIntoList.add(string);
            }


        }
    }


    /**
     * Method that returns the onClick behaviour for the reload button
     * In this case, pairedDevices() is rerun
     *
     * @return reloadListener
     */
    private MaterialButton.OnClickListener setReloadListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesLists();
            }
        };
        return listener;
    }

    /**
     * Method that returns onClick behaviour for the connect button.
     * In this case, an intent to the mainActivity is created and the bluetoothAddress is stored into it
     * @return
     */
    private View.OnClickListener setSwitchToTabsListener() {
        mSwitchToTabsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("BT_ADDRESS", mBtAddress);
                startActivity(intent);
            }
        };

        return mSwitchToTabsListener;
    }


    /**
     * Method that returns the onChecked behaviour for the switch
     * If activated it fills the list view with the allDevices list
     * In reverse, only the 'HC' devices are used
     * @return
     */
    private CompoundButton.OnCheckedChangeListener setSwitchOnChangeListener() {
        mSwitchOnChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == false) {
                    mAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mArduinoList);
                    mListView.setAdapter(mAdapter);
                    mListView.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

                } else if (isChecked == true) {
                    mAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mAllDevicesList);
                    mListView.setAdapter(mAdapter);
                    mListView.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
                }
            }
        };

        return mSwitchOnChangeListener;
    }


    /**
     * Method that returns an OnItemClick Listener for the ListView
     * If an Item is selected, the bluetooth address and device name get stored into global variables
     * The user gets informed about them in a seperate text view
     * Finally the connect button becomes clickable
     */
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            //final String address = info.substring(info.length() - 17);
            mBtAddress = info.substring(info.length() - 17);
            mDeviceName = info.substring(0, info.length() - 17);

            Log.d("Menue Activity:", "Bt Address:" + mBtAddress);
            Log.d("Menue Activity:", "Device Name:" + mDeviceName);


            try {
                mTextInputEditText.setText(mDeviceName + "\n" + mBtAddress);
                mConnectButton.setEnabled(true);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Oops! Something went wrong...", Toast.LENGTH_SHORT).show();
            }

        }
    };


}
