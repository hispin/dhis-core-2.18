/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.aggregation;


/**
 * @author Chau Thu Tran
 * @version $ID : AggregatedDataSetAssociation.java 10:02:44 AM Jul 9, 2010
 */
public class AggregatedDataSetAssociation
{
    private int periodTypeId;

    private int dataSetId;

    private int organisationUnitId;

    private int level;

    private boolean assigned;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public AggregatedDataSetAssociation()
    {
        super();
    }

    /**
     * @param periodTypeId the identifier of the period type.
     * @param dataSetId the identifier of the DataSet.
     * @param organisationUnitId the identifier of the organisation unit id.
     * @param level the identifier of the level
     * @param assigned assigned
     */
    public AggregatedDataSetAssociation( int periodTypeId, int dataSetId, int organisationUnitId, int level,
        boolean assigned )
    {
        this.periodTypeId = periodTypeId;
        this.dataSetId = dataSetId;
        this.organisationUnitId = organisationUnitId;
        this.level = level;
        this.assigned = assigned;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void clear()
    {
        this.periodTypeId = 0;
        this.dataSetId = 0;
        this.organisationUnitId = 0;
        this.level = 0;
        this.assigned = false;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getPeriodTypeId()
    {
        return periodTypeId;
    }

    public void setPeriodTypeId( int periodTypeId )
    {
        this.periodTypeId = periodTypeId;
    }

    public int getDataSetId()
    {
        return dataSetId;
    }

    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public int getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    public void setOrganisationUnitId( int organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel( int level )
    {
        this.level = level;
    }

    public boolean isAssigned()
    {
        return assigned;
    }

    public void setAssigned( boolean assigned )
    {
        this.assigned = assigned;
    }

    // ----------------------------------------------------------------------
    // hashCode and equals
    // ----------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + dataSetId;
        result = prime * result + organisationUnitId;
        result = prime * result + periodTypeId;
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        AggregatedDataSetAssociation other = (AggregatedDataSetAssociation) obj;
        if ( dataSetId != other.dataSetId )
            return false;
        if ( organisationUnitId != other.organisationUnitId )
            return false;
        if ( periodTypeId != other.periodTypeId )
            return false;
        return true;
    }

}
