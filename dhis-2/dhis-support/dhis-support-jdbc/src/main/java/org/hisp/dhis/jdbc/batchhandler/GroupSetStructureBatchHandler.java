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
import org.hisp.dhis.resourcetable.GroupSetStructure;

/**
 * @author Lars Helge Overland
 * @version $Id: GroupSetStructureBatchHandler.java 5062 2008-05-01 18:10:35Z larshelg $
 */
public class GroupSetStructureBatchHandler
    extends AbstractBatchHandler<GroupSetStructure>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public GroupSetStructureBatchHandler( JdbcConfiguration configuration )
    {
        super( configuration, true, true );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "_orgunitgroupsetstructure" );
    }
    
    @Override
    protected void setAutoIncrementColumn()
    {
        statementBuilder.setAutoIncrementColumn( "orgunitgroupsetstructureid" );
    }
    
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "orgunitgroupsetstructureid" );
    }
    
    protected void setIdentifierValues( GroupSetStructure structure )
    {
        statementBuilder.setIdentifierValue( structure.getId() );
    }
    
    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "organisationunitid" );
        statementBuilder.setUniqueColumn( "orgunitgroupid" );
        statementBuilder.setUniqueColumn( "orgunitgroupsetid" );
    }
    
    protected void setUniqueValues( GroupSetStructure structure )
    {        
        statementBuilder.setUniqueValue( structure.getOrganisationUnitId() );
        statementBuilder.setUniqueValue( structure.getGroupId() );
        statementBuilder.setUniqueValue( structure.getGroupSetId() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "organisationunitid" );
        statementBuilder.setColumn( "orgunitgroupid" );
        statementBuilder.setColumn( "orgunitgroupsetid" );
    }
    
    protected void setValues( GroupSetStructure structure )
    {        
        statementBuilder.setValue( structure.getOrganisationUnitId() );
        statementBuilder.setValue( structure.getGroupId() );
        statementBuilder.setValue( structure.getGroupSetId() );
    }
}
