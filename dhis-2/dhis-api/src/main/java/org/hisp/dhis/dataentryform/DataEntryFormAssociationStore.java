package org.hisp.dhis.dataentryform;

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
import java.util.List;

public interface DataEntryFormAssociationStore
{
    String ID = DataEntryFormAssociationStore.class.getName();

    // -------------------------------------------------------------------------
    // DataEntryFormAssociation
    // -------------------------------------------------------------------------

    /**
     * Adds a DataEntryFormAssociation.
     * 
     * @param dataEntryFormAssociation The DataEntryFormAssociation to add.
     * @return The generated unique identifier for this DataEntryForm.
     */
    void addDataEntryFormAssociation( DataEntryFormAssociation dataEntryFormAssociation);

    /**
     * Updates a DataEntryFormAssociation.
     * 
     * @param dataEntryFormAssociation The DataEntryFormAssociation to update.
     */
    void updateDataEntryFormAssociation( DataEntryFormAssociation dataEntryFormAssociation);

    /**
     * Deletes a DataEntryFormAssociation.
     * 
     * @param dataEntryFormAssociation The DataEntryFormAssociation to delete.
     */
    void deleteDataEntryFormAssociation(  DataEntryFormAssociation dataEntryFormAssociation  );

    /**
     *  Get a DataEntryFormAssociation
     * @param associationTableName : table name of the association ( dataset, programstage..)
     * @param associationId : the id of the element in the association table
     * @return The DataEntryFormAssociation with the given associationId and associationTablename or null if it does not exist
     */
    DataEntryFormAssociation getDataEntryFormAssociation( String associationTableName, int associationId   );

    /**
     * Get DataEntryFormAssociation 
     * @param dataEntryForm
     * @return the DataEntryFormAssociation with the given dataEntryForm or null if it does not exist
     */
    DataEntryFormAssociation getDataEntryFormAssociationByDataEntryForm( DataEntryForm dataEntryForm );
    
    /**
     * Get all DataEntryFormAssociations.
     * 
     * @return A collection containing all DataEntryFormAssociations.
     */
    Collection<DataEntryFormAssociation> getAllDataEntryFormAssociations();
    
    /**
     * List distinct DataEntryForms by list associationIds .
     * 
     * @return A collection containing all DataEntryForms corresponds to the given list associationIds.
     */
    public Collection<DataEntryForm> listDisctinctDataEntryFormByAssociationIds(String associationName, List<Integer> associationIds );
}
