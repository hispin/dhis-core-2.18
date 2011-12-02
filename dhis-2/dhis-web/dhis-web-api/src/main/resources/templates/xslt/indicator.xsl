<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:template match="indicator">
    <div class="indicator">
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
            <xsl:value-of select="shortName" />
          </td>
        </tr>
        <tr>
          <td>Denominator</td>
          <td>
            <xsl:value-of select="denominator" />
          </td>
        </tr>
        <tr>
          <td>Denominator Description</td>
          <td>
            <xsl:value-of select="denominatorDescription" />
          </td>
        </tr>
        <tr>
          <td>Numerator</td>
          <td>
            <xsl:value-of select="numerator" />
          </td>
        </tr>
        <tr>
          <td>Numerator Description</td>
          <td>
            <xsl:value-of select="numeratorDescription" />
          </td>
        </tr>
        <tr>
          <td>Annualized</td>
          <td>
            <xsl:value-of select="annualized" />
          </td>
        </tr>
        <tr>
          <td>Sort Order</td>
          <td>
            <xsl:value-of select="sortOrder" />
          </td>
        </tr>

      </table>

      <xsl:apply-templates select="groups|dataSets" />

    </div>
  </xsl:template>

  <xsl:template match="groups">
    <xsl:if test="count(child::*) > 0">
      <h3>Indicator Groups</h3>
      <table border="1" class="indicatorGroups">
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
