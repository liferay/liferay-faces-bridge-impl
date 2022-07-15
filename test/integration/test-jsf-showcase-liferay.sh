#!/bin/sh
mvn verify -P selenium-jsf-showcase,liferay
#mvn verify -P selenium-jsf-showcase,liferay -Dintegration.browser.headless=false
#mvn verify -P selenium-jsf-showcase,liferay -Dit.test=\*InputHiddenConversionTester\*
