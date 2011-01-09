package org.hisp.dhis.datamart.crosstab;

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
import java.util.Map;

import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.CrossTabDataValue;

/**
 * @author Lars Helge Overland
 * @version $Id: CrossTabService.java 6217 2008-11-06 18:53:04Z larshelg $
 */
public interface CrossTabService
{
    String ID = CrossTabService.class.getName();

    /**
     * Creates, populates and trims the crosstab table.
     * 
     * @param operands the collection of DataElementOperands.
     * @param periodIds the collection of Period identifiers.
     * @param organisationUnitIds the collection of OrganisationUnit identifiers.
     * @return the DataElementOperands where data exists.
     */
    boolean populateAndTrimCrossTabTable( final Collection<DataElementOperand> operands,
        final Collection<Integer> periodIds, final Collection<Integer> organisationUnitIds, String key );
    
    /**
     * Creates and populates the crosstab table.
     * 
     * @param operands the collection of DataElementOperands.
     * @param periodIds the collection of Period identifiers.
     * @param organisationUnitIds the collection of OrganisationUnit identifiers.
     * @return the DataElementOperands where data exists.
     */
    Collection<DataElementOperand> populateCrossTabTable( Collection<DataElementOperand> operands, 
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, String key );

    /**
     * Drops the crosstab table.
     */
    void dropCrossTabTable( String key );
    
    /**
     * Trims the crosstab table.
     * 
     * @param operands the DataElementOperands with data.
     */
    void trimCrossTabTable( Collection<DataElementOperand> operands, String key );

    /**
     * Provides a Map with information about the crosstab table where the key is
     * the DataElementOperand and the value is the column index.
     * 
     * @param operands the collection of DataElementOperands.
     * @return a Map with information about the crosstab table.
     */
    Map<DataElementOperand, Integer> getOperandIndexMap( Collection<DataElementOperand> operands, String key );
    
    /**
     * Validates whether the number of columns in the crosstab table will be valid
     * for the current DBMS.
     * 
     * @param operands the DataElementOperands.
     * @return 0 if OK, the number of excess columns if not.
     */
    int validateCrossTabTable( Collection<DataElementOperand> operands );

    /**
     * Gets all CrossTabDataValues for the given collection of period ids and source ids.
     * 
     * @param dataElementIds the dataelement identifiers.
     * @param periodIds the period identifiers.
     * @param sourceIds the source identifiers.
     * @return collection of CrossTabDataValues.
     */
    Collection<CrossTabDataValue> getCrossTabDataValues( Map<DataElementOperand, Integer> operandIndexMap, Collection<Integer> periodIds, 
        Collection<Integer> sourceIds, String key );

    /**
     * Gets all CrossTabDataValues for the given collection of period ids and the source id.
     * 
     * @param dataElementIds the dataelement identifiers.
     * @param periodIds the period identifiers.
     * @param sourceId the source identifier.
     * @return collection of CrossTabDataValues.
     */
    Collection<CrossTabDataValue> getCrossTabDataValues( Map<DataElementOperand, Integer> operandIndexMap, Collection<Integer> periodIds, 
        int sourceId, String key );
}
