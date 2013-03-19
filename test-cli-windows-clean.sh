#!/bin/bash
#
# Simple script to test Java-based execution of Spoon.
set -e

APK=`\ls app/target/*.apk`
TEST_APK=`\ls integration-tests/target/*.apk`

java -jar spoon-*-jar-with-dependencies.jar --apk "$APK" --test-apk "$TEST_APK" --output target

start target/index.html