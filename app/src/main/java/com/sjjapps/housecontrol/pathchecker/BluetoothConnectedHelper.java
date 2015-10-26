package com.sjjapps.housecontrol.pathchecker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Shane Jansen on 4/7/15.
 *
 * This class is used after the connection's mac
 * address has been retrieved. The connect is made
 * and handled through this class.
 */
public class BluetoothConnectedHelper {
    //constants
    public static final int HANDLER_RECEIVED_DATA = 1;
    public static final int HANDLER_CONNECTION_LOST = 2;

    //instances
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private Handler receiveDataHandler;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    //receivers
    private BroadcastReceiver deviceStateReceiver;

    //data
    private Context context;
    private boolean isConnected = false;

    public BluetoothConnectedHelper(Context context, String macAddress, Handler receiveDataHandler) {
        this.context = context;
        this.receiveDataHandler = receiveDataHandler;

        //create bluetooth device from macAddress
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);

        //start connection process
        connectThread = new ConnectThread();
        new Thread(connectThread).start();
    }

    /**
     * Thread to establish connection to bluetooth device
     * after the bluetoothDevice has been selected.
     */
    private class ConnectThread implements Runnable {
        public void run() {
            //Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                //UUID is the app's UUID string, also used by the server code
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")); //standard UUID
            } catch (IOException e) { }

            try {
                //Connect the device through the socket. This will block until it succeeds or throws an exception
                bluetoothSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    bluetoothSocket.close();
                    //needed because we cannot run on UI thread
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Could not connect to device.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException closeException) { }
                return;
            }

            //do work to manage the connection (in a separate thread)
            isConnected = true;
            //needed because we cannot run on UI thread
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Connected to Path Checker.", Toast.LENGTH_SHORT).show();
                    //receiveDataHandler.obtainMessage(HANDLER_CONNECTION_SUCCESS).sendToTarget();
                }
            });
            connectedThread = new ConnectedThread();
            new Thread(connectedThread).start();
        }

        public void close() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) { }
        }
    }

    /**
     * Called after the device has been successfully connected.
     * Allows receiving and sending data using connected bluetooth
     * device.
     */
    private class ConnectedThread implements Runnable {
        private InputStream inputStream;
        private OutputStream outputStream;

        @Override
        public void run() {
            //register receiver for state changes
            deviceStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) { //bluetooth disconnected
                        Toast.makeText(context, "Disconnected from Path Checker.", Toast.LENGTH_SHORT).show();
                        receiveDataHandler.obtainMessage(HANDLER_CONNECTION_LOST).sendToTarget();
                    }
                }
            };
            IntentFilter stateFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            context.registerReceiver(deviceStateReceiver, stateFilter);

            try {
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) { }

            byte[] buffer = new byte[1024]; //buffer store for the stream
            int bytes; //bytes returned from read()

            //keep listening to the InputStream until an exception occurs
            while(true) {
                try {
                    //read from the InputStream
                    bytes = inputStream.read(buffer);
                    //send the obtained bytes to the UI activity
                    receiveDataHandler.obtainMessage(HANDLER_RECEIVED_DATA, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) { }
        }
    }

    /**
     * Returns true if there is a current connection;
     * False otherwise
     * @return
     */
    public boolean isConnected() { return isConnected; }

    /**
     * Closes the bluetooth connection
     */
    public void closeConnection() {
        if (isConnected) {
            connectThread.close();
            if (deviceStateReceiver != null)
                context.unregisterReceiver(deviceStateReceiver);
        }
    }

    /**
     * If connected, sends string data to
     * bluetooth device
     * @param s
     */
    public void sendData(String s) {
        if (isConnected)
            connectedThread.write(s.getBytes());
    }

}
