package org.hisp.dhis.datamart.dataelement;

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

import static org.hisp.dhis.system.util.MathUtils.getRounded;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.datamart.aggregation.dataelement.DataElementAggregator;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.jdbc.BatchHandler;
import org.hisp.dhis.jdbc.BatchHandlerFactory;
import org.hisp.dhis.jdbc.batchhandler.AggregatedDataValueBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultDataElementDataMart.java 6049 2008-10-28 09:36:17Z larshelg $
 */
public class DefaultDataElementDataMart
    implements DataElementDataMart
{
    private static final int DECIMALS = 1;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }
    
    private CrossTabService crossTabService;

    public void setCrossTabService( CrossTabService crossTabService )
    {
        this.crossTabService = crossTabService;
    }    

    // -------------------------------------------------------------------------
    // DataMart functionality
    // -------------------------------------------------------------------------
    
    public int exportDataValues( final Collection<Operand> operands, final Collection<Integer> periodIds, 
        final Collection<Integer> organisationUnitIds, final DataElementAggregator dataElementAggregator )
    {
        final Map<Operand, Integer> operandIndexMap = crossTabService.getOperandIndexMap( operands );
        
        final Collection<Period> periods = getPeriods( periodIds );

        final Collection<OrganisationUnit> organisationUnits = getOrganisationUnits( organisationUnitIds );

        final BatchHandler batchHandler = batchHandlerFactory.createBatchHandler( AggregatedDataValueBatchHandler.class );

        batchHandler.init();
        
        int count = 0;
        int level = 0;
        
        Map<Operand, Double> valueMap = null;
        
        PeriodType periodType = null;
        
        final AggregatedDataValue value = new AggregatedDataValue();
        
        for ( final OrganisationUnit unit : organisationUnits )
        {
            level = organisationUnitService.getLevelOfOrganisationUnit( unit );
            
            for ( final Period period : periods )
            {
                valueMap = dataElementAggregator.getAggregatedValues( operandIndexMap, period, unit );
                
                periodType = period.getPeriodType();
                
                for ( Entry<Operand, Double> entry : valueMap.entrySet() )
                {
                    value.clear();
                    
                    value.setDataElementId( entry.getKey().getDataElementId() );
                    value.setCategoryOptionComboId( entry.getKey().getOptionComboId() );
                    value.setPeriodId( period.getId() );
                    value.setPeriodTypeId( periodType.getId() );
                    value.setOrganisationUnitId( unit.getId() );
                    value.setLevel( level );
                    value.setValue( getRounded( entry.getValue(), DECIMALS ) );
                    
                    batchHandler.addObject( value );
                    
                    count++;
                }
            }
        }
        
        batchHandler.flush();
        
        return count;
    }
    
    // -------------------------------------------------------------------------
    // Id-to-object methods
    // -------------------------------------------------------------------------

    private Collection<Period> getPeriods( final Collection<Integer> periodIds )
    {
        final Set<Period> periods = new HashSet<Period>( periodIds.size() );
        
        for ( Integer id : periodIds )
        {
            periods.add( periodService.getPeriod( id ) );
        }
        
        return periods;
    }
    
    private Collection<OrganisationUnit> getOrganisationUnits( final Collection<Integer> organisationUnitIds )
    {
        final Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>( organisationUnitIds.size() );
        
        for ( Integer id : organisationUnitIds )
        {
            organisationUnits.add( organisationUnitService.getOrganisationUnit( id ) );
        }
        
        return organisationUnits;
    }
}
