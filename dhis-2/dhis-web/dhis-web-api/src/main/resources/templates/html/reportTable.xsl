<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >

  <xsl:template match="d:reportTable">
    <div class="reportTable">
      <h2> <xsl:value-of select="@name" /> </h2>
	  
	  <table>
		<tr>
			<td>Data</td>
			<td><a href="{@link}/data">html</a></td>
			<td><a href="{@link}/data.pdf">pdf</a></td>
			<td><a href="{@link}/data.xls">xls</a></td>
			<td><a href="{@link}/data.csv">csv</a></td>
		</tr>
	  </table><br/>
	  	  
      <table>
        <tr>
          <td>ID</td>
          <td> <xsl:value-of select="@id" /> </td>
        </tr>
        <tr>
          <td>Last Updated</td>
          <td> <xsl:value-of select="@lastUpdated" /> </td>
        </tr>
        <tr>
          <td>Code</td>
          <td> <xsl:value-of select="@code" /> </td>
        </tr>
        <tr>
          <td>DoIndicators</td>
          <td> <xsl:value-of select="d:doIndicators" /> </td>
        </tr>
        <tr>
          <td>DoPeriods</td>
          <td> <xsl:value-of select="d:doPeriods" /> </td>
        </tr>
        <tr>
          <td>DoUnits</td>
          <td> <xsl:value-of select="d:doUnits" /> </td>
        </tr>
        <tr>
          <td>Regression</td>
          <td> <xsl:value-of select="d:regression" /> </td>
        </tr>
        <tr>
          <td>SortOrder</td>
          <td> <xsl:value-of select="d:sortOrder" /> </td>
        </tr>
        <tr>
          <td>TopLimit</td>
          <td> <xsl:value-of select="d:topLimit" /> </td>
        </tr>
      </table>

      <xsl:apply-templates select="d:reportParams|d:indicators|d:dataElements|d:dataSets|d:categoryOptionCombos|d:organisationUnits" mode="short"/>
    </div>
  </xsl:template>

  <xsl:template match="d:reportParams" mode="short">
    <h3>ReportParams</h3>
    <table class="reportParams">
      <tr>
        <td>ParamGrandParentOrganisationUnit</td>
        <td> <xsl:value-of select="d:paramGrandParentOrganisationUnit" /> </td>
      </tr>
      <tr>
        <td>ParamOrganisationUnit</td>
        <td> <xsl:value-of select="d:paramOrganisationUnit" /> </td>
      </tr>
      <tr>
        <td>ParamParentOrganisationUnit</td>
        <td> <xsl:value-of select="d:paramParentOrganisationUnit" /> </td>
      </tr>
      <tr>
        <td>ParamReportingMonth</td>
        <td> <xsl:value-of select="d:paramReportingMonth" /> </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="d:reportTables" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>ReportTables</h3>
      <table class="reportTables">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
