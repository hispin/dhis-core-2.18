<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" 
    xmlns:dxf="http://dhis2.org/schema/dxf/1.0"
    xmlns:dhis="http://dhis2.org/funcs" 
    xmlns:func="http://exslt.org/functions"
    xmlns:str="http://exslt.org/strings" extension-element-prefixes="func" version="1.0">
    
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 20, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>Translates DXF Metadata Export stream into SQL</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:output method="text" indent="no"/>

    <xsl:include href="./functions.xsl"/>
    <xsl:include href="./dimension.xsl"/>
    <xsl:include href="./category.xsl"/>
    <xsl:include href="./orgunit.xsl"/>
    <xsl:include href="./dataelement.xsl"/>
    <xsl:include href="./indicator.xsl"/>
    
    <xsl:template match="/">
        BEGIN TRANSACTION; 
        <!-- fixing previous error -->
        DROP TABLE IF EXISTS _orgunitgroupsetstructure;
        <!-- create 'stubs' for structure tables -->
        DROP TABLE IF EXISTS _categorystructure; 
        CREATE TABLE _categorystructure ( categoryoptioncomboid INTEGER PRIMARY KEY);
        DROP TABLE IF EXISTS _dataelementgroupsetstructure;
        CREATE TABLE _dataelementgroupsetstructure (dataelementid INTEGER PRIMARY KEY);
        DROP TABLE IF EXISTS _indicatorgroupsetstructure;
        CREATE TABLE _indicatorgroupsetstructure (indicatorid INTEGER PRIMARY KEY);
        DROP TABLE IF EXISTS _organisationunitgroupsetstructure;
        CREATE TABLE _organisationunitgroupsetstructure (organisationunitid INTEGER PRIMARY KEY);

        DELETE FROM dimension;        
        <!-- the 'dimension' templates alter tables so must be processed first --> 
        <xsl:apply-templates mode="dimension"/>
        <!-- populate the tables -->
        <xsl:apply-templates />
        
        <xsl:message>Done</xsl:message>

        END TRANSACTION; 
    </xsl:template>
    
    <!-- default action is to do nothing on a node -->
    <xsl:template match="@*|node()">
        <xsl:apply-templates select="@*|node()"/>
    </xsl:template>
    
    <!-- default action is to do nothing on a node -->
    <xsl:template match="@*|node()" mode="dimension">
        <xsl:apply-templates select="@*|node()" mode="dimension"/>
    </xsl:template>
    
</xsl:stylesheet>
