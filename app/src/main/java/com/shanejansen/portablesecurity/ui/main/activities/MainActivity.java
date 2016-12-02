package com.shanejansen.portablesecurity.ui.main.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import butterknife.Bind;
import com.shanejansen.portablesecurity.R;
import com.shanejansen.portablesecurity.ui.common.AppFragmentActivity;
import com.shanejansen.portablesecurity.ui.main.fragments.bluetooth.BluetoothFragment;
import com.shanejansen.portablesecurity.ui.main.fragments.settings.SettingsFragment;
import com.shanejansen.portablesecurity.ui.main.fragments.wifi.WifiFragment;

public class MainActivity extends AppFragmentActivity
    implements BottomNavigationView.OnNavigationItemSelectedListener {
  private final static int MAIN_CONTAINER = R.id.flFragmentContainer;

  @Bind(R.id.bnNavigation) BottomNavigationView mBnNavigation;

  @Override protected int getMainFragmentContainerResourceId() {
    return MAIN_CONTAINER;
  }

  @Override protected String getActionBarTitle(Fragment fragment) {
    if (fragment instanceof BluetoothFragment) {
      return getResources().getString(R.string.bluetooth_devices);
    } else if (fragment instanceof WifiFragment) {
      return getResources().getString(R.string.wifi_devices);
    } else if (fragment instanceof SettingsFragment) {
      return getResources().getString(R.string.settings);
    }
    return getResources().getString(R.string.app_name);
  }

  @Override protected int getLayoutResourceId() {
    return R.layout.activity_main;
  }

  @Override protected int getToolbarResourceId() {
    return R.id.toolbar;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBnNavigation.setOnNavigationItemSelectedListener(this);
  }

  @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_bluetooth:
        addFragment(createOrRetrieveFragment(BluetoothFragment.class), MAIN_CONTAINER, false);
        break;
      case R.id.action_wifi:
        addFragment(createOrRetrieveFragment(WifiFragment.class), MAIN_CONTAINER, true);
        break;
      case R.id.action_settings:
        addFragment(createOrRetrieveFragment(SettingsFragment.class), MAIN_CONTAINER, false);
        break;
    }
    return true;
  }
}
