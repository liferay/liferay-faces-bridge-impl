#!/bin/sh
mvn verify -Dintegration.port=9080 -P selenium-jsf-showcase,pluto
