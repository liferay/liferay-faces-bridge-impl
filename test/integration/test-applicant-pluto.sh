#!/bin/sh
>&2 echo "NOTE: There are test failures in the alloy-applicant-portlet due to https://issues.liferay.com/browse/FACES-2866"
>&2 echo "NOTE: There are test failures in the icefaces-applicant-portlet due to https://issues.liferay.com/browse/FACES-2867"
>&2 echo "NOTE: There are test failures in the jsf-cdi-applicant-portlet due to https://issues.liferay.com/browse/FACES-2865"
>&2 echo "NOTE: There are test failures in the jsf-jsp-applicant-portlet due to https://issues.liferay.com/browse/FACES-3054"
mvn verify -Dintegration.port=9080 -P selenium,pluto -Dit.test=\*Applicant\*
