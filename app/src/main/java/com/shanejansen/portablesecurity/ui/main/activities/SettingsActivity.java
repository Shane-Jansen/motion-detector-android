package com.shanejansen.portablesecurity.ui.main.activities;

import android.support.v4.app.Fragment;
import com.shanejansen.portablesecurity.R;
import com.shanejansen.portablesecurity.ui.common.AppFragmentActivity;
import com.shanejansen.portablesecurity.ui.main.fragments.settings.SettingsFragment;

/**
 * Created by Shane Jansen on 12/19/16.
 */
public class SettingsActivity extends AppFragmentActivity {
  private final static int MAIN_CONTAINER = R.id.flFragmentContainer;

  @Override protected int getMainFragmentContainerResourceId() {
    return MAIN_CONTAINER;
  }

  @Override protected String getActionBarTitle(Fragment fragment) {
    return getResources().getString(R.string.settings);
  }

  @Override protected void addInitialFragments() {
    addFragment(new SettingsFragment(), MAIN_CONTAINER, false);
  }

  @Override protected int getLayoutResourceId() {
    return R.layout.activity_settings;
  }

  @Override protected int getToolbarResourceId() {
    return R.id.toolbar;
  }
}
