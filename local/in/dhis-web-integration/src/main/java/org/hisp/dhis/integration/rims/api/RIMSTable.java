/*
 * Copyright (c) 2004-2008, University of Oslo
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
package org.hisp.dhis.integration.rims.api;

import java.sql.Connection;
import java.sql.SQLException;

import org.hisp.dhis.integration.rims.api.tables.RIMSOrgUnit;
import org.hisp.dhis.integration.rims.util.Configuration;

/**
 * Interface specifying the behavior of RIMS tables for import/export. To add
 * another table, follow these steps:
 * 
 * <ul> <li>Create a RIMSTable class in rims.api.tables </li>
 * <li>Add to Configuration.getExportTables </li>
 * <li>If new RIMS fields are required, add them to Configuration.monthlyFields
 * or yearlyFields</li>
 * <li>If new XML attributes are required: Detect them in 
 * <ul> <li>RIMSService.getAllMappingDataElements</li>
 *   <li> RIMSService.getRIMSDataElementsByDEGroup</li>
 *   <li> Create a field in RIMS_Mapping_Dataelement</li>
 * </ul> </ul>
 * 
 * @author Leif Arne Storset
 * @version $Id$
 */
public interface RIMSTable
{
    public interface Row { }
    /**
     * Fills in totals and other dependent (unnormalized) data.
     * @throws SQLException 
     * 
     */
    public void fillInTotals() throws SQLException;

    /**
     * Returns the name of the table this class represents.
     */
    public String getTableName();
    
    /**
     * A user-friendly description of the table.
     */
    public String getDescription();

    /**
     * Marks completed lines as such.
     * @throws SQLException 
     */
    public void markIfComplete() throws SQLException;

    public void setConnection( Connection conn );

    public boolean isData( RIMSOrgUnit orgUnit, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement ) throws SQLException;

    public int insertData( RIMSOrgUnit rimsOrgUnit, int month,
        int year, RIMS_Mapping_DataElement mappingDataElement, String resValue ) throws SQLException;

    public int updateData( RIMSOrgUnit rimsOrgUnit, int month,
        int year, RIMS_Mapping_DataElement mappingDataElement, String resValue ) throws SQLException;

    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMSOrgUnit orgUnit, int month, int year ) throws SQLException;

    public void commit() throws SQLException;

    public void beginTransaction() throws SQLException;

    public void setConfiguration( Configuration configuration );

    /**
     * Delete all records in the table. Useful mainly for testing.
     * @throws SQLException 
     *
     */
    public int truncate() throws SQLException;
    
    /**
     * Delete district data only.
     */
    public int deleteDistrictData() throws SQLException;
}
