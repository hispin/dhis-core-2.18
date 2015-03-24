<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:gml="http://www.opengis.net/gml"
  xmlns:java="org.hisp.dhis.dxf2.gml.GmlConversionUtils"
  exclude-result-prefixes="java">

  <xsl:param name="precision">4</xsl:param>

  <xsl:template match="gml:coordinates" mode="multipleCoordinates">
    <xsl:text>[</xsl:text>
    <xsl:value-of select="java:gmlCoordinatesToString(normalize-space(.),$precision)"
      disable-output-escaping="yes"/>
    <xsl:text>]</xsl:text>
    <xsl:if test="position() != last()">
      <xsl:text>,</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="gml:coordinates" mode="singleCoordinate">
    <xsl:value-of select="java:gmlCoordinatesToString(normalize-space(.),$precision)"
      disable-output-escaping="yes"/>
  </xsl:template>

  <xsl:template match="gml:pos">
    <xsl:value-of select="java:gmlPosToString(normalize-space(.),$precision)"
      disable-output-escaping="yes" />
  </xsl:template>

  <xsl:template match="gml:posList">
    <xsl:text>[</xsl:text>
    <xsl:value-of select="java:gmlPosListToString(normalize-space(.),$precision)"
      disable-output-escaping="yes"/>
    <xsl:text>]</xsl:text>
    <xsl:if test="position() != last()">
      <xsl:text>,</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="gml:Polygon">
    <featureType>Polygon</featureType>
    <coordinates>
      <xsl:text>[[</xsl:text>
      <xsl:apply-templates select=".//gml:coordinates" mode="multipleCoordinates"/>
      <xsl:apply-templates select=".//gml:posList"/>
      <xsl:text>]]</xsl:text>
      <xsl:if test="position() != last()">
        <xsl:text>,</xsl:text>
      </xsl:if>
    </coordinates>
  </xsl:template>

  <xsl:template match="gml:MultiPolygon">
    <featureType>MultiPolygon</featureType>
    <coordinates>
      <xsl:text>[</xsl:text>
      <xsl:apply-templates select=".//gml:polygonMember"/>
      <xsl:text>]</xsl:text>
    </coordinates>
  </xsl:template>

  <xsl:template match="gml:Point">
    <featureType>Point</featureType>
    <coordinates>
      <xsl:apply-templates select=".//gml:coordinates" mode="singleCoordinate"/>
      <xsl:apply-templates select=".//gml:pos"/>
    </coordinates>
  </xsl:template>

  <xsl:template match="gml:polygonMember">
    <xsl:text>[</xsl:text>
    <xsl:apply-templates select=".//gml:coordinates" mode="multipleCoordinates"/>
    <xsl:apply-templates select=".//gml:posList"/>
    <xsl:text>]</xsl:text>
    <xsl:if test="position() != last()">
      <xsl:text>,</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="gml:featureMember">
    <xsl:variable name="name" select=".//*[local-name()='Name' or local-name()='NAME' or local-name()='name']"/>
    <organisationUnit>
      <xsl:attribute name="name">
        <xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:attribute name="shortName">
        <xsl:value-of select="substring($name,1,50)"/>
      </xsl:attribute>
      <xsl:apply-templates select="./child::node()/child::node()/gml:Polygon|./child::node()/child::node()/gml:MultiPolygon|./child::node()/child::node()/gml:Point"/>
      <active>true</active>
    </organisationUnit>
  </xsl:template>

  <xsl:template match="/">
    <dxf xmlns="http://dhis2.org/schema/dxf/2.0">
      <organisationUnits>
        <xsl:apply-templates select=".//gml:featureMember"/>
      </organisationUnits>
    </dxf>
  </xsl:template>

</xsl:stylesheet>
