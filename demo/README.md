# Building the Liferay Faces Bridge Demos From Source

The Liferay Faces Bridge demos can be built seperately from the bridge. The demos can also be built for different versions of Liferay or Pluto.

To build a Liferay Faces Bridge demo from source:

1. Navigate to the demo you want to build (for example the jsf-applicant-portlet):

		cd liferay-faces-bridge-impl/demos/jsf-applicant-portlet/

2. Build the demo for Liferay:*

		mvn clean package -P liferay,mojarra,tomcat

	Or build the demo for Pluto:

		mvn clean package -P pluto,mojarra,tomcat

Once you have built the demo, the `target` folder will contain a war that you can deploy to the portlet container.

Alternatively, you can build all the demos at once:

1. Navigate to the `demos` directory:

		cd liferay-faces-bridge-impl/demos/

2. Build the demos for Liferay:*

		mvn clean package -P liferay,mojarra,tomcat

	Or build the demos for Pluto:

		mvn clean package -P pluto,mojarra,tomcat

\* **Note:** Some versions of the bridge are compatible with multiple versions of Liferay. For example, the Liferay Faces Bridge `4.x` version is compatible with both Liferay 7.0 and Liferay 6.2. To build demos for older versions of Liferay, specify the version in the profile. For example, specify `-P liferay62` to build a demo for Liferay 6.2. See the [Liferay Faces Version Scheme](https://dev.liferay.com/develop/tutorials/-/knowledge_base/6-2/understanding-the-liferay-faces-version-scheme#liferay-faces-version-scheme-for-releases-after-liferay-faces-ga6) for more details on the bridge's compatibility with Liferay versions.
