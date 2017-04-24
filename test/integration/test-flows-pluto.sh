#!/bin/sh
>&2 echo "NOTE: There are test failures in the jsf-flows-portlet due to https://issues.liferay.com/browse/FACES-2865"
mvn verify -Dintegration.port=9080 -P selenium,pluto -Dit.test=\*Flows\*
