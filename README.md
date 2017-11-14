# Motion Detector Android

## ATTENTION
I was working on updating the project to support multiple Bluetooth devices at once.  I have since abandoned the project leaving the repo in a broken state.  Here is a link to an APK that should work: https://github.com/ShaneJansen/PortableSecurityAndroid/raw/master/app/apk-latest/app-debug.apk

You can checkout this commit for a working state: d94e5f0b80772bb2df9cffbf9c47d8699eb9140e

### Overview

This is the Android code for an infrared motion sensor connected to an Arduino microcontroller.
When motion is detected, the microcontroller communicates to a running Android application via Bluetooth
to play a sound and show a notification. The notification is dismissed and the microcontroller
is ready to detect the next movement. The alarm can be disabled for 1 minute from the application
and if the connection is lost during use, a different sound will play.

For full project details visit: http://shanejansen.com/android-bluetooth-motion-sensor-using-arduino

### Requirements

* Works in conjunction with [this Arduino program.](https://github.com/ShaneJansen/MotionDetectorArduino)
