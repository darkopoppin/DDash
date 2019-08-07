# DDash


## Device Dashboard - centralized view of your devices data

DDash is a system that collects data from each of your devices, and reports it to a central dashboard. 

Our native software, specifically created for Android and sends device information (system metadata, resource usage, etc.) to a central server that you can query through any web browser.

*This is a Work In Progress. See the project timeline for a list of future updates.*

## DDash Android Client

A user-friendly Android app which collects information about your Android device, with a built-in UI. The data is kept on our Cloud Firestore Firebase in individual user accounts and each account can have multiple devices. The dynamic data like RAM, battery status is updated in the database in an interval of 15 minutes, but the location is more frequently. 

Requires **Android version 5.0, Lollipop (release 21)**.

### Data that we can get
* **RAM usage**.
* **Secondary storage usage (internal and SD card).**
* **Internet connection speed.**
* **Battery status.**
* **System/build information.**
* **Network information.**
* **Location.**

### Screenshots

<p>App home window with summary information:</p>
<img alt="app home window" src=/images/main-activity-screen.png width=300>

<p>Extra network information:</p>
<img alt="extra network info window" src=/images/device-2019-07-17-network-extra.png>

<p>Extra system information:</p>
<img alt="extra system info window" src=/images/device-2019-07-17-system-extra.png>

<p>Sign in/Signup functionality to access database</p>
<img alt="Sign in / Sign up activity" src=/images/sign_in_intent.png width=300>

### Known issues or TODO's
* **CPU** usage information can not be retrieved (Android OS limitation).
* Send regular location updates (every 5 mins) to the Firebase.
* 2 memory leaks 


## Project timeline

* Currently working on Firebase database and fixing bugs and memory leaks.
* Next steps in the project: 
  * Web-based dashboard.
  * Desktop device support (maybe). 
