<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="sectionId">learningObjectives</xsl:param>

<xsl:template match="title">
  <h3><xsl:value-of select="."/></h3>
</xsl:template>

<xsl:template match="para">
  <p><xsl:value-of select="."/></p>
</xsl:template>

<xsl:template match="orderedlist">
  <ol>
  <xsl:for-each select="listitem">
    <li><xsl:value-of select="para"/></li>
  </xsl:for-each>
  </ol>
</xsl:template>

<xsl:template match="itemizedlist">
  <ul>
  <xsl:for-each select="listitem">
    <li><xsl:value-of select="para"/></li>
  </xsl:for-each>
  </ul>
</xsl:template>

<xsl:template match="/">
  <xsl:apply-templates select="book/chapter/section[@id=$sectionId]"/>
</xsl:template>

</xsl:stylesheet>
