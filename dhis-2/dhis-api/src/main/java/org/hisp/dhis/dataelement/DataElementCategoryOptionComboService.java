package org.hisp.dhis.dataelement;

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

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface DataElementCategoryOptionComboService
{
    String ID = DataElementCategoryOptionComboService.class.getName();

    /**
     * Adds a DataElementCategoryOptionCombo.
     * 
     * @param dataElementCategoryOptionCombo the DataElementCategoryOptionCombo
     *        to add.
     * @return the generated identifier.
     */
    int addDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo );

    /**
     * Updates a DataElementCategoryOptionCombo.
     * 
     * @param dataElementCategoryOptionCombo the DataElementCategoryOptionCombo
     *        to update.
     */
    void updateDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo );

    /**
     * Deletes a DataElementCategoryOptionCombo.
     * 
     * @param dataElementCategoryOptionCombo the DataElementCategoryOptionCombo
     *        to delete.
     */
    void deleteDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo );

    /**
     * Retrieves a DataElementCategoryOptionCombo with the given identifier.
     * 
     * @param id the identifier of the DataElementCategoryOptionCombo.
     * @return the DataElementCategoryOptionCombo.
     */
    DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( int id );

    /**
     * Retrieves the DataElementCategoryOptionCombos with the given identifiers.
     * 
     * @param identifiers the identifiers of the DataElementCategoryOptionCombos.
     * @return a Collection of DataElementCategoryOptionCombos.
     */
    Collection<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombos( Collection<Integer> identifiers );

    /**
     * Retrieves the DataElementCategoryOptionCombo with the given Collection
     * of DataElementCategoryOptions.
     * 
     * @param categoryOptions
     * @return
     */
    DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( Collection<DataElementCategoryOption> categoryOptions );
    
    /**
     * Retrieves a DataElementCategoryOptionCombo.
     * 
     * @param categoryOptionCombo the DataElementCategoryOptionCombo to
     *        retrieve.
     * @return a DataElementCategoryOptionCombo.
     */
    DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo );

    /**
     * Retrieves all DataElementCategoryOptionCombos.
     * 
     * @return a Collection of DataElementCategoryOptionCombos.
     */
    Collection<DataElementCategoryOptionCombo> getAllDataElementCategoryOptionCombos();

    /**
     * Sorts the DataElementCategoryOptionCombos in the given
     * DataElementCategoryCombo.
     * 
     * @param categoryCombo the DataElementCategoryCombo.
     * 
     */
    Collection<DataElementCategoryOptionCombo> sortOptionCombos( DataElementCategoryCombo categoryCombo );

    /**
     * Generates and persists a default DataElementCategory,
     * DataElmentCategoryOption, DataElementCategoryCombo and
     * DataElementCategoryOptionCombo.
     */
    void generateDefaultDimension();

    /**
     * Retrieves the default DataElementCategoryOptionCombo.
     * 
     * @return the DataElementCategoryOptionCombo.
     */
    DataElementCategoryOptionCombo getDefaultDataElementCategoryOptionCombo();

    /**
     * Generates and persists DataElementCategoryOptionCombos for the given
     * DataElementCategoryCombo.
     * 
     * @param categoryCombo the DataElementCategoryCombo.
     */
    void generateOptionCombos( DataElementCategoryCombo categoryCombo );

    /**
     * 
     * @param dataElementIdentifiers
     * @return
     */
    Collection<Operand> getOperandsByIds( Collection<Integer> dataElementIdentifiers );

    /**
     * Gets the Operands for the given Collection of DataElements.
     * 
     * @param dataElements the Collection of DataElements.
     * @return the Operands for the given Collection of DataElements.
     */
    Collection<Operand> getOperands( Collection<DataElement> dataElements );
}
