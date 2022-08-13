#!/bin/sh

./gradlew clean build -q
java -jar app/build/libs/app-standalone.jar