package com.sjjapps.motiondetector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.sjjapps.motiondetector.fragments.BluetoothFragment;
import com.sjjapps.motiondetector.fragments.HomeFragment;
import com.sjjapps.motiondetector.fragments.TransactionInterface;

public class MainActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener,
        TransactionInterface {
    // Constants
    private final static int FRAGMENT_CONTAINER = R.id.fragment_container;

    // Instances
    FragmentManager mFragmentManager = getSupportFragmentManager();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager.addOnBackStackChangedListener(this);
        addFragment(new HomeFragment(), false); // Initial fragment
    }

    @Override
    public void onBackStackChanged() {
        setActionBarNavigateUp();
        setActionBarTitleFromCurrentFragment();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        setActionBarTitleFromCurrentFragment();
    }

    @Override
    public boolean onSupportNavigateUp() {
        removeCurrentFragment();
        return true;
    }

    @Override
    public void addFragment(Fragment fragment, boolean shouldAddToBackStack) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.add(FRAGMENT_CONTAINER, fragment);
        if (shouldAddToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void removeCurrentFragment() {
        mFragmentManager.popBackStack();
    }

    private void setActionBarTitleFromCurrentFragment() {
        Fragment currentFragment = mFragmentManager.findFragmentById(FRAGMENT_CONTAINER);
        String newTitle = "";
        if (currentFragment instanceof HomeFragment) newTitle = "Main Menu";
        else if (currentFragment instanceof BluetoothFragment) newTitle = "Bluetooth";
        setActionBarTitle(newTitle);
    }
}
