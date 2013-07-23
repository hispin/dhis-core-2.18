package org.hisp.dhis.dashboard.impl;

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

import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;

import java.util.List;

import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.dashboard.Dashboard;
import org.hisp.dhis.dashboard.DashboardItem;
import org.hisp.dhis.dashboard.DashboardSearchResult;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Note: The remove associations methods must be altered if caching is introduced.
 * 
 * @author Lars Helge Overland
 */
@Transactional
public class DefaultDashboardService
    implements DashboardService
{
    private static final int MAX_PER_OBJECT = 5;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HibernateIdentifiableObjectStore<Dashboard> dashboardStore;
    
    public void setDashboardStore( HibernateIdentifiableObjectStore<Dashboard> dashboardStore )
    {
        this.dashboardStore = dashboardStore;
    }

    private UserService userService;
    
    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }

    private MappingService mappingService;

    public void setMappingService( MappingService mappingService )
    {
        this.mappingService = mappingService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
    private ReportTableService reportTableService;

    public void setReportTableService( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
    }
    
    private DocumentService documentService;

    public void setDocumentService( DocumentService documentService )
    {
        this.documentService = documentService;
    }

    // -------------------------------------------------------------------------
    // DashboardService implementation
    // -------------------------------------------------------------------------

    @Override
    public DashboardSearchResult search( String query )
    {
        DashboardSearchResult result = new DashboardSearchResult();
        
        result.setUsers( userService.getAllUsersBetweenByName( query, 0, MAX_PER_OBJECT ) );
        result.setCharts( chartService.getChartsBetweenByName( query, 0, MAX_PER_OBJECT ) );
        result.setMaps( mappingService.getMapsBetweenLikeName( query, 0, MAX_PER_OBJECT ) );
        result.setReportTables( reportTableService.getReportTablesBetweenByName( query, 0, MAX_PER_OBJECT ) );
        result.setReports( reportService.getReportsBetweenByName( query, 0, MAX_PER_OBJECT ) );
        result.setResources( documentService.getDocumentsBetweenByName( query, 0, MAX_PER_OBJECT ) );
        
        return result;
    }
    
    public void mergeDashboard( Dashboard dashboard )
    {
        if ( dashboard.getItems() != null )
        {
            for ( DashboardItem item : dashboard.getItems() )
            {
                if ( item.getChart() != null )
                {
                    item.setChart( chartService.getChart( item.getChart().getUid() ) );
                }
                
                if ( item.getChart() != null )
                {
                    item.setMap( mappingService.getMap( item.getMap().getUid() ) );
                }
                
                if ( item.getUsers() != null )
                {
                    item.setUsers( userService.getUsersByUid( getUids( item.getUsers() ) ) );
                }
                
                if ( item.getReportTables() != null )
                {
                    item.setReportTables( reportTableService.getReportTablesByUid( getUids( item.getReportTables() ) ) );
                }
                
                if ( item.getReports() != null )
                {
                    item.setReports( reportService.getReportsByUid( getUids( item.getReports() ) ) );
                }
                
                if ( item.getResources() != null )
                {
                    item.setResources( documentService.getDocumentsByUid( getUids( item.getResources() ) ) );
                }
            }
        }
    }

    @Override
    public int saveDashboard( Dashboard dashboard )
    {
        return dashboardStore.save( dashboard );
    }

    @Override
    public void updateDashboard( Dashboard dashboard )
    {
        dashboardStore.update( dashboard );
    }

    @Override
    public void deleteDashboard( Dashboard dashboard )
    {
        dashboardStore.delete( dashboard );
    }

    @Override
    public Dashboard getDashboard( int id )
    {
        return dashboardStore.get( id );
    }

    @Override
    public Dashboard getDashboard( String uid )
    {
        return dashboardStore.getByUid( uid );
    }

    @Override
    public List<Dashboard> getByUser( User user )
    {
        return dashboardStore.getByUser( user );
    }
}
