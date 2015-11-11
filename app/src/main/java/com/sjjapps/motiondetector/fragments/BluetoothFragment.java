package com.sjjapps.motiondetector.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sjjapps.motiondetector.R;
import com.sjjapps.motiondetector.utils.BluetoothConnectionHelper;
import com.sjjapps.motiondetector.managers.BluetoothService;

/**
 * Created by Shane Jansen on 4/7/15.
 *
 * Activity for PathChecker
 */
public class BluetoothFragment extends Fragment implements View.OnClickListener {
    // Instances
    private TransactionInterface mTransactionInterface;
    private BluetoothConnectionHelper mConnectionHelper;

    // Views
    private EditText etTimeout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mTransactionInterface = (TransactionInterface) getActivity();
        }
        catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement TransactionInterface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        // Finding views
        Button btnFindDevice = (Button) v.findViewById(R.id.btnFindDevice);
        btnFindDevice.setOnClickListener(this);
        etTimeout = (EditText) v.findViewById(R.id.etTimeout);

        mConnectionHelper = new BluetoothConnectionHelper(getActivity());
        startBluetoothConnection();

        return v;
    }

    @Override
    public void onPause() {
        mConnectionHelper.finishedSearching();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFindDevice:
                mConnectionHelper.discoverDevices();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothConnectionHelper.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth enabled successfully
                Toast.makeText(getActivity(), "Ready to discover devices.", Toast.LENGTH_SHORT).show();
            }
            else {
                // Bluetooth could not be enabled
                mTransactionInterface.removeCurrentFragment();
            }
        }
    }

    private void startBluetoothConnection() {
        Handler bluetoothReceivedDataHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case BluetoothConnectionHelper.HANDLER_READY_TO_CONNECT:
                        // Ready to start service with selected device
                        String macAddress = (String) msg.obj; // Get mac address
                        Intent i = new Intent(getActivity(), BluetoothService.class);
                        i.putExtra("macAddress", macAddress);
                        String timeout = etTimeout.getText().toString();
                        int timeoutInt = 5000;
                        if (timeout.length() != 0) timeoutInt = Integer.parseInt(timeout) * 1000;
                        i.putExtra("timeout", timeoutInt);
                        getActivity().startService(i);
                        break;
                }
                return true;
            }
        });
        mConnectionHelper.setupBluetooth(bluetoothReceivedDataHandler);
    }
}
