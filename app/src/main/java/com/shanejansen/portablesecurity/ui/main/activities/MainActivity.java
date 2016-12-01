package com.shanejansen.portablesecurity.ui.main.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.shanejansen.mvpandroid.activities.FragmentActivity;
import com.shanejansen.portablesecurity.R;

public class MainActivity extends FragmentActivity {
  @Override protected int getMainFragmentContainerResourceId() {
    return R.id.fragment_container;
  }

  @Override protected String getActionBarTitle(Fragment fragment) {
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
  }
}
