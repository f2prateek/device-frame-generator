#!/bin/bash

# MANUAL : Bump version numbers in manifest and website

# Run tests
./test-cli.sh

# Build a signed app

APK=`\ls app/build/apk/*release*.apk`
# Place apk in website directory
cp $APK website/dfg.apk

# Deploy website
./deploy_website.sh

# MANUAL : Upload apk to Google Play
