#!/bin/bash

# MANUAL : Bump version numbers in manifest and website

# Run tests
./test-cli.sh

# MANUAL : Build a signed app

# Deploy website
./deploy_website.sh

# MANUAL : Upload apk to Google Play