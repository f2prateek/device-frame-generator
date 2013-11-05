#!/bin/bash

# MANUAL : Bump version numbers in manifest, update build tools in build.gradle
./gradlew clean check

# Run tests
./test-cli.sh

# MANUAL : Build a signed app
./gradlew clean assembleRelease

# Deploy website
./deploy_website.sh

# MANUAL : Upload apk to Google Play