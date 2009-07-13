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
 * @author joakibj, martinwa
 * 
 */
public interface DataBrowserStore
{

    String ID = DataBrowserStore.class.getName();

    // -------------------------------------------------------------------------
    // DataBrowser
    // -------------------------------------------------------------------------

    /**
     * 
     * Always called first.
     * 
     * Sets the structure in DataBrowserTable for Orgunits. Find all
     * OrganisationUnits with DataValues in betweenPeriod List and given
     * OrganisationUnit parent id. Then call on helpers in DataBrowserTable to
     * set it up.
     * 
     * @param table the DataBrowserTable to set the structure in
     * @param orgUnitParent the OrgUnit parent
     * @param betweenPeriods list of Period ids
     */
    void setStructureForOrgUnitBetweenPeriods( DataBrowserTable table, Integer orgUnitParent,
        List<Integer> betweenPeriods );

    /**
     * 
     * Always called first.
     * 
     * Sets the structure in DataBrowserTable for DataElements. Find all
     * DataSets with DataValue in betweenPeriod List and given DataSetId parent
     * id. Then call on helpers internally to set it up.
     * 
     * @param table the DataBrowserTable to set the structure in
     * @param dataSetId the parent DataSet id
     * @param betweenPeriods list of Period ids
     */
    void setDataElementStructureForDataSetBetweenPeriods( DataBrowserTable table, Integer dataSetId,
        List<Integer> betweenPeriods );

    /**
     * 
     * Finds all DataSets connected to any period in betweenPeriodIds and does
     * an aggregated count.
     * 
     * @param betweenPeriodIds list of Period ids
     * @return the DataBrowserTable with structure for presentation
     */
    DataBrowserTable getDataSetsInPeriod( List<Integer> betweenPeriodIds );

    /**
     * 
     * Sets DataElement count-Columns in DataBrowserTable for one Period id
     * connected to one DataSet.
     * 
     * @param table the DataBrowserTable to insert column into
     * @param dataSetId id of DataSet the DataElements are for
     * @param periodId the Period id
     * @return 0 if no results are found else number of rows inserted
     */
    Integer setCountDataElementsInOnePeriod( DataBrowserTable table, Integer dataSetId, Integer periodId );

    /**
     * 
     * Sets OrgUnit count-Columns in DataBrowserTable for one Period id
     * connected to one OrgUnit parent.
     * 
     * @param table the DataBrowserTable to insert column into
     * @param orgUnitParent the OrgUnit parent
     * @param periodId the Period id
     * @return 0 if no results are found else number of rows inserted
     */
    Integer setCountOrgUnitsInOnePeriod( DataBrowserTable table, Integer orgUnitParent, Integer periodId );

}
