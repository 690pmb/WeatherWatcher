#!/bin/bash
set -e

echo "Building App..."
mvn clean install -DskipTests -q

cd docker
rm -f *.jar 

echo "Copy jar"
cp ../target/*.jar .
echo "Build Docker image"
docker build -t weather .
cd ..
echo "Docker compose"
docker-compose up -d
echo "APP SUCCESSFULLY DEPLOYED"
