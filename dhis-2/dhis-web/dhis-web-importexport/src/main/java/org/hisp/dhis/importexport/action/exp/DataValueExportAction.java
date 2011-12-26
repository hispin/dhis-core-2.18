package org.hisp.dhis.importexport.action.exp;

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

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.ConversionUtils.getIntegerCollection;
import static org.hisp.dhis.system.util.DateUtils.getMediumDate;
import static org.hisp.dhis.system.util.DateUtils.getMediumDateString;

import java.io.InputStream;
import java.util.Collection;

import org.hisp.dhis.common.ServiceProvider;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ExportService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.ConversionUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataValueExportAction
    implements Action
{
    private final static String FILE_EXTENSION = ".zip";
    private final static String FILE_PREFIX = "Export";
    private final static String FILE_SEPARATOR = "_";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private ServiceProvider<ExportService> serviceProvider;

    public void setServiceProvider( ServiceProvider<ExportService> serviceProvider )
    {
        this.serviceProvider = serviceProvider;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }
    
    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private String exportFormat;

    public String getExportFormat()
    {
        return exportFormat;
    }

    public void setExportFormat( String exportFormat )
    {
        this.exportFormat = exportFormat;
    }
    
    private boolean aggregatedData;

    public void setAggregatedData( boolean aggregatedData )
    {
        this.aggregatedData = aggregatedData;
    }

    private boolean excludeChildren;

    public void setExcludeChildren( boolean excludeChildren )
    {
        this.excludeChildren = excludeChildren;
    }
    
    private int dataSourceLevel;

    public void setDataSourceLevel( int dataSourceLevel )
    {
        this.dataSourceLevel = dataSourceLevel;
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

    private Collection<String> selectedDataSets;

    public void setSelectedDataSets( Collection<String> selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        ExportParams params = new ExportParams();

        // ---------------------------------------------------------------------
        // Get DataElements
        // ---------------------------------------------------------------------

        if ( selectedDataSets != null )
        {
            params.setCategories( null );
            params.setCategoryCombos( null );
            params.setCategoryOptions( null );
            params.setCategoryOptionCombos( null );
            
            params.setDataSets( getIntegerCollection( selectedDataSets ) );
            
            params.setDataElements( getIdentifiers( DataElement.class, dataSetService.getDistinctDataElements( params.getDataSets() ) ) );
        }
        
        // ---------------------------------------------------------------------
        // Get Periods
        // ---------------------------------------------------------------------

        if ( startDate != null && startDate.trim().length() > 0 && endDate != null && endDate.trim().length() > 0 )
        {
            params.setStartDate( getMediumDate( startDate ) );
            params.setEndDate( getMediumDate( endDate ) );
            
            params.setPeriods( getIdentifiers( Period.class, 
                periodService.getIntersectingPeriods( getMediumDate( startDate ), getMediumDate( endDate ) ) ) );
        }
        
        // ---------------------------------------------------------------------
        // Get OrganisationUnit
        // ---------------------------------------------------------------------
        
        Collection<OrganisationUnit> selectedUnits = selectionTreeManager.getReloadedSelectedOrganisationUnits();
        
        if ( selectedUnits != null )
        {
            if ( aggregatedData )
            {
                for ( OrganisationUnit unit : selectedUnits )
                {
                    params.getOrganisationUnits().addAll( ConversionUtils.getIdentifiers( OrganisationUnit.class,
                        organisationUnitService.getOrganisationUnitsAtLevel( dataSourceLevel, unit ) ) );
                }
            }
            else
            {
                for ( OrganisationUnit unit : selectedUnits )
                {
                    if ( excludeChildren )
                    {
                        params.getOrganisationUnits().add( unit.getId() );
                    }
                    else
                    {
                        params.getOrganisationUnits().addAll( ConversionUtils.getIdentifiers( OrganisationUnit.class, 
                            organisationUnitService.getOrganisationUnitWithChildren( unit.getId() ) ) );
                    }
                }
            }
        }

        params.setIncludeDataValues( true );
        params.setIncludeCompleteDataSetRegistrations( true );
        params.setAggregatedData( aggregatedData );
        
        // ---------------------------------------------------------------------
        // Export
        // ---------------------------------------------------------------------
        
        ExportService exportService = serviceProvider.provide( exportFormat );        
        
        inputStream = exportService.exportData( params );
        
        fileName = getFileName( params );
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private String getFileName( ExportParams params )
    {
        String fileName = FILE_PREFIX + FILE_SEPARATOR + 
            getMediumDateString( getMediumDate( startDate ) ) + FILE_SEPARATOR + 
            getMediumDateString( getMediumDate( endDate ) );
        
        if ( selectionTreeManager.getSelectedOrganisationUnits().size() == 1 )
        {
            fileName += FILE_SEPARATOR + fileNameEncode( selectionTreeManager.getSelectedOrganisationUnits().iterator().next().getShortName() );
        }
        
        if ( params.getDataSets().size() == 1 )
        {
            fileName += FILE_SEPARATOR + fileNameEncode( dataSetService.getDataSet( params.getDataSets().iterator().next() ).getName() );
        }
        
        fileName += FILE_EXTENSION;
        
        return fileName;
    }
    
    private String fileNameEncode( String in )
    {
        if ( in == null )
        {
            return "";
        }
        
        in = in.replaceAll( " ", FILE_SEPARATOR );
        
        return in;
    }
}
