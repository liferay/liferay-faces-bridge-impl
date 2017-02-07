#!/bin/sh
mvn verify -P selenium,liferay,chrome,jboss "$@"
