package org.hisp.dhis.rt.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;
import org.hisp.dhis.rt.dataaccess.ReportDataAccess;
import org.hisp.dhis.rt.dataaccess.ReportDataAccessException;
import org.hisp.dhis.rt.report.ReportStore;
import org.hisp.dhis.rt.report.ReportStoreException;

/**
 * @author Lars Helge Overland
 * @version $Id: ViewReportAction.java 2871 2007-02-20 16:04:11Z andegje $
 */
public class ViewReportAction
    extends AbstractAction
{    
    // -----------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------

    ReportDataAccess reportDataAccess;
    
    public void setReportDataAccess( ReportDataAccess reportDataAccess )
    {
        this.reportDataAccess = reportDataAccess;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }
    
    private Comparator<Indicator> indicatorComparator;    

    public void setIndicatorComparator( Comparator<Indicator> indicatorComparator )
    {
        this.indicatorComparator = indicatorComparator;
    }
    
    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }    
    
    // -----------------------------------------------------------------------
    // Parameters
    // -----------------------------------------------------------------------

    private String reportName;
    
    private List<DataElement> dataElements;
    
    private List<Indicator> indicators;
    
    private Collection dataElementGroups;
    
    private Collection indicatorGroups;

    private Collection currentReportElements;
    
    private Collection currentChartElements;
    
    private boolean showChartElements;
        
    public String getReportName()
    {
        return reportName;
    }

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    public Collection getDataElementGroups()
    {
        return dataElementGroups;
    }

    public Collection getIndicatorGroups()
    {
        return indicatorGroups;
    }

    public Collection getCurrentReportElements()
    {
        return currentReportElements;
    }

    public Collection getCurrentChartElements()
    {
        return currentChartElements;
    }

    public boolean isShowChartElements()
    {
        return showChartElements;
    }

    public void setShowChartElements( boolean showChartElements )
    {
        this.showChartElements = showChartElements;
    }
    
    // -----------------------------------------------------------------------
    // Execute
    // -----------------------------------------------------------------------
    
    public String execute()
        throws ReportDataAccessException, ReportStoreException
    {
        report = (String) getSessionVar( REPORT );
        
        int reportType = Integer.parseInt( getSessionVar( REPORT_TYPE ).toString() );
        
        reportName = report + " (" + getReportTypeName( reportType ) + ")";
        
        dataElementGroups = reportDataAccess.getAllDataElementGroups();
        
        indicatorGroups = reportDataAccess.getAllIndicatorGroups();
        
        if ( dataElementGroupId == ALL )
        {
            dataElements = new ArrayList<DataElement>( reportDataAccess.getAllDataElements() );
        }
        else
        {
            dataElements = new ArrayList<DataElement>( reportDataAccess.getMembersOfDataElementGroup( dataElementGroupId ) );
        }
        
        Collections.sort( dataElements, dataElementComparator );
        
        dataElements = displayPropertyHandler.handleDataElements( dataElements );
        
        if ( indicatorGroupId == ALL )
        {
            indicators = new ArrayList<Indicator>( reportDataAccess.getAllIndicators() );
        }
        else
        {
            indicators = new ArrayList<Indicator>( reportDataAccess.getMembersOfIndicatorGroup( indicatorGroupId ) );
        }
        
        Collections.sort( indicators, indicatorComparator );
        
        indicators = displayPropertyHandler.handleIndicators( indicators );
        
        if ( report != null )
        {
            designTemplate = reportStore.getDesignTemplate( report );
            
            chartTemplate = reportStore.getChartTemplate( report );
            
            currentReportElements = reportStore.getAllReportElements( report );
            
            currentChartElements = reportStore.getAllChartElements( report );
        }
        
        return SUCCESS;
    }
    
    private String getReportTypeName( int reportType )
    {
        String reportTypeName = new String();
        
        if ( reportType == ReportStore.GENERIC )
        {
            reportTypeName = i18n.getString( "generic" ); 
        }
        else if ( reportType == ReportStore.ORGUNIT_SPESIFIC )
        {
            reportTypeName = i18n.getString( "org_unit_spesific" );
        }
        
        return reportTypeName;
    }
    
    
}

