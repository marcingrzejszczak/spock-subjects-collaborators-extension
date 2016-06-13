#!/usr/bin/env bash
./gradlew clean build && ./gradlew uploadArchives && ./gradlew closeRepository && ./gradlew promoteRepository && ./gradlew pushRelease
