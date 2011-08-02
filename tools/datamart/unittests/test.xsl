<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" version="1.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Apr 27, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:output indent="no" method="text"/>
    <xsl:param name="alternativename"/>
    
    <xsl:template match="/">
        <xsl:message>A message</xsl:message>
        <xsl:apply-templates select="/head/body"/>
    </xsl:template>
    
    <xsl:template match="body">
        <xsl:choose>
            <xsl:when test="string-length($alternativename)>0">
                <xsl:message><xsl:value-of select="$alternativename"/></xsl:message>
                <xsl:value-of select="$alternativename"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="@name"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
