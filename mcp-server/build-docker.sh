#!/bin/bash

./mvnw clean install

./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=mcp-server:1.0.0 -DskipTests
