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
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;

/**
 * @author Lars Helge Overland
 * @version $Id: DataSetService.java 6255 2008-11-10 16:01:24Z larshelg $
 */
public interface DataSetService
{
    String ID = DataSetService.class.getName();

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
     * Returns the DataSet with the given UUID.
     * 
     * @param uuid the UUID.
     * @return the DataSet with the given UUID, or null if no match.
     */
    DataSet getDataSet( String uuid );
    
    /**
     * Returns a DataSets with the given name.
     * 
     * @param name The name.
     * @return A DataSet with the given name.
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
     * @param shortName The code.
     * @return The DataSet with the given code.
     */
    DataSet getDataSetByCode( String code );

    /**
     * Returns all DataSets associated with the specified source.
     */
    Collection<DataSet> getDataSetsBySource( Source source );

    /**
     * Returns all DataSets associated with the specified sources.
     */
    Collection<DataSet> getDataSetsBySources( Collection<? extends Source> sources );

    /**
     * Returns the number of Sources among the specified Sources associated with
     * the specified DataSet.
     */
    int getSourcesAssociatedWithDataSet( DataSet dataSet, Collection<? extends Source> sources );

    /**
     * Get all DataSets.
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
     * Get all DataSets with corresponding identifiers.
     * 
     * @param identifiers the collection of identifiers.
     * @return a collection of indicators.
     */
    Collection<DataSet> getDataSets( Collection<Integer> identifiers );

    /**
     * Get list of available ie. unassigned datasets.
     * 
     * @return A List containing all avialable DataSets.
     */
    List<DataSet> getAvailableDataSets();

    /**
     * Get list of assigned (ie. which had corresponding dataentryform)
     * datasets.
     * 
     * @return A List containing assigned DataSets.
     */
    List<DataSet> getAssignedDataSets();

    /**
     * Get list of assigned (ie. which had corresponding dataentryform) datasets
     * for specific period type.
     * 
     * @return A List containing assigned DataSets for specific period type.
     */
    List<DataSet> getAssignedDataSetsByPeriodType( PeriodType periodType );

    /**
     * Searches through the data sets with the corresponding given identifiers.
     * If the given data element is a member of one of the data sets, that data
     * sets period type is returned. This implies that if the data element is a
     * member of more than one data set, which period type being returned is
     * undefined. If null is passed as the second argument, all data sets will
     * be searched.
     * 
     * @param dataElement the data element to find the period type for.
     * @param dataSetIdentifiers the data set identifiers to search through.
     * @return the period type of the given data element.
     */
    PeriodType getPeriodType( DataElement dataElement, Collection<Integer> dataSetIdentifiers );

    /**
     * Returns a distinct collection of data elements associated with the data
     * sets with the given corresponding data set identifiers.
     * 
     * @param dataSetIdentifiers the data set identifiers.
     * @return a distinct collection of data elements.
     */
    Collection<DataElement> getDistinctDataElements( Collection<Integer> dataSetIdentifiers );

    /**
     * Returns a collection of data elements associated with the given
     * corresponding data set.
     * 
     * @param dataSet the data set object.
     * @return a collection of data elements.
     */
    Collection<DataElement> getDataElements( DataSet dataSet );
    
    /**
     * Returns all DataSets that can be collected through mobile.
     */
    Collection<DataSet> getDataSetsForMobile(Source source);

    /**
     * Get list of realted datasets from categoryOption
     * 
     * @return A List containing related DataSets.
     */
    Collection<DataSet> getMobileDataSetsFromCategoryOption(int categoryOptionId);
    
    /**
     * Get list of realted datasets from category
     * 
     * @return A List containing related DataSets.
     */
    Collection<DataSet> getMobileDataSetsFromCategory(int categoryOptionId);
    
    // -------------------------------------------------------------------------
    // FrequencyOverrideAssociation
    // -------------------------------------------------------------------------

    /**
     * Adds a FrequencyOverrideAssociation.
     * 
     * @param frequencyOverrideAssociation The FrequencyOverrideAssociation to
     *        add.
     */
    void addFrequencyOverrideAssociation( FrequencyOverrideAssociation frequencyOverrideAssociation );

    /**
     * Updates a FrequencyOverrideAssociation.
     * 
     * @param frequencyOverrideAssociation The FrequencyOverrideAssociation to
     *        update.
     */
    void updateFrequencyOverrideAssociation( FrequencyOverrideAssociation frequencyOverrideAssociation );

    /**
     * Removes a FrequencyOverrideAssociation.
     * 
     * @param frequencyOverrideAssociation The FrequencyOverrideAssociation to
     *        remove.
     */
    void deleteFrequencyOverrideAssociation( FrequencyOverrideAssociation frequencyOverrideAssociation );

    /**
     * Retrieves a FrequencyOverrideAssociation.
     * 
     * @param dataSet The DataSet referred to by the
     *        FrequencyOverrideAssociation.
     * @param source The Source referred to by the FrequencyOverrideAssociation.
     * @return The FrequencyOverrideAssociation for the given DataSet and
     *         Source.
     */
    FrequencyOverrideAssociation getFrequencyOverrideAssociation( DataSet dataSet, Source source );

    /**
     * Retrieves FrequencyOverrideAssociations for a given DataSet.
     * 
     * @param dataSet The DataSet.
     * @return A collection of FrequencyOverrideAssociations for the given
     *         DataSet.
     */
    Collection<FrequencyOverrideAssociation> getFrequencyOverrideAssociationsByDataSet( DataSet dataSet );

    /**
     * Retrieves FrequencyOverrideAssociations for a given Source.
     * 
     * @param source The Source.
     * @return A collection of FrequencyOverrideAssociations for the given
     *         Source.
     */
    Collection<FrequencyOverrideAssociation> getFrequencyOverrideAssociationsBySource( Source source );

    int getDataSetCountByName( String name );
    
    Collection<DataSet> getDataSetsBetweenByName( String name, int first, int max );
    
    int getDataSetCount();
    
    Collection<DataSet> getDataSetsBetween(int first, int max );


}
