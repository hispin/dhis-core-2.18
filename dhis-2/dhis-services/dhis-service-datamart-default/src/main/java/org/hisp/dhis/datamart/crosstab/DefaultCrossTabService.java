package org.hisp.dhis.datamart.crosstab;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.CrossTabDataValue;
import org.hisp.dhis.datamart.crosstab.jdbc.CrossTabStore;
import org.hisp.dhis.jdbc.batchhandler.GenericBatchHandler;
import org.hisp.dhis.system.util.PaginatedList;

/**
 * @author Lars Helge Overland
 */
public class DefaultCrossTabService
    implements CrossTabService
{
    private static final Log log = LogFactory.getLog( DefaultCrossTabService.class );

    private static final int MAX_LENGTH = 20;

    private int maxColumns = 1500;

    public void setMaxColumns( int maxColumns )
    {
        this.maxColumns = maxColumns;
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    private CrossTabStore crossTabStore;

    public void setCrossTabStore( CrossTabStore crossTabTableManager )
    {
        this.crossTabStore = crossTabTableManager;
    }

    private AggregatedDataValueService aggregatedDataValueService;

    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    // -------------------------------------------------------------------------
    // CrossTabService implementation
    // -------------------------------------------------------------------------

    public Collection<DataElementOperand> getOperandsWithData( Collection<DataElementOperand> operands )
    {
        return crossTabStore.getOperandsWithData( operands );
    }
    
    public List<String> populateCrossTabTable( final Collection<DataElementOperand> operands,
        final Collection<Integer> periodIds, final Collection<Integer> organisationUnitIds )
    {
        if ( validate( operands, periodIds, organisationUnitIds ) )
        {
            final PaginatedList<DataElementOperand> operandList = new PaginatedList<DataElementOperand>( operands, maxColumns );

            final List<String> crossTabTableKeys = new ArrayList<String>();
            
            List<DataElementOperand> operandPage = new ArrayList<DataElementOperand>();
            
            while ( ( operandPage = operandList.nextPage() ) != null )
            {
                final String key = RandomStringUtils.randomAlphanumeric( 8 );
                
                crossTabTableKeys.add( key );
                
                crossTabStore.dropCrossTabTable( key );    
                crossTabStore.createCrossTabTable( operandPage, key );

                final BatchHandler<Object> batchHandler = batchHandlerFactory.createBatchHandler( GenericBatchHandler.class );
                batchHandler.setTableName( CrossTabStore.TABLE_PREFIX + key );
                batchHandler.init();

                for ( final Integer periodId : periodIds )
                {
                    for ( final Integer sourceId : organisationUnitIds )
                    {
                        final Map<DataElementOperand, String> map = aggregatedDataValueService.getDataValueMap( periodId, sourceId );
    
                        final List<String> valueList = new ArrayList<String>( operandPage.size() + 2 );
    
                        valueList.add( String.valueOf( periodId ) );
                        valueList.add( String.valueOf( sourceId ) );
    
                        boolean hasValues = false;
    
                        for ( DataElementOperand operand : operandPage )
                        {
                            String value = map.get( operand );
    
                            if ( value != null && value.length() > MAX_LENGTH )
                            {
                                log.warn( "Value ignored, too long: '" + value + "'" );                                
                                value = null;
                            }
    
                            if ( value != null )
                            {
                                hasValues = true;
                            }
    
                            valueList.add( value );
                        }
    
                        if ( hasValues )
                        {
                            batchHandler.addObject( valueList );
                        }
                    }
                }
    
                batchHandler.flush();
                
                log.info( "Populated crosstab table for key: " + key );    
            }
            
            return crossTabTableKeys;
        }

        return null;
    }

    public void dropCrossTabTable( String key )
    {
        crossTabStore.dropCrossTabTable( key );
    }

    public Collection<CrossTabDataValue> getCrossTabDataValues( Collection<DataElementOperand> operands,
        Collection<Integer> periodIds, Collection<Integer> sourceIds, List<String> keys )
    {
        return crossTabStore.getCrossTabDataValues( operands, periodIds, sourceIds, keys );
    }

    public Collection<CrossTabDataValue> getCrossTabDataValues( Collection<DataElementOperand> operands,
        Collection<Integer> periodIds, int sourceId, List<String> keys )
    {
        return crossTabStore.getCrossTabDataValues( operands, periodIds, sourceId, keys );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Validates whether the given collections of identifiers are not null and
     * of size greater than 0.
     */
    private boolean validate( Collection<DataElementOperand> operands, Collection<Integer> periodIds,
        Collection<Integer> unitIds )
    {
        if ( operands == null || operands.size() == 0 )
        {
            log.warn( "No operands selected for crosstab table" );
            return false;
        }

        if ( periodIds == null || periodIds.size() == 0 )
        {
            log.warn( "No periods selected for crosstab table" );
            return false;
        }

        if ( unitIds == null || unitIds.size() == 0 )
        {
            log.warn( "No organisation units selected for crosstab table" );
            return false;
        }

        return true;
    }
}
