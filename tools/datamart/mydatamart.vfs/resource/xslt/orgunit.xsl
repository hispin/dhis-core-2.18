<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:dxf="http://dhis2.org/schema/dxf/1.0"
    xmlns:dhis="http://dhis2.org/funcs" 
    xmlns:func="http://exslt.org/functions"
    xmlns:set="http://exslt.org/sets"
    xmlns:str="http://exslt.org/strings" extension-element-prefixes="func" version="1.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Mar 18, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>processes orgunits</xd:p>
        </xd:desc>
    </xd:doc>    

    <xsl:key name="ouid_by_ougroup" 
        match="/dxf:dxf/dxf:organisationUnitGroupMembers/dxf:organisationUnitGroupMember/dxf:organisationUnit"
        use="../dxf:organisationUnitGroup" />    
    <xsl:key name="ougroupsetid_by_ougroup"
        match="/dxf:dxf/dxf:groupSetMembers/dxf:groupSetMember/dxf:groupSet"
        use="../dxf:organisationUnitGroup"/>
    
    <xsl:key name="orgunitrel"
        match="/dxf:dxf/dxf:organisationUnitRelationships/dxf:organisationUnitRelationship"
        use="dxf:child"/>
    
<!--    
    For each orgunit:
    1.  insert entry in orgunit structure table
    2.  insert entry in orgunit table
    3.  insert entry in orgunit groupset structure table
-->
    <xsl:template match="dxf:organisationUnits/dxf:organisationUnit">
        <!--
            Populate orgunint structure table
        -->
        <xsl:variable name="parent1" select="key('orgunitrel',dxf:id)/dxf:parent"/>
        <xsl:variable name="parent2" select="key('orgunitrel',$parent1)/dxf:parent"/>
        <xsl:variable name="parent3" select="key('orgunitrel',$parent2)/dxf:parent"/>
        <xsl:variable name="parent4" select="key('orgunitrel',$parent3)/dxf:parent"/>
        <xsl:variable name="parent5" select="key('orgunitrel',$parent4)/dxf:parent"/>
        <xsl:variable name="parent6" select="key('orgunitrel',$parent5)/dxf:parent"/>
        <xsl:variable name="parent7" select="key('orgunitrel',$parent6)/dxf:parent"/>
        <xsl:variable name="parent8" select="key('orgunitrel',$parent7)/dxf:parent"/>
        <xsl:choose>
            <xsl:when test="count($parent1)=0">INSERT OR REPLACE INTO _orgunitstructure VALUES (<xsl:value-of select="concat(dxf:id,',',1,',',dxf:id)"
                />,NULL,NULL,NULL,NULL,NULL,NULL,NULL);</xsl:when>
            <xsl:when test="count($parent2)=0">INSERT OR REPLACE INTO _orgunitstructure VALUES (<xsl:value-of select="concat(dxf:id,',',2,',',$parent1,',',dxf:id)"
                />,NULL,NULL,NULL,NULL,NULL,NULL);</xsl:when>
            <xsl:when test="count($parent3)=0">INSERT OR REPLACE INTO _orgunitstructure VALUES (<xsl:value-of
                    select="concat(dxf:id,',',3,',',$parent2,',',$parent1,',',dxf:id)"
                />,NULL,NULL,NULL,NULL,NULL);</xsl:when>
            <xsl:when test="count($parent4)=0">INSERT OR REPLACE INTO _orgunitstructure VALUES (<xsl:value-of
                    select="concat(dxf:id,',',4,',',$parent3,',',$parent2,',',$parent1,',',dxf:id)"
                />,NULL,NULL,NULL,NULL);</xsl:when>
            <xsl:when test="count($parent5)=0">INSERT OR REPLACE INTO _orgunitstructure VALUES (<xsl:value-of
                    select="concat(dxf:id,',',5,',',$parent4,',',$parent3,',',$parent2,',',$parent1,',',dxf:id)"
                />,NULL,NULL,NULL);</xsl:when>
            <xsl:when test="count($parent6)=0">INSERT OR REPLACE INTO _orgunitstructure VALUES (<xsl:value-of
                    select="concat(dxf:id,',',6,',',$parent5,',',$parent4,',',$parent3,',',$parent2,',',$parent1,',',dxf:id)"
                />,NULL,NULL);</xsl:when>
            <xsl:when test="count($parent7)=0">INSERT OR REPLACE INTO _orgunitstructure VALUES (<xsl:value-of
                    select="concat(dxf:id,',',7,',',$parent6,',',$parent5,',',$parent4,',',$parent3,',',$parent2,',',$parent1,',',dxf:id)"
                />,NULL);</xsl:when>
            <xsl:when test="count($parent8)=0">INSERT OR REPLACE INTO _orgunitstructure VALUES (<xsl:value-of
                    select="concat(dxf:id,',',8,',',$parent7,',',$parent6,',',$parent5,',',$parent4,',',$parent3,',',$parent2,',',$parent1,',',dxf:id)"
                />);</xsl:when>
        </xsl:choose>
        <xsl:text>
        </xsl:text>
        <!--
        Populate orgunint table
        -->
        <xsl:text/>INSERT OR REPLACE INTO organisationunit (organisationunitid, name, shortname) <xsl:text/>
        <xsl:text/>VALUES (<xsl:value-of select="dxf:id"/>, '<xsl:value-of
            select="dhis:dbescape(dxf:name)"/>', '<xsl:value-of
            select="dhis:dbescape(dxf:shortName)"/>');<xsl:text/>
        <xsl:text>
        </xsl:text>

        <!--
        Populate orgunint groupset table
        -->
        <xsl:variable name="groupsets" select="/dxf:dxf/dxf:groupSets/dxf:groupSet"/>
        <xsl:variable name="allgroups" select="/dxf:dxf/dxf:organisationUnitGroups"/>

        <xsl:variable name="fields">
            <xsl:text/>organisationunitid,<xsl:text/>
            <xsl:for-each select="$groupsets">
                <xsl:variable name="groupsetname"
                    select="./dxf:name"/>
                <xsl:text/><xsl:value-of select="dhis:dbencode($groupsetname)"/>,<xsl:text/>
            </xsl:for-each>
        </xsl:variable>
        
        <xsl:variable name="ouid" select="dxf:id"/>
        <!-- this is the set of groups which an orgunit is part of -->
        <xsl:variable name="ougroups"
            select="/dxf:dxf/dxf:organisationUnitGroups/dxf:organisationUnitGroup[key('ouid_by_ougroup',dxf:id)=$ouid]"/>
        <xsl:variable name="values">
            <xsl:value-of select="dxf:id"/>,<xsl:text/>
            <xsl:for-each select="$groupsets">
            <xsl:variable name="groupsetid" select="dxf:id"/>
            <!-- this is the set of groups in a groupset -->
            <xsl:variable name="groupsetgroups"
                select="/dxf:dxf/dxf:organisationUnitGroups/dxf:organisationUnitGroup[key('ougroupsetid_by_ougroup',dxf:id)=$groupsetid]"/>
            <!-- the intersection represents the group(s) from the groupset which orgunit is part of -->
            <!-- [1] ensures that only the first is returned if there are multiple nodes matching -->
            <xsl:variable name="group" select="set:intersection($ougroups,$groupsetgroups)[1]"/>
            <xsl:choose>
                <xsl:when test="count($group)=1">'<xsl:value-of select="$group/dxf:name"/>',</xsl:when>
                <xsl:otherwise>NULL,</xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="dhis:insert('_organisationunitgroupsetstructure',$fields,$values)"/>
        <xsl:text>
        </xsl:text>
        
    </xsl:template>
    
    <!--
        Populate orgunintlevel table
    -->
    <xsl:template match="dxf:organisationUnitLevel">
        <xsl:text/>INSERT OR REPLACE INTO orgunitlevel (orgunitlevelid, level, name) <xsl:text/>
        <xsl:text/>VALUES (<xsl:value-of select="dxf:id"/>, <xsl:value-of select="dxf:level"/>,
        '<xsl:value-of select="dhis:dbescape(dxf:name)"/>');<xsl:text/>
    </xsl:template>
    
</xsl:stylesheet>
