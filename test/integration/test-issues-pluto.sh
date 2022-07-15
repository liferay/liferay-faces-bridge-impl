#!/bin/sh
mvn verify -Dintegration.port=9080 -P selenium,pluto -Dit.test='*issue.*Test*,*issue.*primefaces.*Test*'
