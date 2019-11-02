#!/bin/sh
mvn verify -P selenium,liferay -Dit.test=\*Flows\* -Dintegration.browser.headless=false
