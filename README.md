# Group6
Greenhouse Monitoring System for SYSC3010 - Group6

Arduino Setup:
  1. In the main folder (Group6) of the git repository, find the file sysc3010ErrorTesting.ino. Upload this file using the Arduino IDE to      the Arduino Uno. 
  2. Setup the Arduino Uno board following the wiring diagram provided in the final report. 
  3. Plug the USB cable into the Greenhouse Raspberry Pi (GP)
  4. The LCD screen on the Arduino should now show the temperature and humidity, and the serial port should be sending data to the GP.

Raspberry Pi Setup (both Server Pi and Greenhouse Pi):
  1. Assuming that the Arduino is already connected to the GP over USB, the next step is to power on both Raspberry Pi's, with both            connected to the local network with an ethernet switch. 
  2. Download the repository on the Raspberry Pi. Go to the folder: Group6/GreenhouseMonitoringSystem/src/
  3. To compile and run the Java program for the GP run: make runMain. This will build and run the GreenhouseMain.java program which            collects data over serial from the Arduino, and sends it to the Server Pi (SP). This program also accepts commands from the SP.
  4. For the Server Pi, follow step 1 again.
  5. To compile and run the Java program for the SP run: make runManage. This will build and run the GreenhouseManagement.java program          which accepts data packets over UDP from the GP, updates the database, and sends commands to the GP. 
  
  Database, Android, and Web Applications
    1. The database is always running so no setup is needed there. 
    2. The Android app is available as an APK file in the main folder. It should be called group6.apk. This should be installed onto the          Android phone and run.
    3. The web application is expected to be run on a local machine. To run the web app, have (library name here NODE???) installed.
    4. Log into either app using the email: test@test.com, and the password: pass123. 
    5. The Greenhouse should be visible, and upon clicking the containing button, should show the current temperature, humidity, and fan          status. 
    6. A command can be sent to the SP using the 'toggle fan' button. After a few seconds, you should see the fan status in the greenhouse        information update. 
    

  
