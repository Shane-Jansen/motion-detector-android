package com.sjjapps.motiondetector.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by shane on 11/10/15.
 */
public interface TransactionInterface {
    void addFragment(Fragment fragment, boolean shouldAddToBackStack);
    void removeCurrentFragment();
}
