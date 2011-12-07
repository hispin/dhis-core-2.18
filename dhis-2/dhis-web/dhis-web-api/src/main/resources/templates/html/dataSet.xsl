<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >

  <xsl:template match="d:dataSet">
    <div class="dataSet">
      <h2>
        <xsl:value-of select="@name" />
      </h2>
      <table border="1">
        <tr>
          <td>ID</td>
          <td> <xsl:value-of select="@id" /> </td>
        </tr>
        <tr>
          <td>Last Updated</td>
          <td> <xsl:value-of select="@lastUpdated" /> </td>
        </tr>
        <tr>
          <td>Short Name</td>
          <td> <xsl:value-of select="d:shortName" /> </td>
        </tr>
        <tr>
          <td>Version</td>
          <td> <xsl:value-of select="d:version" /> </td>
        </tr>
        <tr>
          <td>Mobile</td>
          <td> <xsl:value-of select="d:mobile" /> </td>
        </tr>

      </table>

      <xsl:apply-templates select="d:dataElements|d:indicators|d:sources" />

    </div>
  </xsl:template>

  <xsl:template match="d:dataElements">
    <xsl:if test="count(child::*) > 0">
      <h3>DataElements</h3>
      <table border="1" class="dataElements">
          <xsl:apply-templates mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="d:indicators">
    <xsl:if test="count(child::*) > 0">
      <h3>Indicators</h3>
      <table border="1" class="indicator">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="d:sources">
    <xsl:if test="count(child::*) > 0">
      <h3>OrganisationUnits</h3>
      <table border="1" class="sources">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
