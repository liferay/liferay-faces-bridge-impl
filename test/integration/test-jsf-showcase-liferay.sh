#!/bin/sh
mvn verify -P selenium-jsf-showcase,liferay
#mvn verify -P selenium-jsf-showcase,liferay -Dit.test=\*Input\*ConversionTester\* -Dintegration.browser.headless=false
