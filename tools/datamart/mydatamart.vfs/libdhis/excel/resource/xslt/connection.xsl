<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xl="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" version="1.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 24, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>Update connection string in excel connections.xml</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:param name="connstring"/>
    
    <xsl:template match="@connection">
    <xsl:attribute name="connection">
        <xsl:value-of select="$connstring"/>
    </xsl:attribute>        
    </xsl:template>
    
    <!-- identity template -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
</xsl:stylesheet>
