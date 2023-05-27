#!/bin/bash
set -e
docker exec -it maria mysql --user root -pweather weather
