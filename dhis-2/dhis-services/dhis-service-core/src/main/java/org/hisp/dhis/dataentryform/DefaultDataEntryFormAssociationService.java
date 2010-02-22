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

import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormAssociation;
import org.hisp.dhis.dataentryform.DataEntryFormAssociationService;
import org.hisp.dhis.dataentryform.DataEntryFormAssociationStore;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Viet Nguyen 
 * 
 */
@Transactional
public class DefaultDataEntryFormAssociationService
    implements DataEntryFormAssociationService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataEntryFormAssociationStore dataEntryFormAssociationStore;

    public void setDataEntryFormAssociationStore( DataEntryFormAssociationStore dataEntryFormAssociationStore )
    {
        this.dataEntryFormAssociationStore = dataEntryFormAssociationStore;
    }

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    // -------------------------------------------------------------------------
    // DataEntryFormAssociationService implementation
    // -------------------------------------------------------------------------

    public void addDataEntryFormAssociation( DataEntryFormAssociation dataEntryFormAssociation )
    {
        dataEntryFormAssociationStore.addDataEntryFormAssociation( dataEntryFormAssociation );
    }

    public void deleteDataEntryFormAssociation( DataEntryFormAssociation dataEntryFormAssociation )
    {
        dataEntryFormAssociationStore.deleteDataEntryFormAssociation( dataEntryFormAssociation );
    }

    public Collection<DataEntryFormAssociation> getAllDataEntryFormAssociations()
    {
        return dataEntryFormAssociationStore.getAllDataEntryFormAssociations();
    }

    public DataEntryFormAssociation getDataEntryFormAssociation( String associationTableName, int associationId )
    {
        return dataEntryFormAssociationStore.getDataEntryFormAssociation( associationTableName, associationId );
    }

    public DataEntryFormAssociation getDataEntryFormAssociationByDataSet( int dataSetId )
    {
        return dataEntryFormAssociationStore.getDataEntryFormAssociation(
            DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSetId );
    }

    public DataEntryFormAssociation getDataEntryFormAssociationByProgramStage( int programStageId )
    {
        return dataEntryFormAssociationStore.getDataEntryFormAssociation(
            DataEntryFormAssociation.DATAENTRY_ASSOCIATE_PROGRAMSTAGE, programStageId );
    }

    public void updateDataEntryFormAssociation( DataEntryFormAssociation dataEntryFormAssociation )
    {
        dataEntryFormAssociationStore.updateDataEntryFormAssociation( dataEntryFormAssociation );
    }

    public DataEntryFormAssociation getDataEntryFormAssociationByDataEntryForm( DataEntryForm dataEntryForm )
    {
        return dataEntryFormAssociationStore.getDataEntryFormAssociationByDataEntryForm( dataEntryForm );
    }

    public Collection<DataEntryForm> listDisctinctDataEntryFormByAssociationIds( String associationName,
        List<Integer> associationIds )
    {
        if( associationIds == null || associationIds.size() == 0 )
        {
            return null; 
        }
        return dataEntryFormAssociationStore.listDisctinctDataEntryFormByAssociationIds( associationName, associationIds );
    }

}
