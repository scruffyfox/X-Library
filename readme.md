Read Me
=======
X Library is an open source Android control, library and utility library written by [Callum Taylor][website].
The library has been used on many Android apps including Jackson Stops &amp; Staff ([Link][jss]), First Aid by British Red Cross ([Link][rca]), Chef+ by The Staff Canteen (TBA), Eye Check by Boots Opticians (TBA), Vision Test 2.0 (TBA), Micro Management (In Development - [Sources here][mpm]).

** [Follow me on twitter][twitter] **

** Don't forget to watch the project as it's always changing! **

Installation
------------
NOTE: The library requires your project to be 2.2 + Google APIs or higher. If you do not want to use the Google APIs, delete XUIMapOverlay.java.
To install the library, clone the source into a folder of your choice.

1. Import into Eclipse.
2. Right click on the project you wish to use the library on
3. Click on properties
4. Go to the Android tab
5. Click the add button in the library section and select X Library.
6. Press OK.

NOTE: The library requires your project to be at least version 2.2 and using the Google Libraries SDK (Because of the MapOverlay class) but you can remove this class if you wish. The minimum SDK version is still 2.2.
To use the library UI elements within XML, you must add the namespace to the top of your source (as with the standard Android namespace). Instead of "android" at the end of the URL, use the package name of your project, for example `xmlns:x="http://schemas.android.com/apk/res/com.cube.bec"` where com.cube.bec is your package name.

Suggestions
-----------
Feel free to email or message me any suggestions or bugs you find in the code/documentation and I will fix it/help you out.

Contributing
------------
Please feel free to fork this library and change or add what ever you like. If you find a bug or have something that should be included in the library, you must send a request and I will review and add it in to the official release. Please also make a note in your fork that it is not the official release, but a forked release. And most importantly, only take credit for what YOU have changed.

Documentation
-------------
The documentation for the library can be found [Here][doc], but is not always as up-to date as the library its self.

Demo
----
There is a library demo for the UI elements and can be found [Here][demo]

[jss]: http://market.android.com/details?id=uk.co.jacksonstops.property
[rca]: http://market.android.com/details?id=com.cube.rca
[mpm]: https://github.com/scruffyfox/Micro-Management
[doc]: http://scruffyfox.github.com/X-Library
[demo]: http://github.com/scruffyfox/X-Library-Demo
[website]: https://callumtaylor.net
[twitter]: http://twitter.com/scruffyfox