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

import org.hisp.dhis.period.PeriodType;

/**
 * @author jonasaar
 * 
 */
public interface DataBrowserService
{
    String ID = DataBrowserService.class.getName();

    // -------------------------------------------------------------------------
    // DataBrowser
    // -------------------------------------------------------------------------

    /**
     * 
     * Method that uses the helper-method getAllPeriodIdsBetweenDatesOnPeriodType
     * to retrieve all the DataSets in the given period and returns a DataBrowserTable
     * with the data from the DAO-layer.
     * 
     * @param startDate
     * @param endDate
     * @param periodType
     */
    DataBrowserTable getCountDataSetsInPeriod( String startDate, String endDate, PeriodType periodType );

    /**
     * 
     * Method that uses the helper-method getAllPeriodIdsBetweenDatesOnPeriodType
     * to retrieve all the OrganisationUnits and then uses the DAO-layer to set 
     * up the structure for the table as well as getting the number of results
     * for each period. Returns a DataBrowserTable with the results.
     * 
     * in the given period and returns a 
     * DataBrowserTable with the data from the DAO-layer. 
     * 
     * @param startDate
     * @param endDate
     * @param periodType
     * @return
     */
    DataBrowserTable getCountOrgUnitsInPeriod( Integer orgUnitParent, String startDate, String endDate,
        PeriodType periodType );

    /**
     * Method that uses the helper-method getAllPeriodIdsBetweenDatesOnPeriodType
     * to retrieve all the DataElements and then uses the DAO-layer to set 
     * up the structure for the table as well as getting the number of results
     * for each period. Returns a DataBrowserTable with the results.

     * @param startDate
     * @param endDate
     * @param periodType
     * @return
     */
    DataBrowserTable getCountDataElementsInPeriod( Integer dataSetId, String startDate, String endDate,
        PeriodType periodType );

    /**
     * 
     * Method that calls getCountDataSetsInPeriod and adds a really old and a 
     * very new date to be sure that all periods are retrieved. 
     * 
     * @param periodType
     */
    DataBrowserTable getAllCountDataSetsByPeriodType( PeriodType periodType );

    /**
     * 
     * Method that calls getCountOrgUnitsInPeriod and adds a really old and a 
     * very new date to be sure that all periods are retrieved. 
     * 
     * @param orgUnitParent
     * @param periodType
     * @return
     */
    DataBrowserTable getAllCountOrgUnitsByPeriodType( Integer orgUnitParent, PeriodType periodType );

    /**
     * 
     * Method that calls getCountDataElementsInPeriod and adds a really old and a 
     * very new date to be sure that all periods are retrieved. 
     *       
     * @param dataSetId
     * @param periodType
     * @return
     */
    DataBrowserTable getAllCountDataElementsByPeriodType( Integer dataSetId, PeriodType periodType );

}
