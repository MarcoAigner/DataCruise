package com.example.maign.car_it_projekt;

import android.os.Handler;

import java.io.UnsupportedEncodingException;

public abstract class BTMsgHandler extends Handler {

    abstract void receiveMessage(String msg);

    abstract void receiveConnectStatus(boolean isConnected);

    abstract void handleException(Exception e);


    public void handleMessage(android.os.Message msg) {

        if (msg.what == BTManager.MESSAGE_READ) {
            String readMessage;
            try {
                readMessage = new String((byte[]) msg.obj, "UTF-8");
                receiveMessage(readMessage);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                handleException(e);
            }
        }

        if (msg.what == BTManager.CONNECTING_STATUS) {

            if (msg.arg1 == 1)
                //Connected to device
                receiveConnectStatus(true);
            else {
                //Connection failed
                receiveConnectStatus(false);
                handleException(new Exception("Connection Failed"));
            }

        }
    }

}
