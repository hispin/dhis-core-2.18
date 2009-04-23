package org.hisp.dhis.datamart.impl;

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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.datamart.DataMartInternalProcess;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.aggregation.dataelement.DataElementAggregator;
import org.hisp.dhis.datamart.calculateddataelement.CalculatedDataElementDataMart;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.datamart.dataelement.DataElementDataMart;
import org.hisp.dhis.datamart.indicator.IndicatorDataMart;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.TimeUtils;

import static org.hisp.dhis.datamart.util.ParserUtil.getDataElementIdsInExpression;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultDataMartService.java 6260 2008-11-11 15:58:43Z larshelg $
 */
public class DefaultDataMartService
    extends DataMartInternalProcess
{
    private static final Log log = LogFactory.getLog( DefaultDataMartService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    protected AggregationCache aggregationCache;
        
    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }
    
    private DataMartStore dataMartStore;

    public void setDataMartStore( DataMartStore dataMartStore )
    {
        this.dataMartStore = dataMartStore;
    }

    private CrossTabService crossTabService;

    public void setCrossTabService( CrossTabService crossTabService )
    {
        this.crossTabService = crossTabService;
    }

    private DataElementDataMart dataElementDataMart;

    public void setDataElementDataMart( DataElementDataMart dataElementDataMart )
    {
        this.dataElementDataMart = dataElementDataMart;
    }

    private IndicatorDataMart indicatorDataMart;

    public void setIndicatorDataMart( IndicatorDataMart indicatorDataMart )
    {
        this.indicatorDataMart = indicatorDataMart;
    }
    
    private CalculatedDataElementDataMart calculatedDataElementDataMart;

    public void setCalculatedDataElementDataMart( CalculatedDataElementDataMart calculatedDataElementDataMart )
    {
        this.calculatedDataElementDataMart = calculatedDataElementDataMart;
    }
    
    private DataElementAggregator sumIntAggregator;

    public void setSumIntAggregator( DataElementAggregator sumIntDataElementAggregator )
    {
        this.sumIntAggregator = sumIntDataElementAggregator;
    }

    private DataElementAggregator averageIntAggregator;

    public void setAverageIntAggregator( DataElementAggregator averageIntDataElementAggregator )
    {
        this.averageIntAggregator = averageIntDataElementAggregator;
    }

    private DataElementAggregator sumBoolAggregator;

    public void setSumBoolAggregator( DataElementAggregator sumBooleanDataElementAggregator )
    {
        this.sumBoolAggregator = sumBooleanDataElementAggregator;
    }

    private DataElementAggregator averageBoolAggregator;

    public void setAverageBoolAggregator( DataElementAggregator averageBooleanDataElementAggregator )
    {
        this.averageBoolAggregator = averageBooleanDataElementAggregator;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private DataElementCategoryOptionComboService categoryOptionComboService;

    public void setCategoryOptionComboService( DataElementCategoryOptionComboService categoryOptionComboService )
    {
        this.categoryOptionComboService = categoryOptionComboService;
    }
    
    // -------------------------------------------------------------------------
    // DataMartInternalProcess implementation
    // -------------------------------------------------------------------------

    public int export( final Collection<Integer> dataElementIds, final Collection<Integer> indicatorIds,
        final Collection<Integer> periodIds, final Collection<Integer> organisationUnitIds )
    {   
        int count = 0;
             
        log.info( "Export process started" );
        
        TimeUtils.start();

        setMessage( "deleting_existing_aggregated_data" );

        // ---------------------------------------------------------------------
        // Delete existing aggregated data
        // ---------------------------------------------------------------------

        dataMartStore.deleteAggregatedDataValues( dataElementIds, periodIds, organisationUnitIds );
        
        dataMartStore.deleteAggregatedIndicatorValues( indicatorIds, periodIds, organisationUnitIds );
        
        log.info( "Deleted existing aggregated data: " + TimeUtils.getHMS() );

        // ---------------------------------------------------------------------
        // Crosstabulate data
        // ---------------------------------------------------------------------

        final Set<Integer> nonCalculatedDataElementIds = filterCalculatedDataElementIds( dataElementIds, false );
        final Set<Integer> calculatedDataElementIds = filterCalculatedDataElementIds( dataElementIds, true );
        
        final Set<Integer> dataElementInIndicatorIds = getDataElementIdsInIndicators( indicatorIds );
        final Set<Integer> dataElementInCalculatedDataElementIds = getDataElementIdsInCalculatedDataElements( calculatedDataElementIds );
        
        final Set<Integer> allDataElementIds = new HashSet<Integer>();
        allDataElementIds.addAll( nonCalculatedDataElementIds );
        allDataElementIds.addAll( dataElementInIndicatorIds );
        allDataElementIds.addAll( dataElementInCalculatedDataElementIds );
        
        final Collection<Operand> allDataElementOperands = categoryOptionComboService.getOperands( dataElementService.getDataElements( allDataElementIds ) );
        final Collection<Operand> dataElementInIndicatorOperands = categoryOptionComboService.getOperands( dataElementService.getDataElements( dataElementInIndicatorIds ) );
        final Collection<Operand> dataElementInCalculatedDataElementOperands = categoryOptionComboService.getOperands( dataElementService.getDataElements( dataElementInCalculatedDataElementIds ) );

        // ---------------------------------------------------------------------
        // Validate crosstabtable
        // ---------------------------------------------------------------------

        if ( crossTabService.validateCrossTabTable( allDataElementOperands ) != 0 )
        {
            int excess = crossTabService.validateCrossTabTable( allDataElementOperands );
            
            log.warn( "Cannot crosstabulate since the number of data elements exceeded maximum columns: " + excess );
            
            setMessage( "could_not_export_too_many_data_elements" );
            
            return 0;
        }           

        setMessage( "crosstabulating_data" );

        final Collection<Operand> emptyOperands = crossTabService.populateCrossTabTable( allDataElementOperands, getIntersectingIds( periodIds ), 
            getIdsWithChildren( organisationUnitIds ) );

        log.info( "Populated crosstab table: " + TimeUtils.getHMS() );
                
        crossTabService.trimCrossTabTable( emptyOperands );

        log.info( "Trimmed crosstab table: " + TimeUtils.getHMS() );
        
        final Collection<DataElement> dataElements = dataElementService.getDataElements( nonCalculatedDataElementIds );
        
        final Collection<Operand> sumIntOperands = getOperands( dataElements, DataElement.AGGREGATION_OPERATOR_SUM, DataElement.TYPE_INT );
        final Collection<Operand> averageIntOperands = getOperands( dataElements, DataElement.AGGREGATION_OPERATOR_AVERAGE, DataElement.TYPE_INT );
        final Collection<Operand> sumBooleanOperands = getOperands( dataElements, DataElement.AGGREGATION_OPERATOR_SUM, DataElement.TYPE_BOOL );
        final Collection<Operand> averageBooleanOperands = getOperands( dataElements, DataElement.AGGREGATION_OPERATOR_AVERAGE, DataElement.TYPE_BOOL );

        // ---------------------------------------------------------------------
        // Data element export
        // ---------------------------------------------------------------------

        setMessage( "exporting_data_for_data_elements" );

        if ( sumIntOperands.size() > 0 )
        {
            count += dataElementDataMart.exportDataValues( sumIntOperands, periodIds, organisationUnitIds, sumIntAggregator );
        
            log.info( "Exported values for data elements with sum aggregation operator of type number (" + sumIntOperands.size() + "): " + TimeUtils.getHMS() );
        }

        if ( averageIntOperands.size() > 0 )
        {            
            count += dataElementDataMart.exportDataValues( averageIntOperands, periodIds, organisationUnitIds, averageIntAggregator );
        
            log.info( "Exported values for data elements with average aggregation operator of type number (" + averageIntOperands.size() + "): " + TimeUtils.getHMS() );
        }

        if ( sumBooleanOperands.size() > 0 )
        {
            count += dataElementDataMart.exportDataValues( sumBooleanOperands, periodIds, organisationUnitIds, sumBoolAggregator );
            
            log.info( "Exported values for data elements with sum aggregation operator of type yes/no (" + sumBooleanOperands.size() + "): " + TimeUtils.getHMS() );
        }

        if ( averageBooleanOperands.size() > 0 )
        {
            count += dataElementDataMart.exportDataValues( averageBooleanOperands, periodIds, organisationUnitIds, averageBoolAggregator );
            
            log.info( "Exported values for data elements with average aggregation operator of type yes/no (" + averageBooleanOperands.size() + "): " + TimeUtils.getHMS() );
        }

        setMessage( "exporting_data_for_indicators" );

        // ---------------------------------------------------------------------
        // Indicator export
        // ---------------------------------------------------------------------

        if ( indicatorIds != null && indicatorIds.size() > 0 )
        {
            count += indicatorDataMart.exportIndicatorValues( indicatorIds, periodIds, organisationUnitIds, dataElementInIndicatorOperands );
            
            log.info( "Exported values for indicators (" + indicatorIds.size() + "): " + TimeUtils.getHMS() );
        }

        setMessage( "exporting_data_for_calculated_data_elements" );

        // ---------------------------------------------------------------------
        // Calculated data element export
        // ---------------------------------------------------------------------

        if ( calculatedDataElementIds != null && calculatedDataElementIds.size() > 0 )
        {
            count += calculatedDataElementDataMart.exportCalculatedDataElements( calculatedDataElementIds, periodIds, organisationUnitIds, dataElementInCalculatedDataElementOperands );
            
            log.info( "Exported values for calculated data elements (" + calculatedDataElementIds.size() + "): " + TimeUtils.getHMS() );
        }

        crossTabService.dropCrossTabTable();
        
        log.info( "Export process completed: " + TimeUtils.getHMS() );
        
        TimeUtils.stop();

        setMessage( "export_process_done" );
        
        aggregationCache.clearCache();
        
        return count;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Sorts calculated data elements from non-calculated based on the given collection
     * of identifiers and flag.
     */
    private Set<Integer> filterCalculatedDataElementIds( final Collection<Integer> dataElementIds, boolean calculated )
    {
        final Set<Integer> identifiers = new HashSet<Integer>();
        
        for ( final Integer id : dataElementIds )
        {
            final DataElement element = dataElementService.getDataElement( id );
            
            if ( ( element instanceof CalculatedDataElement ) == calculated )
            {
                identifiers.add( id );
            }
        }
        
        return identifiers;
    }
    
    /**
     * Returns all data element identifiers included in the indicators in the given 
     * identifier collection.
     */
    private Set<Integer> getDataElementIdsInIndicators( final Collection<Integer> indicatorIds )
    {
        final Set<Integer> identifiers = new HashSet<Integer>( indicatorIds.size() );
        
        for ( final Integer id : indicatorIds )
        {
            final Indicator indicator = indicatorService.getIndicator( id );
            
            identifiers.addAll( getDataElementIdsInExpression( indicator.getNumerator() ) );
            identifiers.addAll( getDataElementIdsInExpression( indicator.getDenominator() ) );            
        }
        
        return identifiers;
    }
    
    /**
     * Returns all data element identifiers included in the calculated data elements
     * in the given identifier collection.
     */
    private Set<Integer> getDataElementIdsInCalculatedDataElements( final Collection<Integer> calculatedDataElementIds )
    {
        final Set<Integer> identifiers = new HashSet<Integer>();
        
        for ( final Integer id : calculatedDataElementIds )
        {
            final DataElement element = dataElementService.getDataElement( id );
            
            if ( element instanceof CalculatedDataElement )
            {
                final CalculatedDataElement calculatedElement = (CalculatedDataElement) element;
                
                final Set<Integer> dataElementIds = getDataElementIdsInExpression( calculatedElement.getExpression().getExpression() );
                
                if ( dataElementIds != null )
                {
                    identifiers.addAll( dataElementIds );
                }
            }
        }
        
        return identifiers;
    }
        
    /**
     * Returns the idenfifiers in given collection including all of its children.
     */
    private Collection<Integer> getIdsWithChildren( final Collection<Integer> organisationUnitIds )
    {
        final OrganisationUnitHierarchy hierarchy = aggregationCache.getLatestOrganisationUnitHierarchy();
        
        final Set<Integer> identifers = new HashSet<Integer>( organisationUnitIds.size() );
        
        for ( final Integer id : organisationUnitIds )
        {
            identifers.addAll( aggregationCache.getChildren( hierarchy, id ) );
        }
        
        return identifers;
    }
    
    /**
     * Returns the identifiers of the periods in the given collection including 
     * all intersecting periods.
     */
    private Collection<Integer> getIntersectingIds( final Collection<Integer> periodIds )
    {
        final Set<Integer> identifiers = new HashSet<Integer>( periodIds.size() );
        
        for ( final Integer id : periodIds )
        {
            final Period period = periodService.getPeriod( id );
            
            final Collection<Period> periods = periodService.getIntersectingPeriods( period.getStartDate(), period.getEndDate() );
            
            identifiers.addAll( ConversionUtils.getIdentifiers( Period.class, periods ) );
        }
        
        return identifiers;
    }
    
    /**
     * Sorts out the data element identifers of the given aggregation operator and 
     * the given type.
     */
    private Collection<Operand> getOperands( final Collection<DataElement> dataElements, String aggregationOperator, String type )
    {
        final Collection<DataElement> section = new ArrayList<DataElement>();
        
        for ( final DataElement element : dataElements )
        {
            if ( element.getAggregationOperator().equals( aggregationOperator ) && element.getType().equals( type ) )
            {
                section.add( element );
            }
        }
        
        return categoryOptionComboService.getOperands( section );
    }
}
