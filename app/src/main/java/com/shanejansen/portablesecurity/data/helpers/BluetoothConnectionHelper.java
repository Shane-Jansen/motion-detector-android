package com.shanejansen.portablesecurity.data.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

/**
 * Created by Shane Jansen on 4/7/15.
 *
 * This class is responsible for enabling bluetooth,
 * discovering and showing a list of available devices,
 * and returning the mac address of the selected device.
 */
public class BluetoothConnectionHelper {
  // Constants
  public static final int HANDLER_READY_TO_CONNECT = 1;

  // Instances
  private Handler mConnectionHandler;
  private BluetoothAdapter mBluetoothAdapter;
  private Dialog mDevicesDialog;
  private Activity mContext;
  private boolean mIsSearching;

  // Receivers
  private BroadcastReceiver mStartedSearchReceiver;
  private BroadcastReceiver mFinishedSearchReceiver;
  private BroadcastReceiver mDeviceFoundReceiver;

  public BluetoothConnectionHelper(Activity context, Handler connectionHandler,
      BluetoothAdapter bluetoothAdapter) {
    this.mContext = context;
    this.mConnectionHandler = connectionHandler;
    this.mBluetoothAdapter = bluetoothAdapter;
  }

  /**
   * Searches for and displays a dialog list
   * of available bluetooth devices. Sends handler
   * with mac address when a device is selected from
   * the list.
   */
  public void discoverDevices() {
    // Prepare list dialog to display found devices
    mIsSearching = true;
    final AlertDialog.Builder deviceAlertBuilder =
        new AlertDialog.Builder(mContext).setTitle("Searching...")
            .setCancelable(false)
            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                finishedSearching();
              }
            });
    final ListView devicesList = new ListView(mContext);
    final ArrayList<BluetoothDevice> foundBluetoothDevices = new ArrayList<>();
    final ArrayList<String> foundBluetoothNames = new ArrayList<>();
    final ArrayAdapter<String> deviceAdapter =
        new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1,
            foundBluetoothNames);
    devicesList.setAdapter(deviceAdapter);
    deviceAlertBuilder.setView(devicesList);
    mDevicesDialog = deviceAlertBuilder.create();
    devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String macAddress = foundBluetoothDevices.get(position).getAddress();
        mConnectionHandler.obtainMessage(HANDLER_READY_TO_CONNECT, macAddress).sendToTarget();
        finishedSearching();
      }
    });
    mDevicesDialog.show();

    mBluetoothAdapter.startDiscovery(); // Start searching and broadcasts: ACTION_DISCOVERY_STARTED, ACTION_DISCOVERY_FINISHED, ACTION_FOUND

    // Create and register broadcast receivers
    mStartedSearchReceiver = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
      }
    };
    mFinishedSearchReceiver = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
        mDevicesDialog.setTitle("Finished Searching");
      }
    };
    mDeviceFoundReceiver = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        foundBluetoothDevices.add(device);
        foundBluetoothNames.add(device.getName());
        deviceAdapter.notifyDataSetChanged();
      }
    };
    mContext.registerReceiver(mStartedSearchReceiver,
        new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
    mContext.registerReceiver(mFinishedSearchReceiver,
        new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    mContext.registerReceiver(mDeviceFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
  }

  /**
   * Should be called when finished/canceled searching
   * for and selecting a device.
   */
  public void finishedSearching() {
    if (mIsSearching) {
      mBluetoothAdapter.cancelDiscovery();
      if (mStartedSearchReceiver != null) mContext.unregisterReceiver(mStartedSearchReceiver);
      if (mFinishedSearchReceiver != null) mContext.unregisterReceiver(mFinishedSearchReceiver);
      if (mDeviceFoundReceiver != null) mContext.unregisterReceiver(mDeviceFoundReceiver);
      mDevicesDialog.dismiss();
      mIsSearching = false;
    }
  }
}
