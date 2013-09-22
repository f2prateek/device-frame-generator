#!/bin/bash
#
# Simple script to test Java-based execution of Spoon. You must have assembled
# the jar prior to running this script (i.e., mvn clean verify).

adb uninstall com.f2prateek.dfg.debug
adb uninstall com.f2prateek.dfg.debug.tests
adb uninstall com.f2prateek.dfg
gradle clean

gradle assemble
gradle assembleTest

set -e

APK=`\ls app/build/apk/*debug*.apk`
TEST_APK=`\ls app/build/apk/*test*.apk`
SPOON_JAR=`\ls spoon-*-jar-with-dependencies.jar`

java -jar $SPOON_JAR --apk "$APK" --test-apk "$TEST_APK" --output target

start target/index.html