#!/bin/sh
mvn -Dintegration.port=9080 verify -P selenium,pluto,chrome,tomcat "$@"
