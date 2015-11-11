package com.sjjapps.motiondetector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.sjjapps.motiondetector.fragments.HomeFragment;
import com.sjjapps.motiondetector.fragments.TransactionInterface;

public class MainActivity extends BaseActivity implements TransactionInterface {
    private final static int FRAGMENT_CONTAINER = R.id.fragment_container;

    FragmentManager mFragmentManager = getSupportFragmentManager();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment initialFragment = new HomeFragment();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(FRAGMENT_CONTAINER, initialFragment);
        transaction.commit();
        setActionBarTitle("Main Menu");
    }

    @Override
    public void replaceFragment(Fragment replacement) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(FRAGMENT_CONTAINER, replacement);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
