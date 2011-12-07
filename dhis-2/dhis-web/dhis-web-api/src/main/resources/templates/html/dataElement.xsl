<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >

  <xsl:template match="d:dataElement">
    <div class="dataElement">
      <h2> <xsl:value-of select="@name" /> </h2>

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
          <td> <xsl:value-of select="@shortName" /> </td>
        </tr>
        <tr>
          <td>Type</td>
          <td> <xsl:value-of select="d:type" /> </td>
        </tr>
        <tr>
          <td>Zero is Significant</td>
          <td> <xsl:value-of select="d:zeroIsSignificant" /> </td>
        </tr>
        <tr>
          <td>Sort Order</td>
          <td> <xsl:value-of select="d:sortOrder" /> </td>
        </tr>
        <tr>
          <td>Active</td>
          <td> <xsl:value-of select="d:active" /> </td>
        </tr>
        <tr>
          <td>Aggregation Operator</td>
          <td> <xsl:value-of select="d:aggregationOperator" /> </td>
        </tr>
        <tr>
          <td>Domain Type</td>
          <td> <xsl:value-of select="d:domainType" /> </td>
        </tr>
      </table>
      <xsl:apply-templates select="d:categoryCombo|d:dataElementGroups|d:dataSets"/>
    </div>
  </xsl:template>

  <xsl:template match="d:categoryCombo">
    <h3>CategoryCombo</h3>
    <table border="1" class="categoryCombo">
      <xsl:apply-templates select="child::*" mode="row"/>
    </table>
  </xsl:template>

  <xsl:template match="d:dataElementGroups">
    <xsl:if test="count(child::*) > 0">
      <h3>DataElementGroups</h3>
      <table border="1" class="dataElementGroups">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="d:dataSets">
    <xsl:if test="count(child::*) > 0">
      <h3>DataSets</h3>
      <table border="1" class="dataSets">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
