<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gml="http://www.opengis.net/gml">

<xsl:param name="roundingFactor">10000</xsl:param>
  
<xsl:template match="gml:coordinates">
  <coordinatesTuple>
  <xsl:call-template name="coordinates-delimiter">
    <xsl:with-param name="coordinates"><xsl:value-of select="."/></xsl:with-param>
  </xsl:call-template>
</coordinatesTuple>
</xsl:template>

<xsl:template name="coordinates-delimiter">
  <xsl:param name="coordinates"/>
  <xsl:variable name="newlist" select="concat(normalize-space($coordinates), ' ')"/>
  <xsl:variable name="first" select="substring-before($newlist, ' ')"/>
  <xsl:variable name="xcoord" select="substring-before($first, ',')"/>
  <xsl:variable name="ycoord" select="substring-after($first, ',')"/>
  <xsl:variable name="remaining" select="substring-after($newlist, ' ')"/>
  <coord><xsl:value-of select="round($roundingFactor*$xcoord) div $roundingFactor"/>,<xsl:value-of select="round($roundingFactor*$ycoord) div $roundingFactor"/></coord>
  <xsl:if test="$remaining">
    <xsl:call-template name="coordinates-delimiter">
      <xsl:with-param name="coordinates" select="$remaining"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>


<xsl:template match="gml:Polygon">
  <feature type="Polygon">
    <xsl:apply-templates select=".//gml:coordinates"/>
  </feature>
</xsl:template>

<xsl:template match="gml:MultiPolygon">
  <feature type="MultiPolygon">
    <xsl:apply-templates select=".//gml:coordinates"/>
  </feature>
</xsl:template>

<xsl:template match="gml:Point">
  <feature type="Point">
    <xsl:apply-templates select=".//gml:coordinates"/>
  </feature>
</xsl:template>

<xsl:template match="gml:featureMember">
  <xsl:variable name="name" select=".//*[local-name()='Name' or local-name()='NAME' or local-name()='name']"/>
  <organisationUnit>
    <id>0</id>
    <uuid/>
    <name><xsl:value-of select="$name"/></name>
    <shortName><xsl:value-of select="$name"/></shortName>
    <code/>
    <openingDate/>
    <closedDate/>
    <active>true</active>
    <comment/>
    <geoCode/>
    <xsl:apply-templates select="./child::node()/child::node()/gml:Polygon|./child::node()/child::node()/gml:MultiPolygon|./child::node()/child::node()/gml:Point"/>
    <lastUpdated/>
  </organisationUnit>
</xsl:template>

<xsl:template match="/">
<dxf xmlns="http://dhis2.org/schema/dxf/1.0" minorVersion="1.1">
<organisationUnits>
  <xsl:apply-templates select=".//gml:featureMember"/>
</organisationUnits>
</dxf>
</xsl:template>

</xsl:stylesheet>