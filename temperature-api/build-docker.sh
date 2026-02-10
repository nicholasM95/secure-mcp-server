#!/bin/bash

./mvnw clean install

./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=temperature-api:1.0.0 -DskipTests
