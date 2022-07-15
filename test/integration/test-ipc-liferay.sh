#!/bin/sh
#mvn verify -P selenium,liferay -Dit.test=\*IPC\*
mvn verify -Dintegration.browser.headless=false -P selenium,liferay -Dit.test=\*IPC\*
