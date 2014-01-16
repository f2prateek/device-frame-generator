Change Log
===============================================================================

This is a changelog of the app internals, not just user facing features.

Version 2.0.0
----------------------------
 * Internal re-writes
 * Bug fixes, check that received screenshots actually exist
 * build.gradle updates
 * Update dependencies
 * New naming scheme

Version 1.1.6
----------------------------
 * Add Xperia Z1 frames

Version 1.1.2
----------------------------
 * Fix NPE bug when viewing about screen

Version 1.1.1
----------------------------
 * Add frames for HTC One
 * Fix dimensions for Nexus 7 (2013) frames

Version 1.0.9
----------------------------
 * Add frames for Nexus 7 (2013)

Version 1.0.8
----------------------------
 * Add Crashlytics

Version 1.0.7
----------------------------
 * Actually update the dependencies

Version 1.0.6
----------------------------
 * Update dagger
 * Dagger now uses code-gen again, faster loading!

Version 1.0.5
----------------------------
 * Fix a bug that crashed the app in landscape
 * Add a website


Version 1.0.4
----------------------------

 * Switch to Gradle
 * Add RoboGuice utilities
 * Update code styles
 * Drop support for API<14
 * Drop the memory cache in DeviceFrameGenerator.java
 * More consistent touch feedback for images and text
 * Use picasso for image loading
 * Fix Android 4.3 bug that wouldn't save images
 * Drop Bugsense
