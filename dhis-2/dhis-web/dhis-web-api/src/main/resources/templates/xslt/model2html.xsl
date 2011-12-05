<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
    <xsl:output method="html"/>
    
    <!-- html page level settings -->
    <xsl:include href="html-wrapper.xsl"/>
    
    <!-- for list views -->
    <xsl:include href="list.xsl"/>
    
    <!-- for rendering elements -->
    <xsl:include href="map.xsl"/>
    <xsl:include href="chart.xsl"/>
    <xsl:include href="dataElement.xsl"/>
    <xsl:include href="indicator.xsl"/>
    <xsl:include href="organisationUnit.xsl"/>
    <xsl:include href="dataSet.xsl"/>
    <!-- etc ... -->
        
</xsl:stylesheet>