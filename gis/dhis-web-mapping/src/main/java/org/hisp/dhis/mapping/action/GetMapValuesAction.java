package org.hisp.dhis.mapping.action;

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

import org.hisp.dhis.aggregation.AggregatedMapValue;
import org.hisp.dhis.datamart.DataMartStore;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetMapValuesAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataMartStore dataMartStore;

    public void setDataMartStore( DataMartStore dataMartStore )
    {
        this.dataMartStore = dataMartStore;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private int indicatorId;

    public void setIndicatorId( int indicatorId )
    {
        this.indicatorId = indicatorId;
    }

    private int periodId;

    public void setPeriodId( int periodId )
    {
        this.periodId = periodId;
    }

    private int level;

    public void setLevel( int level )
    {
        this.level = level;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Collection<AggregatedMapValue> object;

    public Collection<AggregatedMapValue> getObject()
    {
        return object;
    }

    // -------------------------------------------------------------------------
    // Action implemenation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        object = dataMartStore.getAggregatedMapValues( indicatorId, periodId, level );
        
        return SUCCESS;
    }
}
