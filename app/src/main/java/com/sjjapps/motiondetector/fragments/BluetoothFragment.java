package com.sjjapps.motiondetector.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
public class BluetoothFragment extends Fragment {
    //instances
    private BluetoothConnectionHelper connectionHelper;

    //views
    private Button btnFindDevice;
    private EditText etTimeout;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bluetooth);

        btnFindDevice = (Button)findViewById(R.id.btnFindDevice);
        btnFindDevice.setOnClickListener(this);
        etTimeout = (EditText)findViewById(R.id.etTimeout);

        startBluetoothConnection();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFindDevice:
                connectionHelper.discoverDevices();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothConnectionHelper.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //bluetooth enabled successfully
                Toast.makeText(BluetoothFragment.this, "Ready to discover devices.", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_path_checker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void startBluetoothConnection() {
        Handler bluetoothReceivedDataHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case BluetoothConnectionHelper.HANDLER_READY_TO_CONNECT:
                        //ready to start service with selected device
                        String macAddress = (String)msg.obj; //get mac address
                        Intent i = new Intent(BluetoothFragment.this, BluetoothService.class);
                        i.putExtra("macAddress", macAddress);
                        String timeout = etTimeout.getText().toString();
                        int timeoutInt = 5000;
                        if (timeout.length() != 0) timeoutInt = Integer.parseInt(timeout) * 1000;
                        i.putExtra("timeout", timeoutInt);
                        startService(i);
                        break;
                }
                return true;
            }
        });

        connectionHelper = new BluetoothConnectionHelper(this);
        connectionHelper.setupBluetooth(bluetoothReceivedDataHandler);
    }

    @Override
    protected void onDestroy() {
        connectionHelper.finishedSearching();
        super.onDestroy();
    }*/
}
