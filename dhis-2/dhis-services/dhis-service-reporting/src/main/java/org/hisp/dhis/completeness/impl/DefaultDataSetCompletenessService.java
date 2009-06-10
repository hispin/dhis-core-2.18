package org.hisp.dhis.completeness.impl;

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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.completeness.DataSetCompletenessConfiguration;
import org.hisp.dhis.completeness.DataSetCompletenessResult;
import org.hisp.dhis.completeness.DataSetCompletenessService;
import org.hisp.dhis.completeness.DataSetCompletenessStore;
import org.hisp.dhis.completeness.cache.DataSetCompletenessCache;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.external.configuration.ConfigurationManager;
import org.hisp.dhis.external.configuration.NoConfigurationFoundException;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.external.location.LocationManagerException;
import org.hisp.dhis.jdbc.BatchHandler;
import org.hisp.dhis.jdbc.BatchHandlerFactory;
import org.hisp.dhis.jdbc.batchhandler.DataSetCompletenessResultBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.source.Source;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@Transactional
public class DefaultDataSetCompletenessService
    implements DataSetCompletenessService
{
    private static final Log log = LogFactory.getLog( DefaultDataSetCompletenessService.class );
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private String configDir;

    public void setConfigDir( String configDir )
    {
        this.configDir = configDir;
    }
    
    private String configFile;

    public void setConfigFile( String configFile )
    {
        this.configFile = configFile;
    }
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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
    
    private CompleteDataSetRegistrationService registrationService;
    
    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
    }

    private LocationManager locationManager;

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }

    private ConfigurationManager<DataSetCompletenessConfiguration> configurationManager;
    
    public void setConfigurationManager( ConfigurationManager<DataSetCompletenessConfiguration> configurationManager )
    {
        this.configurationManager = configurationManager;
    }

    private DataSetCompletenessCache completenessCache;

    public void setCompletenessCache( DataSetCompletenessCache completenessCache )
    {
        this.completenessCache = completenessCache;
    }

    private DataSetCompletenessStore completenessStore;

    public void setCompletenessStore( DataSetCompletenessStore completenessStore )
    {
        this.completenessStore = completenessStore;
    }
    
    // -------------------------------------------------------------------------
    // DataSetCompletenessService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // DataSetCompleteness
    // -------------------------------------------------------------------------

    public void exportDataSetCompleteness( Collection<Integer> dataSetIds, 
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, Integer reportTableId )
    {
        log.info( "Data completeness export process started" );
        
        completenessStore.deleteDataSetCompleteness( dataSetIds, periodIds, organisationUnitIds );
        
        BatchHandler batchHandler = batchHandlerFactory.createBatchHandler( DataSetCompletenessResultBatchHandler.class );
        
        batchHandler.init();
        
        Collection<Period> periods = periodService.getPeriods( periodIds );
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnits( organisationUnitIds );
        Collection<DataSet> dataSets = dataSetService.getDataSets( dataSetIds );
        
        Collection<Period> intersectingPeriods = null;
        Date deadline = null;
        DataSetCompletenessResult result = null;
        
        for ( final Period period : periods )
        {
            intersectingPeriods = periodService.getIntersectingPeriods( period.getStartDate(), period.getEndDate() );
            
            for ( final OrganisationUnit unit : units )
            {
                for ( final DataSet dataSet : dataSets )
                {
                    final DataSetCompletenessResult aggregatedResult = new DataSetCompletenessResult();
                    
                    aggregatedResult.setDataSetId( dataSet.getId() );
                    aggregatedResult.setPeriodId( period.getId() );
                    aggregatedResult.setPeriodName( period.getName() );
                    aggregatedResult.setOrganisationUnitId( unit.getId() );
                    aggregatedResult.setReportTableId( reportTableId );
                    
                    for ( final Period intersectingPeriod : intersectingPeriods )
                    {
                        if ( intersectingPeriod.getPeriodType().equals( dataSet.getPeriodType() ) )
                        {
                            deadline = completenessCache.getDeadline( intersectingPeriod );
                            
                            result = getDataSetCompleteness( intersectingPeriod, deadline, unit, dataSet );
                            
                            aggregatedResult.incrementSources( result.getSources() );
                            aggregatedResult.incrementRegistrations( result.getRegistrations() );
                            aggregatedResult.incrementRegistrationsOnTime( result.getRegistrationsOnTime() );
                        }
                    }
                    
                    if ( aggregatedResult.getSources() > 0 )
                    {
                        batchHandler.addObject( aggregatedResult );
                    }
                }
            }
            
            log.info( "Exported data completeness for period " + period.getId() );
        }
        
        completenessCache.clear();
        
        batchHandler.flush();
        
        log.info( "Export process done" );
    }
    
    public Collection<DataSetCompletenessResult> getDataSetCompleteness( int periodId, int organisationUnitId )
    {
        final Period period = periodService.getPeriod( periodId );
        
        Date deadline = null;
        
        try
        {
            deadline = getConfiguration().getDeadline( period );
        }
        catch ( NoConfigurationFoundException ex )
        {
            log.warn( "Disabling on-time completeness calculations because no configuration was found" );
        }
        
        final Collection<? extends Source> children = organisationUnitService.getOrganisationUnitWithChildren( organisationUnitId );
        
        final Collection<DataSet> dataSets = dataSetService.getDataSetsBySources( children );
        
        final Collection<DataSetCompletenessResult> results = new ArrayList<DataSetCompletenessResult>();
        
        for ( final DataSet dataSet : dataSets )
        {
            final DataSetCompletenessResult result = new DataSetCompletenessResult();
            
            result.setName( dataSet.getName() );
            result.setRegistrations( registrationService.getCompleteDataSetRegistrationsForDataSet( dataSet, children, period ) );
            result.setRegistrationsOnTime( deadline != null ? registrationService.getCompleteDataSetRegistrationsForDataSet( dataSet, children, period, deadline ) : 0 );
            result.setSources( dataSetService.getSourcesAssociatedWithDataSet( dataSet, children ) );
            
            result.setDataSetId( dataSet.getId() );
            result.setPeriodId( periodId );
            result.setOrganisationUnitId( organisationUnitId );
            
            results.add( result );
        }
        
        return results;
    }
    
    public Collection<DataSetCompletenessResult> getDataSetCompleteness( int periodId, int parentOrganisationUnitId, int dataSetId )
    {
        final Period period = periodService.getPeriod( periodId );

        Date deadline = null;
        
        try
        {
            deadline = getConfiguration().getDeadline( period );
        }
        catch ( NoConfigurationFoundException ex )
        {
            log.warn( "Disabling on-time completeness calculations because no configuration was found" );
        }
        
        final DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        final OrganisationUnit parent = organisationUnitService.getOrganisationUnit( parentOrganisationUnitId );
        
        final Collection<OrganisationUnit> units = parent.getChildren();
        
        final Collection<DataSetCompletenessResult> results = new ArrayList<DataSetCompletenessResult>();
        
        Collection<OrganisationUnit> children = null;
        
        for ( final OrganisationUnit unit : units )
        {
            children = organisationUnitService.getOrganisationUnitWithChildren( unit.getId() );
            
            final DataSetCompletenessResult result = new DataSetCompletenessResult();
            
            result.setName( unit.getName() );
            result.setRegistrations( registrationService.getCompleteDataSetRegistrationsForDataSet( dataSet, children, period ) );
            result.setRegistrationsOnTime( deadline != null ? registrationService.getCompleteDataSetRegistrationsForDataSet( dataSet, children, period, deadline ) : 0 );
            result.setSources( dataSetService.getSourcesAssociatedWithDataSet( dataSet, children ) );
            
            result.setDataSetId( dataSetId );
            result.setPeriodId( periodId );
            result.setOrganisationUnitId( unit.getId() );
            
            results.add( result );
        }
        
        return results;
    }

    public DataSetCompletenessResult getDataSetCompleteness( Period period, Date deadline, OrganisationUnit unit, DataSet dataSet )
    {        
        final Collection<OrganisationUnit> children = organisationUnitService.getOrganisationUnitWithChildren( unit.getId() );
        
        final DataSetCompletenessResult result = new DataSetCompletenessResult();
        
        result.setName( unit.getName() );
        result.setRegistrations( registrationService.getCompleteDataSetRegistrationsForDataSet( dataSet, children, period ) );
        result.setRegistrationsOnTime( deadline != null ? registrationService.getCompleteDataSetRegistrationsForDataSet( dataSet, children, period, deadline ) : 0 );
        result.setSources( dataSetService.getSourcesAssociatedWithDataSet( dataSet, children ) );
        
        result.setDataSetId( dataSet.getId() );
        result.setPeriodId( period.getId() );
        result.setOrganisationUnitId( unit.getId() );
        
        return result;
    }
    
    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    public void setConfiguration( DataSetCompletenessConfiguration configuration )
    {
        try
        {
            OutputStream out = locationManager.getOutputStream( configFile, configDir );
            
            configurationManager.setConfiguration( configuration, out );
        }
        catch ( LocationManagerException ex )
        {
            throw new RuntimeException( "Failed to set configuration", ex );
        }
    }
    
    public DataSetCompletenessConfiguration getConfiguration()
        throws NoConfigurationFoundException
    {
        try
        {
            InputStream in = locationManager.getInputStream( configFile, configDir );
            
            return configurationManager.getConfiguration( in, DataSetCompletenessConfiguration.class );
        }
        catch ( LocationManagerException ex )
        {
            throw new NoConfigurationFoundException( "No configuration file found" );
        }
    }
}
