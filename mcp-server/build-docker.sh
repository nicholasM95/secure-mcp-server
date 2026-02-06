#!/bin/bash

./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=mcp-server:1.0.2 -DskipTests -Pnative
