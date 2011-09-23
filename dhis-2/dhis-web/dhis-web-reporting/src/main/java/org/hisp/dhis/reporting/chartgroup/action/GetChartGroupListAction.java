package org.hisp.dhis.reporting.chartgroup.action;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.chart.ChartGroup;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.chart.comparator.ChartGroupNameComparator;
import org.hisp.dhis.paging.ActionPagingSupport;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class GetChartGroupListAction
    extends ActionPagingSupport<ChartGroup>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<ChartGroup> chartGroups;

    public List<ChartGroup> getChartGroups()
    {
        return chartGroups;
    }
    
    private String key;
    
    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( chartService.getChartGroupCountByName( key ) );
            
            chartGroups = new ArrayList<ChartGroup>( chartService.getChartGroupsBetweenByName( key, paging.getStartPos(), paging.getPageSize() ) );
        }
        else
        {
            this.paging = createPaging( chartService.getChartGroupCount() );
            
            chartGroups = new ArrayList<ChartGroup>( chartService.getChartGroupsBetween( paging.getStartPos(), paging.getPageSize() ) );
        }

        Collections.sort( chartGroups, new ChartGroupNameComparator() );

        return SUCCESS;
    }
}
