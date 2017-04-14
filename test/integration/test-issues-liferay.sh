#!/bin/sh
mvn verify -P selenium,liferay,chrome -Dit.test=\*issue.\*Test\*
