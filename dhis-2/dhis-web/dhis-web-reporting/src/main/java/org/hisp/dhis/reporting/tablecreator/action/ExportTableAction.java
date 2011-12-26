package org.hisp.dhis.reporting.tablecreator.action;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.util.SessionUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class ExportTableAction
    implements Action
{
    private static final String DEFAULT_TYPE = "html";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportTableService reportTableService;

    public void setReportTableService( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
    }
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    private String reportingPeriod;

    public void setReportingPeriod( String reportingPeriod )
    {
        this.reportingPeriod = reportingPeriod;
    }

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }
    
    private Boolean useLast = false;

    public void setUseLast( boolean useLast )
    {
        this.useLast = useLast;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Grid grid;

    public Grid getGrid()
    {
        return grid;
    }

    private Map<String, Object> params = new HashMap<String, Object>();

    public Map<String, Object> getParams()
    {
        return params;
    }

    // -------------------------------------------------------------------------
    // Result implementation
    // -------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public String execute()
        throws Exception
    {
        if ( useLast )
        {
            grid = (Grid) SessionUtils.getSessionVar( SessionUtils.KEY_REPORT_TABLE_GRID );
            params = (Map<String, Object>) SessionUtils.getSessionVar( SessionUtils.KEY_REPORT_TABLE_PARAMS );
        }
        else
        {
            ReportTable reportTable = reportTableService.getReportTable( id );

            Date date = reportingPeriod != null ? DateUtils.getMediumDate( reportingPeriod ) : new Date();
            
            grid = reportTableService.getReportTableGrid( id, format, date, organisationUnitId );
            
            params.putAll( constantService.getConstantParameterMap() );
            params.putAll( reportTable.getOrganisationUnitGroupMap( organisationUnitGroupService.getCompulsoryOrganisationUnitGroupSets() ) );
        }

        SessionUtils.setSessionVar( SessionUtils.KEY_REPORT_TABLE_GRID, grid );
        SessionUtils.setSessionVar( SessionUtils.KEY_REPORT_TABLE_PARAMS, params );
        
        return type != null ? type : DEFAULT_TYPE;
    }
}
