<!--
  Applied in the maven test targets to one portlet definition 
  file at a time to build a java properties file with one 
  entry for each portlet definition.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pa="http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd">
  <xsl:output method="xml" indent="yes" doctype-system="http://java.sun.com/dtd/properties.dtd"/> 
  <xsl:strip-space elements="pa:portlet-app"/>
  <xsl:param name="page-path"/>
  <xsl:template match="/">
    <xsl:element name="properties">
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="pa:portlet-app/pa:portlet">
    <xsl:element name="entry">
      <xsl:attribute name="key"><xsl:value-of select='$page-path'/><xsl:value-of select="pa:portlet-name"/></xsl:attribute>
      <xsl:value-of select="substring-before(substring-after(pa:portlet-name,'-'),'-')"/>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
