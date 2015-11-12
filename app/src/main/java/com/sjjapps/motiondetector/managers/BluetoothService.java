package com.sjjapps.motiondetector.managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.sjjapps.motiondetector.MainActivity;
import com.sjjapps.motiondetector.R;
import com.sjjapps.motiondetector.utils.BluetoothConnectedHelper;

/**
 * Created by Shane Jansen on 4/7/15.
 *
 * Handles the bluetooth connection and notifications.
 */
public class BluetoothService extends Service {
    // Constants
    private static final String ACTION_WAIT_PRESSED = "com.sjjapps.BluetoothService.ACTION_WAIT_PRESSED";
    private static final String ACTION_STOP_PRESSED = "com.sjjapps.BluetoothService.ACTION_STOP_PRESSED";
    private static final String ACTION_MOTION_DETECTED_PRESSED = "com.sjjapps.BluetoothService.ACTION_MOTION_DETECTED_PRESSED";
    private static final String ACTION_DISCONNECTED_CONFIRMED_PRESSED = "com.sjjapps.BluetoothService.ACTION_DISCONNECTED_CONFIRMED_PRESSED";
    private static final int NOTIFICATION_ID = 1;
    private static final int SERVICE_ID = 1;

    // Instances
    public BluetoothConnectedHelper mConnectedHelper;
    private MediaPlayer mMotionDetectedPlayer, mDisconnectedPlayer;
    private NotificationManager mNotificationManager;
    private BroadcastReceiver mWaitReceiver, mStopReceiver, mMotionDetectedReceiver, mDisconnectedReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Initialize media player
        mMotionDetectedPlayer = MediaPlayer.create(this, R.raw.alert);
        mMotionDetectedPlayer.setLooping(true);
        mDisconnectedPlayer = MediaPlayer.create(this, R.raw.alert_disconnected);
        mDisconnectedPlayer.setLooping(true);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String macAddress = intent.getStringExtra("macAddress");
        startBluetoothConnection(macAddress);
        showOngoingNotification();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Starts the bluetooth connection and sets
     * appropriate handlers for handling received data.
     * @param macAddress
     */
    private void startBluetoothConnection(String macAddress) {
        Handler receiveDataHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case BluetoothConnectedHelper.HANDLER_RECEIVED_DATA:
                        // Data was received from bluetooth
                        // Wake the screen
                        PowerManager pm = (PowerManager)BluetoothService.this.getSystemService(Context.POWER_SERVICE);
                        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyLock");
                        wl.acquire();
                        // Get the received string
                        byte[] receivedBytes = (byte[]) msg.obj;
                        String received = new String(receivedBytes, 0, msg.arg1);
                        // Check what was received
                        if (received.equals("m")) { // Motion detected
                            // Send back a "1" to indicate that the data was received successfully
                            mConnectedHelper.sendData("m");
                            // Set volume to max and play sound
                            AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                            mMotionDetectedPlayer.start();
                            // Display new notification to replace current one
                            showMotionDetectedNotification();
                        }
                        else if (received.equals("w")) { // Wait command received
                            //Toast.makeText(BluetoothService.this, "W received.", Toast.LENGTH_SHORT).show();
                            // Show countdown notification
                            new CountDownTimer(60000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    showCountdownNotification((int)(millisUntilFinished / 1000));
                                }

                                @Override
                                public void onFinish() {
                                    showOngoingNotification();
                                }
                            }.start();
                        }
                        break;
                    case BluetoothConnectedHelper.HANDLER_CONNECTION_LOST:
                        // Play disconnect sound
                        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                        mDisconnectedPlayer.start();
                        // Display new notification to replace current one
                        showDisconnectedNotification();
                        break;
                }

                return true;
            }
        });

        // Start the bluetooth connection
        mConnectedHelper = new BluetoothConnectedHelper(this, macAddress, receiveDataHandler);
    }

    /**
     * Shows the active bluetooth connection as a
     * persistent notification. Includes buttons for
     * waiting and stopping connection.
     */
    private void showOngoingNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                        .setContentTitle("House Control")
                        .setContentText("Path Checker - Bluetooth Connection Established");
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);

        // Set and register broadcast receivers for clicking notification buttons
        mWaitReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Send "w". Should receive "w" to confirm and start countdown
                mConnectedHelper.sendData("w");
            }
        };
        mStopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Remove notification and stop service
                mNotificationManager.cancel(NOTIFICATION_ID);
                bluetoothFinished();
            }
        };
        registerReceiver(mWaitReceiver, new IntentFilter(ACTION_WAIT_PRESSED));
        registerReceiver(mStopReceiver, new IntentFilter(ACTION_STOP_PRESSED));

        // Create intents for pressing notification buttons
        Intent waitReceive = new Intent(ACTION_WAIT_PRESSED);
        Intent stopReceive = new Intent(ACTION_STOP_PRESSED);
        PendingIntent pendingIntentWait = PendingIntent.getBroadcast(this, 123, waitReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(this, 123, stopReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        // Add the buttons and intents to notification
        mBuilder.addAction(android.R.drawable.ic_dialog_alert, "Wait 1 Min.", pendingIntentWait);
        mBuilder.addAction(android.R.drawable.ic_delete, "Stop", pendingIntentStop);

        // Build and show notification
        Notification notification = mBuilder.build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);

        // Keeps this service in the foreground so it will not be destroyed. Even if the app is closed
        // Use alarm manager instead if you want to perform operations periodically
        startForeground(SERVICE_ID, notification);
    }

    /**
     * Shows a notification when motion is detected.
     * Includes buttons for stopping sound and replaces
     * with the original ongoing notification when pressed.
     */
    private void showMotionDetectedNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                        .setContentTitle("House Control")
                        .setContentText("MOTION DETECTED");
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);

        // Set and register broadcast receivers for clicking notification buttons
        mMotionDetectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mMotionDetectedPlayer.pause();
                showOngoingNotification();
            }
        };
        registerReceiver(mMotionDetectedReceiver, new IntentFilter(ACTION_MOTION_DETECTED_PRESSED));

        // Create intents for pressing notification buttons
        Intent motionReceive = new Intent(ACTION_MOTION_DETECTED_PRESSED);
        PendingIntent pendingIntentMotion = PendingIntent.getBroadcast(this, 123, motionReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        // Add the buttons and intents to notification
        mBuilder.addAction(android.R.drawable.ic_dialog_alert, "Stop Alarm", pendingIntentMotion);

        // Build and show notification
        Notification notification = mBuilder.build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);

        startForeground(SERVICE_ID, notification);
    }

    /**
     * Shows a notification when the connection
     * to the bluetooth device has been lost.
     */
    private void showDisconnectedNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                        .setContentTitle("House Control")
                        .setContentText("CONNECTION LOST");
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);

        // Set and register broadcast receivers for clicking notification buttons
        mDisconnectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mDisconnectedPlayer.pause();
                // Remove notification and stop service
                mNotificationManager.cancel(NOTIFICATION_ID);
                bluetoothFinished();
            }
        };
        registerReceiver(mDisconnectedReceiver, new IntentFilter(ACTION_DISCONNECTED_CONFIRMED_PRESSED));

        // Create intents for pressing notification buttons
        Intent disconnectedReceive = new Intent(ACTION_DISCONNECTED_CONFIRMED_PRESSED);
        PendingIntent pendingIntentMotion = PendingIntent.getBroadcast(this, 123, disconnectedReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        // Add the buttons and intents to notification
        mBuilder.addAction(android.R.drawable.ic_dialog_alert, "OK", pendingIntentMotion);

        // Build and show notification
        Notification notification = mBuilder.build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);

        startForeground(SERVICE_ID, notification);
    }

    /**
     * Shows a notification when the connection
     * to the bluetooth device has been lost.
     */
    private void showCountdownNotification(int currentSecond) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                        .setContentTitle("House Control")
                        .setContentText("Time: " + currentSecond);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);

        // Build and show notification
        Notification notification = mBuilder.build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);

        startForeground(SERVICE_ID, notification);
    }

    private void bluetoothFinished() {
        if (mConnectedHelper.isConnected())
            mConnectedHelper.closeConnection();
        if (mWaitReceiver != null)
            unregisterReceiver(mWaitReceiver);
        if (mStopReceiver != null)
            unregisterReceiver(mStopReceiver);
        if (mMotionDetectedReceiver != null)
            unregisterReceiver(mMotionDetectedReceiver);
        if (mDisconnectedReceiver != null)
            unregisterReceiver(mDisconnectedReceiver);
        mMotionDetectedPlayer.release();
        mDisconnectedPlayer.release();
        BluetoothService.this.stopSelf();
    }

}
