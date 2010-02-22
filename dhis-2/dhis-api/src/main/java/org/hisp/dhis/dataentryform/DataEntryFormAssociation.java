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

import java.io.Serializable;

/**
 * 
 * @author Viet Nguyen
 * 
 */
public class DataEntryFormAssociation
    implements Serializable
{

    /**
     * The Universally Unique Identifer for this Object. 
     */    
    protected String uuid;

    public static final String DATAENTRY_ASSOCIATE_DATASET = "dataset";

    public static final String DATAENTRY_ASSOCIATE_PROGRAMSTAGE = "programstage";

    /**
     * Part of the composite ID The table name ( dataset, programstage ... )
     */
    private String associationTableName;

    /**
     * Part of the composite ID id of the element in the association table (
     * datasetId, programstageId ... )
     */
    private int associationId;

    /**
     * DataEntryForm, lazy = false
     */
    private DataEntryForm dataEntryForm;

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public DataEntryFormAssociation()
    {

    }

    public DataEntryFormAssociation( String associationName, int associationId, DataEntryForm dataEntryForm )
    {
        this.associationTableName = associationName;
        this.associationId = associationId;
        this.dataEntryForm = dataEntryForm;
    }

    // -------------------------------------------------------------------------
    // HashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = prime * result + associationId;
        result = prime * result + associationTableName.hashCode();
        result = prime * result + dataEntryForm.getId();

        return result;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof DataEntryFormAssociation) )
        {
            return false;
        }

        final DataEntryFormAssociation other = (DataEntryFormAssociation) o;

        return associationId == (other.getAssociationId())
            && associationTableName.equals( other.getAssociationTableName() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }

    public void setDataEntryForm( DataEntryForm dataEntryForm )
    {
        this.dataEntryForm = dataEntryForm;
    }

    public int getAssociationId()
    {
        return associationId;
    }

    public void setAssociationId( int associationId )
    {
        this.associationId = associationId;
    }

    public String getAssociationTableName()
    {
        return associationTableName;
    }

    public void setAssociationTableName( String associationTableName )
    {
        this.associationTableName = associationTableName;
    }

}
