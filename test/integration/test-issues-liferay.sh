#!/bin/sh
#mvn verify -P selenium,liferay -Dit.test=\*1470\*Test\*
mvn verify -P selenium,liferay  -Dit.test='*issue.*Test*,*issue.*primefaces.*Test*'
