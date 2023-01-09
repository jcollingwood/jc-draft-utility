#!/bin/sh

./gradlew build -q -p cli-app
java -jar cli-app/build/libs/cli-app-standalone.jar