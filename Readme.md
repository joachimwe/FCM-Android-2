FCM-Android 2
=============

Android based program for adjusting and diagnosis of FCM over FCMCP
API 22

<h2>Announcments</h2>
2015-05-13
Based on the experience of FCM Android (1).
* Improved Softwaredesign with Frontend <-> Backgroud-Service pattern
* Communication programmed to an interface, to support different sources (e.g. DummyFCM, realFCM, ...)
* designated for stacked data frames like sensors and gps "simultanious" (not implemented in FCM Module)
* JSON support for logging

<h2>Third Party Libs</h2>
To build, link the following Libs
achartengine-1.1.0.jar
(https://code.google.com/p/achartengine/downloads/list)
imported via gradle:
    compile 'org.achartengine:achartengine:1.2.0'

osmbonuspack v 5.1 - must be copied in /libs
https://github.com/MKergall/osmbonuspack

osmdroid-android-4.2.jar
(https://github.com/osmdroid/osmdroid)
please integrate also the following requests:
https://code.google.com/p/osmdroid/wiki/Prerequisites
imported via gradle:
    compile 'org.osmdroid:osmdroid-android:4.3'
    compile 'org.slf4j:slf4j-simple:1.6.1'

json support
    compile 'com.google.code.gson:gson:2.2.4'
