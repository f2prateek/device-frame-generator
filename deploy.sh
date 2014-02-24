#!/bin/bash

# MANUAL : Bump version numbers in manifest, update build tools in build.gradle
./gradlew clean check spoon

./gradlew clean build

# Generate a changelog
github-changes -o f2prateek -r android-device-frame-generator -a -d commits

# Deploy website
./deploy_website.sh

# MANUAL : Upload apk to Google Play
