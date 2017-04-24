#!/bin/sh
mvn verify -P selenium,liferay -Dit.test=\*issue.\*Test\*
