#!/bin/sh
>&2 echo "NOTE: There are test failures in the richfaces-applicant-portlet due to https://issues.liferay.com/browse/FACES-3571"
mvn verify -Dintegration.port=9080 -P selenium,pluto -Dit.test="!ADF*ApplicantPortletTester,!*Ice*Applicant*,!*Rich*Applicant*,*Applicant*"
