package com.shanejansen.portablesecurity.data.helpers;

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
  // Constants
  public static final int HANDLER_RECEIVED_DATA = 1;
  public static final int HANDLER_CONNECTION_LOST = 2;

  // Instances
  private BluetoothDevice mBluetoothDevice;
  private BluetoothSocket mBluetoothSocket;
  private Handler mReceiveDataHandler;
  private ConnectThread mConnectThread;
  private ConnectedThread mConnectedThread;
  private BroadcastReceiver mDeviceStateReceiver;
  private Context mContext;
  private boolean mIsConnected = false;

  public BluetoothConnectedHelper(Context context, String macAddress, Handler receiveDataHandler) {
    this.mContext = context;
    this.mReceiveDataHandler = receiveDataHandler;

    // Create bluetooth device from macAddress
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    mBluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);

    // Start connection process
    mConnectThread = new ConnectThread();
    new Thread(mConnectThread).start();
  }

  /**
   * Returns true if there is a current connection;
   * False otherwise
   */
  public boolean isConnected() {
    return mIsConnected;
  }

  /**
   * Closes the bluetooth connection
   */
  public void closeConnection() {
    if (mIsConnected) {
      mConnectThread.close();
      if (mDeviceStateReceiver != null) mContext.unregisterReceiver(mDeviceStateReceiver);
    }
  }

  /**
   * If connected, sends string data to
   * bluetooth device
   */
  public void sendData(String s) {
    if (mIsConnected) mConnectedThread.write(s.getBytes());
  }

  /**
   * Thread to establish connection to bluetooth device
   * after the mBluetoothDevice has been selected.
   */
  private class ConnectThread implements Runnable {
    public void run() {
      // Get a BluetoothSocket to connect with the given BluetoothDevice
      try {
        // UUID is the app's UUID string, also used by the server code
        mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")); // Standard UUID
      } catch (IOException e) {
        e.printStackTrace();
      }

      try {
        // Connect the device through the socket. This will block until it succeeds or throws an exception
        mBluetoothSocket.connect();
      } catch (IOException connectException) {
        // Unable to connect. Close the socket
        try {
          mBluetoothSocket.close();
          // Needed because we cannot run on UI thread
          Handler handler = new Handler(Looper.getMainLooper());
          handler.post(new Runnable() {
            @Override public void run() {
              Toast.makeText(mContext, "Could not connect to device.", Toast.LENGTH_SHORT).show();
            }
          });
        } catch (IOException e) {
          e.printStackTrace();
        }
        return;
      }

      // Do work to manage the connection (in a separate thread)
      mIsConnected = true;
      // Needed because we cannot run on UI thread
      Handler handler = new Handler(Looper.getMainLooper());
      handler.post(new Runnable() {
        @Override public void run() {
          Toast.makeText(mContext, "Connected to Path Checker.", Toast.LENGTH_SHORT).show();
          //mReceiveDataHandler.obtainMessage(HANDLER_CONNECTION_SUCCESS).sendToTarget();
        }
      });
      mConnectedThread = new ConnectedThread();
      new Thread(mConnectedThread).start();
    }

    public void close() {
      try {
        mBluetoothSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Called after the device has been successfully connected.
   * Allows receiving and sending data using connected bluetooth
   * device.
   */
  private class ConnectedThread implements Runnable {
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    @Override public void run() {
      // Register receiver for state changes
      mDeviceStateReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
          if (intent.getAction()
              .equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) { // Bluetooth disconnected
            Toast.makeText(context, "Disconnected from Motion Detector.", Toast.LENGTH_SHORT)
                .show();
            mReceiveDataHandler.obtainMessage(HANDLER_CONNECTION_LOST).sendToTarget();
          }
        }
      };
      IntentFilter stateFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
      mContext.registerReceiver(mDeviceStateReceiver, stateFilter);

      try {
        mInputStream = mBluetoothSocket.getInputStream();
        mOutputStream = mBluetoothSocket.getOutputStream();
      } catch (IOException e) {
        e.printStackTrace();
      }

      byte[] buffer = new byte[1024]; // Buffer store for the stream
      int bytes; // Bytes returned from read()

      // Keep listening to the InputStream until an exception occurs
      while (true) {
        try {
          // Read from the InputStream
          bytes = mInputStream.read(buffer);
          // Send the obtained bytes to the UI activity
          mReceiveDataHandler.obtainMessage(HANDLER_RECEIVED_DATA, bytes, -1, buffer)
              .sendToTarget();
        } catch (IOException e) {
          e.printStackTrace();
          break;
        }
      }
    }

    /* Call this to send data to the remote device */
    public void write(byte[] bytes) {
      try {
        mOutputStream.write(bytes);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
