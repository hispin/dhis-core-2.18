<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mfl="http://ehealth.or.ke/schema" xmlns:dxf2="http://dhis2.org/schema/dxf/2.0">

  <xsl:template match="mfl:Facilities">

    <xsl:element name="dxf2:metaData">
      <xsl:element name="dxf2:organisationUnits">
        <xsl:apply-templates />
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template match="mfl:Facility">
    <xsl:element name="dxf2:organisationUnit">
      <xsl:attribute name="name">
        <xsl:value-of select="mfl:OfficialName" />
      </xsl:attribute>
      <xsl:attribute name="shortName">
        <xsl:value-of select="substring(mfl:OfficialName, 0, 49)" />
      </xsl:attribute>
      <xsl:attribute name="code">
        <xsl:value-of select="mfl:Code" />
      </xsl:attribute>

      <xsl:element name="dxf2:active">
        <xsl:value-of select="mfl:Active" />
      </xsl:element>

      <xsl:if test="mfl:Latitude and mfl:Longitude">
        <xsl:element name="dxf2:featureType">
          <xsl:text>Point</xsl:text>
        </xsl:element>

        <xsl:element name="dxf2:coordinates">
          <xsl:text>[</xsl:text>
          <xsl:value-of select="mfl:Longitude" />
          <xsl:text>,</xsl:text>
          <xsl:value-of select="mfl:Latitude" />
          <xsl:text>]</xsl:text>
        </xsl:element>
      </xsl:if>

      <xsl:if test="string-length(mfl:OfficialEmail) > 0">
        <xsl:element name="dxf2:email">
          <xsl:value-of select="mfl:OfficialEmail" />
        </xsl:element>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="string-length(mfl:OfficialLandline) > 0">
          <xsl:element name="dxf2:phoneNumber">
            <xsl:value-of select="mfl:OfficalLandline" />
          </xsl:element>
        </xsl:when>
        <xsl:when test="string-length(mfl:OfficialMobile) > 0">
          <xsl:element name="dxf2:phoneNumber">
            <xsl:value-of select="mfl:OfficialMobile" />
          </xsl:element>
        </xsl:when>
      </xsl:choose>

      <xsl:if test="string-length(mfl:AddressBox) > 0 and string-length(mfl:AddressTown) > 0 and string-length(mfl:AddressPostCode) > 0">
        <xsl:element name="dxf2:address">
          <xsl:value-of select="mfl:AddressBox" />
          <xsl:text>, </xsl:text>
          <xsl:value-of select="mfl:AddressPostCode" />
          <xsl:text> </xsl:text>
          <xsl:value-of select="mfl:AddressTown" />
        </xsl:element>
      </xsl:if>

    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
