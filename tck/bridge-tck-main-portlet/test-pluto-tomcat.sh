#!/bin/sh
mvn verify -P selenium,pluto,chrome,tomcat "$@"
