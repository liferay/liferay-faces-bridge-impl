#!/bin/sh
mvn verify -P selenium,liferay -Dit.test=\*Applicant\* -Dintegration.browser.headless=false
