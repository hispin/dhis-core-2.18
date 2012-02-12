package org.hisp.dhis.datamart.impl;

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

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.datamart.DataMartExport;
import org.hisp.dhis.datamart.DataMartService;
import org.hisp.dhis.datamart.engine.DataMartEngine;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
public class DefaultDataMartService
    implements DataMartService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataMartEngine dataMartEngine;
    
    public void setDataMartEngine( DataMartEngine dataMartEngine )
    {
        this.dataMartEngine = dataMartEngine;
    }

    private GenericIdentifiableObjectStore<DataMartExport> dataMartExportStore;

    public void setDataMartExportStore( GenericIdentifiableObjectStore<DataMartExport> dataMartExportStore )
    {
        this.dataMartExportStore = dataMartExportStore;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Export
    // -------------------------------------------------------------------------

    @Transactional //TODO potential problem with reload periods inside same tx
    public void export( int id )
    {
        DataMartExport dataMartExport = getDataMartExport( id );
        
        Collection<Period> allPeriods = new HashSet<Period>( dataMartExport.getPeriods() );
        
        if ( dataMartExport.getRelatives() != null )
        {
            allPeriods.addAll( periodService.reloadPeriods( dataMartExport.getRelatives().getRelativePeriods() ) );
        }
        
        dataMartEngine.export( 
            getIdentifiers( DataElement.class, dataMartExport.getDataElements() ), 
            getIdentifiers( Indicator.class, dataMartExport.getIndicators() ), 
            getIdentifiers( Period.class, allPeriods ),
            getIdentifiers( OrganisationUnit.class, dataMartExport.getOrganisationUnits() ),
            null, false );
    }

 
    public void export( Collection<Integer> dataElementIds, Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds, null, false );
    }
    
    public void export( Collection<Integer> dataElementIds, Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, RelativePeriods relatives )
    {
        export( dataElementIds, indicatorIds, periodIds, organisationUnitIds, null, relatives, false );
    }

    public void export( Collection<Integer> dataElementIds, Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, Collection<Integer> organisationUnitGroupIds, 
        RelativePeriods relatives, boolean completeExport )
    {
        if ( relatives != null )
        {
            periodIds.addAll( getIdentifiers( Period.class, periodService.reloadPeriods( relatives.getRelativePeriods() ) ) );
        }
        
        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds, organisationUnitGroupIds, completeExport );
    }
    
    // -------------------------------------------------------------------------
    // DataMartExport
    // -------------------------------------------------------------------------
    
    @Transactional
    public void saveDataMartExport( DataMartExport export )
    {
        dataMartExportStore.save( export );
    }

    @Transactional
    public void deleteDataMartExport( DataMartExport export )
    {
        dataMartExportStore.delete( export );
    }

    @Transactional
    public DataMartExport getDataMartExport( int id )
    {
        return dataMartExportStore.get( id );
    }

    @Transactional
    public Collection<DataMartExport> getAllDataMartExports()
    {
        return dataMartExportStore.getAll();
    }

    @Transactional
    public DataMartExport getDataMartExportByName( String name )
    {
        return dataMartExportStore.getByName( name );
    }

    @Transactional
    public int getDataMartExportCount()
    {
        return dataMartExportStore.getCount();
    }

    @Transactional
    public int getDataMartExportCountByName( String name )
    {
        return dataMartExportStore.getCountByName( name );
    }

    @Transactional
    public Collection<DataMartExport> getDataMartExportsBetween( int first, int max )
    {
        return dataMartExportStore.getBetween( first, max );
    }

    @Transactional
    public Collection<DataMartExport> getDataMartExportsBetweenByName( String name, int first, int max )
    {
        return dataMartExportStore.getBetweenByName( name, first, max );
    }
}
