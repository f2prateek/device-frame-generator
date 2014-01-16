#!/bin/bash

# MANUAL : Bump version numbers in manifest, update build tools in build.gradle
./gradlew clean check spoon

# MANUAL : pass in keystore credentials
./gradlew clean build

# Deploy website
./deploy_website.sh

# MANUAL : Upload apk to Google Play