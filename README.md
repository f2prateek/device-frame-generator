Device Frame Generator
=======================

Wrap your app screenshots in real device artwork. For more information, see the [website](http://f2prateek.com/device-frame-generator).

Adding Devices
--------------

To add new devices, you'll need to add 7 images in the [`app/src/main/res/drawable-nodpi`](https://github.com/f2prateek/device-frame-generator/tree/master/app/src/main/res/drawable-nodpi):
* `device_land_back`
* `device_land_glare`
* `device_land_shadow`
* `device_port_back`
* `device_port_glare`
* `device_port_shadow`
* `device_thumb`

All these should be in the png format and named as above - with `device` replaced with a key that will identify this set of frames.
Once that's done, simply add the metadata in [`DeviceModule.java`](https://github.com/f2prateek/device-frame-generator/blob/master/app/src/main/java/com/f2prateek/dfg/DeviceModule.java) located at `app/src/main/java/com/f2prateek/dfg/DeviceModule.java`.
Here's what an example for the Nexus 5 would look like.

```java
@Provides(type = SET) Device provideNexus5() {
    return new Device.Builder().setId("nexus_5")
        .setName("Nexus 5")
        .setUrl("http://www.google.com/nexus/5/")
        .setPhysicalSize(5.43f)
        .setDensity("XXHDPI")
        .setLandOffset(436, 306)
        .setPortOffset(306, 436)
        .setPortSize(1080, 1920)
        .setRealSize(1080, 1920)
        .addProductId("hammerhead")
        .build();
  }
```

You can see what the metadata fields mean at [`Device.java`](https://github.com/f2prateek/device-frame-generator/blob/master/app/src/main/java/com/f2prateek/dfg/model/Device.java#L27)

Building
---------
Simply execute `./gradlew clean build`.

[![Build Status](https://travis-ci.org/f2prateek/device-frame-generator.png)](https://travis-ci.org/f2prateek/device-frame-generator)

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Device%20Frame%20Generator-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1493)
