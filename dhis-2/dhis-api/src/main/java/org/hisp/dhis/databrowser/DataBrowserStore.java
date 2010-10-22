package org.hisp.dhis.databrowser;

/*
 * Copyright (c) 2004-${year}, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.List;

/**
 * Contains methods for creating aggregated count queries for the Data Browser
 * module.
 * 
 * @author joakibj, martinwa, briane, eivinhb
 * 
 */
public interface DataBrowserStore
{
    String ID = DataBrowserStore.class.getName();

    // -------------------------------------------------------------------------
    // DataBrowser
    // -------------------------------------------------------------------------

    /**
     * Finds all DataSets connected to any period in betweenPeriodIds and does
     * an aggregated count.
     * 
     * @param betweenPeriodIds list of Period ids
     * @return the DataBrowserTable with structure for presentation
     */
    DataBrowserTable getDataSetsBetweenPeriods( List<Integer> betweenPeriodIds );

    /**
     * Finds all DataElementGroups connected to any period in betweenPeriodIds
     * and does an aggregated count.
     * 
     * @param betweenPeriodIds list of Period ids
     * @return the DataBrowserTable with structure for presentation
     */
    DataBrowserTable getDataElementGroupsBetweenPeriods( List<Integer> betweenPeriodIds );

    /**
     * Finds all OrganisationUnitGroups connected to any period in
     * betweenPeriodIds and does an aggregated count.
     * 
     * @param betweenPeriodIds list of Period ids
     * @return the DataBrowserTable with structure for presentation
     */
    DataBrowserTable getOrgUnitGroupsBetweenPeriods( List<Integer> betweenPeriodIds );

    /**
     * Always called first.
     * 
     * Sets the structure in DataBrowserTable for DataElements. Finds all
     * DataSets with DataValue in betweenPeriod List and given DataSetId. Then
     * calls on helpers internally to set it up.
     * 
     * @param table the DataBrowserTable to set the structure in
     * @param dataSetId the DataSet id
     * @param betweenPeriods list of Period ids
     */
    void setDataElementStructureForDataSetBetweenPeriods( DataBrowserTable table, Integer dataSetId,
        List<Integer> betweenPeriods );

    /**
     * Always called first.
     * 
     * Sets the structure in DataBrowserTable for DataElements. Finds all
     * DataElementGroups with DataValue in betweenPeriod List and given
     * DataElementGroupId. Then calls on helpers internally to set it up.
     * 
     * @param table the DataBrowserTable to set the structure in
     * @param dataElementGroupId the DataElementGroup id
     * @param betweenPeriods list of Period ids
     */
    void setDataElementStructureForDataElementGroupBetweenPeriods( DataBrowserTable table, Integer dataElementGroupId,
        List<Integer> betweenPeriods );

    /**
     * Always called first.
     * 
     * Sets the structure in DataBrowserTable for DataElementGroups. Finds all
     * OrganisationUnitGroups with DataValue in betweenPeriod List and given
     * OrganisationUnitGroup id. Then calls on helpers in DataBrowserTable to
     * set it up.
     * 
     * @param table the DataBrowserTable to set the structure in
     * @param orgUnitGroupId the OrganisationUnitGroup id
     * @param betweenPeriods lit of Period ids
     */
    void setDataElementGroupStructureForOrgUnitGroupBetweenPeriods( DataBrowserTable table, Integer orgUnitGroupId,
        List<Integer> betweenPeriods );

    /**
     * Always called first.
     * 
     * Sets the structure in DataBrowserTable for OrgUnits. Finds all
     * OrganisationUnits with DataValues in betweenPeriod List and given
     * OrganisationUnit parent id. Then calls on helpers in DataBrowserTable to
     * set it up.
     * 
     * @param table the DataBrowserTable to set the structure in
     * @param orgUnitParent the OrganisationUnit parent id
     * @param betweenPeriods list of Period ids
     */
    void setStructureForOrgUnitBetweenPeriods( DataBrowserTable table, Integer orgUnitParent,
        List<Integer> betweenPeriods );

    /**
     * Always called first.
     * 
     * Sets the structure in DataBrowserTable for DataElements. Finds all
     * OrganisationUnits with DataValue in betweenPeriod List and given
     * OrganisationUnit id. Then calls on helpers in DataBrowserTable to set it
     * up.
     * 
     * @param table the DataBrowserTable to set the structure in
     * @param orgUnitId the OrganisationUnit id
     * @param betweenPeriodIds List of Period ids
     */
    void setDataElementStructureForOrgUnitBetweenPeriods( DataBrowserTable table, Integer orgUnitId,
        List<Integer> betweenPeriodIds );

    /**
     * Sets DataElement count-Columns in DataBrowserTable for betweenPeriod List
     * connected to one DataSet.
     * 
     * @param table the DataBrowserTable to insert column into
     * @param dataSetId id of DataSet the DataElements are for
     * @param betweenPeriodIds list of Period ids
     * @return 0 if no results are found else number of rows inserted
     */
    Integer setCountDataElementsForDataSetBetweenPeriods( DataBrowserTable table, Integer dataSetId,
        List<Integer> betweenPeriodIds );

    /**
     * Sets DataElement count-Columns in DataBrowserTable for betweenPeriod List
     * connected to one DataElementGroup.
     * 
     * @param table the DataBrowserTable to insert column into
     * @param dataElementGroupId id of DataElementGroup the DataElements are for
     * @param betweenPeriodIds list of Period ids
     * @return 0 if no results are found else number of rows inserted
     */
    Integer setCountDataElementsForDataElementGroupBetweenPeriods( DataBrowserTable table, Integer dataElementGroupId,
        List<Integer> betweenPeriodIds );

    /**
     * Sets the DataElementGroup count-Columns in DataBrowserTable for
     * betweenPeriod List connected to one OrgUnitGroup.
     * 
     * @param table the DataBrowserTable to insert column into
     * @param orgUnitGroupId id of OrgUnitGroup the DataElementGroups are for
     * @param betweenPeriodIds list of Period ids
     * @return 0 if no results are found else number of rows inserted
     */
    Integer setCountDataElementGroupsForOrgUnitGroupBetweenPeriods( DataBrowserTable table, Integer orgUnitGroupId,
        List<Integer> betweenPeriodIds );

    /**
     * Sets OrgUnit count-Columns in DataBrowserTable for betweenPeriod List
     * connected to one OrganisationUnit parent.
     * 
     * @param table the DataBrowserTable to insert column into
     * @param orgUnitParent the OrganisationUnit parent id
     * @param betweenPeriodIds list of Period ids
     * @param maxLevel is the max level of the hierarchy
     * @return 0 if no results are found else number of rows inserted
     */
    Integer setCountOrgUnitsBetweenPeriods( DataBrowserTable table, Integer orgUnitParent,
        List<Integer> betweenPeriodIds, Integer maxLevel );

    /**
     * Sets DataElement count-Columns in DataBrowserTable for betweenPeriod List
     * connected to one OrgUnit.
     * 
     * @param table the DataBrowserTable to insert column into
     * @param orgUnitId id of OrganisationUnit the DataElements are for
     * @param betweenPeriodIds list of Period ids
     * @return 0 if no results are found else number of rows inserted
     */
    Integer setCountDataElementsForOrgUnitBetweenPeriods( DataBrowserTable table, Integer orgUnitId,
        List<Integer> betweenPeriodIds );

}
