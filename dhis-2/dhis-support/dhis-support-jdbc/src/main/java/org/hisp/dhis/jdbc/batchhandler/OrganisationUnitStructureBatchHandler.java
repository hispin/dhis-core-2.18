package org.hisp.dhis.jdbc.batchhandler;

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

import org.amplecode.quick.JdbcConfiguration;
import org.amplecode.quick.batchhandler.AbstractBatchHandler;
import org.hisp.dhis.resourcetable.OrganisationUnitStructure;
/**
 * @author Lars Helge Overland
 * @version $Id: OrganisationUnitStructureBatchHandler.java 5359 2008-06-06 10:36:39Z larshelg $
 */
public class OrganisationUnitStructureBatchHandler
    extends AbstractBatchHandler<OrganisationUnitStructure>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public OrganisationUnitStructureBatchHandler( JdbcConfiguration configuration )
    {
        super( configuration, false, false );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "_orgunitstructure" );
    }
    
    @Override
    protected void setAutoIncrementColumn()
    {
        statementBuilder.setAutoIncrementColumn( "orgunitstructureid" );
    }
    
    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "orgunitstructureid" );
    }
    
    @Override
    protected void setIdentifierValues( OrganisationUnitStructure structure )
    {        
        statementBuilder.setIdentifierValue( structure.getId() );
    }

    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "organisationunitid" );
    }
    
    protected void setUniqueValues( OrganisationUnitStructure structure )
    {        
        statementBuilder.setUniqueValue( structure.getOrganisationUnitId() );
    }    
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "organisationunitid" );
        statementBuilder.setColumn( "level" );
        
        statementBuilder.setColumn( "idlevel1" );
        statementBuilder.setColumn( "idlevel2" );
        statementBuilder.setColumn( "idlevel3" );
        statementBuilder.setColumn( "idlevel4" );
        statementBuilder.setColumn( "idlevel5" );
        statementBuilder.setColumn( "idlevel6" );
        statementBuilder.setColumn( "idlevel7" );
        statementBuilder.setColumn( "idlevel8" );
        
        statementBuilder.setColumn( "geolevel1" );
        statementBuilder.setColumn( "geolevel2" );
        statementBuilder.setColumn( "geolevel3" );
        statementBuilder.setColumn( "geolevel4" );
        statementBuilder.setColumn( "geolevel5" );
        statementBuilder.setColumn( "geolevel6" );
        statementBuilder.setColumn( "geolevel7" );
        statementBuilder.setColumn( "geolevel8" );
    }
    
    protected void setValues( OrganisationUnitStructure structure )
    {        
        statementBuilder.setValue( structure.getOrganisationUnitId() );
        statementBuilder.setValue( structure.getLevel() );
        
        statementBuilder.setValue( structure.getIdLevel1() );
        statementBuilder.setValue( structure.getIdLevel2() );
        statementBuilder.setValue( structure.getIdLevel3() );
        statementBuilder.setValue( structure.getIdLevel4() );
        statementBuilder.setValue( structure.getIdLevel5() );
        statementBuilder.setValue( structure.getIdLevel6() );
        statementBuilder.setValue( structure.getIdLevel7() );
        statementBuilder.setValue( structure.getIdLevel8() );

        statementBuilder.setValue( structure.getGeoCodeLevel1() );
        statementBuilder.setValue( structure.getGeoCodeLevel2() );
        statementBuilder.setValue( structure.getGeoCodeLevel3() );
        statementBuilder.setValue( structure.getGeoCodeLevel4() );
        statementBuilder.setValue( structure.getGeoCodeLevel5() );
        statementBuilder.setValue( structure.getGeoCodeLevel6() );
        statementBuilder.setValue( structure.getGeoCodeLevel7() );
        statementBuilder.setValue( structure.getGeoCodeLevel8() );
    }
}
