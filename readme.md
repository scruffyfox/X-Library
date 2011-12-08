Read Me
=======
X Library is an open source Android control, library and utility library written by Callum Taylor.
The library has been used on many Android apps including First Aid by British Red Cross ([Link][rca]), Chef+ by The Staff Canteen (TBA) and Eye Check by Boots Opticians (TBA).

Installation
------------
To install the library, clone the source into a folder of your choice and import into Eclipse. Inside Eclipse, right click on the project you wish to use the library on, click on properties, go to Android, click add and select X Library and press OK.
NOTE: The library requires your project to be at least version 2.2 and using the Google Libraries SDK (Because of the MapOverlay class) but you can remove this class if you wish. The minimum SDK version is still 2.2.
To use the library UI elements within XML, you must add the namespace to the top of your source (as with the standard Android namespace). Instead of "android" at the end of the URL, use the package name of your project, for example `xmlns:x="http://schemas.android.com/apk/res/com.cube.bec"` where com.cube.bec is your package name.

Contributing
------------
Please feel free to fork this library and change or add what ever you like. If you find a bug or have something that should be included in the library, you must send a request and I will review and add it in to the official release. Please also make a note in your fork that it is not the official release, but a forked release. And most importantly, only take credit for what YOU have changed.

Documentation
-------------
The documentation for the library can be found [Here][doc], but is not always as up-to date as the library its self.

Demo
----
There is a library demo for the UI elements and can be found [Here][demo]

[rca]: http://market.android.com/details?id=com.cube.rca
[doc]: http://scruffyfox.github.com/X-Library
[demo]: http://github.com/scruffyfox/X-Library-Demo