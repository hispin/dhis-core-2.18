<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="dataElement">
    <div class="dataElement">
      <h2>
        <xsl:value-of select="@name" />
      </h2>

      <table border="1">
        <tr>
          <td>ID</td>
          <td>
            <xsl:value-of select="@id" />
          </td>
        </tr>
        <tr>
          <td>Last Updated</td>
          <td>
            <xsl:value-of select="@lastUpdated" />
          </td>
        </tr>
        <tr>
          <td>Short Name</td>
          <td>
            <xsl:value-of select="@shortName" />
          </td>
        </tr>
        <tr>
          <td>Type</td>
          <td>
            <xsl:value-of select="type" />
          </td>
        </tr>
        <tr>
          <td>Zero is Significant</td>
          <td>
            <xsl:value-of select="zeroIsSignificant" />
          </td>
        </tr>
        <tr>
          <td>Sort Order</td>
          <td>
            <xsl:value-of select="sortOrder" />
          </td>
        </tr>
        <tr>
          <td>Active</td>
          <td>
            <xsl:value-of select="active" />
          </td>
        </tr>
        <tr>
          <td>Aggregation Operator</td>
          <td>
            <xsl:value-of select="aggregationOperator" />
          </td>
        </tr>
        <tr>
          <td>Domain Type</td>
          <td>
            <xsl:value-of select="domainType" />
          </td>
        </tr>
      </table>

      <xsl:apply-templates select="categoryCombo|groups|dataSets" />
    </div>
  </xsl:template>

  <xsl:template match="categoryCombo">
    <h3>DataElementCategoryCombo</h3>
    <table border="1" class="categoryCombo">
      <xsl:apply-templates select="child::*" mode="row"/>
    </table>
  </xsl:template>

  <xsl:template match="groups">
    <xsl:if test="count(child::*) > 0">
      <h3>DataElementGroups</h3>
      <table border="1" class="dataElementGroups">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="dataSets">
    <xsl:if test="count(child::*) > 0">
      <h3>DataSets</h3>
      <table border="1" class="dataSets">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
