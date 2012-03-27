<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
                version="1.0">

  <xsl:template match="d:chart">
    <div class="chart">
      <h2> <xsl:value-of select="@d:name"/> </h2>
	  
	  <table>
		<tr>
			<td>Resource Data</td>
			<td><a href="{@d:link}/data">png</a></td>
		</tr>
		<tr>
			<td>width</td>
			<td>width in px (opt)</td>
		</tr>
		<tr>
			<td>height</td>
			<td>height in px (opt)</td>
		</tr>
	  </table><br/>
	  
	  <table>
		<tr>
			<td>Dynamic Data</td>
			<td><a href="data">png</a></td>
		</tr>
		<tr>
			<td>in</td>
			<td>indicator uid (req)</td>
		</tr>
		<tr>
			<td>ou</td>
			<td>organisation unit uid (req)</td>
		</tr>
		<tr>
			<td>periods</td>
			<td>use periods or org units (opt)</td>
		</tr>
		<tr>
			<td>width</td>
			<td>width in px (opt)</td>
		</tr>
		<tr>
			<td>height</td>
			<td>height in px (opt)</td>
		</tr>
		<tr>
			<td>skipTitle</td>
			<td>skip title (opt)</td>
		</tr>
	  </table><br/>
	  
      <a href="{@d:link}/data"><img src="{@d:link}/data" style="border-style:solid; border-width: 1px; padding: 5px;" /></a>

      <h3>Details</h3>

      <table>
        <tr>
          <td>ID</td>
          <td> <xsl:value-of select="@d:id"/> </td>
        </tr>
        <tr>
          <td>Last Updated</td>
          <td> <xsl:value-of select="@d:lastUpdated"/> </td>
        </tr>
        <tr>
          <td>Code</td>
          <td> <xsl:value-of select="@d:code" /> </td>
        </tr>
        <tr>
          <td>Dimension</td>
          <td> <xsl:value-of select="d:dimension"/> </td>
        </tr>
        <tr>
          <td>Hide legend</td>
          <td> <xsl:value-of select="d:hideLegend"/> </td>
        </tr>
        <tr>
          <td>Hide subtitle</td>
          <td> <xsl:value-of select="d:hideSubtitle"/> </td>
        </tr>
        <tr>
          <td>Horizontal Pilot  Orientation</td>
          <td> <xsl:value-of select="d:horizontalPlotOrientation"/> </td>
        </tr>
        <tr>
          <td>Regression</td>
          <td> <xsl:value-of select="d:regression"/> </td>
        </tr>
        <tr>
          <td>Size</td>
          <td> <xsl:value-of select="d:size"/> </td>
        </tr>
        <tr>
          <td>Target line</td>
          <td> <xsl:value-of select="d:targetLine"/> </td>
        </tr>
        <tr>
          <td>Target line label</td>
          <td> <xsl:value-of select="d:targetLineLabel"/> </td>
        </tr>
        <tr>
          <td>Type</td>
          <td> <xsl:value-of select="d:type"/> </td>
        </tr>
        <tr>
          <td>User organisation unit</td>
          <td> <xsl:value-of select="d:userOrganisationUnit"/> </td>
        </tr>
        <tr>
          <td>Vertical labels</td>
          <td> <xsl:value-of select="d:verticalLabels"/> </td>
        </tr>
      </table>

      <xsl:apply-templates select="d:organisationUnits|d:dataElements|d:indicators" mode="short"/>

    </div>
  </xsl:template>

</xsl:stylesheet>
