package com.example.maign.car_it_projekt;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class WelcomeDialog extends DialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TypefaceUtil.overrideFont(getActivity(), "SERIF", "fonts/abel_regular.ttf");
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Get Layout Inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();


        

        builder.setCancelable(false);
        builder.setView(inflater.inflate(R.layout.welcome_dialog, null))
                .setPositiveButton(R.string.welcomeButtonAccept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
