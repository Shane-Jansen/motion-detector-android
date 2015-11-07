# Motion Detector Android

### Overview

This is the Android code for an infrared motion sensor connected to an Arduino microcontroller.
When motion is detected, the microcontroller communicates to a running Android application via Bluetooth
to play a sound and show a notification. The notification is dismissed and the microcontroller
is ready to detect the next movement. The alarm can be disabled for 1 minute from the application
and if the connection is lost during use, a different sound will play.

### Requirements

* Works in conjunction with [this Arduino program.](https://github.com/ShaneJansen/MotionDetectorArduino)

### Notes

To see a working example, list of parts, and video demonstration visit: http://sjjapps.com/
