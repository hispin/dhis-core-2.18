package org.hisp.dhis.reporttable;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.period.Period;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public interface ReportTableService
{
    String ID = ReportTableService.class.getName();

    /**
     * Generates and sets report parameters for reporting period, parent organisation 
     * unit and organisation unit. Delegates to <code>createReportTable( ReportTable, boolean )</code> 
     * to generate the table.
     * 
     * @param id the identifier.
     * @param mode the mode, can be <l>dataelements</i>, <i>indicators</i>, and <i>datasets</i>.
     * @param reportingPeriod the number of months back in time which will be used
     *        as basis for the generation of relative periods.
     * @param parentOrganisationUnitId the identifier of the parent organisation
     *        unit of the report parameter.
     * @param organisationUnitId the identifier of the organisation unit of the
     *        report parameter.
     * @param format the I18nFormat to use.
     */
    void createReportTables( int id, String mode, Integer reportingPeriod, 
        Integer parentOrganisationUnitId, Integer organisationUnitId, I18nFormat format );
    
    /**
     * Creates a report table. Exports the relevant data to data mart, updates
     * the existing database table name, add potential regression columns and data
     * and generates the table. 
     * 
     * @param reportTable the ReportTable to create.
     * @param doDataMart indicators whether to perform datamart before processing.
     */
    void createReportTable( ReportTable reportTable, boolean doDataMart );
    
    /**
     * Removes a ReportTable.
     * 
     * @param reportTable the ReportTable to remove.
     */
    void removeReportTable( ReportTable reportTable );
    
    /**
     * Retrieves a List of Periods of the RelativePeriodType.
     * 
     * @param relatives the RelativePeriods.
     * @param months the number of months back in time which will be used as
     *        basis for the generation of relative Periods.
     *        
     * @return a Collection of Periods of the RelativePeriodType.
     */
    List<Period> getRelativePeriods( RelativePeriods relatives, int months );
    
    /**
     * Returns the current date subtracted with the given number of months.
     * 
     * @param months the numner of months.
     * @return a Date.
     */
    Date getDateFromPreviousMonth( int months );
    
    /**
     * Saves a ReportTable.
     * 
     * @param reportTable the ReportTable to save.
     * @return the generated identifier.
     */
    int saveReportTable( ReportTable reportTable );
    
    /**
     * Updates a ReportTable.
     * 
     * @param reportTable the ReportTable to update.
     */
    void updateReportTable( ReportTable reportTable );
    
    /**
     * Deletes a ReportTable.
     * 
     * @param reportTable the ReportTable to delete.
     */
    void deleteReportTable( ReportTable reportTable );
    
    /**
     * Retrieves the ReportTable with the given identifier.
     * 
     * @param id the identifier of the ReportTable to retrieve.
     * @return the ReportTable.
     */
    ReportTable getReportTable( int id );
    
    /**
     * Retrieves a Collection of all ReportTables.
     * 
     * @return a Collection of ReportTables.
     */
    Collection<ReportTable> getAllReportTables();
    
    /**
     * Retrieves ReportTables with the given identifiers.
     * 
     * @param reportTables the identfiers of the ReportTables to retrieve.
     * @return a Collection of ReportTables.
     */
    Collection<ReportTable> getReportTables( Collection<Integer> reportTables );
    
    /**
     * Retrieves the ReportTable with the given name.
     * 
     * @param name the name of the ReportTable.
     * @return the ReportTable.
     */
    ReportTable getReportTableByName( String name );
    
    /**
     * Retrieves a ReportTableData object for the ReportTable with the given
     * identifier.
     * 
     * @param id the identifier of the ReportTable which the ReportTableData is
     *        based on.
     * @return a ReportTableData object.
     */
    ReportTableData getReportTableData( int id, I18nFormat format );
}
