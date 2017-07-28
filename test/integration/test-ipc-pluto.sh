#!/bin/sh
# NOTE: It is not possible to test the icefaces-ipc-ajax-push-portlet on Pluto: https://issues.liferay.com/browse/FACES-3163
mvn verify -Dintegration.port=9080 -P selenium,pluto -Dit.test=\*JSF_IPC\*
