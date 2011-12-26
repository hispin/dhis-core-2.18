package org.hisp.dhis.completeness.impl;

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

import static org.hisp.dhis.options.SystemSettingManager.DEFAULT_COMPLETENESS_OFFSET;
import static org.hisp.dhis.options.SystemSettingManager.KEY_COMPLETENESS_OFFSET;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.completeness.DataSetCompletenessResult;
import org.hisp.dhis.completeness.DataSetCompletenessService;
import org.hisp.dhis.completeness.DataSetCompletenessStore;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.jdbc.batchhandler.DataSetCompletenessResultBatchHandler;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.system.util.ConversionUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@Transactional
public abstract class AbstractDataSetCompletenessService
    implements DataSetCompletenessService
{
    private static final Log log = LogFactory.getLog( AbstractDataSetCompletenessService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    protected BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    protected OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    protected DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    protected DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    protected PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    protected DataSetCompletenessStore completenessStore;

    public void setCompletenessStore( DataSetCompletenessStore completenessStore )
    {
        this.completenessStore = completenessStore;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // DataSetCompletenessService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    public abstract int getRegistrations( DataSet dataSet, Collection<Integer> relevantSources, Period period );

    public abstract int getRegistrationsOnTime( DataSet dataSet, Collection<Integer> relevantSources, Period period,
        Date deadline );

    public abstract int getSources( DataSet dataSet, Collection<Integer> relevantSources );

    // -------------------------------------------------------------------------
    // DataSetCompleteness
    // -------------------------------------------------------------------------

    public void exportDataSetCompleteness( Collection<Integer> dataSetIds, RelativePeriods relatives,
        Collection<Integer> organisationUnitIds )
    {
        if ( relatives != null )
        {
            Collection<Integer> periodIds = ConversionUtils.getIdentifiers( Period.class,
                periodService.reloadPeriods( relatives.getRelativePeriods() ) );

            exportDataSetCompleteness( dataSetIds, periodIds, organisationUnitIds );
        }
    }

    public void exportDataSetCompleteness( Collection<Integer> dataSetIds, Collection<Integer> periodIds,
        Collection<Integer> organisationUnitIds )
    {
        log.info( "Data completeness export process started" );

        completenessStore.dropIndex();

        log.info( "Dropped potential index" );

        int days = (Integer) systemSettingManager.getSystemSetting( KEY_COMPLETENESS_OFFSET,
            DEFAULT_COMPLETENESS_OFFSET );

        completenessStore.deleteDataSetCompleteness( dataSetIds, periodIds, organisationUnitIds );

        log.info( "Deleted existing completeness data" );

        BatchHandler<DataSetCompletenessResult> batchHandler = batchHandlerFactory
            .createBatchHandler( DataSetCompletenessResultBatchHandler.class );

        batchHandler.init();

        Collection<Period> periods = periodService.getPeriods( periodIds );
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnits( organisationUnitIds );
        Collection<DataSet> dataSets = dataSetService.getDataSets( dataSetIds );

        periods = completenessStore.getPeriodsWithRegistrations( periods );
        dataSets = completenessStore.getDataSetsWithRegistrations( dataSets );

        OrganisationUnitHierarchy hierarchy = organisationUnitService.getOrganisationUnitHierarchy();
        hierarchy.prepareChildren( units );

        // TODO Re-implement period aggregation with sql to improve performance

        for ( final DataSet dataSet : dataSets )
        {
            for ( final OrganisationUnit unit : units )
            {
                Collection<Integer> sources = hierarchy.getChildren( unit.getId() );

                Collection<Integer> relevantSources = getRelevantSources( dataSet, sources );

                for ( final Period period : periods )
                {
                    if ( period.getPeriodType() != null && dataSet.getPeriodType() != null
                        && period.getPeriodType().equals( dataSet.getPeriodType() ) )
                    {
                        final Date deadline = getDeadline( period, days );

                        final DataSetCompletenessResult result = getDataSetCompleteness( period, deadline, unit,
                            relevantSources, dataSet );

                        if ( result.getSources() > 0 )
                        {
                            batchHandler.addObject( result );
                        }
                    }
                }
            }

            log.info( "Exported completeness for data set: " + dataSet );
        }

        batchHandler.flush();

        completenessStore.createIndex();

        log.info( "Created index" );

        log.info( "Completeness export process done" );
    }

    public Collection<DataSetCompletenessResult> getDataSetCompleteness( int periodId, int organisationUnitId )
    {
        final Period period = periodService.getPeriod( periodId );

        int days = (Integer) systemSettingManager.getSystemSetting( KEY_COMPLETENESS_OFFSET,
            DEFAULT_COMPLETENESS_OFFSET );
        Date deadline = getDeadline( period, days );

        final Collection<Integer> children = organisationUnitService.getOrganisationUnitHierarchy().getChildren(
            organisationUnitId );

        final Collection<DataSet> dataSets = dataSetService.getAllDataSets();

        final Collection<DataSetCompletenessResult> results = new ArrayList<DataSetCompletenessResult>();

        for ( final DataSet dataSet : dataSets )
        {
            final Collection<Integer> relevantSources = getRelevantSources( dataSet, children );

            final DataSetCompletenessResult result = new DataSetCompletenessResult();

            result.setSources( getSources( dataSet, relevantSources ) );

            if ( result.getSources() > 0 )
            {
                result.setName( dataSet.getName() );
                result.setRegistrations( getRegistrations( dataSet, relevantSources, period ) );
                result.setRegistrationsOnTime( deadline != null ? getRegistrationsOnTime( dataSet, relevantSources,
                    period, deadline ) : 0 );

                result.setDataSetId( dataSet.getId() );
                result.setPeriodId( periodId );
                result.setOrganisationUnitId( organisationUnitId );

                results.add( result );
            }
        }

        return results;
    }

    public Collection<DataSetCompletenessResult> getDataSetCompleteness( int periodId,
        Collection<Integer> organisationUnitIds, int dataSetId )
    {
        final Period period = periodService.getPeriod( periodId );

        int days = (Integer) systemSettingManager.getSystemSetting( KEY_COMPLETENESS_OFFSET,
            DEFAULT_COMPLETENESS_OFFSET );
        Date deadline = getDeadline( period, days );

        final DataSet dataSet = dataSetService.getDataSet( dataSetId );

        final Collection<DataSetCompletenessResult> results = new ArrayList<DataSetCompletenessResult>();

        for ( final Integer unitId : organisationUnitIds )
        {
            final OrganisationUnit unit = organisationUnitService.getOrganisationUnit( unitId );

            final Collection<Integer> children = organisationUnitService.getOrganisationUnitHierarchy().getChildren(
                unit.getId() );

            final Collection<Integer> relevantSources = getRelevantSources( dataSet, children );

            final DataSetCompletenessResult result = getDataSetCompleteness( period, deadline, unit, relevantSources, dataSet );

            if ( result.getSources() > 0 )
            {
                results.add( result );
            }
        }

        return results;
    }
    
    public void deleteDataSetCompleteness()
    {
        completenessStore.deleteDataSetCompleteness();
    }

    // -------------------------------------------------------------------------
    // Index
    // -------------------------------------------------------------------------

    public void createIndex()
    {
        completenessStore.createIndex();
    }

    public void dropIndex()
    {
        completenessStore.dropIndex();
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private DataSetCompletenessResult getDataSetCompleteness( Period period, Date deadline, OrganisationUnit unit,
        Collection<Integer> relevantSources, DataSet dataSet )
    {
        final DataSetCompletenessResult result = new DataSetCompletenessResult();

        result.setName( unit.getName() );
        result.setSources( getSources( dataSet, relevantSources ) );

        if ( result.getSources() > 0 )
        {
            result.setRegistrations( getRegistrations( dataSet, relevantSources, period ) );
            result.setRegistrationsOnTime( deadline != null ? getRegistrationsOnTime( dataSet, relevantSources, period,
                deadline ) : 0 );

            result.setDataSetId( dataSet.getId() );
            result.setPeriodId( period.getId() );
            result.setPeriodName( period.getName() );
            result.setOrganisationUnitId( unit.getId() );
        }

        return result;
    }

    @SuppressWarnings( "unchecked" )
    private Collection<Integer> getRelevantSources( DataSet dataSet, Collection<Integer> sources )
    {
        Collection<Integer> dataSetSources = ConversionUtils.getIdentifiers( OrganisationUnit.class,
            dataSet.getSources() );

        return CollectionUtils.intersection( dataSetSources, sources );
    }

    private Date getDeadline( Period period, int days )
    {
        Calendar cal = Calendar.getInstance();

        Date date = null;

        if ( period != null )
        {
            cal.clear();
            cal.setTime( period.getEndDate() );
            cal.add( Calendar.DAY_OF_MONTH, days );

            date = cal.getTime();
        }

        return date;
    }
}
