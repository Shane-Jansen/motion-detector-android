package com.sjjapps.housecontrol;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

/**
 * Created by Shane Jansen on 8/29/14.
 */
public class MyApplication extends Application {
	private final static String TAG = "app";

	@Override
	public void onCreate() {
		super.onCreate();

		//init parse
		Parse.initialize(this, "dESyUZeAf30WPlqY2dVveMkZ3ZHRFfRpmmlYORUL", "hUvYsXLdWYpV97C9ofhu84v7NT1B1WvjfYWrrL43");
		ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				String parseId = ParseInstallation.getCurrentInstallation().getObjectId();
				Log.d(TAG, "Parse ID: " + parseId);
			}
		});
	}

}
