package com.shanejansen.portablesecurity.ui.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import butterknife.Bind;
import com.shanejansen.portablesecurity.R;
import com.shanejansen.portablesecurity.ui.common.AppFragmentActivity;
import com.shanejansen.portablesecurity.ui.main.fragments.bluetooth.BluetoothFragment;
import com.shanejansen.portablesecurity.ui.main.fragments.cell.CellFragment;
import com.shanejansen.portablesecurity.ui.main.fragments.wifi.WifiFragment;

public class MainActivity extends AppFragmentActivity
    implements BottomNavigationView.OnNavigationItemSelectedListener {
  private final static int MAIN_CONTAINER = R.id.flFragmentContainer;
  @Bind(R.id.bnNavigation) BottomNavigationView mBnNavigation;
  private int mCurrentMenuItemId;

  @Override protected int getMainFragmentContainerResourceId() {
    return MAIN_CONTAINER;
  }

  @Override protected String getActionBarTitle(Fragment fragment) {
    if (fragment instanceof BluetoothFragment) {
      return getResources().getString(R.string.bluetooth_devices);
    } else if (fragment instanceof WifiFragment) {
      return getResources().getString(R.string.wifi_devices);
    } else if (fragment instanceof CellFragment) {
      return getResources().getString(R.string.cell_devices);
    }
    return getResources().getString(R.string.app_name);
  }

  @Override protected void addInitialFragments() {
    addFragment(new BluetoothFragment(), MAIN_CONTAINER, false);
  }

  @Override protected int getLayoutResourceId() {
    return R.layout.activity_main;
  }

  @Override protected int getToolbarResourceId() {
    return R.id.toolbar;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mCurrentMenuItemId = R.id.action_bluetooth;
    mBnNavigation.setOnNavigationItemSelectedListener(this);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();
    if (mCurrentMenuItemId != itemId) {
      mCurrentMenuItemId = itemId;
      switch (item.getItemId()) {
        case R.id.action_bluetooth:
          addFragment(new BluetoothFragment(), MAIN_CONTAINER, false);
          break;
        case R.id.action_wifi:
          addFragment(new WifiFragment(), MAIN_CONTAINER, false);
          break;
        case R.id.action_cell:
          addFragment(new CellFragment(), MAIN_CONTAINER, false);
          break;
      }
    }
    return true;
  }
}
