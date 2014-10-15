Device Frame Generator
=======================

[![Build Status](https://travis-ci.org/f2prateek/android-device-frame-generator.png)](https://travis-ci.org/f2prateek/android-device-frame-generator)

Wrap your app screenshots in real device artwork. For more information, see the [website](http://f2prateek.com/android-device-frame-generator).


Building
---------
Simply execute `./gradlew clean assembleDebug checkDebug`.

To run a full build (including release), you'll need to fill in some missing files.

First add a `AndroidManifest.xml` under `app/src/release` with this template, replacing `YOUR_CRASHLYTICS_KEY_HERE` with the appropriate field.
```xml
<?xml version="1.0" encoding="utf-8"?>

<manifest xmlns:android="http://schemas.android.com/apk/res/android">

  <application>
    <meta-data android:name="com.crashlytics.ApiKey"
        android:value="YOUR_CRASHLYTICS_KEY_HERE"/>
  </application>

</manifest>
```
You'll also need to add a `signing.properties` file at the root of the project, with the correct values

```
STORE_FILE=[keystore file]
STORE_PASSWORD=[keystore password]
KEY_ALIAS=[keystore alias]
KEY_PASSWORD=[alias password]
```

Then execute `./gradlew clean build`