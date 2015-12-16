<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pa="http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd">
  <xsl:strip-space elements="pa:portlet-app"/>
  <xsl:output method="xml" indent="yes"/>
  <xsl:param name="main-portlet-context"/>
  <xsl:param name="extra-portlet-def"/>
  <xsl:param name="extra-portlet-context"/>
  <xsl:template match="/">
  <pluto-portal-driver
    xmlns="http://portals.apache.org/pluto/xsd/pluto-portal-driver-config.xsd"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://portals.apache.org/pluto/xsd/pluto-portal-driver-config.xsd
                        http://portals.apache.org/pluto/pluto-portal/1.1/pluto-portal-driver-config.xsd"
    version="1.1">


    <portal-name>pluto-portal-driver</portal-name>
    <portal-version>2.0.0</portal-version>
    <container-name>Pluto Portal Driver</container-name>

    <supports>
      <portlet-mode>view</portlet-mode>
      <portlet-mode>edit</portlet-mode>
      <portlet-mode>help</portlet-mode>
      <portlet-mode>config</portlet-mode>

      <window-state>normal</window-state>
      <window-state>maximized</window-state>
      <window-state>minimized</window-state>
    </supports>
    <xsl:element name="render-config">
      <xsl:attribute name="default">TestPage001</xsl:attribute>
      <xsl:comment>Main tests</xsl:comment>
      <xsl:apply-templates/>
      <xsl:comment>Section3.2 Lifecycle set</xsl:comment>
      <xsl:apply-templates select="document($extra-portlet-def)/pa:portlet-app"/>
    </xsl:element>
    </pluto-portal-driver>
  </xsl:template>

  <xsl:template match="pa:portlet-app/pa:portlet">
      <xsl:element name="page">
        <xsl:attribute name="name">TestPage<xsl:number format="001"/></xsl:attribute>
        <xsl:attribute name="uri">/WEB-INF/themes/pluto-default-theme.jsp</xsl:attribute>
          <xsl:element name="portlet">
            <xsl:attribute name="context">/<xsl:value-of select='$main-portlet-context'/></xsl:attribute>
            <xsl:attribute name="name"><xsl:value-of select="pa:portlet-name"/></xsl:attribute>
          </xsl:element>
      </xsl:element>    
  </xsl:template>

  <xsl:template match="pa:portlet-app/pa:portlet[pa:portlet-info/pa:title = 'chapter3Tests-lifecycleTest-portlet - LIFECYCLE IMPLEMENTATION CLASS SET']">
      <xsl:element name="page">
        <xsl:attribute name="name">TestPage001a</xsl:attribute>
        <xsl:attribute name="uri">/WEB-INF/themes/pluto-default-theme.jsp</xsl:attribute>
          <xsl:element name="portlet">
            <xsl:attribute name="context">/<xsl:value-of select='$extra-portlet-context'/></xsl:attribute>
            <xsl:attribute name="name"><xsl:value-of select="pa:portlet-name"/></xsl:attribute>
          </xsl:element>
      </xsl:element>    
  </xsl:template>

  <xsl:template match="text()|@*"/>

</xsl:stylesheet>
