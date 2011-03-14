package org.hisp.dhis.reporting.pivottable.action;

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

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.pivottable.PivotTable;
import org.hisp.dhis.pivottable.PivotTableService;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetPivotTableAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PivotTableService pivotTableService;

    public void setPivotTableService( PivotTableService pivotTableService )
    {
        this.pivotTableService = pivotTableService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer indicatorGroupId;

    public void setIndicatorGroupId( Integer indicatorGroupId )
    {
        this.indicatorGroupId = indicatorGroupId;
    }
    
    private String periodTypeName;

    public void setPeriodTypeName( String periodTypeName )
    {
        this.periodTypeName = periodTypeName;
    }
    
    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }
    
    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private PivotTable pivotTable;
    
    public PivotTable getPivotTable()
    {
        return pivotTable;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        pivotTable = pivotTableService.getPivotTable( indicatorGroupId, periodTypeName, startDate, endDate, organisationUnitId );
        
        for ( Period period : pivotTable.getPeriods() )
        {
            period.setName( format.formatPeriod( period ) );
        }
                
        return SUCCESS;
    }
}
