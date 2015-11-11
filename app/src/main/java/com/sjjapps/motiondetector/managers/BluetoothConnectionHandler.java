package com.sjjapps.motiondetector.managers;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.sjjapps.motiondetector.utils.BluetoothConnectionHelper;

/**
 * Created by Shane Jansen on 11/11/15.
 */
public class BluetoothConnectionHandler extends Handler {
    private Activity mActivity;

    public BluetoothConnectionHandler(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case BluetoothConnectionHelper.HANDLER_READY_TO_CONNECT:
                // Ready to start service with selected device
                String macAddress = (String) msg.obj; // Get mac address
                Intent i = new Intent(mActivity, BluetoothService.class);
                i.putExtra("macAddress", macAddress);
                //String timeout = etTimeout.getText().toString();
                //int timeoutInt = 5000;
                //if (timeout.length() != 0) timeoutInt = Integer.parseInt(timeout) * 1000;
                //i.putExtra("timeout", mTimeout);
                mActivity.startService(i);
                break;
        }
    }
}