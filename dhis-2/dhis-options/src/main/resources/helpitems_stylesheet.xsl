<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="chapter">
  <h3><xsl:value-of select="title"/></h3>
  <ul><xsl:apply-templates select="descendant::section[@id]"/></ul>
</xsl:template>

<xsl:template match="section">
  <li><a href="getHelpContent('{@id}')"><xsl:value-of select="title"/></a></li>
</xsl:template>

<xsl:template match="/">
  <xsl:apply-templates select="book/chapter"/>
</xsl:template>

</xsl:stylesheet>
