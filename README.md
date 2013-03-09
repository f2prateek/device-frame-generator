# Device Frame Generator App

This repository contains the source code for the [Device Frame Generator](http://www.androidbootstrap.com/)
Android app available from [Google Play](https://play.google.com/store/apps/details?id=com.f2prateek.dfg).

Please see the [issues](https://github.com.f2prateek.dfg/issues) section
to report any bugs or feature requests and to see the list of known issues.

Have a questions about Device Frame Generator? Ask away on the [com-f2prateek-dfg discussion forum](https://groups.google.com/forum/#!forum/com-f2prateek-dfg). 

<a href="https://play.google.com/store/apps/details?id=com.f2prateek.dfg" alt="Download from Google Play">
  <img src="http://f.cl.ly/items/000s3k2I2J1V281X2A1f/bootstrap-image.png">
</a>

## HOW TO
Learn how to develop with IntelliJ and Maven. [Watch the how to video](http://www.youtube.com/watch?v=YbyzRNxcvZw&feature=youtube_gdata)

## Authentication
Log into this demo app with the following credentials:

user: demo@androidbootstrap.com

password: android


## Generating your Bootstrap App
Why generate? Simple ... renaming files, folders, copy and pasting is SUPER error prone and well... it sucks overall. This can easily take a few days with debugging if you run into issues and perform a lot of typo's. Using the generator on [AndroidBootstrap.com](http://www.androidbootstrap.com) you can generate your application with your application name as well as the package (and folder structure) that you want to work with. 

As an example, you know that you want your app name and package to the following: 

  - *App Name*: Notify
  - *Package Name*: com.notify.app.mobile

After generating the app on [AndroidBootstrap.com](http://www.androidbootstrap.com) the folder structure of the source code for the app will change:

  - From: __com.f2prateek.dfg__ 
  - To: __com/notify/app/mobile__

At that point all the source files that were located in ____com.f2prateek.dfg__ will be moved to the new folder __com/notify/app/mobile__. 

All import statments that reference the old resources (__R.com.f2prateek.dfg.R__) will now be renamed to the correct package. The artifact id's in the *pom.xml* (and various other places) will be replaced. The App Name will be replaced in the strings/etc. 

The end result is that you will be given a zip file with the correct structure. Open the zip and then execute *mvn clean package* and your app should be ready for development. 

Enjoy!

The application 

## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)


Copyright 2012 Donn Felker


Copyright 2012 GitHub Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


## Building

The build requires [Maven](http://maven.apache.org/download.html)
v3.0.3+ and the [Android SDK](http://developer.android.com/sdk/index.html)
to be installed in your development environment. In addition you'll need to set
the `ANDROID_HOME` environment variable to the location of your SDK:

    export ANDROID_HOME=/home/donnfelker/tools/android-sdk

After satisfying those requirements, the build is pretty simple:

* Run `mvn clean package` from the `app` directory to build the APK only
* Run `mvn clean install` from the root directory to build the app and also run
  the integration tests, this requires a connected Android device or running
  emulator

You might find that your device doesn't let you install your build if you
already have the version from the Android Market installed.  This is standard
Android security as it it won't let you directly replace an app that's been
signed with a different key.  Manually uninstall Device Frame Generator from your device and
you will then be able to install your own built version.

## Acknowledgements

Device Frame Generator is a result of a template project I've developed over the years as well as
a combination of a lot of great work that the [GitHub Gaug.es](http://www.github.com/github/gauges-android)
app and [GitHub Android](http://www.github.com/github/android) app showcased. Some fo the
code in this project is based on the GitHub Gaug.es and GitHub Android app.

Device Frame Generator is built on the awesome [Parse.com API](http://www.parse.com/)
and uses many great open-source libraries from the Android dev community:

* [ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock) for a
  consistent, great looking header across all Android platforms,
  [ViewPagerIndicator](https://github.com/JakeWharton/Android-ViewPagerIndicator)
  for swiping between fragments and
  [NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids) for 
  view animations - all from [Jake Wharton](http://jakewharton.com/).
* [RoboGuice](http://code.google.com/p/roboguice/) for dependency-injection.
* [Robotium](http://code.google.com/p/robotium/)
  for driving our app during integration tests.
* [android-maven-plugin](https://github.com/jayway/maven-android-plugin)
  for automating our build and producing release-ready APKs.
* [http-request](https://github.com/kevinsawicki/http-request) for interacting with
  remote HTTP resources (API's in this case).
* [google-gson](http://code.google.com/p/google-gson/) for consuming JSON and hydrating
  POJO's for use in the app.


## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com.f2prateek.dfg/pulls).

Any contributions, large or small, major features, bug fixes, additional
language translations, unit/integration tests are welcomed and appreciated
but will be thoroughly reviewed and discussed.

I hope this helps you in building your next android app.
