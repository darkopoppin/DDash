# DDash


## Device Dashboard - centralized view of your devices

DDash is a system that collects data from each of your devices, and reports it to a central dashboard. 

Our native software, specifically created for each platform, runs on each of your devices (whether an Android/iOS phone, a Windows/Linux machine, or anything else) and sends device information (system metadata, resource usage, etc.) to a central server that you can query through any web browser.


*This is a Work In Progress. Currently we only have an Android app that runs on Android devices and displays device data through the app's UI. See the project timeline for a list of future updates.*


## DDash Android Client


A user-friendly Android app to collect information about your Android device, with a built-in UI.

Requires Android version 5.0, Lollipop (release 21).


### Data that we can get
* **RAM usage**.
* **Secondary storage usage (internal and SD card).**
* **Internet connection speed.**

* **Battery status.**
<p align="center">
  <img width="460" height="700" src=/screenshots/device-2019-07-17-homepage.png>
</p>

* **System/build information.**
<p align="center">
  <img width="460" height="700" src=/screenshots/device-2019-07-17-system-extra.png>
</p>


* **Network information.**
<p align="center">
  <img width="460" height="700" src=/screenshots/device-2019-07-17-network-extra.png>
</p>



### Known issues or TODO's
* **CPU** usage information can not be retrieved (OS limitation).
* The app does not report device information anywhere yet. All data is kept **local**.
* The app does not run in the **background**.


## Project timeline

* 2019 July/August: release Android Client app on Google Play Store
* Foreseeable future:
  * Database implementation.
  * Establish log in system.
  * Develop web based dashboard.
  * Desktop device support.
  * iOS build.?
