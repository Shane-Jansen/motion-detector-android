package com.sjjapps.motiondetector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sjjapps.motiondetector.R;
import com.sjjapps.motiondetector.activity.bluetooth.BluetoothActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    //views
    private Button btnPathChecker, btnSpotlightToggle, btnAirConditioner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPathChecker = (Button)findViewById(R.id.bthPathChecker);
        btnPathChecker.setOnClickListener(this);
        btnSpotlightToggle = (Button)findViewById(R.id.btnSpotlightToggle);
        btnSpotlightToggle.setOnClickListener(this);
        btnAirConditioner = (Button)findViewById(R.id.btnAirConditioner);
        btnAirConditioner.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bthPathChecker:
                Intent i = new Intent(this, BluetoothActivity.class);
                startActivity(i);
                break;
            case R.id.btnSpotlightToggle:
                Toast.makeText(this, "Not yet implemented.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnAirConditioner:
                Toast.makeText(this, "Not yet implemented.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
