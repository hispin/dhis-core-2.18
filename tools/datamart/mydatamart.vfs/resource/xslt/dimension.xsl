<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:dxf="http://dhis2.org/schema/dxf/1.0"
    xmlns:dhis="http://dhis2.org/funcs" xmlns:func="http://exslt.org/functions"
    xmlns:str="http://exslt.org/strings" extension-element-prefixes="func" version="1.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Mar 18, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>Teases out dimensons from categories and groupsets</xd:p>
        </xd:desc>
    </xd:doc>

    <!--    For each category - add column to categorystructure and add a dimension-->
    <xsl:template match="dxf:categories/dxf:category" mode="dimension">
        <xsl:variable name="catname" select="dxf:name"/>
        <xsl:variable name="garbled_catname" select="dhis:dbencode($catname)"/>
        INSERT INTO dimension (name,dimtype,dimcolumn,display,concept) VALUES ('<xsl:value-of 
            select="dhis:dbescape(dxf:name)"/>',1,'<xsl:value-of
                select="$garbled_catname"/>','<xsl:value-of select="$garbled_catname"/>','<xsl:value-of
                    select="dxf:conceptid"/>'); 
        ALTER TABLE _categorystructure ADD COLUMN <xsl:value-of
            select="$garbled_catname"/> CHARACTER VARYING(250); 
    </xsl:template>
    
    <!--    For each orgunit groupset - add column to orgunit groupsetstructure and add a dimension-->
    <xsl:template match="dxf:groupSets/dxf:groupSet" mode="dimension">
        <xsl:variable name="gsname" select="dxf:name"/>
        <xsl:variable name="garbled_gsname" select="dhis:dbencode($gsname)"/>
        INSERT INTO dimension (name,dimtype,dimcolumn,display,concept) values('<xsl:value-of 
            select="dhis:dbescape($gsname)"/>',4,'<xsl:value-of select="$garbled_gsname"/>','<xsl:value-of select="$garbled_gsname"/>',NULL);
        ALTER TABLE _organisationunitgroupsetstructure ADD COLUMN <xsl:value-of select="$garbled_gsname"/> CHARACTER VARYING(250);
    </xsl:template>
    
    <!--    For each de groupset - add column to de groupsetstructure and add a dimension-->
    <xsl:template match="dxf:dataElementGroupSets/dxf:dataElementGroupSet" mode="dimension">
        <xsl:variable name="gsname" select="dxf:name"/>
        <xsl:variable name="garbled_gsname" select="dhis:dbencode($gsname)"/>
        INSERT INTO dimension (name,dimtype,dimcolumn,display,concept) VALUES ('<xsl:value-of
            select="dhis:dbescape($gsname)"/>',2,'<xsl:value-of select="$garbled_gsname"/>','<xsl:value-of
                select="$garbled_gsname"/>',NULL); 
        ALTER TABLE _dataelementgroupsetstructure ADD COLUMN
        <xsl:value-of select="$garbled_gsname"/> CHARACTER VARYING(250); 
    </xsl:template>
    
    <!--    For each indicator groupset - add column to indicator groupsetstructure and add a dimension-->
    <xsl:template match="dxf:indicatorGroupSets/dxf:indicatorGroupSet" mode="dimension">
        <xsl:variable name="gsname" select="dxf:name"/>
        <xsl:variable name="garbled_gsname" select="dhis:dbencode($gsname)"/>
        INSERT INTO dimension (name,dimtype,dimcolumn,display,concept) values <xsl:text>
</xsl:text>('<xsl:value-of select="dhis:dbescape($gsname)"/>',3,'<xsl:value-of select="$garbled_gsname"/>','<xsl:value-of select="$garbled_gsname"/>',NULL);
        ALTER TABLE _indicatorgroupsetstructure ADD COLUMN <xsl:value-of select="$garbled_gsname"/> CHARACTER VARYING(250);
    </xsl:template>
    
</xsl:stylesheet>
