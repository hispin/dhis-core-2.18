package org.hisp.dhis.datamart;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import org.hisp.dhis.period.RelativePeriods;

/**
 * @author Lars Helge Overland
 */
public interface DataMartService
{
    final String ID = DataMartService.class.getName();

    // ----------------------------------------------------------------------
    // Export
    // ----------------------------------------------------------------------
    
    /**
     * Export to data mart for the given DataMartExport.
     * 
     * @id the DataMartExport identifier.
     * @return the number of exported values.
     */
    void export( int id );

    /**
     * Exports to data mart for the given arguments.
     * 
     * @param dataElementIds the data element identifiers.
     * @param indicatorIds the indicator identifiers.
     * @param periodIds the period identifiers.
     * @param organisationUnitIds the organisation unit identifiers.
     */
    void export( Collection<Integer> dataElementIds, Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds );
    
    /**
     * Exports to data mart for the given arguments.
     * 
     * @param dataElementIds the data element identifiers.
     * @param indicatorIds the indicator identifiers.
     * @param periodIds the period identifiers.
     * @param organisationUnitIds the organisation unit identifiers.
     * @param relatives the RelativePeriods.
     */
    void export( Collection<Integer> dataElementIds, Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, RelativePeriods relatives );

    /**
     * Exports to data mart for the given arguments.
     * 
     * @param dataElementIds the data element identifiers.
     * @param indicatorIds the indicator identifiers.
     * @param periodIds the period identifiers.
     * @param organisationUnitIds the organisation unit identifiers.
     * @param organisationUnitGroupIds the organisation unit group identifiers.
     * @param relatives the RelativePeriods.
     * @param completeExport indicates whether this is a complete export.
     */
    void export( Collection<Integer> dataElementIds, Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, Collection<Integer> organisationUnitGroupIds,
        RelativePeriods relatives, boolean completeExport );
    
    // ----------------------------------------------------------------------
    // DataMartExport
    // ----------------------------------------------------------------------
    
    /**
     * Saves a DataMartExport.
     * 
     * @param export the DataMartExport to save.
     */
    void saveDataMartExport( DataMartExport export );

    /**
     * Retrieves the DataMartExport with the given identifier.
     * 
     * @param id the identifier of the DataMartExport.
     * @return the DataMartExport.
     */
    DataMartExport getDataMartExport( int id );
    
    /**
     * Deletes a DataMartExport.
     * 
     * @param export the DataMartExport to delete.
     */
    void deleteDataMartExport( DataMartExport export );
    
    /**
     * Retrieves all DataMartExports.
     * 
     * @return a Collection of DataMartExports.
     */
    Collection<DataMartExport> getAllDataMartExports();
    
    /**
     * Retrieves the DataMartExport with the given name.
     * 
     * @param name the name of the DataMartExport to retrieve.
     * @return the DataMartExport.
     */
    DataMartExport getDataMartExportByName( String name );    

    Collection<DataMartExport> getDataMartExportsBetween( int first, int max );
    
    Collection<DataMartExport> getDataMartExportsBetweenByName( String name, int first, int max );
    
    int getDataMartExportCount();
    
    int getDataMartExportCountByName( String name );
}
