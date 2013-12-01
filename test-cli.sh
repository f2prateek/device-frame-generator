#!/bin/bash

./gradlew clean assembleDebug assembleTest

set -e

APK=`\ls app/build/apk/*debug*.apk`
TEST_APK=`\ls app/build/apk/*test*.apk`
SPOON_JAR=`\ls spoon-*-jar-with-dependencies.jar`

java -jar $SPOON_JAR --apk "$APK" --test-apk "$TEST_APK" --output target

start target/index.html