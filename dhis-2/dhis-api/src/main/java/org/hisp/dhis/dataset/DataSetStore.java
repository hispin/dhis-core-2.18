package org.hisp.dhis.dataset;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import org.hisp.dhis.common.GenericNameableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Kristian Nordal
 * @version $Id: DataSetStore.java 6255 2008-11-10 16:01:24Z larshelg $
 */
public interface DataSetStore
    extends GenericNameableObjectStore<DataSet>
{
    String ID = DataSetStore.class.getName();

    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------

    /**
     * Adds a DataSet.
     * 
     * @param dataSet The DataSet to add.
     * @return The generated unique identifier for this DataSet.
     */
    int addDataSet( DataSet dataSet );

    /**
     * Updates a DataSet.
     * 
     * @param dataSet The DataSet to update.
     */
    void updateDataSet( DataSet dataSet );

    /**
     * Deletes a DataSet.
     * 
     * @param dataSet The DataSet to delete.
     */
    void deleteDataSet( DataSet dataSet );

    /**
     * Get a DataSet
     * 
     * @param id The unique identifier for the DataSet to get.
     * @return The DataSet with the given id or null if it does not exist.
     */
    DataSet getDataSet( int id );

    /**
     * Returns the DataSet with the given UID.
     * 
     * @param uid the UID.
     * @return the DataSet with the given UID, or null if no match.
     */
    DataSet getDataSet( String uid );

    /**
     * Returns the DataSet with the given name.
     * 
     * @param name The name.
     * @return The DataSet with the given name.
     */
    DataSet getDataSetByName( String name );

    /**
     * Returns the DataSet with the given short name.
     * 
     * @param shortName The short name.
     * @return The DataSet with the given short name.
     */
    DataSet getDataSetByShortName( String shortName );

    /**
     * Returns the DataSet with the given code.
     * 
     * @param code The code.
     * @return The DataSet with the given code.
     */
    DataSet getDataSetByCode( String code );

    /**
     * Gets all DataSets.
     * 
     * @return A collection containing all DataSets.
     */
    Collection<DataSet> getAllDataSets();

    /**
     * Gets all DataSets associated with the given PeriodType.
     * 
     * @param periodType the PeriodType.
     * @return a collection of DataSets.
     */
    Collection<DataSet> getDataSetsByPeriodType( PeriodType periodType );

    /**
     * Returns all DataSets that can be collected through mobile.
     */
    Collection<DataSet> getDataSetsForMobile( OrganisationUnit source );

    Collection<DataSet> getDataSetsForMobile();

    Collection<DataSet> getDataSetsBySources( Collection<OrganisationUnit> sources );

    int getDataSetCountByName( String name );

    Collection<DataSet> getDataSetsBetweenByName( String name, int first, int max );

    int getDataSetCount();

    Collection<DataSet> getDataSetsBetween( int first, int max );

}
