package com.example.maign.car_it_projekt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class AppStartActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        setupElements();
        showWelcomeDialog();
    }

    @Override
    protected void onResume(){
        super.onResume();
        showWelcomeDialog();

    }




    private void showWelcomeDialog(){
        AlertDialog.Builder welcomeAlert = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.welcome_dialog, null);
        welcomeAlert.setView(view);
        welcomeAlert.setTitle(R.string.welcomeDiagTitle);
        welcomeAlert.setMessage(R.string.welcomeDiagMsg);
        welcomeAlert.setPositiveButton(R.string.welcomeButtonAccept, createAcceptWelcomeListener());
        welcomeAlert.show();


    }

    private DialogInterface.OnClickListener createAcceptWelcomeListener(){
       DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               switch (which){
                   case BUTTON_POSITIVE:

                       if(bluetoothAdapter.isEnabled()){
                           Intent intent = new Intent(AppStartActivity.this, MenuActivity.class);
                           startActivity(intent);

                       }else{
                           Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                           startActivityForResult(turnBTon, 1);
                           Intent intent = new Intent(AppStartActivity.this, MenuActivity.class);
                           startActivity(intent);
                       }
               }



           }
       };

        return listener;
    }

    private void setupElements(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

}
