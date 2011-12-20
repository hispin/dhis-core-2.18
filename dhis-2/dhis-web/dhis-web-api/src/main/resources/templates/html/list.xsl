<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:include href="identifiable-row.xsl"/>

  <!-- match all plural elements -->
  <xsl:template match="d:resources|d:maps|d:charts|d:categoryCombos|d:categories|
    d:categoryOptions|d:categoryOptionCombos|d:dataElements|d:indicators|
    d:organisationUnits|d:dataElementGroups|d:dataElementGroupSets|d:dataSets|
    d:documents|d:indicatorGroups|d:indicatorGroupSets|d:organisationUnitGroups|
    d:organisationUnitGroupSets|d:indicatorTypes|d:attributeTypes|d:reports|d:constants|
    d:sqlViews|d:validationRules|d:validationRuleGroups|d:users|d:reportTables">

    <xsl:choose>
      <xsl:when test="@page">
        <table>
          <tr>
            <td>Page <xsl:value-of select="@page" /> / <xsl:value-of select="@pageCount" /></td>
          </tr>
        </table>
      </xsl:when>
      <xsl:otherwise>
        <table>
          <tr>
            <td>Page 1 / 1</td>
          </tr>
        </table>
      </xsl:otherwise>
    </xsl:choose>

    <h3> <xsl:value-of select="local-name()" /> </h3>

    <table>
      <xsl:apply-templates select="child::*" mode="row"/>
    </table>
  </xsl:template>

</xsl:stylesheet>
