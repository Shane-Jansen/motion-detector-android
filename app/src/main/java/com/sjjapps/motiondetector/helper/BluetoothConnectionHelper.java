package com.sjjapps.motiondetector.helper;

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
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Shane Jansen on 4/7/15.
 *
 * This class is responsible for enabling bluetooth,
 * discovering and showing a list of available devices,
 * and returning the mac address of the selected device.
 */
public class BluetoothConnectionHelper {
    //constants
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int HANDLER_READY_TO_CONNECT = 1;

    //instances
    private Handler connectionHandler;
    private BluetoothAdapter bluetoothAdapter;
    private Dialog devicesDialog;

    //data
    private Activity aContext;

    //receivers
    private BroadcastReceiver startedSearchReceiver;
    private BroadcastReceiver finishedSearchReceiver;
    private BroadcastReceiver deviceFoundReceiver;

    public BluetoothConnectionHelper(Activity aContext) {
        this.aContext = aContext;
    }

    /**
     * Check if bluetooth is available for this device.
     * Check if bluetooth is turned on for this device
     * and requests to turn it on if not.
     */
    public void setupBluetooth(Handler connectionHandler) {
        this.connectionHandler = connectionHandler;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            //device does not support bluetooth; exit activity
            new AlertDialog.Builder(aContext)
                    .setTitle("Error")
                    .setMessage("Your device does not support bluetooth.")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aContext.finish();
                        }
                    })
                    .show();
        }
        else {
            if(!bluetoothAdapter.isEnabled()) {
                //bluetooth is not enabled; request to enable it
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                aContext.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                Toast.makeText(aContext, "Ready to discover devices.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Searches for and displays a dialog list
     * of available bluetooth devices. Sends handler
     * with mac address when a device is selected from
     * the list.
     */
    public void discoverDevices() {
        //prepare list dialog to display found devices
        final AlertDialog.Builder deviceAlertBuilder = new AlertDialog.Builder(aContext).setTitle("Searching...").setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bluetoothAdapter.cancelDiscovery();
                        devicesDialog.dismiss();
                    }
                });
        final ListView devicesList = new ListView(aContext);
        final ArrayList<BluetoothDevice> foundBluetoothDevices = new ArrayList<>();
        final ArrayList<String> foundBluetoothNames = new ArrayList<>();
        final ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(aContext, android.R.layout.simple_list_item_1, android.R.id.text1, foundBluetoothNames);
        devicesList.setAdapter(deviceAdapter);
        deviceAlertBuilder.setView(devicesList);
        devicesDialog = deviceAlertBuilder.create();
        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                devicesDialog.dismiss();
                String macAddress = foundBluetoothDevices.get(position).getAddress();
                bluetoothAdapter.cancelDiscovery();
                connectionHandler.obtainMessage(HANDLER_READY_TO_CONNECT, macAddress).sendToTarget();
            }
        });
        devicesDialog.show();

        bluetoothAdapter.startDiscovery(); //start searching and broadcasts: ACTION_DISCOVERY_STARTED, ACTION_DISCOVERY_FINISHED, ACTION_FOUND

        //create and register broadcast receivers
        startedSearchReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {}
        };
        finishedSearchReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                devicesDialog.setTitle("Finished Searching");
            }
        };
        deviceFoundReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                foundBluetoothDevices.add(device);
                foundBluetoothNames.add(device.getName());
                deviceAdapter.notifyDataSetChanged();
            }
        };
        aContext.registerReceiver(startedSearchReceiver,  new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        aContext.registerReceiver(finishedSearchReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        aContext.registerReceiver(deviceFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }


    /**
     * Should be called when finished searching
     * for and selecting a device.
     */
    public void finishedSearching() {
        if (startedSearchReceiver != null)
            aContext.unregisterReceiver(startedSearchReceiver);
        if (finishedSearchReceiver != null)
            aContext.unregisterReceiver(finishedSearchReceiver);
        if (deviceFoundReceiver != null)
            aContext.unregisterReceiver(deviceFoundReceiver);
    }

}
