package com.sjjapps.motiondetector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Shane Jansen on 11/9/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Toolbar toolbar;

    protected abstract int getLayoutResource();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // Check if the back arrow should be displayed
            int numBack = getSupportFragmentManager().getBackStackEntryCount();
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (numBack != 0 || upIntent != null) {
                assert getSupportActionBar() != null;
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    protected void setActionBarTitle(String title) {
        if (toolbar != null) {
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle(title);
        }
    }
}
