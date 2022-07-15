#!/bin/sh
mvn verify -Dintegration.port=9080 -P selenium-alloy-showcase,pluto
#mvn verify -Dintegration.port=9080 -P selenium-alloy-showcase,pluto -Dit.test=\*DataTable\*
