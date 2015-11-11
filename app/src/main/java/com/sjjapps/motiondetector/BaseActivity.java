package com.sjjapps.motiondetector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
            setActionBarNavigateUp();
        }
    }

    /**
     * Checks if the back arrow in the action bar should
     * be displayed and enables it if is should be.
     */
    protected void setActionBarNavigateUp() {
        if (toolbar != null) {
            int numBack = getSupportFragmentManager().getBackStackEntryCount();
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (numBack != 0 || upIntent != null) {
                assert getSupportActionBar() != null;
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            else {
                assert getSupportActionBar() != null;
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
