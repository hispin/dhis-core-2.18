<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:header">
	<th> <xsl:value-of select="@name"/> </th>
  </xsl:template>
  
  <xsl:template match="d:rows">
	<xsl:apply-templates select="d:row"/>
  </xsl:template>
  
  <xsl:template match="d:row">
	<tr>
	  <xsl:for-each select="d:rowData">
		<td> <xsl:value-of select="."/> </td>
	  </xsl:for-each>
	</tr>
  </xsl:template>
	
  <xsl:template match="d:grid">
    <div class="grid">
	  <h2>
        <xsl:value-of select="d:title"/>
      </h2>
	  <h4>
		<xsl:value-of select="d:subtitle"/>
	  </h4>
	  <table>
		<thead>
		  <tr>
			<xsl:apply-templates select="d:headers"/>
		  </tr>
		</thead>
		<tbody>
		  <xsl:apply-templates select="d:rows"/>
		</tbody>
	  </table>	  
	</div>
  </xsl:template>

</xsl:stylesheet>
