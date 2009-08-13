package org.hisp.dhis.jdbc.batchhandler;

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

import org.amplecode.quick.JdbcConfiguration;
import org.amplecode.quick.batchhandler.AbstractBatchHandler;
import org.hisp.dhis.datadictionary.ExtendedDataElement;

/**
 * @author Lars Helge Overland
 * @version $Id: ExtendedDataElementBatchHandler.java 5805 2008-10-03 13:16:15Z larshelg $
 */
public class ExtendedDataElementBatchHandler
    extends AbstractBatchHandler<ExtendedDataElement>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public ExtendedDataElementBatchHandler( JdbcConfiguration configuration )
    {
        super( configuration, true, false );
    }    

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "extendeddataelement" );
    }
    
    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "extendeddataelementid" );
    }

    @Override
    protected void setIdentifierValues( ExtendedDataElement element )
    {        
        statementBuilder.setIdentifierValue( element.getId() );
    }
    
    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "mnemonic" );
    }

    protected void setUniqueValues( ExtendedDataElement element )
    {
        statementBuilder.setUniqueValue( element.getMnemonic() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "mnemonic" );
        statementBuilder.setColumn( "version" );
        statementBuilder.setColumn( "context" );
        statementBuilder.setColumn( "synonyms" );
        statementBuilder.setColumn( "hononyms" );
        statementBuilder.setColumn( "keywords" );
        statementBuilder.setColumn( "status" );
        statementBuilder.setColumn( "statusDate" );
        statementBuilder.setColumn( "dataElementType" );
        
        statementBuilder.setColumn( "dataType" );
        statementBuilder.setColumn( "representationalForm" );
        statementBuilder.setColumn( "representationalLayout" );
        statementBuilder.setColumn( "minimumSize" );
        statementBuilder.setColumn( "maximumSize" );
        statementBuilder.setColumn( "dataDomain" );
        statementBuilder.setColumn( "validationRules" );
        statementBuilder.setColumn( "relatedDataReferences" );
        statementBuilder.setColumn( "guideForUse" );        
        statementBuilder.setColumn( "collectionMethods" );
        
        statementBuilder.setColumn( "responsibleAuthority" );
        statementBuilder.setColumn( "updateRules" );
        statementBuilder.setColumn( "accessAuthority" );
        statementBuilder.setColumn( "updateFrequency" );
        statementBuilder.setColumn( "location" );
        statementBuilder.setColumn( "reportingMethods" );
        statementBuilder.setColumn( "versionStatus" );
        statementBuilder.setColumn( "previousVersionReferences" );
        statementBuilder.setColumn( "sourceDocument" );
        statementBuilder.setColumn( "sourceOrganisation" );
        statementBuilder.setColumn( "comment" );
        statementBuilder.setColumn( "saved" );
        statementBuilder.setColumn( "lastUpdated" );
    }
    
    protected void setValues( ExtendedDataElement element )
    {        
        statementBuilder.setValue( element.getMnemonic() );
        statementBuilder.setValue( element.getVersion() );
        statementBuilder.setValue( element.getContext() );
        statementBuilder.setValue( element.getSynonyms() );
        statementBuilder.setValue( element.getHononyms() );
        statementBuilder.setValue( element.getKeywords() );
        statementBuilder.setValue( element.getStatus() );
        statementBuilder.setValue( element.getStatusDate() );
        statementBuilder.setValue( element.getDataElementType() );
        
        statementBuilder.setValue( element.getDataType() );
        statementBuilder.setValue( element.getRepresentationalForm() );
        statementBuilder.setValue( element.getRepresentationalLayout() );
        statementBuilder.setValue( element.getMinimumSize() );
        statementBuilder.setValue( element.getMaximumSize() );
        statementBuilder.setValue( element.getDataDomain() );
        statementBuilder.setValue( element.getValidationRules() );
        statementBuilder.setValue( element.getRelatedDataReferences() );
        statementBuilder.setValue( element.getGuideForUse() );
        statementBuilder.setValue( element.getCollectionMethods() );
        
        statementBuilder.setValue( element.getResponsibleAuthority() );
        statementBuilder.setValue( element.getUpdateRules() );
        statementBuilder.setValue( element.getAccessAuthority() );
        statementBuilder.setValue( element.getUpdateFrequency() );
        statementBuilder.setValue( element.getLocation() );
        statementBuilder.setValue( element.getReportingMethods() );
        statementBuilder.setValue( element.getVersionStatus() );
        statementBuilder.setValue( element.getPreviousVersionReferences() );
        statementBuilder.setValue( element.getSourceDocument() );
        statementBuilder.setValue( element.getSourceOrganisation() );
        statementBuilder.setValue( element.getComment() );
        statementBuilder.setValue( element.getSaved() );
        statementBuilder.setValue( element.getLastUpdated() );
    }
}
